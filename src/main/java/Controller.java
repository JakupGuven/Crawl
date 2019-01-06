import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Controller {
	private ExecutorService threadManager = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() > 1 ? Runtime.getRuntime().availableProcessors() : 1  ); //Test with Executors.cachedThreadPool
	private int threads = (Runtime.getRuntime().availableProcessors() > 1 ? Runtime.getRuntime().availableProcessors() : 1);
	private Set<Link> seenLinks = new HashSet();
	
	private Set<Page> pageSet = new HashSet();
	private Queue<Link> linksToCrawl = new LinkedList<Link>();
	
	private long startTime;
	private long endTime;
	
	public static void main(String[] args){
		Controller c = new Controller();
		if(args.length > 0){
			c.start(args[0]);
		}else{
			c.start("https://news.ycombinator.com/");
		}
	}
	
	public void start(String initialURL){
		startTime = System.currentTimeMillis();
		seenLinks.add(new Link(initialURL));
		System.out.println("Starting crawl process at: " + initialURL);
		ArrayList<Link> firstURL = new ArrayList<Link>();
		firstURL.add(new Link(initialURL));
		CrawlJob firstCrawlJob = new CrawlJob(firstURL);
		Future<Set<Page>> crawlFuture = threadManager.submit(firstCrawlJob);
		
		try {
			Set<Page> firstCrawlResults = crawlFuture.get();
			
			if(firstCrawlResults.size() < 1){
				System.out.println(("First crawl could find no links."));
				System.exit(2);
			}
			
			for(Page crawlResult : firstCrawlResults){
				pageSet.add(crawlResult);
				for(Link link : crawlResult.getLinks()){
					if(!seenLinks.contains(link)){
						seenLinks.add(link);
						linksToCrawl.add(link);
					}
				}
			}
			
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(3);
		}
		
		crawlLoop();
		
	}
	
	public void crawlLoop(){
		long pagesVisitedCounter = 0;
		LinkedList<Future<Set<Page>>> futureBoard = new LinkedList<Future<Set<Page>>>();
		
		while(!linksToCrawl.isEmpty() || !futureBoard.isEmpty()){
			
			if(futureBoard.size() <= 10 && linksToCrawl.size() > threads){
				long linksPerThread = linksToCrawl.size() / threads;
				long remainder = linksToCrawl.size() - (linksPerThread*threads);
				
				for(int i = 0; i < threads; i++){
					ArrayList<Link> urls = new ArrayList<Link>();
					for(long j = 0; j < linksPerThread-1; j++){
						urls.add(linksToCrawl.remove());
					}
					CrawlJob crawlJob = new CrawlJob(urls);
					futureBoard.add(threadManager.submit(crawlJob));
				}
				
				if(remainder >= 1){
					ArrayList<Link> urls = new ArrayList<Link>();
					for(long i = 0; i < remainder; i++){
						urls.add(linksToCrawl.remove());
					}
					CrawlJob crawlJob = new CrawlJob(urls);
					futureBoard.add(threadManager.submit(crawlJob));
				}
			}else if(futureBoard.size() <= 10){
				ArrayList<Link> urls = new ArrayList<Link>();
				for(long i = 0; i < linksToCrawl.size(); i++){
					urls.add(linksToCrawl.remove());
				}
				CrawlJob crawlJob = new CrawlJob(urls);
				futureBoard.add(threadManager.submit(crawlJob));
			}
			
			for(int i = 0; i < futureBoard.size(); i++){
				Future<Set<Page>> result;
				if((result = futureBoard.get(i)).isDone()){
					try {
						for(Page page : result.get()){
							for(Link link : page.getLinks()){
								if(!seenLinks.contains(link)){
									linksToCrawl.add(link);
									seenLinks.add(link);
								}
							}
							if(!pageSet.contains(page)){
								pageSet.add(page);
								pagesVisitedCounter++;
							}else{
								System.out.println(result.toString() + " " + i);
							}
							
						}
						futureBoard.remove(i);
						break;
					} catch (InterruptedException | ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
			if(pageSet.size() > 200){
				System.out.println("Writing to DB");
				SaveJob writeToDB = new SaveJob(pageSet);
				threadManager.execute(writeToDB);
				pageSet = new HashSet();
			}
			
		}
		endTime = System.currentTimeMillis();
		System.out.println("Visited " + pagesVisitedCounter + " in " + (endTime-startTime));
	}

}

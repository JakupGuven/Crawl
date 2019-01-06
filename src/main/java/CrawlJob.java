import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlJob implements Callable<Set<Page>>{
	private ArrayList<Link> links;
	private Set<Page> pageList = new HashSet<Page>();
	
	public CrawlJob(ArrayList<Link> linksToCrawl){
		links = linksToCrawl;
	}
	
	@Override
	public Set<Page> call() throws Exception {
		for(Link link : links){
			try{
				Page currentPage = new Page();
				
				Document document = Jsoup.connect(link.getUrl()).userAgent("Mozilla").get();
//				System.out.println("Crawling " + link.getUrl());
				currentPage.setTitle(document.title());
				currentPage.setUrl(link.getUrl());
				Elements pageLinks = document.select("a[href]");
				for(Element pageLink : pageLinks){
					String url = pageLink.attr("href");
					if(url.startsWith("http")){
						currentPage.addLink(new Link(url, link));
					}
				}
				
				Elements pageParagraphs = document.select("p");
				for(Element pageParagraph : pageParagraphs){
					String paragraph = pageParagraph.text();
					currentPage.addParagraph(paragraph);
				}
				
				pageList.add(currentPage);
				try {
					Thread.sleep(200);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		return pageList;
	}

}

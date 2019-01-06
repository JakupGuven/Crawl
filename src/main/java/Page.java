import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class Page {
	private String title;
	private String url;
	private Set<Link> links = new LinkedHashSet();
	private ArrayList<String> paragraphs = new ArrayList<String>();
	
	@Override
	public boolean equals(Object obj){
		if(obj == null || !(obj instanceof Link) ){
			return false;
		}
		return url.equals(((Page) obj).getUrl());
	}
	
	public void addLink(Link link){
		links.add(link);
	}
	
	public void addParagraph(String paragraph){
		paragraphs.add(paragraph);
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Set<Link> getLinks() {
		return links;
	}
	
	public ArrayList<String> getParagraphs() {
		return paragraphs;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	
}

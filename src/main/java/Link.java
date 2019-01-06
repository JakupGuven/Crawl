
public class Link {
	
	private String url;
	private Link source;
	
	@Override
	public boolean equals(Object obj){
		if(obj == null || !(obj instanceof Link) ){
			return false;
		}
		return url.equals(((Link) obj).getUrl());
	}
	
	public Link(String url, Link source){
		this.url = url;
		this.source = source;
	}
	
	public Link(String url){
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Link getSource() {
		return source;
	}
	public void setSource(Link source) {
		this.source = source;
	}

}

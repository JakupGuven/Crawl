import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SaveJob implements Runnable{
	private Set<Page> pageSet = new HashSet();
	
	public SaveJob(Set<Page> pageSet){
		this.pageSet = pageSet;
	}

	@Override
	public void run() {
          	String url = "jdbc:sqlite:C:/Users/Jakup/workspace/Crawl/src/main/java/CrawlDB.db";
	        try(Connection dbConnection = DriverManager.getConnection(url)) {
	            dbConnection.setAutoCommit(false);
	            String pageString = "INSERT INTO Pages (URL,SOURCE, TITLE) VALUES (?,?,?)";
	            String paragraphString = "INSERT INTO Paragraphs (PARAGRAPH,URL, TITLE) VALUES (?,?,?)";
	            for(Page page : pageSet){
	            	for(Link link : page.getLinks()){
	            		PreparedStatement insertPage = dbConnection.prepareStatement(pageString);
	            		insertPage.setString(1, link.getUrl());
	            		insertPage.setString(2, link.getSource().getUrl());
	            		insertPage.setString(3, page.getTitle());
	            		insertPage.execute();
	    	            dbConnection.commit();
	            	}
	            	for(String paragraph : page.getParagraphs()){
	            		PreparedStatement insertParagraph = dbConnection.prepareStatement(paragraphString);
	            		insertParagraph.setString(1, paragraph);
	            		insertParagraph.setString(2, page.getUrl());
	            		insertParagraph.setString(3, page.getTitle());
	            		insertParagraph.execute();
	    	            dbConnection.commit();
	            	}
	            }
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        } 
	}
}

package edu.asu.yelpcrawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author csiebler
 */
public class YelpCrawler {
    
    // Define the Logger
    private static final Logger LOGGER
            = Logger.getLogger(YelpCrawler.class.getName());
    
    // Yelp Friend URL
    private static final String FRIENDS_PAGE
            = "http://www.yelp.com/user_details_friends?userid=";
    
    // Define the HTML Parsing
    private static final String FRIEND_COUNT = "range-of-total";
    private static final String FRIEND_LIST = "friend_box_list";
    private static final String FRIEND_CLASS = "friend_box";
    private static final String USER_CLASS = "user-name";
    
    // Yelp Friend Per Page
    private static final int FRIENDS_PER_PAGE = 100;
    
    // Yelp UserID length
    private static final int USER_ID_LENGTH = 22;
    
    // DOM Parser Helpers
    private static final String HREF = "href";
    
    /**
     * 
     */
    public YelpCrawler() {
        
    }

    /**
     * 
     * @param currentUser
     * @return 
     */
    public ArrayList<String> getFriends(String currentUser) {
        // Initialize the list of friends
        ArrayList friends = new ArrayList<>();
        
        try {
            // Grab the Document from the URL
            Document doc = retrieveDocument(currentUser);
            
            /*
             > Grab all the current user's friends
             > Extract the href attribute for the friends
             > Take the substring to get each friends user id
             */
            Elements users = doc.getElementsByClass(USER_CLASS);
            
            // Loop through all the friends of the user
            for (Element user : users) {
                // Extract the href attribute from the child element
                String link = user.child(0).attr(HREF);

                // Make sure the DOM Parser extracted the href properly
                if (!link.isEmpty()) {
                    // Extract the User ID from the href attribute
                    String userId = link.substring(
                            link.length() - USER_ID_LENGTH,
                            link.length()
                    );

                    // Insert the User ID into the friends array
                    friends.add(userId);
                }
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        
        return friends;
    }

    /**
     * Perform a GET request to acquire a URLs HTML. Then Parse the HTML into
     * a Document object.
     * 
     * @param username
     * @return
     * @throws IOException 
     */
    private Document retrieveDocument(String username) throws IOException {
        return Jsoup.connect(FRIENDS_PAGE + username).get();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Initialize the Yelp Crawler
        YelpCrawler crawler = new YelpCrawler();
        
        // Initialize a Test User
        String sampleUser = "Rzf35jKV8e8EJdssrae_AA";
        
        // Perform Web Crawling
        System.out.println(crawler.getFriends(sampleUser));
    }

}

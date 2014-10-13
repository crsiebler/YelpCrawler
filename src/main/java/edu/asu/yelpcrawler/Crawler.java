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
public class Crawler {
    
    // Define the Logger
    private static final Logger LOGGER
            = Logger.getLogger(Crawler.class.getName());
    
    // List of Friends for a User
    private static ArrayList<String> friends;
    
    // Yelp Friend URL
    private static final String FRIENDS_PAGE
            = "http://www.yelp.com/user_details_friends?userid=";
    
    // Yelp Page Identifier
    private static final String PAGE_ID = "&start=";
    
    // Define the HTML Parsing
    private static final String FRIEND_COUNT = "range-of-total";
    private static final String USER_CLASS = "user-name";
    
    // Yelp Friend Per Page
    private static final Double FRIENDS_PER_PAGE = 100d;
    
    // Define the Sleep time to prevent too many HTTP requests
    private static final long SLEEP_TIME = 10000L;
    
    // Yelp UserID length
    private static final int USER_ID_LENGTH = 22;
    
    // JSoup Time
    private static final int SOCKET_TIMEOUT = Integer.MAX_VALUE;
    
    // DOM Parser Helpers
    private static final String HREF = "href";
    
    /**
     * Crawl the friends of the current user. Utilize JSoup's HTML DOM parsing
     * to assist with the data retrieval.
     * 
     * @param currentUser
     * @return 
     */
    public static ArrayList<String> parseFriends(String currentUser) {
        // Initialize the list of friends
        friends = new ArrayList<>();
        
        try {
            // Grab the Document from the URL
            Document doc = retrieveDocument(currentUser);

            /*
             > Grab the number of friends for the current user
             > Specify how many pages should be analyzed
             */
            Element range = doc.getElementsByClass(FRIEND_COUNT).get(0);
            String friendRange = range.child(0).text();
            double numFriends = Double.parseDouble(friendRange.substring(
                    friendRange.indexOf("of") + 3,
                    friendRange.length()
            ));
            int pages = (int) Math.ceil(numFriends/FRIENDS_PER_PAGE);
            
            // Extract Friends for the first page
            extractFriends(doc);
            
            // Loop throuh additional page if available
            for (int i = 1; i < pages; ++i) {
                // Extract Friends for additional pages
                extractFriends(retrieveDocument(currentUser + PAGE_ID + (i*100)));
 
                /*
                 Due to the high number of HTTP requests created when running
                 the program, sleeping the main thread is required. This will
                 add an inherant delay to each request and prevent my IP from
                 being blocked.
                 */
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        
        System.out.println(friends.size());
        return friends;
    }
    
    /**
     * This function performs the following:<br>
     * <li>
     * <ul>
     *  <li>Grab all the current user's friends</li>
     *  <li>Extract the HREF attribute for the friends</li>
     *  <li>Take the substring to get each friends user ID</li>
     * </ul>
     * 
     * @param doc 
     */
    private static void extractFriends(Document doc) {
        // Grab the users that are friends for the current user
        Elements users = doc.getElementsByClass(USER_CLASS);

        // Loop through all the friends of the user
        for (Element user : users) {
            // Extract the href attribute from the child element
            String link = user.child(0).attr(HREF);

            // Make sure the DOM Parser extracted the href properly
            if (!link.isEmpty()) {
                // Extract the User ID from the HREF attribute
                String userId = link.substring(
                        link.length() - USER_ID_LENGTH,
                        link.length()
                );

                // Insert the User ID into the friends array
                friends.add(userId);
            }
        }
    }

    /**
     * Perform a GET request to acquire a URLs HTML. Then Parse the HTML into
     * a Document object.
     * 
     * @param username
     * @return
     * @throws IOException 
     */
    private static Document retrieveDocument(String username) throws IOException {
        return Jsoup.connect(FRIENDS_PAGE + username).timeout(SOCKET_TIMEOUT).get();
    }

}

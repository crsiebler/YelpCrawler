package edu.asu.yelpcrawler;

/**
 *
 * @author csiebler
 */
public class EdgeList {
    
    // Define the filename to store the edge list
    private static final String FILE_NAME = "edges.txt";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Initialize a Test User
//        String sampleUser = "Rzf35jKV8e8EJdssrae_AA";
//        String sampleUser = "UiRx8jyS6H957ItwFsP2nQ";
        String sampleUser = "EJ7ZhRHsMWj8du77LX34gw";

        // Perform Web Crawling
        System.out.println(Crawler.parseFriends(sampleUser));
    }

}

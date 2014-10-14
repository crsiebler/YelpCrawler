package edu.asu.yelpcrawler;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Builds the edge list for the web crawl. Because Yelp friends are a
 * bidirectional relationship, each edge will be stored as vertex, edge &
 * edge, vertex. If a node has been visited previously then that edge will be
 * ignored as to remove duplicates. The edge list will be saved to a file within
 * the project directory.
 *
 * @author csiebler
 */
public class EdgeList {

    // Define the Logger
    private static final Logger LOGGER = Logger.getLogger(EdgeList.class.getName());

    // Define the filename to store the edge list
    private static final String FILE_NAME = "edges.txt";

    // Define the limit of vertex to visit
    private static final int VERTEX_LIMIT = 1000;

    // Define the Sleep time to prevent too many HTTP requests
    private static final long SLEEP_TIME = 2000L;

    // Define the Strings frequently used
    private static final String COMMA = ",";
    private static final String NEW_LINE = "\n";

    // Declare a Queue to hold users
    private static final PriorityQueue<String> QUEUE = new PriorityQueue<>();

    // Declare a Collection to hold visited users
    private static final Collection<String> VISITED = new HashSet<>();

    /**
     * Write the vertex-edge relationship to a file in order to persist the
     * data. The relationship is bidirectional, so each connection must be
     * written as vertex-edge & edge-vertex.
     * 
     * @param vertex
     * @param edges 
     */
    private static void saveData(String vertex, ArrayList<String> edges) {
        // Initialize a file writer to append data
        try (FileWriter fw = new FileWriter(FILE_NAME, true)) {
            // Loop through all the edges and write them to the file
            for (String edge : edges) {
                fw.write(vertex + COMMA + edge + NEW_LINE);
                fw.write(edge + COMMA + vertex + NEW_LINE);
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Take the data retrieved from the crawl and inserts it into the
     * appropriate data types. Use a PriorityQueue to know what users should be
     * visited. Also, use a HashSet to keep track of users that have already
     * been visited.
     *
     * @param initial
     */
    private static void gatherData(String initial) {
        // Initialize a counter for the vertex visits
        int count = 0;

        // Add the initial vertex to visited & queue
        QUEUE.add(initial);
        VISITED.add(initial);

        do {
            // Grab the next vertex in the Queue
            String vertex = QUEUE.poll();

            // Crawl the current vertex
            ArrayList<String> edges = Crawler.parseFriends(vertex);

            // Add vertex to the queue until the limit is reached
            if (count <= VERTEX_LIMIT) {
                // Add the results to the queue of vertex
                for (String edge : edges) {
                    // Make sure the edge has not been visited
                    if (!VISITED.contains(edge)) {
                        // Increment the count
                        count++;

                        // Insert the vertex into the Queue
                        QUEUE.add(edge);

                        // Add the vertex to the collection of visited
                        VISITED.add(vertex);
                    } else {
                        // Connection has already been saved, so delete edge
                        edges.remove(edge);
                    }
                }
            }

            // Save the results from the crawl
            saveData(vertex, edges);

            /*
             Due to the high number of HTTP requests created when running the
             program, sleeping the main thread is required. This will add an
             inherant delay to each request and prevent my IP from being
             blocked.
             */
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        } while (QUEUE.peek() != null);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Initialize a Test User
        String sampleUser = "Rzf35jKV8e8EJdssrae_AA";
//        String sampleUser = "UiRx8jyS6H957ItwFsP2nQ";
//        String sampleUser = "EJ7ZhRHsMWj8du77LX34gw";

        // Perform Web Crawling
        gatherData(sampleUser);
    }

}

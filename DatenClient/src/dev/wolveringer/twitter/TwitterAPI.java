package dev.wolveringer.twitter;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TwitterAPI {

    public static void main(String[] args) {
        try {
        	/*
        	 * cb.setDebugEnabled(true)
  .setOAuthConsumerKey("*********************")
  .setOAuthConsumerSecret("******************************************")
  .setOAuthAccessToken("**************************************************")
  .setOAuthAccessTokenSecret("******************************************");
        	 */
            Twitter twitter = new TwitterFactory().getInstance();
            long cursor = -1;
            IDs ids;
            System.out.println("Listing followers's ids.");
            do {
                if (0 < args.length) {
                    ids = twitter.getFollowersIDs("EpicPvPMC", cursor);
                } else {
                    ids = twitter.getFollowersIDs(cursor);
                }
                for (long id : ids.getIDs()) {
                    System.out.println(id);
                }
            } while ((cursor = ids.getNextCursor()) != 0);
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get followers' ids: " + te.getMessage());
            System.exit(-1);
        }
    }
}

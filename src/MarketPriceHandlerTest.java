import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class MarketPriceHandlerTest {
	
	   public static class Subscriber implements Flow.Subscriber<String> {
		      private Flow.Subscription subscription;
		      private boolean isDone;
		      
		      @Override
		      public void onSubscribe(Flow.Subscription subscription) {
		         System.out.println("Subscribed to Feed");
		         this.subscription = subscription;
		         this.subscription.request(1);
		      }
		      @Override
		      public void onNext(String item) {
		         System.out.print("Processing Message -> " + item);
		         String[] values = item.split(",");    // use comma as separator  
		         System.out.println("ID= " + values[0]);
		         System.out.println("NAME= " + values[1] );
		         System.out.println("BID+Commission  " + bidCommision(Double.parseDouble(values[2])));
		         System.out.println("ASK+Commission  " + askCommision(Double.parseDouble(values[3])));
		         System.out.println("TIMESTAMP = " + values[4]);
		         this.subscription.request(1);
		      }
		      @Override
		      public void onError(Throwable throwable) {
		         throwable.printStackTrace();
		      }
		      @Override
		      public void onComplete() {
		         System.out.println("Processing done");
		         isDone = true;
		      }
		      public double bidCommision(double value) {
		    	  return (value*0.001) + value;
		      }
		      public double askCommision(double value) {
		    	  return (value*0.001*-1) +value;
		      }
		      
		      private void callRestEndpoint() throws IOException { //not used or tested in example
		          URL url = new URL("this the rest URL");
		          String values = "the values for the service";

		          //make connection
		          URLConnection urlc = url.openConnection();

		          //use post mode
		          urlc.setDoOutput(true);
		          urlc.setAllowUserInteraction(false);

		          //send values
		          PrintStream ps = new PrintStream(urlc.getOutputStream());
		          ps.print(values);
		          ps.close();

		          //get result
		          BufferedReader br = new BufferedReader(new InputStreamReader(urlc
		              .getInputStream()));
		          String l = null;
		          while ((l=br.readLine())!=null) {
		              System.out.println(l);
		          }
		          br.close();
		      }
		      
		      
		   }
	   
	   
	   
		   public static void main(String args[]) throws InterruptedException, IOException {
		      SubmissionPublisher<String> publisher = new SubmissionPublisher<>();
		      Subscriber subscriber = new Subscriber();

		      String message = "";  
		      Scanner sc = new Scanner(new File("C:\\Users\\Public\\"+"feedCSV"+".csv"));   //file that represent the feed and should be in the user "public" folder
		      sc.useDelimiter("\\n");   //sets the delimiter pattern  
		      publisher.subscribe(subscriber);
		      while (sc.hasNext())  //returns a boolean value  
		      {  
		    	  message = sc.next();  //find and returns a message from this scanner  
		    	  publisher.submit(message);
		      }		      
		      sc.close();  //closes the scanner  
	
		      publisher.close();
		      
		      while(!subscriber.isDone) {
		         Thread.sleep(40); // 40 is more enogh for tests
		      }
		      System.out.println("Test complete Done");
		   }
}

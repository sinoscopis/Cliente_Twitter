package client;

import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;
 
public class ClientThread extends Thread{

	public void run() {
		Socket socket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		InetAddress host = null;
		int client_id = 0;
		
		try {
			host = InetAddress.getLocalHost();
			socket = new Socket(host.getHostName(), 55555);
 
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 
			String fromServer;
			String fromUser = null;
			
			while ((fromServer = in.readLine()) != null) {
				System.out.println("Server - " + fromServer);
				sleep(5000);
				if (fromServer.equals("exit"))
					break;
				if (fromServer.startsWith("inserted,")){
					String[] peticion = fromServer.split(",", 2);
					client_id = Integer.parseInt(peticion[1]);
				}
				if (fromServer.startsWith("......")){
					fromUser = "insertuser,"+randomIdentifier(15);
					if (fromUser != null) {
						System.out.println("Client - " + fromUser);
						synchronized (socket){
							out.println(fromUser);
						}
					}
				}
				if (fromServer.startsWith("Tweets [")){
					Hashtable twts=new Hashtable();
		            StringTokenizer tokens=new StringTokenizer(fromServer, "(|)");
		            int i=0;
		            while(tokens.hasMoreTokens()){
		                String a = tokens.nextToken();
		                try  
		                  {  
		                     if(Integer.parseInt(a)>0){
		                    	 i++;
		                    	 twts.put(i, Integer.parseInt(a));
		                     }
		                  }  
		                  catch(NumberFormatException nfe)  
		                  {  
		                  }
		            }

					double rd = Math.random();
					double d2 = rd * i;
					int rand_tweet = (int)d2 +1;
					fromUser = "retweet,"+ client_id +","+twts.get(rand_tweet);
					if (fromUser != null) {
						System.out.println("Client - " + fromUser);
						synchronized (socket){
							out.println(fromUser);
						}
					}
				}
				else {
					
					double randNumber = Math.random();
					double d1 = randNumber * 100;
					int range = (int)d1;
					if (range>=1 && range <60){
						fromUser ="friendstweets,"+ client_id;
					}
					else if (range>=60 && range <90){
						fromUser ="insertrandomtweet,"+ client_id;
					}
					else {
						double randclient = Math.random();
						double d2 = randclient * client_id;
						int new_friend = (int)d2;
						if (new_friend > 0)
							fromUser ="insertfriendship," + client_id + "," + Integer.toString(new_friend);
					}
					
					if (fromUser != null) {
						System.out.println("Client - " + fromUser);
						out.println(fromUser);
					}								
				}
			}
		} catch (UnknownHostException e) {
			System.err.println("Cannot find the host: " + host.getHostName());
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Couldn't read/write from the connection: " +e.toString() );
			e.printStackTrace();
			System.exit(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { //Make sure we always clean up	
			try {
				out.close();
				in.close();
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	
	public static String randomIdentifier(int max_length) {
		final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";
		final java.util.Random rand = new java.util.Random();
		// consider using a Map<String,Boolean> to say whether the identifier is being used or not 
		final Set<String> identifiers = new HashSet<String>();
			StringBuilder builder = new StringBuilder();
		    while(builder.toString().length() == 0) {
		    	double randNumber = Math.random();
				double d1 = randNumber * max_length;
				int length = (int)d1;
		        for(int i = 0; i < length; i++)
		            builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
		        if(identifiers.contains(builder.toString())) 
		            builder = new StringBuilder();
		    }
		    return builder.toString();
	}
} 
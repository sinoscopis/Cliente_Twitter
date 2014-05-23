package client;

import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;
 
public class ClientThread extends Thread{

	private final static Object lock = new Object();
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
				try {
					Integer.parseInt(fromServer);
					double randclient = Math.random();
					double d2 = randclient * Integer.parseInt(fromServer);
					int new_friend = (int)d2;
					if (new_friend > 0)
						fromUser ="insertfriendship," + client_id + "," + Integer.toString(new_friend);
					System.out.println("Client - " + fromUser);
					out.println(fromUser);
					}
				catch (Exception e){
					//e.printStackTrace();
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
				else {
					if (fromServer.startsWith("ReTweet")){
			            fromUser ="Retweet analizado";
			            out.println(fromUser);
					}
					else if (fromServer.startsWith("Tweets [")){
						Hashtable twts=new Hashtable();
			            StringTokenizer tokens=new StringTokenizer(fromServer, "(|)");
			            int i=0;
			            //Guardo la lista de tweets de todos mis amigos
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
			            //Select random twit
						double rd = Math.random();
						double d2 = rd * i;
						int rand_tweet = (int)d2 +1;
						
						//Anado probabilidad de consumir el twit 50 50
						double randNumber = Math.random();
						double d1 = randNumber * 100;
						int range = (int)d1;
						if (range>=1 && range <50){
							//CONSUME el Twit
							
							//Analizo si es URL
							String consumir=null;
				            StringTokenizer tokens2=new StringTokenizer(fromServer, "(|)");
				            while(tokens2.hasMoreTokens()){
				            	String a = tokens2.nextToken();
				                try  
				                  {  
				                     if(a.startsWith("http")){
				                    	 consumir = a;
				                     }
				                  }  
				                
				                  catch(Exception nfe)  
				                  {  
				                	  	//nfe.printStackTrace();
				                  }	
				            }
				            if (consumir!=null){
					            synchronized (lock) {
					            	consumirContenido(consumir);
					            }
				            }
				            
							//anado probabilidad para que lo retweetee
							double randNumber2 = Math.random();
							double d3 = randNumber2 * 100;
							int range2 = (int)d3;
							if (range2>=1 && range2 <80){
								//retweet
								fromUser = "retweet,"+ client_id +","+twts.get(rand_tweet);
							}
							else {
								// no retweet
								fromUser = "No retweeteo";
							}
						}
						else {
							//No consume el twit
							//anado probabilidad para que lo retweetee
							double randNumber4 = Math.random();
							double d4 = randNumber4 * 100;
							int range4 = (int)d4;
							if (range4>=1 && range4 <20){
								//retweet
								fromUser = "retweet,"+ client_id +","+twts.get(rand_tweet);
							}
							else {
								// no retweet
								fromUser = "No retweeteo";
							}
						}
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
							fromUser ="countusers";
						}
						
						if (fromUser != null) {
							System.out.println("Client - " + fromUser);
							out.println(fromUser);
						}								
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
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	
	private void consumirContenido(String consumir) {
		Socket cache_socket = null;
		PrintWriter cache_out = null;
		BufferedReader cache_in = null;
		InetAddress host = null;
		
		try {
			host = InetAddress.getLocalHost();
			cache_socket = new Socket(host.getHostName(), 66666);
 
			cache_out = new PrintWriter(cache_socket.getOutputStream(), true);
			cache_in = new BufferedReader(new InputStreamReader(cache_socket.getInputStream()));
 
			String cache_fromServer;
			String cache_fromUser = null;
			
			while ((cache_fromServer = cache_in.readLine()) != null) {
				System.out.println("cache_Server - " + cache_fromServer);
				if (cache_fromServer.equals("exit"))
					break;
				cache_fromUser = "Consumir,"+consumir;
				if (cache_fromUser != null) {
					System.out.println("cache_Client - " + cache_fromUser);
					cache_out.println(cache_fromUser);
				}
			}
		} catch (UnknownHostException e) {
			System.err.println("Cannot find the host: " + host.getHostName());
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Couldn't read/write from the connection: " +e.toString() );
			e.printStackTrace();
			System.exit(1);
		} finally { //Make sure we always clean up	
			try {
				cache_out.close();
				cache_in.close();
				cache_socket.close();
			} catch (Exception e) {
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
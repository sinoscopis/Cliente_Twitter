package client;

import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;
 
public class ClientThread extends Thread{

	static String Server_host;
	static String Cache_host;
	
	private final static Object lock = new Object();
	public void run() {
		Socket socket = null;
		PrintWriter out = null;
		@SuppressWarnings("unused")
		String friendsByCache = "";
		BufferedReader in = null;
		//InetAddress _host = null;
		int posted=0;
		int client_id = 0;
		int User_pos = ClientLauncher.usuarios_conectados;
		ClientLauncher.usuarios_conectados=ClientLauncher.usuarios_conectados+1;
		
		
		try {
			socket = new Socket(Server_host, 55555);
 
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 
			String fromServer;
			String fromUser = null;
			TimeUnit.SECONDS.sleep(40);
			
			while ((fromServer = in.readLine()) != null) {
				//System.out.println("Server - " + fromServer);
				if (fromServer.equals("exit"))
					break;
				else if (fromServer.startsWith("Conectedwith-")){
					String[] peticion = fromServer.split("-", 2);
					friendsByCache = peticion[1];
					fromUser = "continua";
					if (fromUser != null) {
						//System.out.println("Client - " + fromUser);
						synchronized (socket){
							out.println(fromUser);
						}
					}
				}
				else if (fromServer.startsWith("......")){
					fromUser = "conectUser," + ClientLauncher.cache_num;
					if (fromUser != null) {
						//System.out.println("Client - " + fromUser);
						synchronized (socket){
							out.println(fromUser);
						}
					}
				}
				else if (fromServer.startsWith("conectado,")){
					String[] peticion = fromServer.split(",", 2);
					client_id = Integer.parseInt(peticion[1]);
					fromUser = "conectado_almacenado,"+client_id;
					ClientLauncher.ClientsConectedArray[User_pos]=client_id;
					if (fromUser != null) {
						//System.out.println("Client - " + fromUser);
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
					else if(fromServer.startsWith("Consumir -")){
						//Analizo si es URL
						String consumir=null;
						int id_retweet=0;
						int i = 0;
						int num_followers_cache=0;
				        StringTokenizer tokens2=new StringTokenizer(fromServer, "(|)");
				        while(tokens2.hasMoreTokens()){
				        	
				        	String a = tokens2.nextToken();
				            try  
				              {  
				                 if(a.startsWith("http")){
				                	 consumir = a;
				                 }
				                 if(Integer.parseInt(a)>0){
				                	 id_retweet=Integer.parseInt(a);
				                	 i=i+1;
			                     }
				                 if((Integer.parseInt(a)>0) && (i!=0)){
				                	 num_followers_cache=Integer.parseInt(a);
			                     }
				              }  
				            
				              catch(Exception nfe)  
				              {  
				            	  	//nfe.printStackTrace();
				              }	
				        }
				        if (consumir!=null){
				            synchronized (lock) {
				            	consumirContenido(consumir,num_followers_cache);
				            }
				        }
				      //anado probabilidad para que lo retweetee
						double randNumber2 = Math.random();
						double d3 = randNumber2 * 100;
						int range2 = (int)d3;
						if (range2>=1 && range2 <80){
							//retweet
							fromUser = "retweet,"+ client_id +","+id_retweet;
							posted=posted+1;
							System.out.println("user - " + client_id + "  posted: "+ posted );
						}
						else {
							// no retweet
							fromUser = "No retweeteo";
						}
						if (fromUser != null) {
							//System.out.println("Client - " + fromUser);
							synchronized (socket){
								out.println(fromUser);
							}
						}
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
						double rt = Math.random();
						double drt = rt * i;
						int rand_tweet = (int)drt +1;
						
						//Anado probabilidad de consumir el twit 50 50
						double randNumber = Math.random();
						double d1 = randNumber * 100;
						int range = (int)d1;
						if (range>=1 && range <50){
							//CONSUME el Twit
							fromUser = "Consumo,"+twts.get(rand_tweet)+","+client_id;

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
								posted=posted+1;
								System.out.println("user - " + client_id + "  posted: "+ posted );
							}
							else {
								// no retweet
								fromUser = "No retweeteo";
							}
						}
						if (fromUser != null) {
							//System.out.println("Client - " + fromUser);
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
							posted=posted+1;
							System.out.println("user - " + client_id + "  posted: "+ posted );
						}
						else {
							fromUser ="continua";
						}
						
						if (fromUser != null) {
							//System.out.println("Client - " + fromUser);
							out.println(fromUser);
						}								
					}
				}
			}
		} catch (UnknownHostException e) {
			System.err.println("Cannot find the host: " + Server_host);
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Couldn't read/write from the connection: " +e.toString() );
			e.printStackTrace();
			System.exit(1);
		}catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				in.close();
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void consumirContenido(String consumir, int num_followers_cache) {
		Socket ClientSoc = null;

		DataInputStream din;
		DataOutputStream dout;
		try{
		
			//InetAddress host = InetAddress.getLocalHost();
			ClientSoc = new Socket(Cache_host, 60000);
			
			din=new DataInputStream(ClientSoc.getInputStream());
			dout=new DataOutputStream(ClientSoc.getOutputStream());
			
			ReceiveFile(din,dout,consumir,num_followers_cache);
		}
		catch(Exception ex){
			
		}
	}

	public static void ReceiveFile(DataInputStream din, DataOutputStream dout, String consumir, int num_followers_cache) throws Exception{
		dout.writeUTF("GET");
		String filename = consumir.substring(17);
		String sSistemaOperativo = System.getProperty("os.name");
		String file_path = null;
		String[] filenamesplit = filename.split("\\.", 2);
		String extension = filenamesplit[1];
		String nombre =  filenamesplit[0];
		if(sSistemaOperativo.startsWith("Win")){
			file_path = "C:\\Users\\Alberto\\Desktop\\Client_Content\\"+filename;
			//file_path = ".\\Client_Content\\"+filename;
		}
		else {
			file_path = "./Client_Content/"+filename;
		}
		dout.writeUTF(ClientLauncher.cache_num+"/"+filename+"/"+num_followers_cache);
		String msgFromCacheServer=din.readUTF();
		
		if(msgFromCacheServer.compareTo("File Not Found")==0)
		{
			System.out.println("File not found on cache waiting for cache to found it ...");
		}
		else if(msgFromCacheServer.compareTo("READY")==0)
		{
			System.out.println("Receiving File ...");
			File f=new File(file_path);
			while(f.exists())
			{
				if(sSistemaOperativo.startsWith("Win")){
					file_path = "C:\\Users\\Alberto\\Desktop\\Client_Content\\"+ nombre +"-"+randomIdentifier(10)+"."+extension;
					//file_path = ".\\Client_Content\\"+ nombre +"-"+randomIdentifier(10)+"."+extension;
				}
				else {
					file_path = "./Client_Content/"+ nombre +"-"+randomIdentifier(10)+"."+extension;
				}
				f=new File(file_path);
			}
			FileOutputStream fout=new FileOutputStream(f);
			int ch;
			String temp;
			do
			{
				temp=din.readUTF();
				ch=Integer.parseInt(temp);
				if(ch!=-1)
				{
					fout.write(ch);					
				}
			}while(ch!=-1);
			fout.close();
			System.out.println(din.readUTF());
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
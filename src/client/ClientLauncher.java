package client;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import cache.LRUCache;

 
public class ClientLauncher {
	
	public static String content = null;
	static LRUCache<String,String> c = new LRUCache<String, String>(100);
	
	public static void main(String[] args) throws IOException, InterruptedException {
	
		int clientes = 20;
		Set<ClientThread> ClientThreads = new HashSet<ClientThread>();
		int peticiones = 0;
		int hit = 0;
		
		for(int i=1; i<=clientes; i++){
			ClientThread cliente = new ClientThread();
			cliente.setName(Integer.toString(i));
			new Thread(cliente).start();
			ClientThreads.add((ClientThread) cliente);
			TimeUnit.SECONDS.sleep(2);
		}
		while (true){
			if (content != null){
				String peticion = content;
				peticiones ++;
				if (c.inCache(content)){
					hit++;
				}
				else{
					int next = c.usedEntries()+1;
					c.put (Integer.toString(next), peticion);
					System.out.println(+peticiones+ "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! - > "+peticion);
					for (Map.Entry<String, String> e : c.getAll())
					      System.out.println (e.getKey() + " : " + e.getValue());
					content = null;
				}
			}
		}
	}
}
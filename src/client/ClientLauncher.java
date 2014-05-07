package client;

import java.io.*;
import java.util.*;

 
public class ClientLauncher {
	public static void main(String[] args) throws IOException, InterruptedException {
	
		int clientes = 2;
		Set<ClientThread> ClientThreads = new HashSet<ClientThread>();
		
		for(int i=1; i<=clientes; i++){
			ClientThread cliente = new ClientThread();
			cliente.setName(Integer.toString(i));
			new Thread(cliente).start();
			ClientThreads.add((ClientThread) cliente);
		}
	}
}
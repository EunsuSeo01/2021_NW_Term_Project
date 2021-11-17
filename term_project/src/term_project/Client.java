package term_project;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
	
	public static void main(String[] args) {
		try {
			Socket socket = null;
            		
			InetAddress local = InetAddress.getLocalHost();
			String ip = local.getHostAddress(); //Save the ip address of the computer
			
			socket = new Socket(ip,1324); //Client socket with port number

			System.out.println("access sucess!");
			
			ListeningThread t1 = new ListeningThread(socket);
			WritingThread t2 = new WritingThread(socket); //  Thread to send message Server

			t1.start(); 
			t2.start(); 
            
		} catch (IOException e) {
			e.printStackTrace(); // exception
		}
	}
}
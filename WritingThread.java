package term_project;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class WritingThread extends Thread { 
	Socket socket = null;
	Scanner scanner = new Scanner(System.in); 
	
	public WritingThread(Socket socket) { 
		this.socket = socket; 
	}
	
	public void run() {
		try {

			OutputStream out = socket.getOutputStream();

			PrintWriter writer = new PrintWriter(out, true);
			
			while(true) { 
				//infinite loop
				writer.println(scanner.nextLine()); // send message
			}
			
		} catch (Exception e) {
			e.printStackTrace(); // exception
		}
		
		
	}


}
package term_project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server extends Thread {
	static ArrayList<Socket> list = new ArrayList<Socket>(); // 유저 확인용
	static Socket socket = null;
	
	public Server(Socket socket) {
		this.socket = socket; // allocate user socket
		list.add(socket); // add user to list
	}
    	
    	public void run() 
    	{
		try {
        	
			System.out.println("서버 : " + socket.getInetAddress() + " IP의 클라이언트와 연결되었습니다");
			
			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			
			OutputStream out = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(out, true);
			
			writer.println("connect to sever success! Enter your nickname!");
			
			String readValue; // Save the value sent to the Client.
			String nickname = null; // For setting client nicknames.
			boolean identify = false;
			
			while((readValue = reader.readLine()) != null ) 
			{
				if(!identify) 
				{
					nickname = readValue; // allocate nickname
					identify = true;
					writer.println(nickname + " enter into this room.");
					continue;
				}
				
                		
				for(int i = 0; i<list.size(); i++) // Client information is in the list.
				{ 
					out = list.get(i).getOutputStream();
					writer = new PrintWriter(out, true);
					writer.println(nickname + " : " + readValue); 
				}
			}
		} catch (Exception e) {
		    e.printStackTrace(); 
		}    		
    	}	
	
	public static void main(String[] args) {
    		try {
                      int socketPort = 1324; 
                      ServerSocket serverSocket = new ServerSocket(socketPort); 
			
                      // loop
                      while(true) {
                          Socket socketUser = serverSocket.accept(); 
                          Thread thd = new Server(socketUser);
                          thd.start(); 
                      }                 
            
		} catch (IOException e) {
			e.printStackTrace(); 
		}

	}

}


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


public class Server extends Thread {
	static ArrayList<Socket> list = new ArrayList<Socket>(); 
	static Socket socket = null;
	
	public Server(Socket socket) {
		this.socket = socket; // allocate user socket
		list.add(socket); // add user to list
	}
    	
    	public void run() 
    	{
		try {
			Scanner keyboard = new Scanner(System.in);
			BufferedReader reader = new BufferedReader( new InputStreamReader(socket.getInputStream()));	
			OutputStream out = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(out, true);
			
			String readValue = null; // Save the value sent to the Client.
			String nickname = null; // For setting client nicknames.
	        
	  
			while (true) //The server must continue to run.
			{	
				int protocol = reader.read(); 
			
				
				if(protocol == 0) 
				{
					System.out.println("access sucess!");
					readValue = reader.readLine();
					nickname = readValue; // allocate nickname
					
				}
				else if(protocol == 1) 
				{
					System.out.println("access sucess!");
					writer.println(nickname + " enter into this room.");
					/*while(게임 시작 버튼 눌릴 때까지) 
					{		                		
						for(int i = 0; i<list.size(); i++) // Client information is in the list.
						{ 
							out = list.get(i).getOutputStream();
							writer = new PrintWriter(out, true);
							writer.println(nickname + " : " + readValue); 
						}
					}
					
					//게임화면으로 이동
					*/
				}
				else if(protocol==2)
				{
					
		            
				}
				else if(protocol==3) 
				{
					
				}
				else
				{
					System.out.print("false");
				}
			}
			
			
			
		} catch (Exception e) {
		    e.printStackTrace(); 
		}    		
    	}	
	
	public static void main(String[] args) {
    		try {
                      int socketPort = 11324; 
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
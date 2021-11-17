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
			
			String readValue; // Client에서 보낸 값 저장
			String name = null; // 클라이언트 이름 설정용
			boolean identify = false;
			
            		// 클라이언트가 메세지 입력시마다 수행
			while((readValue = reader.readLine()) != null ) 
			{
				if(!identify) 
				{ // 연결 후 한번만 노출
					name = readValue; // 이름 할당
					identify = true;
					writer.println(name + "Enter this room.");
					continue;
				}
				
                		// list 안에 클라이언트 정보가 담겨있음
				for(int i = 0; i<list.size(); i++) 
				{ 
					out = list.get(i).getOutputStream();
					writer = new PrintWriter(out, true);
                    			// 클라이언트에게 메세지 발송
					writer.println(name + " : " + readValue); 
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
                          Socket socketUser = serverSocket.accept(); // 서버에 클라이언트 접속 시
                          // Thread 안에 클라이언트 정보를 담아줌
                          Thread thd = new Server(socketUser);
                          thd.start(); 
                      }                 
            
		} catch (IOException e) {
			e.printStackTrace(); 
		}

	}

}
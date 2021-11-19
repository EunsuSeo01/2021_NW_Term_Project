import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;


class EchoThread extends Thread{
	Socket socket;
	Vector<Socket> vec;
	public EchoThread(Socket socket, Vector<Socket> vec){
		this.socket = socket;
		this.vec = vec;
	}
	public void run(){
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String string = null;
			while(true)
			{
				string = reader.readLine();
				if(string == null)
				{
					vec.remove(socket);
					break;
				}
				if(string.equals("!p"))
				{
					broadcast("마피아 게임을 시작합니다!");
				}
				else
				{
					sending(string);	
				}
			}
			
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}finally{
			try{
				if(reader != null) reader.close();
				if(socket != null) socket.close();
			}catch(IOException ie){
				System.out.println(ie.getMessage());
			}
		}
	}
	
	//전송받은 문자열 다른 클라이언트들에게 보내주는 메서드
	public void sending(String str){
		try{
			for(Socket socket:vec){
				//for를 돌되 현재의 socket이 데이터를 보낸 클라이언트인 경우를 제외하고 
				//나머지 socket들에게만 데이터를 보낸다.
				if(socket != this.socket){
					PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
					writer.println(str);
					writer.flush();
				}
			}
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}
	}
	public void broadcast(String start) {
		synchronized(vec) {
		try{
			for(Socket socket:vec){
				PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
				writer.println(start);
				writer.flush();	
			}
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}
		}
	}
}


public class ChatServer {
	public static void main(String[] args) {
		ServerSocket server = null;
		Socket socket =null;
		Vector<Socket> vec = new Vector<Socket>();
		try{
			server= new ServerSocket(3000);
			while(true){
				socket = server.accept();
				vec.add(socket);
				new EchoThread(socket, vec).start();
			}
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}
	}
}

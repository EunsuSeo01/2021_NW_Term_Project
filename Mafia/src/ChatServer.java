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
		// If Socket and Vector are correct, set it to value.
		if (socket != null && vec != null) {
			this.socket = socket;
			this.vec = vec;
		}
		// Otherwise, the thread is interrupted!
		else {
			System.out.println("Invalid!");
			this.interrupt();
		}
	}
	public void run(){
		// To distinguish which client it is when running multiple threads.
		String clientInfo = "[" + socket.getInetAddress() + ":" + socket.getPort() + "]";

		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// Print the message that successfully connected with Client.
			System.out.println(clientInfo + " - Connection to Client successful.");
			
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
					Game game = new Game(socket, vec);
					game.start();
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

/**
 * 직업 랜덤 부여 기능의 결과가 어떻게 될지 몰라서 미완.
 * mafia가 다수인 경우 Vector에 마피아를 부여받은 쓰레드만 저장해서 할 경우로 가정해서 일단 구현함.
 */
class MafiaThread extends Thread {
	Socket socket;
	Vector<Socket> mafia;
	
	public MafiaThread(Socket socket, Vector<Socket> mafia){
		// If Socket and Vector are correct, set it to value.
		if (socket != null && mafia != null) {
			this.socket = socket;
			this.mafia = mafia;
		}
		// Otherwise, the thread is interrupted!
		else {
			System.out.println("Invalid!");
			this.interrupt();
		}
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
					mafia.remove(socket);
					break;
				}
				else if (string.equals("!k")) {
					string = reader.readLine();
					kill(string);
				}
				else
					secret(string);
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
	
	// 마피아들끼리 대화. 서로에게만 전송됨.
	public void secret(String start) {
		synchronized(mafia) {
			try{
				for(Socket socket:mafia){
					PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
					writer.println(start);
					writer.flush();	
				}
			}catch(IOException ie){
				System.out.println(ie.getMessage());
			}
		}
	}
	
	public void kill(String killed) {
		/**
		 * 투표
		 */
	}
}

public class ChatServer {
	public static void main(String[] args) {
		ServerSocket server = null;
		Socket socket =null;
		Vector<Socket> vec = new Vector<Socket>();
		try{
			server= new ServerSocket(3000);
			System.out.println("The server is running...");
			
			while(true){
				socket = server.accept();
				vec.add(socket);
				new EchoThread(socket, vec).start();
			}
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		} finally {
			// Close sockets.
			try {
				if (socket != null) socket.close();
				if (server != null) server.close();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
}

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

class EchoThread extends Thread{
	Socket socket;
	Vector<Socket> vec;
	int currentClient;
	ArrayList<Integer> indexList;
	
	public EchoThread(Socket socket, Vector<Socket> vec, ArrayList<Integer> indexList)
	{
		// If Socket and Vector are correct, set it to value.
		if (socket != null && vec != null) {
			this.socket = socket;
			this.vec = vec;
			currentClient = vec.size();
			this.indexList = indexList;
		}
		// Otherwise, the thread is interrupted!
		else {
			System.out.println("Invalid!");
			this.interrupt();
		}
	}
	public void run()
	{
		// To distinguish which client it is when running multiple threads.
		String clientInfo = "[" + socket.getInetAddress() + ":" + socket.getPort() + "]";
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// Print the message that successfully connected with Client.
			
			System.out.println(clientInfo + " - Connection to Client successful.");
			
			File file = new File("clientInfo.txt");
			
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true)); //파일을 새로만들지 않고 이어쓴다	
			
        	
			 if(file.isFile() && file.canWrite())
			 {
	                bufferedWriter.write(currentClient + " 0" + " 0" + " 0");
	                bufferedWriter.newLine();
	                bufferedWriter.close();
			 }
			 //test -> 누군가 종료했을 때는 반영되지 않음..
			 System.out.println("currentClient: "+ currentClient);
			 
			String string = null;
			
			while(true)
			{
				string = reader.readLine();
				System.out.println("Server" + string);
				if(string == null)
				{
					vec.remove(socket);
					break;
				}
				if(string.equals("/p") && vec.size() >= 4 && vec.size() <= 8)
				{
					broadcast("마피아 게임을 시작합니다!");
					// Test
					for (int i = 0; i < indexList.size(); i++)
						System.out.println("index:" + indexList.get(i));
					Game game = new Game(socket, vec, indexList);
					game.start();
				}
				else if((string.equals("/p") && vec.size() < 4) || (string.equals("/p") && vec.size() > 8))
				{
					broadcast("인원이 너무 적거나 많습니다!");
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
					System.out.println("sending");
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
		int num = 0;
		// Socket을 배열로 저장하기 위해 선언한 vec.
		Vector<Socket> vec = new Vector<Socket>();
		// Thread들의 인덱스를 저장하기 위해, 즉 Thread를 특정할 때 사용하기 위해 선언한 indexList.
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		try{
			server= new ServerSocket(3000);
			System.out.println("The server is running...");
			while(true){
				socket = server.accept();
				vec.add(socket);
				indexList.add(num);
				num++;	// 인덱스 하나씩 올리는 방식으로.
				new EchoThread(socket, vec, indexList).start();
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
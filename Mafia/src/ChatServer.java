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
	int currentClient;;
	int playerID = 1;//게임진행시 필요한 플레이어 식별번호
	int voteNum = 0; //투표를 받은 사람의 번호 저장
	int confirmVote = 0; // 투표를 했는지 안했는지 저장
	
	public EchoThread(Socket socket, Vector<Socket> vec, int playerID)
	{
		// If Socket and Vector are correct, set it to value.
		if (socket != null && vec != null) {
			this.socket = socket;
			this.vec = vec;
			currentClient = vec.size();
			this.playerID = playerID;
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
				bufferedWriter.write(playerID + " 0" + " 0" + " 0");
				bufferedWriter.newLine();
				bufferedWriter.close();
			}
			//test -> 누군가 종료했을 때는 반영되지 않음..
			System.out.println("currentClient: "+ currentClient);

			String string = null;

			while(true)
			{
				string = reader.readLine();
				System.out.println("Server " + string);
				if(string == null)
				{
					vec.remove(socket);
					break;
				}
				if(string.equals("/p") && vec.size() >= 4 && vec.size() <= 8)
				{
					broadcast("마피아 게임을 시작합니다!");
					// Test
					for(int i=0; i< vec.size(); i++) {
						Game game = new Game(vec.get(i), vec, playerID);
						playerID++;
						game.start();
					}
				}
				else if((string.equals("/p") && vec.size() < 4) || (string.equals("/p") && vec.size() > 8))
				{
					broadcast("인원이 너무 적거나 많습니다!");
				}
				else if(string.equals("/d"))
				{
					view("<System> 낮이 되었습니다. 토론을 시작하세요.");
					confirmVote = 0;
				}
				else if(string.equals("/n"))
				{
					view("<System> 밤이 되었습니다.");
				}
				else if(string.contains("/vote") && confirmVote == 0)
				{
					voteNum = Integer.parseInt(string.substring(5).trim());
					System.out.println("Success!"+ voteNum);
					confirmVote++;
				}
				else if(string.contains("/die"))
				{
					System.out.println("??");
					String[] arr = string.split(" ");
					System.out.println(arr[0] + "," + arr[1]);
					broadcast("<System> " + arr[1] + "번 님이 사망하셨습니다.");
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

	// 메세지가 해당 쓰레드에게만 뜨게 하는 메서드.
	public void view(String str){
		try{
			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
			writer.println(str);
			writer.flush();
		}catch(IOException ie){
			System.out.println(ie.getMessage());
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
		int playerID = 1;
		EchoThread et = null;
		// Socket을 배열로 저장하기 위해 선언한 vec.
		Vector<Socket> vec = new Vector<Socket>();

		try{
			server= new ServerSocket(3000);
			System.out.println("The server is running...");
			while(true){
				socket = server.accept();
				vec.add(socket);

				new EchoThread(socket, vec, playerID).start();
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
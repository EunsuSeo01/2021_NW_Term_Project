import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

class EchoThread extends Thread{
	Socket socket;
	Vector<Socket> vec;
	int currentClient;;
	int playerID = 1;//게임진행시 필요한 플레이어 식별번호
	int voteNum = 0; //투표를 받은 사람의 번호 저장
	int mafiaTriedToKill = 0;//마피아가 죽이려든사람
	int docTriedToheal = 0;//의사가 살릴려고 한사람
	int copTriedTocheck = 0;	// 경찰이 수사한 사람.
	int confirmVote = 0; // 투표를 했는지 안했는지 저장
	int confirmKill = 0;//죽였는지 아닌지 저장
	int confirmHeal = 0;//살렸는지 아닌지 저장
	int confirmCheck = 0;	// 수사했는지(1) 안 했는지(0) 저장.

	Game game;

	public EchoThread(Socket socket, Vector<Socket> vec, int playerID)
	{
		// If Socket and Vector are correct, set it to value.
		if (socket != null && vec != null) {
			this.socket = socket;
			this.vec = vec;
			currentClient = vec.size();
			this.playerID = playerID;
			System.out.println("In ChatServer, 나는 " +playerID +"번이야");
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

			//test
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
					playerID = 1;
					// Test
					for(int i=0; i< vec.size(); i++) {
						game = new Game(vec.get(i), vec, playerID);
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
					new FileOutputStream("voteInfo.txt").close();//voteinfo초기화
					view("<System> 낮이 되었습니다. 토론을 시작하세요.");
					confirmVote = 0;

				}
				else if(string.equals("/n"))
				{
					view("<System> 밤이 되었습니다.");
					confirmKill = 0;
					confirmHeal = 0;
					confirmCheck = 0;
				}
				else if(string.contains("/vote") && confirmVote == 0)
				{
					String[] arr = string.split(" ");
					System.out.println(arr[0] + "," + arr[1]);	// test
					voteNum = Integer.parseInt(arr[1]);
					System.out.println(playerID + "가 투표한 건 " + voteNum);
					makeVoteFile(voteNum);
					System.out.println("Success!"+ voteNum);
					confirmVote++;
				}
				else if(string.contains("/kill")&& confirmKill == 0)//마피아능력
				{
					String[] arr = string.split(" ");
					mafiaTriedToKill = Integer.parseInt(arr[1]);
					broadcast("<System> 마피아가 죽일사람을 결정했습니다! /n 의사는 살릴 사람을 결정해주세요");//마피아 죽일사람 정한 이후에 의사가 결정? but 의사가 죽으면????

					System.out.println(playerID + "가 죽이려한건 " + mafiaTriedToKill);
					System.out.println("kill!"+ mafiaTriedToKill);
					confirmKill++;
				}
				else if(string.contains("/heal")&& confirmHeal == 0)//의사 능력
				{
					String[] arr = string.split(" ");
					docTriedToheal = Integer.parseInt(arr[1]);
					System.out.println(playerID + "가 살리려 한건 " + docTriedToheal);
					System.out.println("Heal!"+ docTriedToheal);
					confirmHeal++;

				}
				else if(string.contains("/check")&& confirmCheck == 0)//경찰 능력
				{
					String[] arr = string.split(" ");
					copTriedTocheck = Integer.parseInt(arr[1]);
					System.out.println(playerID + "가 수사한 건 " + copTriedTocheck);
					if (isMafia(copTriedTocheck))
						view(copTriedTocheck + "번은 마피아입니다.");
					else
						view(copTriedTocheck + "번은 마피아가 아닙니다.");
					System.out.println("check! "+ copTriedTocheck);
					confirmCheck++;

				}
				else if(string.contains("/check")&& confirmCheck == 1)//경찰의 실수로 명령어 들키는 거 방지
				{
					view("이번 턴에 이미 수사 기능을 사용하셨습니다. 다음 턴에 다시 시도하세요.");
				}
				else if(string.contains("/die"))
				{
					String[] arr = string.split(" ");
					System.out.println(arr[0] + "," + arr[1]);
					//broadcast("<System> " + arr[1] + "번 님이 사망하셨습니다.");
				}
				else if(string.equals("/victory citizen"))
				{
					broadcast("<System> 시민이 승리했습니다.");
				}
				else if(string.equals("/victory mafia"))
				{
					broadcast("<System> 마피아가 승리했습니다.");
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
	public void mafVsDoc() {//마피아가 죽일지 의사가 살릴지
		if((mafiaTriedToKill!=docTriedToheal)&&(mafiaTriedToKill!=0)) {

		}
		else if(mafiaTriedToKill==docTriedToheal&&(mafiaTriedToKill!=0)){
			broadcast("<System> 의사가 플레이어" + docTriedToheal +"님을 살렸습니다!");
		}
	}

	// 해당 playerID를 가진 사람이 마피아인지 아닌지 확인. -> 경찰 수사 기능.
	public boolean isMafia(int playerID) {
		try {
			BufferedReader fileReader = new BufferedReader(new FileReader("clientInfo.txt"));
			String str;
			int line = 0;

			System.out.println("Cop's checking ... ");
			while ((str = fileReader.readLine()) != null) {
				String arr[] = str.split(" ");
				if (line + 1 == playerID && arr[1].equals("1")) {
					return true;
				}
				line++;
			}

			fileReader.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return false;
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
					writer.println("(playerID: " + playerID + ") "+ str);
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

	public void makeVoteFile(int voteNum) {
		File file = new File("voteInfo.txt");
		BufferedWriter bw;
		String num = Integer.toString(voteNum);

		System.out.println("vote file writer");
		// 파일에 쓰기
		try {
			bw = new BufferedWriter(new FileWriter(file, true));
			bw.write(num);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
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
				playerID++;
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
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
	int playerID = 1;//��������� �ʿ��� �÷��̾� �ĺ���ȣ
	int voteNum = 0; //��ǥ�� ���� ����� ��ȣ ����
	int mafiaTriedToKill = 0;//���Ǿư� ���̷�����
	int docTriedToheal = 0;//�ǻ簡 �츱���� �ѻ��
	int copTriedTocheck = 0;	// ������ ������ ���.
	int confirmVote = 0; // ��ǥ�� �ߴ��� ���ߴ��� ����
	int confirmKill = 0;//�׿����� �ƴ��� ����
	int confirmHeal = 0;//��ȴ��� �ƴ��� ����
	int confirmCheck = 0;	// �����ߴ���(1) �� �ߴ���(0) ����.

	Game game;

	public EchoThread(Socket socket, Vector<Socket> vec, int playerID)
	{
		// If Socket and Vector are correct, set it to value.
		if (socket != null && vec != null) {
			this.socket = socket;
			this.vec = vec;
			currentClient = vec.size();
			this.playerID = playerID;
			System.out.println("In ChatServer, ���� " +playerID +"���̾�");
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
					broadcast("���Ǿ� ������ �����մϴ�!");
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
					broadcast("�ο��� �ʹ� ���ų� �����ϴ�!");
				}
				else if(string.equals("/d"))
				{
					new FileOutputStream("voteInfo.txt").close();//voteinfo�ʱ�ȭ
					view("<System> ���� �Ǿ����ϴ�. ����� �����ϼ���.");
					confirmVote = 0;

				}
				else if(string.equals("/n"))
				{
					view("<System> ���� �Ǿ����ϴ�.");
					confirmKill = 0;
					confirmHeal = 0;
					confirmCheck = 0;
				}
				else if(string.contains("/vote") && confirmVote == 0)
				{
					String[] arr = string.split(" ");
					System.out.println(arr[0] + "," + arr[1]);	// test
					voteNum = Integer.parseInt(arr[1]);
					System.out.println(playerID + "�� ��ǥ�� �� " + voteNum);
					makeVoteFile(voteNum);
					System.out.println("Success!"+ voteNum);
					confirmVote++;
				}
				else if(string.contains("/kill")&& confirmKill == 0)//���Ǿƴɷ�
				{
					String[] arr = string.split(" ");
					mafiaTriedToKill = Integer.parseInt(arr[1]);
					broadcast("<System> ���Ǿư� ���ϻ���� �����߽��ϴ�! /n �ǻ�� �츱 ����� �������ּ���");//���Ǿ� ���ϻ�� ���� ���Ŀ� �ǻ簡 ����? but �ǻ簡 ������????

					System.out.println(playerID + "�� ���̷��Ѱ� " + mafiaTriedToKill);
					System.out.println("kill!"+ mafiaTriedToKill);
					confirmKill++;
				}
				else if(string.contains("/heal")&& confirmHeal == 0)//�ǻ� �ɷ�
				{
					String[] arr = string.split(" ");
					docTriedToheal = Integer.parseInt(arr[1]);
					System.out.println(playerID + "�� �츮�� �Ѱ� " + docTriedToheal);
					System.out.println("Heal!"+ docTriedToheal);
					confirmHeal++;

				}
				else if(string.contains("/check")&& confirmCheck == 0)//���� �ɷ�
				{
					String[] arr = string.split(" ");
					copTriedTocheck = Integer.parseInt(arr[1]);
					System.out.println(playerID + "�� ������ �� " + copTriedTocheck);
					if (isMafia(copTriedTocheck))
						view(copTriedTocheck + "���� ���Ǿ��Դϴ�.");
					else
						view(copTriedTocheck + "���� ���Ǿư� �ƴմϴ�.");
					System.out.println("check! "+ copTriedTocheck);
					confirmCheck++;

				}
				else if(string.contains("/check")&& confirmCheck == 1)//������ �Ǽ��� ��ɾ� ��Ű�� �� ����
				{
					view("�̹� �Ͽ� �̹� ���� ����� ����ϼ̽��ϴ�. ���� �Ͽ� �ٽ� �õ��ϼ���.");
				}
				else if(string.contains("/die"))
				{
					String[] arr = string.split(" ");
					System.out.println(arr[0] + "," + arr[1]);
					//broadcast("<System> " + arr[1] + "�� ���� ����ϼ̽��ϴ�.");
				}
				else if(string.equals("/victory citizen"))
				{
					broadcast("<System> �ù��� �¸��߽��ϴ�.");
				}
				else if(string.equals("/victory mafia"))
				{
					broadcast("<System> ���Ǿư� �¸��߽��ϴ�.");
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
	public void mafVsDoc() {//���Ǿư� ������ �ǻ簡 �츱��
		if((mafiaTriedToKill!=docTriedToheal)&&(mafiaTriedToKill!=0)) {

		}
		else if(mafiaTriedToKill==docTriedToheal&&(mafiaTriedToKill!=0)){
			broadcast("<System> �ǻ簡 �÷��̾�" + docTriedToheal +"���� ��Ƚ��ϴ�!");
		}
	}

	// �ش� playerID�� ���� ����� ���Ǿ����� �ƴ��� Ȯ��. -> ���� ���� ���.
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

	// �޼����� �ش� �����忡�Ը� �߰� �ϴ� �޼���.
	public void view(String str){
		try{
			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
			writer.println(str);
			writer.flush();
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}
	}

	//���۹��� ���ڿ� �ٸ� Ŭ���̾�Ʈ�鿡�� �����ִ� �޼���
	public void sending(String str){
		try{
			for(Socket socket:vec){
				//for�� ���� ������ socket�� �����͸� ���� Ŭ���̾�Ʈ�� ��츦 �����ϰ� 
				//������ socket�鿡�Ը� �����͸� ������.
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
		// ���Ͽ� ����
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
		// Socket�� �迭�� �����ϱ� ���� ������ vec.
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
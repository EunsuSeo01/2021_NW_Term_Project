import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

class EchoThread extends Thread{
	int currentClient = 0;
	Socket socket;
	Vector<Socket> vec;
	public EchoThread(Socket socket, Vector<Socket> vec)
	{
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
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
			 if(file.isFile() && file.canWrite())
			 {
	                bufferedWriter.write(socket.getPort() + " 0" + " 0" + " 0");
	                bufferedWriter.newLine();
	                bufferedWriter.close();
	                currentClient++;
			 }
			String string = null;
			
			while(true)
			{
				string = reader.readLine();
				if(string == null)
				{
					vec.remove(socket);
					break;
				}
				if(string.equals("!p") && currentClient >= 4 && currentClient <= 8)
				{
					broadcast("���Ǿ� ������ �����մϴ�!");
					Game game = new Game(socket, vec);
					game.start();
				}
				else if(string.equals("!p") && currentClient < 4 && currentClient > 8)
				{
					broadcast("�ο��� �ʹ� ���ų� �����ϴ�!");
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

	//���۹��� ���ڿ� �ٸ� Ŭ���̾�Ʈ�鿡�� �����ִ� �޼���
	public void sending(String str){
		try{
			for(Socket socket:vec){
				//for�� ���� ������ socket�� �����͸� ���� Ŭ���̾�Ʈ�� ��츦 �����ϰ� 
				//������ socket�鿡�Ը� �����͸� ������.
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
 * ���� ���� �ο� ����� ����� ��� ���� ���� �̿�.
 * mafia�� �ټ��� ��� Vector�� ���ǾƸ� �ο����� �����常 �����ؼ� �� ���� �����ؼ� �ϴ� ������.
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
	
	// ���ǾƵ鳢�� ��ȭ. ���ο��Ը� ���۵�.
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
		 * ��ǥ
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
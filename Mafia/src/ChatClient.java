import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

class WritingThread{
	Socket socket;
	ClientFrame client;
	String str;
	String nickname;
	public WritingThread(ClientFrame client) {
		this.client  = client;
		this.socket= client.socket;
	}
	public void message() 
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter writer = null;
		try{
			writer = new PrintWriter(socket.getOutputStream(),true);
			
			if(client.isFirst==true)
			{
				InetAddress address = socket.getLocalAddress();				
				String ip = address.getHostAddress();				
				getId();
				str = "*****"+"["+nickname+"] enter this room"+"*****"; 
				writer.println(str);
			}
			else if(client.textField.getText().equals("/p"))// protocol
			{
				str = "/p";
				System.out.println(str);
				writer.print(str);
			}
			else
			{
				str= "["+nickname+"] "+client.textField.getText();
				writer.println(str);
			}
		
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}finally{
			try{
				if(reader!=null) reader.close();
			}catch(IOException ie){
				System.out.println(ie.getMessage());
			}
		}
	}	
	public void getId(){		
		nickname = Id.getId(); 
	}
}


class ListeningThread extends Thread{
	Socket socket;
	ClientFrame client;
	public ListeningThread(Socket socket, ClientFrame client) {
		this.client = client;
		this.socket=socket;
	}
	public void run() {
		BufferedReader reader =null;
		try{
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while(true){
				String str=reader.readLine();
				if(str==null)
				{
					System.out.println("disconnect");
					break;
				}

				client.textArea.append(str+"\n");
			}
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}finally{
			try{
				if(reader!=null) reader.close();
				if(socket!=null) socket.close();
			}catch(IOException ie){}
		}
	}
}
public class ChatClient {
	public static void main(String[] args) {
		Socket socket=null;
		ClientFrame client;
		try{
			socket=new Socket("127.0.0.1",3000);
			client = new ClientFrame(socket);
			new ListeningThread(socket, client).start();
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}
	}
}













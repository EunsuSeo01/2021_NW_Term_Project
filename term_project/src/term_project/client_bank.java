import java.io.*; 
import java.net.*;
import java.util.Scanner; 
public class client_bank {

	public static void main(String[] args) throws Exception
	{
		Scanner keyboard = new Scanner(System.in);
		InetAddress local = InetAddress.getLocalHost();
		String ip = local.getHostAddress(); //Save the ip address of the computer

		String name, sender, receiver; //Save the name that the user inputs.
		int balance = 0 , balance2, mon = 0; //Store the integer (amount) input by the user.
		int number = 0; //Save the number of the option selected by the user.
		int portnum = 0; //Save the port number in the file.
		
		File file = new File("serverinfo.txt"); //The file name is serverinfo.txt.
		FileReader filereader = new FileReader(file);
        BufferedReader bufReader = new BufferedReader(filereader);
        
	     String line = "";
		 line = bufReader.readLine();
		 portnum = Integer.parseInt(line); //Change the string to int and save it.	 
		 
		Socket clientSocket = new Socket(ip,portnum); //Client socket with port number
		
		OutputStream out = clientSocket.getOutputStream();
		DataOutputStream writer = new DataOutputStream(out); 
		 
		 InputStream input = clientSocket.getInputStream(); 
		 DataInputStream reader = new DataInputStream(input);
		 
		System.out.print("Press 1: check\n"
				+ "Press 2: deposit\n"
				+ "Press 3: withdraw\n"
				+ "press 4: transfer\n"); //Presenting options to the user.
		System.out.print("Select : ");
		number = keyboard.nextInt(); //User selects and enters one of the option numbers.
		
		if(number == 1) //When the user selects check
		{
			writer.writeInt(1); //Sent 1 (a kind of protocol) to the server.
			
			keyboard.nextLine();
			System.out.print("Enter the name of the person you want to check: ");
			name = keyboard.nextLine(); //Save the name of the person you want to check.
			writer.writeUTF(name); //send "name" to the server.
			
			
			name = reader.readUTF(); //receive name information from the server
			balance = reader.readInt(); //receive amount information from the server
			
			if(name.equals("0") && balance == 0) //When an error occurs(if an error occurs in the server, 0 (name information) and 0 (amount information) are sent)
				System.out.println("Account not found"); //print
			else //If there's no error
				System.out.println(name + " has "+ balance+"$");
		}
		else if(number == 2) //When the user selects deposit
		{
			
			writer.writeInt(2);//Sent 2 (a kind of protocol) to the server.
			
			keyboard.nextLine();
			System.out.print("Enter the name of the person you want to deposit: ");
			name = keyboard.nextLine(); //Save the name of the person you want to deposit
			writer.writeUTF(name);//send "name" to the server.
			
			System.out.print("Enter the amount to deposit: ");
			mon = keyboard.nextInt(); //Save the amount you want to deposit
			writer.writeInt(mon); //send "mon" to the server.
			
			name = reader.readUTF(); //receive name information from the server
			
			balance = reader.readInt(); //receive amount information from the server
			
			if(name.equals("0") && balance == 0) //When an error occurs(if an error occurs in the server, 0 (name information) and 0 (amount information) are sent)
				System.out.println("Account not found"); //print
			else //If there's no error
				System.out.println(name + " now has "+ balance+"$");
		}
		else if(number == 3) //When the user selects withdraw
		{
			writer.writeInt(3);//Sent 3 (a kind of protocol) to the server.
			
			keyboard.nextLine();
			System.out.print("Enter the name of the person you want to withdraw: ");
			name = keyboard.nextLine(); //Save the name of the person you want to withdraw
			writer.writeUTF(name);//send "name" to the server.
			
			System.out.print("Enter the amount to withdraw: ");
			mon = keyboard.nextInt(); //Save the amount you want to withdraw
			writer.writeInt(mon); //send "mon" to the server.
			
			name = reader.readUTF();//receive name information from the server
			balance = reader.readInt(); //receive amount information from the server
			
			if(name.equals("0") && balance == 0)//When an error occurs
				System.out.println("Account not enough");
			else	//If there's no error
				System.out.print(name + " now has "+ balance+"$");
		}
		else if(number == 4) //When the user selects transfer
		{
			writer.writeInt(4);//Sent 4 (a kind of protocol) to the server.
			
			keyboard.nextLine();
			System.out.print("Enter the name of sender for transfer: ");
			sender = keyboard.nextLine(); //Save the name of the sender
			System.out.print("Enter the name of receiver for transfer: ");
			receiver = keyboard.nextLine(); //Save the name of the receiver
			
			writer.writeUTF(sender); //send "sender" to the server.
			writer.writeUTF(receiver); //send "receiver" to the server.
			
			System.out.print("Enter the amount to transfer: ");
			mon = keyboard.nextInt(); //Save the amount you want to transfer
			writer.writeInt(mon); //send "mon" to the server.
			
			sender = reader.readUTF(); //receive sender information from the server
			receiver = reader.readUTF();//receive receiver information from the server
			
			balance = reader.readInt(); //receive sender's amount information from the server
			balance2 = reader.readInt(); //receive receiver's amount information from the server
			
			if(sender.equals("0") && balance == 0 && receiver.equals("0") && balance2 == 0) //When an error occurs
			{
				System.out.println("Account not enough"); //print
			}
			else //If there's no error	
			{
				System.out.println(sender + " now has "+ balance+"$"); //print
				System.out.println(receiver + " now has "+ balance2+"$"); //print
			}
		}
		else //If you enter a number other than the number of the given option
			System.out.println("Please enter again"); //print
		
		clientSocket.close();
	}

}

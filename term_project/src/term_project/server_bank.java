import java.io.*; 
import java.net.*;
import java.util.Scanner; 

public class server_bank {

	public static void main(String[] args) throws Exception
	{
		Scanner keyboard = new Scanner(System.in);
		String clientSentence,clientSentence2; //Save the name you received from the client.

		File file = new File("serverinfo.txt"); //The file name is serverinfo.txt.
		
		String[] namearr = new String[5]; // An array that stores name information in a file.
		int[] moneyarr = new int[5]; // An array that stores account information in a file.
		
		int deposit = 0,withdraw = 0,transfer = 0; //The integer (amount) received from the client is stored.
		int check = 0; // Checked whether the port number in the first part of the file was saved or not.
		int portnum = 0; //Save the port number in the file.
		FileReader filereader = new FileReader(file);
        BufferedReader bufReader = new BufferedReader(filereader);
        
        String line = "";
        String temp = "";
        
        while((line = bufReader.readLine()) != null)
        {
        	if(check == 0)
        	{
        		portnum = Integer.parseInt(line); //Save port number
        		check++; //If check is from 0 to 1, it means that the port number has been entered.
        	}
        	else
        		temp += line + " "; // Save all the information in the file.
        }
        
        String[] temparr = temp.split(" "); //Cut the string based on the space and place it in the arrangement.
        
        for(int a = 0; a < temparr.length;a++)
        {
        	if(a % 2==0) //There is information about the name in the even number of the array
        	{
        		namearr[a/2] = temparr[a]; //Save name information.
        	}
        	else //There is information about the amount in the odd number of the array
        	{
        		moneyarr[a/2] = Integer.parseInt(temparr[a]); //Save account information.
        	}
        }
        
        ServerSocket serversoket= new ServerSocket(portnum); 
  
		while (true) //The server must continue to run.
		{	
			 Socket connectionSocket = serversoket.accept(); //receive a request from the client.
			 
			 InputStream input = connectionSocket.getInputStream(); 
			 DataInputStream reader = new DataInputStream(input);
			 
			 OutputStream out = connectionSocket.getOutputStream();
			 DataOutputStream writer = new DataOutputStream(out); 
			 
			int protocol = reader.readInt(); //Depending on the protocol sent by the client, the task to be executed varies.
		
			
			if(protocol==1) //If protocol is 1, check operation is performed.
			{
				clientSentence = reader.readUTF(); //Receive name information from client
								
				int b = 0; 
	            for(int a = 0; a < namearr.length;a++)
	            {
	               if(namearr[a].equals(clientSentence)) //If the clientSentence and the information in the namearr array are the same
	               {
	            	   writer.writeUTF(namearr[a]); //Send the name information to the client
	            	   writer.writeInt(moneyarr[a]); //Send the amount information to the client
	            	   break; //escape
	               }
	               b++;
	            }
	            
				if(b==namearr.length) //If b is equal to the size of the namearr array, it means that no one has been found to check.
				{
					writer.writeUTF("0"); //Send 0 (name information) to the client.
					writer.writeInt(0); //Send 0 (money information) to the client.	
				}
			}
			else if(protocol==2) //If protocol is 2, deposit operation is performed.
			{
				clientSentence = reader.readUTF();//Receive name information from client
				deposit = reader.readInt(); //Save the amount delivered to the client

				int b = 0;
				for(int a = 0; a<namearr.length;a++)
	            {
	               if(namearr[a].equals(clientSentence)) //If the clientSentence and the information in the namearr array are the same
	               {
	            	   writer.writeUTF(namearr[a]); //Send the name information to the client
	            	   moneyarr[a] += deposit; //"deposit" and moneyarr plus
	            	   writer.writeInt(moneyarr[a]); //Send the amount information to the client
	            	   break;
	               }
	               b++;
	            }
				if(b==namearr.length) //If b is equal to the size of the namearr array, it means that no one has been found to check.
				{
					writer.writeUTF("0"); //Send 0 (name information) to the client.
					writer.writeInt(0); //Send 0 (money information) to the client.	
				}
	               
			}
			else if(protocol==3) //If protocol is 3, withdraw operation is performed.
			{
				clientSentence = reader.readUTF(); //Receive name information from client
				withdraw = reader.readInt(); //Save the amount delivered to the client
				
				int b =0;
				for(int a = 0; a<namearr.length;a++)
	            {
					if(namearr[a].equals(clientSentence)) //If the clientSentence and the information in the namearr array are the same
		            {
						if(moneyarr[a] < withdraw) //If the amount to be withdrawn is larger than the amount stored in moneyarr
						{
							writer.writeUTF("0"); //Send 0 (name information) to the client.
							writer.writeInt(0); //Send 0 (amount information) to the client.
							break;
						}
						else
						{
							writer.writeUTF(namearr[a]); //Send the name information to the client
							moneyarr[a] = moneyarr[a] - withdraw; //"deposit" and moneyarr minus
							writer.writeInt(moneyarr[a]); //Send the amount information to the client
							break;
						}
		            }
					b++;
	            }
				
				if(b==namearr.length) //If b is equal to the size of the namearr array, it means that no one has been found to check.
				{
					writer.writeUTF("0"); //Send 0 (name information) to the client.
					writer.writeInt(0); //Send 0 (money information) to the client.	
				}
			}
			else if(protocol==4) //If protocol is 4, transfer operation is performed.
			{
				clientSentence = reader.readUTF(); //Receive sender information from client
				clientSentence2 =reader.readUTF(); //Receive receiver information from client
				transfer = reader.readInt(); //Save the amount delivered to the client
				
				int d= 0;
				for(int a = 0; a<namearr.length;a++)
	            {
					for(int b = 0; b<namearr.length;b++)
		            {
						if(namearr[a].equals(clientSentence) && namearr[b].equals(clientSentence2)) 
						{
							if(moneyarr[a] < transfer) //If the amount to be transferred is larger than the amount stored in moneyarr
							{
								writer.writeUTF("0"); //Send the 0(sender) information to the client
								writer.writeUTF("0"); //Send the 0(receiver) information to the client
								writer.writeInt(0); //Send the 0(sender) information to the client
								writer.writeInt(0); //Send the 0(receiver) information to the client
								break;
							}
							else
							{
								writer.writeUTF(namearr[a]); //Send the name(sender) information to the client
								writer.writeUTF(namearr[b]); //Send the name(receiver) information to the client
								moneyarr[a] -= transfer; //Subtract "transfer" from the sender's amount information
								moneyarr[b] += transfer; //Add "transfer" from the amount information of the receiver.
								writer.writeInt(moneyarr[a]);//Send the amount(sender) information to the client
								writer.writeInt(moneyarr[b]); //Send the amount(receiver) information to the client
								break;
							}
						}
		            }
					d++;
	            }
				if(d ==namearr.length) //If b is equal to the size of the namearr array, it means that no one has been found to check.
				{
					writer.writeUTF("0"); //Send 0 (name information) to the client.
					writer.writeUTF("0"); //Send 0 (name information) to the client.
					writer.writeInt(0); //Send 0 (money information) to the client.	
					writer.writeInt(0); //Send 0 (money information) to the client.	
				}
			}
			else
			{
				System.out.print("false");
			}
		}
	}
	
}

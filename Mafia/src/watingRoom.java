import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import java.awt.TextArea;
import java.awt.Font;
import java.awt.TextField;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class watingRoom extends JFrame {

	private JPanel contentPane;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					watingRoom frame = new watingRoom();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public watingRoom() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		TextArea textArea = new TextArea();
		textArea.setFont(new Font("Arial", Font.PLAIN, 12));
		textArea.setEnabled(false);
		textArea.setBounds(10, 10, 414, 206);
		contentPane.add(textArea);
		
		textField = new JTextField();
		textField.setBounds(10, 230, 303, 21);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("send");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				OutputStream out = socket.getOutputStream();
				DataOutputStream writer = new DataOutputStream(out); 
				 
				InputStream input = socket.getInputStream(); 
				DataInputStream reader = new DataInputStream(input);
				 
				System.out.println("access sucess!");
				
				writer.writeInt(1); // protocol number of Login
				
				ListeningThread t1 = new ListeningThread(socket);
				WritingThread t2 = new WritingThread(socket); //  Thread to send message Server

				t1.start(); 
				t2.start(); 
			}
		});
		btnNewButton.setFont(new Font("Arial", Font.PLAIN, 12));
		btnNewButton.setBounds(327, 229, 97, 23);
		contentPane.add(btnNewButton);
	}
	
	public class WritingThread extends Thread { 
		Socket socket = null;
		Scanner scanner = new Scanner(System.in); 
		
		public WritingThread(Socket socket) { 
			this.socket = socket; 
		}
		
		public void run() {
			try {

				OutputStream out = socket.getOutputStream();

				PrintWriter writer = new PrintWriter(out, true);
				
				while(true) { 
					//infinite loop
					writer.println(scanner.nextLine()); // send message
				}
				
			} catch (Exception e) {
				e.printStackTrace(); // exception
			}
			
			
		}
	}
	public class ListeningThread extends Thread 
	{ 
		Socket socket = null;

		public ListeningThread(Socket socket) {
			this.socket = socket; 
		}
		
		public void run() {
			try {
				
				InputStream input = socket.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				
				while(true) 
				{ // infinite loop
					System.out.println(reader.readLine());
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}

	}
}

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import java.awt.TextField;
import java.awt.Button;
import java.awt.Panel;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
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
	public Login() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnNewButton = new JButton("Play");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				try {
					
					Socket socket = null;
		            		
					InetAddress local = InetAddress.getLocalHost();
					String ip = local.getHostAddress(); //Save the ip address of the computer
					
					socket = new Socket(ip,11324); //Client socket with port number

					OutputStream out = socket.getOutputStream();
					DataOutputStream writer = new DataOutputStream(out); 
					 
					InputStream input = socket.getInputStream(); 
					DataInputStream reader = new DataInputStream(input);
					 
					System.out.println("access sucess!");
					
					writer.writeInt(0); // protocol number of Login
					
					String nickname = textField.getText();
					writer.writeUTF(nickname);
					
					dispose();	// Login screen off
					watingRoom watingRoom = new watingRoom(); 
					watingRoom.setVisible(true);
					
				} catch (IOException e1) {
					e1.printStackTrace(); // exception
				}
		            
				
			}
		});
		btnNewButton.setFont(new Font("Arial", Font.PLAIN, 20));
		btnNewButton.setBounds(157, 215, 97, 23);
		contentPane.add(btnNewButton);
		
		textField = new JTextField();
		textField.setFont(new Font("Arial", Font.PLAIN, 20));
		textField.setBounds(127, 159, 158, 40);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JTextPane txtpnEnterYour = new JTextPane();
		txtpnEnterYour.setEditable(false);
		txtpnEnterYour.setEnabled(false);
		txtpnEnterYour.setFont(new Font("Arial", Font.PLAIN, 40));
		txtpnEnterYour.setBackground(SystemColor.menu);
		txtpnEnterYour.setText("Enter your nickname");
		txtpnEnterYour.setBounds(34, 62, 370, 66);
		contentPane.add(txtpnEnterYour);
		
	}
	
}

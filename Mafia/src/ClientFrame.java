import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.TextField;
import java.awt.Scrollbar;
import javax.swing.JScrollPane;
import java.awt.ScrollPane;
import java.awt.TextArea;


class Id extends JFrame implements ActionListener{
	static JTextField textField = new JTextField(8);
	JButton button = new JButton("입력");	
	
	WritingThread writing;	
	ClientFrame client;

	public Id(WritingThread writing, ClientFrame client) {
		super("닉네임");		
		this.writing = writing;
		this.client = client;
		
		
		setLayout(new FlowLayout());
		add(new JLabel("닉네임"));
		add(textField);
		add(button);
		
		button.addActionListener(this);
		
		setBounds(300, 300, 250, 100);
		setVisible(true);
	}
	 
	public void actionPerformed(ActionEvent e) {		
		writing.message();	
		client.isFirst = false;
		client.setVisible(true);
		this.dispose();
	}
	static public String getId(){
		return textField.getText();
	}
}




public class ClientFrame extends JFrame implements ActionListener{
	TextArea textArea = new TextArea();
	JTextField textField = new JTextField(15);
	JButton button_1 = new JButton("전송");
	JButton button_2 = new JButton("닫기");
	boolean isFirst=true;
	JPanel panel = new JPanel();
	Socket socket;
	WritingThread writing;
		
	public ClientFrame(Socket socket)
	{
		super("Mafia Game");
		this.socket = socket;
		writing = new WritingThread(this);
		new Id(writing, this);
		getContentPane().setLayout(null);
		textArea.setBounds(0, 0, 334, 228);
		
		getContentPane().add(textArea);
		panel.setBounds(0, 228, 334, 33);
		
		panel.add(textField);
		panel.add(button_1);
		panel.add(button_2);
		getContentPane().add(panel);
		
		//메세지를 전송하는 클래스 생성.
		
		button_1.addActionListener(this);
		button_2.addActionListener(this);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(300, 300, 350, 300);
		setVisible(false);	
	}
	
	public void actionPerformed(ActionEvent e){
		String id = Id.getId();
		if(e.getSource()==button_1)
		{
			if(textField.getText().equals(""))
			{
				return;
			}			
			textArea.append("["+id+"] "+ textField.getText()+"\n");
			writing.message();
			textField.setText("");
		}else{
			this.dispose();
		}
	}
}
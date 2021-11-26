import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class VoteFrame extends JFrame implements ActionListener {//누굴 투표 할건지 playerID 값을 입력 받아 저장해주는 GUI
	static JTextField textField = new JTextField(8);
	JButton button = new JButton("입력");
	VoteFrame vote;

	public VoteFrame() {
		super("플레이어 선택");
	

		setLayout(new FlowLayout());
		add(new JLabel("플레이어 번호"));
		add(textField);
		add(button);

		button.addActionListener(this);

		setBounds(300, 300, 340, 100);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {

		this.setVisible(true);
		this.dispose();
	}

	 public String getToVotedNum() {// 입력받을 값을 game 클래스의 votedPlayer 변수에 저장
		//int voted = Integer.parseInt(textField.getText());//입력받은 값을 Intger 값으로 변경
		return textField.getText();
	}

}

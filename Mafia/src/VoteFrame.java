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

public class VoteFrame extends JFrame implements ActionListener {//���� ��ǥ �Ұ��� playerID ���� �Է� �޾� �������ִ� GUI
	static JTextField textField = new JTextField(8);
	JButton button = new JButton("�Է�");
	VoteFrame vote;

	public VoteFrame() {
		super("�÷��̾� ����");
	

		setLayout(new FlowLayout());
		add(new JLabel("�÷��̾� ��ȣ"));
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

	 public String getToVotedNum() {// �Է¹��� ���� game Ŭ������ votedPlayer ������ ����
		//int voted = Integer.parseInt(textField.getText());//�Է¹��� ���� Intger ������ ����
		return textField.getText();
	}

}

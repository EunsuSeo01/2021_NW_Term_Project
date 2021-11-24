
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

/**
 * ��ü���� ������ ����Ǵ� Ŭ����.
 * ���� - ���� �ʱ�ȭ & Ÿ�̸� ��� ����.
 */
public class Game {
	Socket socket;
	Vector<Socket> player;
	int survivorNum;	// ������ ��
	int deadNum;	// ���� ��� ��
	long daytime;
	long night;
	int voteTime;
	
	public Game(Socket socket, Vector<Socket> player) {
		this.socket = socket;
		this.player = player;
	}

	private void set() {
		survivorNum = player.size();	// ������ ������ ���� ���� �÷��̾� ��. ��, ������ ��.
		deadNum = 0;
		daytime = survivorNum * 15000;	// �� �ð� (�����ڼ�*15��)
		voteTime = 15000;	// �� �ð� (= ��ǥ�ð� 15��)
	}

	public void start() {
		set();
		EchoThread et = new EchoThread(socket, player);
		Timer daytimeTimer = new Timer();
		TimerTask daytimeTask = new TimerTask(){
			@Override
			public void run() {
				et.broadcast("���� �Ǿ����ϴ�. ����� �����ϼ���.");
			}
		};

		daytimeTimer.schedule(daytimeTask, 0, daytime + night + voteTime);
		
		Timer nightTimer = new Timer();
		TimerTask nightTimeTask = new TimerTask() {
			@Override
			public void run() {
				et.broadcast("���� �Ǿ����ϴ�. ��ǥ�� �������ּ���.");
			}
		};
		
		nightTimer.schedule(nightTimeTask, daytime, daytime + night + voteTime);		
	}
	
	//vote �޼ҵ�
	public void vote() {//�׽�Ʈ��
		
	}

	/**
	 * ���� ���� ���� ���. -> �μ���
	 */

	/**
	 * ��ǥ ���. -> ȿ����
	 */

	/**
	 * ������ �ɷ� ��� ���. -> protocol ���. ��) !heal nickname
	 */
	
	/**
	 * �¸�
	 */
	
	/**
	 * �й�
	 */
}

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
	long dayTime;
	long nightTime;
	int voteTime;
	
	public Game(Socket socket, Vector<Socket> player) {
		this.socket = socket;
		this.player = player;
	}

	private void set() {
		survivorNum = player.size();	// ������ ������ ���� ���� �÷��̾� ��. ��, ������ ��.
		deadNum = 0;
		dayTime = survivorNum * 1000;	// �� �ð� (�����ڼ�*15��)
		nightTime = survivorNum * 1000; // �� �ð� (�����ڼ�*15��)
		voteTime = 15000;	//  ��ǥ�ð� 15��
	}

	public void start() {
		set();
		EchoThread et = new EchoThread(socket, player);
		Timer daytimeTimer = new Timer();
		TimerTask daytimeTask = new TimerTask(){
			@Override
			public void run() {
				et.broadcast("<System> ���� �Ǿ����ϴ�. ����� �����ϼ���.");
			}
		};
		
		Timer votetimeTimer = new Timer();
		TimerTask votetimeTask = new TimerTask(){
			@Override
			public void run() {
				et.broadcast("<System> ��ǥ�� �����մϴ�");
			    //vote();
			}
		};
		
		Timer nightTimer = new Timer();
		TimerTask nightTimeTask = new TimerTask() {
			@Override
			public void run() {
				et.broadcast("<System> ���� �Ǿ����ϴ�. ��ǥ�� �����մϴ�");
			}
		};

		daytimeTimer.schedule(daytimeTask, 0, dayTime + nightTime + voteTime);	
		votetimeTimer.schedule(votetimeTask, dayTime, dayTime + nightTime + voteTime);		
		nightTimer.schedule(nightTimeTask,dayTime + voteTime, dayTime + nightTime + voteTime);
		
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
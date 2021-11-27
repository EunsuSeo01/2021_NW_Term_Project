import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;


/**
 * ��ü���� ������ ����Ǵ� Ŭ����.
 */
public class Game {
	Socket socket;
	Vector<Socket> player;
	int playerID; //�ش��÷��̾� ���� �ĺ� ��ȣ 1~
	String votedPlayer;//��ǥ�� �÷��̾� ����� (��ǥ������ �ʱ�ȭ �ʿ�)
	int survivorNum; // ������ ��
	int deadNum; // ���� ��� ��
	long dayTime;
	long nightTime;
	int voteTime;
	EchoThread et;

	public Game(Socket socket, Vector<Socket> player, int playerID) {
		this.socket = socket;
		this.player = player;
		this.playerID = playerID;
	}

	private void set() {
		survivorNum = player.size(); // ������ ������ ���� ���� �÷��̾� ��. ��, ������ ��.
		deadNum = 0;
		dayTime = survivorNum * 5000; // �� �ð� (�����ڼ�*15��)
		nightTime = survivorNum * 5000; // �� �ð� (�����ڼ�*15��)
		voteTime = 15000; // ��ǥ�ð� 15��
	}

	public void start() throws IOException { 
		set();
		if(playerID ==1) { //���� ���� ó�� �ѹ���
			setRoles();
		}

		System.out.println("���� ������ �÷��̾� ID" + playerID);//player ID test
		et = new EchoThread(socket, player, playerID);

		Timer daytimeTimer = new Timer();
		TimerTask daytimeTask = new TimerTask() {
			@Override
			public void run() {
				PrintWriter writer = null;
				try{
					writer = new PrintWriter(socket.getOutputStream(),true);
					writer.println("/d");	// protocol
					writer.flush();

					checkDie();	// �� ������ ���� ��� �ִ��� Ȯ��.
				}catch(IOException ie){
					System.out.println(ie.getMessage());
				}
			}
		};

		Timer nightTimer = new Timer();
		TimerTask nightTimeTask = new TimerTask() {
			@Override
			public void run() {
				PrintWriter writer = null;
				try{
					writer = new PrintWriter(socket.getOutputStream(),true);
					writer.println("/n");	// protocol
					writer.flush();
				}catch(IOException ie){
					System.out.println(ie.getMessage());
				}

				System.out.println("���� ������ �÷��̾� ID"+ playerID +"���� ��ǥ�� �����"+ votedPlayer);//��ǥ �Է� �޴��� test
			}
		};

		daytimeTimer.schedule(daytimeTask, 0, dayTime + nightTime + voteTime);
		nightTimer.schedule(nightTimeTask, dayTime + voteTime, dayTime + nightTime + voteTime);

	}
	//*********** vote ���� �޼ҵ����

	public void voteCount() throws IOException {//��ǥ�� �÷��̾�� ��ǥ�� +1

		int [][] array = new int [survivorNum][4];
		String str[] = null;
		String s;
		int toVote = Integer.parseInt(votedPlayer);

		File file = new File("clientInfo.txt");

		int k = 0;
		str = new String[10];
		BufferedReader brv = new BufferedReader(new FileReader(file));//���� ���� ��ü ����
		while ((s = brv.readLine()) != null) {
			str[k] = s;
			k++;
		}

		for (int i = 0; i < survivorNum; i++) {// �迭�� ���� ���� int ���·� ����

			s = str[i];
			String split[] = s.split(" ");

			array[i][0] = Integer.parseInt(split[0]);
			array[i][1] = Integer.parseInt(split[1]);
			array[i][2] = Integer.parseInt(split[2]);;
			array[i][3] = Integer.parseInt(split[3]);
		}

		array[toVote-1][3]++;//��ǥ�� +1
		brv.close();

		BufferedWriter bwv1 = new BufferedWriter(new FileWriter(file));//�Ƹ� ���� ����
		bwv1.close();

		BufferedWriter bwv2 = new BufferedWriter(new FileWriter(file,true));
		for (int i = 0; i < survivorNum; i++) {
			bwv2.write(array[i][0] + " " + array[i][1] +" " + array[i][2]+" " + array[i][3]);
			bwv2.newLine();
		}
		bwv2.close();

		//votedPlayer = null;//��ǥ�� ��� �ʱ�ȭ

	}

	public int voteResult()throws IOException {//�缱�� ��� ���� ������ ������ �������� ó��
		int [][] array = new int [survivorNum][4];
		int [][] tieChecker = new int [survivorNum][2];//���� �˻�� �迭
		int maxVoteID;//�ִ� ��ǥ�� playerID (int ���̶� ���� string���� ��ȯ�ؾ� �� ����)

		String str[] = null;
		String s;
		File file = new File("clientInfo.txt");

		int k = 0;
		str = new String[10];
		BufferedReader brv = new BufferedReader(new FileReader(file));//���� ���� ��ü ����
		while ((s = brv.readLine()) != null) {
			str[k] = s;
			k++;
		}

		for (int i = 0; i < survivorNum; i++) {// �迭�� ���� ���� int ���·� ����

			s = str[i];
			String split[] = s.split(" ");

			array[i][0] = Integer.parseInt(split[0]);
			array[i][1] = Integer.parseInt(split[1]);
			array[i][2] = Integer.parseInt(split[2]);;
			array[i][3] = Integer.parseInt(split[3]);
		}
		brv.close();

		for (int i = 0; i < survivorNum; i++) {// �迭 �ʱ�ȭ
			for (int j = 0; j < 2; j++) {
				tieChecker[i][j] = 0;

			}
		}


		int tieNum = 1;//������� +1
		int max =array[0][3];//max�� �ʱ�ȭ
		for (int i = 0; i < survivorNum; i++) {// �� �÷��̾� ���� ��ǥ�� ��
			if(array[i][3]> max) {
				max = array[i][3];

			}
		}
		for (int i = 0; i < survivorNum; i++) {//����check�ؼ� ������ ��� �����ΰ� �߿� �������� 
			if(array[i][3] == max){
				tieChecker[i][0] = array[i][3];
				tieNum ++;
			}
		}


		int randomSelect[] = new int[tieNum];
		int t = 0;
		for (int i = 0; i < tieNum; i++) {
			if (tieChecker[i][0]!=0) {
				randomSelect[t] = i;
				t++;
			}
		}
		int rmd = new Random().nextInt(randomSelect.length);
		maxVoteID = randomSelect[rmd];//�����϶� ������ �������� �ϳ� �ƴϸ� �׳� ����. ��¥ ID�� ������ +1 �ʿ� (������ �迭 index����)

		return maxVoteID;

	}

	public void killedByVote(int tokill) throws IOException {//���� ��� ID int ���·� �Ѱܹ޾� ���Ϸΰ��� �� ��� �������� ���ó��(0= ����, 1=���)
		int [][] array = new int [survivorNum][4];

		String str[] = null;
		String s;
		File file = new File("clientInfo.txt");

		int k = 0;
		str = new String[10];
		BufferedReader brv = new BufferedReader(new FileReader(file));//���� ���� ��ü ����
		while ((s = brv.readLine()) != null) {
			str[k] = s;
			k++;
		}

		for (int i = 0; i < survivorNum; i++) {// �迭�� ���� ���� int ���·� ����

			s = str[i];
			String split[] = s.split(" ");

			array[i][0] = Integer.parseInt(split[0]);
			array[i][1] = Integer.parseInt(split[1]);
			array[i][2] = Integer.parseInt(split[2]);;
			array[i][3] = Integer.parseInt(split[3]);
		}
		array[tokill][2] = 1;
		brv.close();

		BufferedWriter bwv1 = new BufferedWriter(new FileWriter(file));// ���� ����
		bwv1.close();

		BufferedWriter bwv2 = new BufferedWriter(new FileWriter(file,true));//���Ͽ��� ��ǥ ��� �ݿ�(���̱�)
		for (int i = 0; i < survivorNum; i++) {
			bwv2.write(array[i][0] + " " + array[i][1] +" " + array[i][2]+" " + array[i][3]);
			bwv2.newLine();
		}
		bwv2.close();
	}

	public void voteReset()throws IOException {//��ǥ ������ ��ǥ�� 0���� �ʱ�ȭ ���ִ� �޼ҵ�

		int [][] array = new int [survivorNum][4];
		String str[] = null;
		String s;


		File file = new File("clientInfo.txt");

		int k = 0;
		str = new String[10];
		BufferedReader brv = new BufferedReader(new FileReader(file));//���� ���� ��ü ����
		while ((s = brv.readLine()) != null) {
			str[k] = s;
			k++;
		}

		for (int i = 0; i < survivorNum; i++) {// �迭�� ���� ���� int ���·� ����

			s = str[i];
			String split[] = s.split(" ");

			array[i][0] = Integer.parseInt(split[0]);
			array[i][1] = Integer.parseInt(split[1]);
			array[i][2] = Integer.parseInt(split[2]);;
			array[i][3] = Integer.parseInt(split[3]);
		}
		for (int i = 0; i < survivorNum; i++) {//��ǥ�� 0���� �ʱ�ȭ

			array[i][3] = 0;
		}

		brv.close();

		BufferedWriter bwv1 = new BufferedWriter(new FileWriter(file));// ���� ����
		bwv1.close();

		BufferedWriter bwv2 = new BufferedWriter(new FileWriter(file,true));
		for (int i = 0; i < survivorNum; i++) {
			bwv2.write(array[i][0] + " " + array[i][1] +" " + array[i][2]+" " + array[i][3]);
			bwv2.newLine();
		}
		bwv2.close();

	}
	//***********vote ���� �޼ҵ� ��

	public void rand(int roles[], int playernum) {// ���� �迭 ���� ����
		Random rd = new Random();
		for (int i = playernum - 1; i > 0; i--) {
			int j = rd.nextInt(i + 1);
			int temp = roles[i];
			roles[i] = roles[j];
			roles[j] = temp;
		}
		System.out.println(Arrays.toString(roles));
	}

	public void setRoles() throws IOException {//���� ����

		int playerNum = survivorNum;
		int mafiaNum = 1;
		int doctorNum = 1;
		int copNum = 1;
		String[][] array = new String[survivorNum][4];
		String str[] = null;
		int count = 0;
		String s;

		// ���� �ʱ�ȭ
		// civilian = 0
		// mafia = 1
		// doctor = 2
		// cop = 3

		int[] roles = new int[playerNum];


		roles[0] = 1;
		roles[1] = 2;
		roles[2] = 3;


		for (int i = 3; i < playerNum; i++) {
			roles[i] = 0;// civilian
		}
		rand(roles, playerNum);

		File file = new File("clientInfo.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));

		while ((s = br.readLine()) != null) {// ������ ���� count�� ����
			count++;
		}
		br.close();

		str = new String[count];// �Ҵ�

		int i = 0;// ������ ����
		BufferedReader br2 = new BufferedReader(new FileReader(file));
		while ((s = br2.readLine()) != null) {
			str[i] = s;
			i++;
		}

		for (i = 0; i < playerNum; i++) {// �ʱ�ȭ
			for (int j = 0; j < 4; j++) {
				array[i][j] = "";

			}
		}
		for (i = 0; i < playerNum; i++) {// �迭�� ���� ���� ����
			s = str[i];
			String split[] = s.split(" ");

			array[i][0] = split[0];
			array[i][1] = split[1];
			array[i][2] = split[2];
			array[i][3] = split[3];
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.close();
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(file,true));

		int m = 0;
		// ���Ͽ� ����
		for (i = 0; i < playerNum; i++) {
			bw2.write(array[i][0] + " " + roles[i] + " 0" + " 0");
			bw2.newLine();
		}
		bw2.close();
	}   

	// ���� �÷��̾� �ִ��� ã�� â ����.
	public void checkDie() {
		try {
			BufferedReader fileReader = new BufferedReader(new FileReader("clientInfo.txt"));
			String str;
			int line = 0;

			System.out.println("Checking ... ");
			while ((str = fileReader.readLine()) != null) {
				String arr[] = str.split(" ");
				if (arr[2].equals("1")) {
					if(et.playerID == line + 1) {
						PrintWriter writer = null;
						try{
							writer = new PrintWriter(socket.getOutputStream(),true);
							writer.println("/die");	// protocol
							writer.flush();
						}catch(IOException ie){
							System.out.println(ie.getMessage());
						}
					}
				}
				line++;
			}

			fileReader.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}	  
	}

	/**
	 * �¸�
	 */


	/**
	 * �й�
	 */
}
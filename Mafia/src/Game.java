import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
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
 * 전체적인 게임이 진행되는 클래스.
 */
public class Game {
	Socket socket;
	Vector<Socket> player;
	int playerID; //해당플레이어 고유 식별 번호 1~
	String votedPlayer;//투표할 플레이어 저장용 (투표끝나고 초기화 필요)
	int totalPlayer;//전체 플레이어수
	int survivorNum; // 생존자 수
	int deadNum; // 죽은 사람 수
	long dayTime;
	long nightTime;
	int voteTime;
	int SV1;
	EchoThread et;

	public Game(Socket socket, Vector<Socket> player, int playerID) {
		this.socket = socket;
		this.player = player;
		this.playerID = playerID;

		et = new EchoThread(socket, player, playerID);
		System.out.println("In Game, 난 " + playerID + "번이야");
	}

	private void set() {
		survivorNum = player.size(); // 게임을 시작한 현재 게임 플레이어 수. 즉, 생존자 수.
		totalPlayer = player.size();
		deadNum = 0;
		dayTime = survivorNum * 10000; // 낮 시간 (생존자수*10초)
		nightTime = survivorNum * 10000; // 밤 시간 (생존자수*10초)
	}

	public void start() throws IOException {
		set();
		if(playerID == 1) { //역할 설정 처음 한번만
			setRoles();
			System.out.println("역할 설정할게");
		}
		
		et.view("당신의 playerID는 " + playerID + "번입니다.");	// 자기도 자기의 playerID를 알아야 하니까 처음에 알려줌.

		System.out.println("나는 게임의 플레이어 ID" + playerID);//player ID test

		jobAlert();

		Timer daytimeTimer = new Timer();
		TimerTask daytimeTask = new TimerTask() {
			@Override
			public void run() {
				PrintWriter writer = null;
				try{
					File file = new File("clientInfo.txt");
					BufferedReader br3 = new BufferedReader(new FileReader(file));
					String s ="";
					String[][] array = new String[survivorNum][4];
					String str[] = null;
					int count = 0;
					int mafiaNum = 0;
					int citizenNum = 0;
					int playerNum = survivorNum;
					str = new String[count];// 할당

					BufferedReader br = new BufferedReader(new FileReader(file));

					while ((s = br.readLine()) != null) {// 데이터 갯수 count에 저장
						count++;
					}
					br.close();

					str = new String[count];// 할당

					int i = 0;// 데이터 저장
					BufferedReader br2 = new BufferedReader(new FileReader(file));
					while ((s = br2.readLine()) != null) {
						str[i] = s;
						i++;
					}

					for (i = 0; i < playerNum; i++) {// 초기화
						for (int j = 0; j < 4; j++) {
							array[i][j] = "";

						}
					}
					for (i = 0; i < playerNum; i++) {// 배열에 파일 정보 저장
						s = str[i];
						String split[] = s.split(" ");

						array[i][0] = split[0];
						array[i][1] = split[1];
						array[i][2] = split[2];
						array[i][3] = split[3];
					}

					for (i = 0; i < playerNum; i++) {
						if(array[i][2].equals("0"))
						{
							SV1 = i;
							break;
						}
					}

					System.out.println(SV1+" Sv1 come");

					if(playerID == SV1+1) {
						mafiaVsDoc();//마피아 의사 능력 결과 반영
						checkWin(); //승리조건 확인
					}
					writer = new PrintWriter(socket.getOutputStream(),true);
					writer.println("/d");	// protocol
					writer.flush();
					
					checkDie();// 매 낮마다 죽은 사람 있는지 확인.

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
					File file = new File("clientInfo.txt");
					BufferedReader br3 = new BufferedReader(new FileReader(file));
					String s ="";
					String[][] array = new String[survivorNum][4];
					String str[] = null;
					int count = 0;
					int mafiaNum = 0;
					int citizenNum = 0;
					int playerNum = survivorNum;
					str = new String[count];// 할당

					BufferedReader br = new BufferedReader(new FileReader(file));

					while ((s = br.readLine()) != null) {// 데이터 갯수 count에 저장
						count++;
					}
					br.close();

					str = new String[count];// 할당

					int i = 0;// 데이터 저장
					BufferedReader br2 = new BufferedReader(new FileReader(file));
					while ((s = br2.readLine()) != null) {
						str[i] = s;
						i++;
					}

					for (i = 0; i < playerNum; i++) {// 초기화
						for (int j = 0; j < 4; j++) {
							array[i][j] = "";

						}
					}
					for (i = 0; i < playerNum; i++) {// 배열에 파일 정보 저장
						s = str[i];
						String split[] = s.split(" ");

						array[i][0] = split[0];
						array[i][1] = split[1];
						array[i][2] = split[2];
						array[i][3] = split[3];
					}

					for (i = 0; i < playerNum; i++) {
						if(array[i][2].equals("0"))
						{
							SV1 = i;
							break;
						}
					}

					System.out.println(SV1+" Sv1 come");
					if(playerID == SV1 +1)
					{	
						checkWin(); //승리조건 확인
						voteCalculater();
					}
					writer = new PrintWriter(socket.getOutputStream(),true);
					writer.println("/n");	// protocol
					writer.flush();
					
					checkDie();
					
				}catch(IOException ie){
					System.out.println(ie.getMessage());
				}

				System.out.println("나는 게임의 플레이어 ID"+ playerID  +"내가 투표한 사람은"+ votedPlayer);//투표 입력 받는지 test
			}
		};

		daytimeTimer.schedule(daytimeTask, 0, dayTime + nightTime);
		nightTimer.schedule(nightTimeTask, dayTime, dayTime + nightTime);

	}

	public void jobAlert() throws IOException {
		EchoThread et = new EchoThread(socket, player, playerID);
		int playerNum = survivorNum;
		String[][] array = new String[playerNum][4];
		String str[] = null;
		int count = 0;
		String s;

		File file = new File("clientInfo.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));

		while ((s = br.readLine()) != null) {// 데이터 갯수 count에 저장
			count++;
		}
		br.close();

		str = new String[count];// 할당

		int i = 0;// 데이터 저장
		BufferedReader br2 = new BufferedReader(new FileReader(file));
		while ((s = br2.readLine()) != null) {
			str[i] = s;
			i++;
		}

		for (i = 0; i < playerNum; i++) {// 초기화
			for (int j = 0; j < 4; j++) {
				array[i][j] = "";

			}
		}
		for (i = 0; i < playerNum; i++) {// 배열에 파일 정보 저장
			s = str[i];
			String split[] = s.split(" ");

			array[i][0] = split[0];
			array[i][1] = split[1];
			array[i][2] = split[2];
			array[i][3] = split[3];
		}

		// civilian = 0
		// mafia = 1
		// doctor = 2
		// cop = 3
		System.out.println("? " + playerID);
		if(array[playerID - 1][1].equals("0")) {
			System.out.println("0");
			et.view("당신의 직업은 시민입니다");

		}
		else if(array[playerID - 1][1].equals("1")) {

			System.out.println("1");
			et.view("당신의 직업은 마피아입니다.\n살인 능력을 쓰시려면 /kill playerID 형식으로 입력해주세요.");

		}
		else if(array[playerID - 1][1].equals("2")) {
			System.out.println("2");
			et.view("당신의 직업은 의사입니다.\n살리기 능력을 쓰시려면 /heal playerID 형식으로 입력해주세요.");

		}
		else if(array[playerID - 1][1].equals("3")) {
			System.out.println("3");
			et.view("당신의 직업은 경찰입니다.\n수사 능력을 쓰시려면 /check playerID 형식으로 입력해주세요.");

		}
	}

	//*********** vote 관련 메소드시작

	public void voteCount(String votedPlayer) throws IOException {//투표된 플레이어에게 득표수 +1

		int [][] array = new int [totalPlayer][4];
		String str[] = null;
		String s;
		int toVote = Integer.parseInt(votedPlayer);

		File file = new File("clientInfo.txt");

		int k = 0;
		str = new String[10];
		BufferedReader brv = new BufferedReader(new FileReader(file));//파일 내용 전체 저장
		while ((s = brv.readLine()) != null) {
			str[k] = s;
			k++;
		}

		for (int i = 0; i < totalPlayer; i++) {// 배열에 파일 정보 int 형태로 저장

			s = str[i];
			String split[] = s.split(" ");

			array[i][0] = Integer.parseInt(split[0]);
			array[i][1] = Integer.parseInt(split[1]);
			array[i][2] = Integer.parseInt(split[2]);;
			array[i][3] = Integer.parseInt(split[3]);
		}

		array[toVote-1][3]++;//득표수 +1
		brv.close();

		BufferedWriter bwv1 = new BufferedWriter(new FileWriter(file));//아마 파일 비우기
		bwv1.close();

		BufferedWriter bwv2 = new BufferedWriter(new FileWriter(file,true));
		for (int i = 0; i < totalPlayer; i++) {
			bwv2.write(array[i][0] + " " + array[i][1] +" " + array[i][2]+" " + array[i][3]);
			bwv2.newLine();
		}
		bwv2.close();

		//votedPlayer = null;//투표할 사람 초기화

	}

	public int voteResult()throws IOException {//당선된 사람 결정 동률은 동률중 랜덤으로 처리
		int [][] array = new int [totalPlayer][4];
		int [][] tieChecker = new int [totalPlayer][2];//동률 검사용 배열
		int maxVoteID;//최다 득표자 playerID(int 값이라 추후 string으로 변환해야 할 수도)

		String str[] = null;
		String s;
		File file = new File("clientInfo.txt");

		int k = 0;
		str = new String[10];
		BufferedReader brv = new BufferedReader(new FileReader(file));//파일 내용 전체 저장
		while ((s = brv.readLine()) != null) {
			str[k] = s;
			k++;
		}

		for (int i = 0; i < totalPlayer; i++) {// 배열에 파일 정보 int 형태로 저장

			s = str[i];
			String split[] = s.split(" ");

			array[i][0] = Integer.parseInt(split[0]);
			array[i][1] = Integer.parseInt(split[1]);
			array[i][2] = Integer.parseInt(split[2]);;
			array[i][3] = Integer.parseInt(split[3]);
		}
		brv.close();

		for (int i = 0; i < totalPlayer; i++) {// 배열 초기화
			tieChecker[i][0] = 0;


		}


		int tieNum = 0;//비긴사람수
		int max =array[0][3];//max값 초기화
		for (int i = 0; i < totalPlayer; i++) {// 각 플레이어 마다 득표수 비교
			if(array[i][3]> max) {
				max = array[i][3];

			}
		}
		for (int i = 0; i < totalPlayer; i++) {//동률check해서 동률일 경우 동률인것 중에 랜덤으로 
			if(array[i][3] == max){
				tieChecker[i][0] = array[i][3];
				tieNum ++;
			}
		}


		int randomSelect[] = new int[tieNum];
		int t = 0;
		for (int i = 0; i < totalPlayer; i++) {
			if (tieChecker[i][0]!=0) {
				randomSelect[t] = i;
				t++;
			}
		}
		if(randomSelect.length == 1) {
			maxVoteID = randomSelect[0];
		}
		else {
			int rmd = new Random().nextInt(randomSelect.length);
			maxVoteID = randomSelect[rmd];//동률일땐 동률중 랜덤으로 하나 아니면 그냥 저장.
		}
		return maxVoteID;//진짜 ID로 쓸려면 +1 필요 (지금은 배열 index값임)


	}

	public void killedByVote(int tokill) throws IOException {//죽일 사람 ID int 형태로 넘겨받아 파일로가서 그 사람 생존여부 사망처리(0= 생존, 1=사망)
		int [][] array = new int [totalPlayer][4];

		String str[] = null;
		String s;
		File file = new File("clientInfo.txt");

		int k = 0;
		str = new String[10];
		BufferedReader brv = new BufferedReader(new FileReader(file));//파일 내용 전체 저장
		while ((s = brv.readLine()) != null) {
			str[k] = s;
			k++;
		}

		for (int i = 0; i < totalPlayer; i++) {// 배열에 파일 정보 int 형태로 저장

			s = str[i];
			String split[] = s.split(" ");

			array[i][0] = Integer.parseInt(split[0]);
			array[i][1] = Integer.parseInt(split[1]);
			array[i][2] = Integer.parseInt(split[2]);;
			array[i][3] = Integer.parseInt(split[3]);
		}
		array[tokill][2] = 1;
		brv.close();

		BufferedWriter bwv1 = new BufferedWriter(new FileWriter(file));// 파일 비우기
		bwv1.close();

		BufferedWriter bwv2 = new BufferedWriter(new FileWriter(file,true));//파일열고 투표 결과 반영(죽이기)
		for (int i = 0; i < totalPlayer; i++) {
			bwv2.write(array[i][0] + " " + array[i][1] +" " + array[i][2]+" " + array[i][3]);
			bwv2.newLine();
		}
		bwv2.close();
	}

	public void voteReset()throws IOException {//투표 끝난뒤 득표수 0으로 초기화 해주는 메소드

		int [][] array = new int [totalPlayer][4];
		String str[] = null;
		String s;


		File file = new File("clientInfo.txt");

		int k = 0;
		str = new String[10];
		BufferedReader brv = new BufferedReader(new FileReader(file));//파일 내용 전체 저장
		while ((s = brv.readLine()) != null) {
			str[k] = s;
			k++;
		}

		for (int i = 0; i < totalPlayer; i++) {// 배열에 파일 정보 int 형태로 저장

			s = str[i];
			String split[] = s.split(" ");

			array[i][0] = Integer.parseInt(split[0]);
			array[i][1] = Integer.parseInt(split[1]);
			array[i][2] = Integer.parseInt(split[2]);;
			array[i][3] = Integer.parseInt(split[3]);
		}
		for (int i = 0; i < totalPlayer; i++) {//득표수 0으로 초기화

			array[i][3] = 0;
		}

		brv.close();

		BufferedWriter bwv1 = new BufferedWriter(new FileWriter(file));// 파일 비우기
		bwv1.close();

		BufferedWriter bwv2 = new BufferedWriter(new FileWriter(file,true));
		for (int i = 0; i < totalPlayer; i++) {
			bwv2.write(array[i][0] + " " + array[i][1] +" " + array[i][2]+" " + array[i][3]);
			bwv2.newLine();
		}
		bwv2.close();


	}
	public void voteCalculater()throws IOException {//파일을 열어 표를 집계한뒤 결과까지 총반영
		File file = new File("voteInfo.txt");
		BufferedReader brv = new BufferedReader(new FileReader(file));
		String str ="";
		while((str = brv.readLine()) != null) {
			voteCount(str);	
		}
		brv.close();
		new FileOutputStream("voteInfo.txt").close();//파일내용 초기화
		int elected = voteResult();
		killedByVote(elected);
		announceVoteResult(elected);
		
		voteReset();

	}
	public void announceVoteResult(int electedplayerID) {//당선 결과 알림
		electedplayerID = electedplayerID + 1;
		et.broadcast("플레이어 "+electedplayerID+" 님이 투표로 인해 사망하였습니다");
	}
	//***********vote 관련 메소드 끝
	
	public void mafiaVsDoc() throws IOException{//마피아 의사 능력결과
		File file1 = new File("mafiavoteInfo.txt");
		File file2 = new File("docvoteInfo.txt");
		BufferedReader brv1 = new BufferedReader(new FileReader(file1));
		BufferedReader brv2 = new BufferedReader(new FileReader(file2));
		int mafiaKilled;
		String str1 ="";
		String str2 ="";
		String count = "";
		
		while((count = brv1.readLine()) != null) {
			str1= brv1.readLine();
		}
		
		while((count = brv2.readLine()) != null) {
			str2= brv1.readLine();
		}
		
		if(str1.equals(str2)&& (file1.length()!= 0) && (file2.length()!= 0) ) {
			et.broadcast("의느님이 플레이어 "+ str1+ " 님을 살렸습니다!" );
		}
		else if((str1.equals(str2) == false)&& (file1.length()!= 0)){
			 et.broadcast("마피아가 "+ str1+ " 님을 살해했습니다!" );
			mafiaKilled = Integer.parseInt(str1);
			mafiaResult(mafiaKilled);
		}
		 brv1.close();
		 brv2.close();
		
		
	}
	
	public void mafiaResult(int mafiKilled)throws IOException{//마피아 능력 결과 반영
		int [][] array = new int [totalPlayer][4];
		String str[] = null;
		String s;


		File file = new File("clientInfo.txt");

		int k = 0;
		str = new String[10];
		BufferedReader brv = new BufferedReader(new FileReader(file));//파일 내용 전체 저장
		while ((s = brv.readLine()) != null) {
			str[k] = s;
			k++;
		}

		for (int i = 0; i < totalPlayer; i++) {// 배열에 파일 정보 int 형태로 저장

			s = str[i];
			String split[] = s.split(" ");

			array[i][0] = Integer.parseInt(split[0]);
			array[i][1] = Integer.parseInt(split[1]);
			array[i][2] = Integer.parseInt(split[2]);;
			array[i][3] = Integer.parseInt(split[3]);
		}
		array[mafiKilled-1][2] = 1;//죽임처리

		brv.close();

		BufferedWriter bwv1 = new BufferedWriter(new FileWriter(file));// 파일 비우기
		bwv1.close();

		BufferedWriter bwv2 = new BufferedWriter(new FileWriter(file,true));
		for (int i = 0; i < totalPlayer; i++) {
			bwv2.write(array[i][0] + " " + array[i][1] +" " + array[i][2]+" " + array[i][3]);
			bwv2.newLine();
		}
		bwv2.close();

	}
	

	public void rand(int roles[], int playernum) {// 역할 배열 랜덤 섞기
		Random rd = new Random();
		for (int i = playernum - 1; i > 0; i--) {
			int j = rd.nextInt(i + 1);
			int temp = roles[i];
			roles[i] = roles[j];
			roles[j] = temp;
		}
		System.out.println(Arrays.toString(roles));
	}

	public void setRoles() throws IOException {//역할 설정

		int playerNum = totalPlayer;
		int mafiaNum = 1;
		int doctorNum = 1;
		int copNum = 1;
		String[][] array = new String[totalPlayer][4];
		String str[] = null;
		int count = 0;
		String s;

		// 역할 초기화
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
		int i = 0;
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));

		// 파일에 쓰기
		for (i = 0; i < playerNum; i++) {
			bw.write(i + 1 + " " + roles[i] + " 0" + " 0");
			bw.newLine();
		}
		bw.close();
	}

	// 죽은 플레이어 있는지 찾고 창 종료.
	public void checkDie() {
		try {
			BufferedReader fileReader = new BufferedReader(new FileReader("clientInfo.txt"));
			String str;
			int line = 0;

			System.out.println("Checking ... ");
			while ((str = fileReader.readLine()) != null) {
				String arr[] = str.split(" ");
				if (arr[2].equals("1")) {
					if(playerID == line + 1) {
						PrintWriter writer = null;
						try{
							writer = new PrintWriter(socket.getOutputStream(),true);
							writer.println("/die " + playerID);	// protocol
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

	public void checkWin() {
		PrintWriter writer = null;
		try{
			File file = new File("clientInfo.txt");
			BufferedReader br3 = new BufferedReader(new FileReader(file));
			String s ="";
			String[][] array = new String[survivorNum][4];
			String str[] = null;
			int count = 0;
			int mafiaNum = 0;
			int citizenNum = 0;
			int playerNum = survivorNum;
			str = new String[count];// 할당

			BufferedReader br = new BufferedReader(new FileReader(file));

			while ((s = br.readLine()) != null) {// 데이터 갯수 count에 저장
				count++;
			}
			br.close();

			str = new String[count];// 할당

			int i = 0;// 데이터 저장
			BufferedReader br2 = new BufferedReader(new FileReader(file));
			while ((s = br2.readLine()) != null) {
				str[i] = s;
				i++;
			}

			for (i = 0; i < playerNum; i++) {// 초기화
				for (int j = 0; j < 4; j++) {
					array[i][j] = "";

				}
			}
			for (i = 0; i < playerNum; i++) {// 배열에 파일 정보 저장
				s = str[i];
				String split[] = s.split(" ");

				array[i][0] = split[0];
				array[i][1] = split[1];
				array[i][2] = split[2];
				array[i][3] = split[3];
			}

			for (i = 0; i < playerNum; i++)
			{
				if(array[i][1].equals("1") && array[i][2].equals("0"))
				{
					mafiaNum++;
				}
				else if(!array[i][1].equals("1") && array[i][2].equals("0"))
				{
					citizenNum++;
				}
			}

			if(mafiaNum == 0)
			{
				writer = new PrintWriter(socket.getOutputStream(),true);
				writer.println("victory citizen");
				writer.flush();
				//et.broadcast("victory citizen");

				System.exit(0);
			}
			else if(mafiaNum >= citizenNum)
			{
				//et.broadcast("victory mafia");
				writer = new PrintWriter(socket.getOutputStream(),true);
				writer.println("victory mafia");
				writer.flush();

				System.exit(0);
			}
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}
	}
}
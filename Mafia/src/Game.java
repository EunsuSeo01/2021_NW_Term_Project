import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;
import java.util.Arrays;

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
   int[] mafiaId;
   long dayTime;
   long nightTime;
   int voteTime;

   public Game(Socket socket, Vector<Socket> player, int playerID) {
	   
      this.socket = socket;
      this.player = player;
      this.playerID = playerID;
   }

   private void set() {
      survivorNum = player.size(); // ������ ������ ���� ���� �÷��̾� ��. ��, ������ ��.
      deadNum = 0;
      mafiaId = new int[survivorNum / 4];
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
      EchoThread et = new EchoThread(socket, player, playerID);
      
      Timer daytimeTimer = new Timer();
      TimerTask daytimeTask = new TimerTask() {
         @Override
         public void run() {
        	 PrintWriter writer = null;
     		try{
     			writer = new PrintWriter(socket.getOutputStream(),true);
     			writer.println("/d");
				writer.flush();
     		}catch(IOException ie){
    			System.out.println(ie.getMessage());
    		}
         }
      };

      Timer nightTimer = new Timer();
      TimerTask nightTimeTask = new TimerTask() {
         @Override
         public void run() {
             et.view("<System> ���� �Ǿ����ϴ�. ��ǥ�� �����մϴ�");
             
        	 System.out.println("���� ������ �÷��̾� ID"+ playerID +"���� ��ǥ�� �����"+ votedPlayer);//��ǥ �Է� �޴��� test
         }
      };

      daytimeTimer.schedule(daytimeTask, 0, dayTime + nightTime + voteTime);
      nightTimer.schedule(nightTimeTask, dayTime + voteTime, dayTime + nightTime + voteTime);

   }
   

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

   public void setRoles() throws IOException {

      int playerNum = survivorNum;
      int mafiaNum = playerNum / 4;
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

      roles[0] = 2;
      roles[1] = 3;

      for (int i = 2; i < mafiaNum+2; i++) {
         roles[i] = 1;// mafia
      }

      for (int i = 2 + mafiaNum; i < playerNum; i++) {
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
         
         // ���Ǿ��� ������� ��ȣ�� ���� �����صα� ����.
         if (roles[i] == 1) {
        	 mafiaId[m] = i + 1;
        	 System.out.println("I'm mafia " + mafiaId[m]);	// test
        	 m++;
         }
      }
      bw2.close();
   }

   
   
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
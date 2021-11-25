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
 * 전체적인 게임이 진행되는 클래스.
 */
public class Game {
   Socket socket;
   Vector<Socket> player;
   int survivorNum; // 생존자 수
   int deadNum; // 죽은 사람 수
   long dayTime;
   long nightTime;
   int voteTime;

   public Game(Socket socket, Vector<Socket> player) {
      this.socket = socket;
      this.player = player;
   }

   private void set() {
      survivorNum = player.size(); // 게임을 시작한 현재 게임 플레이어 수. 즉, 생존자 수.
      deadNum = 0;
      dayTime = survivorNum * 1000; // 낮 시간 (생존자수*15초)
      nightTime = survivorNum * 1000; // 밤 시간 (생존자수*15초)
      voteTime = 15000; // 투표시간 15초
   }

   public void start() throws IOException {
      set();
      setRoles();
      EchoThread et = new EchoThread(socket, player);
      Timer daytimeTimer = new Timer();
      TimerTask daytimeTask = new TimerTask() {
         @Override
         public void run() {
            et.broadcast("<System> 낮이 되었습니다. 토론을 시작하세요.");
         }
      };

      Timer votetimeTimer = new Timer();
      TimerTask votetimeTask = new TimerTask() {
         @Override
         public void run() {
            et.broadcast("<System> 투표를 시작합니다");
            // vote();
         }
      };

      Timer nightTimer = new Timer();
      TimerTask nightTimeTask = new TimerTask() {
         @Override
         public void run() {
            et.broadcast("<System> 밤이 되었습니다. 투표를 시작합니다");
         }
      };

      daytimeTimer.schedule(daytimeTask, 0, dayTime + nightTime + voteTime);
      votetimeTimer.schedule(votetimeTask, dayTime, dayTime + nightTime + voteTime);
      nightTimer.schedule(nightTimeTask, dayTime + voteTime, dayTime + nightTime + voteTime);

   }

   // vote 메소드
   public void vote() {// 테스트용

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

   public void setRoles() throws IOException {

      int playerNum = survivorNum;
      int mafiaNum = playerNum / 4;
      int doctorNum = 1;
      int copNum = 1;
      String[][] array = new String[survivorNum][4];
      String str[] = null;
      int count = 0;
      String s;

      // 역할 초기화
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
      BufferedWriter bw = new BufferedWriter(new FileWriter(file));
      bw.close();
      BufferedWriter bw2 = new BufferedWriter(new FileWriter(file,true));

      // 파일에 쓰기
      for (i = 0; i < playerNum; i++) {
         bw2.write(array[i][0] + " " + roles[i] + " 0" + " 0");
         bw2.newLine();
      }
      bw2.close();
   }

   /**
    * 투표 기능. -> 효영님
    */

   /**
    * 직업별 능력 사용 기능. -> protocol 사용. 예) !heal nickname
    */

   /**
    * 승리
    */

   /**
    * 패배
    */
}
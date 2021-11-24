
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

/**
 * 전체적인 게임이 진행되는 클래스.
 * 현재 - 게임 초기화 & 타이머 기능 구현.
 */
public class Game {
	Socket socket;
	Vector<Socket> player;
	int survivorNum;	// 생존자 수
	int deadNum;	// 죽은 사람 수
	long daytime;
	long night;
	int voteTime;
	
	public Game(Socket socket, Vector<Socket> player) {
		this.socket = socket;
		this.player = player;
	}

	private void set() {
		survivorNum = player.size();	// 게임을 시작한 현재 게임 플레이어 수. 즉, 생존자 수.
		deadNum = 0;
		daytime = survivorNum * 15000;	// 낮 시간 (생존자수*15초)
		voteTime = 15000;	// 밤 시간 (= 투표시간 15초)
	}

	public void start() {
		set();
		EchoThread et = new EchoThread(socket, player);
		Timer daytimeTimer = new Timer();
		TimerTask daytimeTask = new TimerTask(){
			@Override
			public void run() {
				et.broadcast("낮이 되었습니다. 토론을 시작하세요.");
			}
		};
		
		Timer votetimeTimer = new Timer();
		TimerTask votetimeTask = new TimerTask(){
			@Override
			public void run() {
				et.broadcast("투표를 시작니합니다");
			    //vote();
			}
		};
		
		Timer nightTimer = new Timer();
		TimerTask nightTimeTask = new TimerTask() {
			@Override
			public void run() {
				et.broadcast("밤이 되었습니다. 마피아들은 죽일사람을 골라주세요");
			}
		};

		daytimeTimer.schedule(daytimeTask, 0, daytime + night + voteTime);	
		votetimeTimer.schedule(votetimeTask, daytime, daytime + night + voteTime);		
		nightTimer.schedule(nightTimeTask, daytime+voteTime, daytime + night + voteTime);
		
	}
	
	//vote 메소드
	public void vote() {//테스트용
		
		
		
	}

	/**
	 * 직업 랜덤 설정 기능. -> 민서님
	 */

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
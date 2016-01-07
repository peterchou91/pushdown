package sctl.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TCPConnection {
	private ServerSocket ss;
	private Socket socket;
	private ArrayList<String> sendMsgs;
//	private ArrayList<String> recvMsgs;
	private SendThread sendThread;
//	private RecvThread recvThread;
	private Object so = new Object();

	// private String ipaddr;
	// private int port;

	public TCPConnection(int port) throws IOException {
		// this.ipaddr = ipaddr;
		// this.port = port;
		this.ss = new ServerSocket(3333);
		sendMsgs = new ArrayList<String>();
		sendThread = new SendThread(this);
//		recvMsgs = new ArrayList<String>();
	}
	
	public void startSendThread() {
		new Thread(sendThread).start();
		System.out.println("send message thread start");
	}
	
	public String getSendMsg() throws InterruptedException {
		synchronized (sendMsgs) {
			if(sendMsgs.isEmpty()) {
				sendMsgs.wait();
			}
			return sendMsgs.remove(0);
			
		}
	}
	
	public void addSendMsg(String str) {
		synchronized (sendMsgs) {
			sendMsgs.add(str);
			sendMsgs.notify();
		}
	}
	

	public boolean connect() {
		try {
			System.out.println("trying to find a client");
			socket = ss.accept();
			System.out.println("client accepted");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void sendMsg(String str) throws IOException {
		PrintWriter bw = new PrintWriter(socket.getOutputStream());
		bw.print(str);
		bw.flush();
//		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//		System.out.println(br.readLine());
	}
	
	public static void main(String[] args) {
		try {
			TCPConnection conn = new TCPConnection(6000);
			conn.connect();
			conn.startSendThread();
//			conn.sendMsg("helloworld");
			conn.addSendMsg("helloworld\nelle\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class SendThread implements Runnable {
	private TCPConnection conn;
	
	public SendThread(TCPConnection conn) {
		this.conn = conn;
	}

	@Override
	public void run() {
		while(true) {
			try {
				String msg = conn.getSendMsg();
				conn.sendMsg(msg);
				
				System.out.println("message sent");
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}

class RecvThread implements Runnable {

	@Override
	public void run() {
		
	}
	
}

package connection;

import java.net.Socket;
import behavior.RemotePeerInfo;

public class IncomingRequest implements Runnable{
	private Socket socket;
	private RemotePeerInfo remotePeerInfo;
	boolean b = true;
	
	
	
	public IncomingRequest(Socket socket, RemotePeerInfo remotePeerInfo) {
		this.remotePeerInfo = remotePeerInfo;
		this.socket = socket;
	}
	
	@Override
	public void run() {
		System.out.println("There are a incoming request from peer" + this.remotePeerInfo.getPeerID());
		connectionPeer connectionpeer = new connectionPeer(this.remotePeerInfo, this.socket); 
		try {
			connectionpeer.startCommunicating();
		}
		catch(Exception e){
			throw new RuntimeException("Wrong starting message exchange in incoming request from " + this.remotePeerInfo.getPeerID(), e);
		}
		
		
	}
	
}

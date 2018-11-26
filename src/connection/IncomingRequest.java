package connection;

import java.io.IOException;
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
		try {
			peer.getPeerInstance().getLog().logTCPFrom(this.remotePeerInfo.getPeerID());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("There is an incoming request from peer " + this.remotePeerInfo.getPeerID());
		connectionPeer connectionpeer = new connectionPeer(this.remotePeerInfo, this.socket); 
		try {
			connectionpeer.startCommunicating();
		}
		catch(Exception e){
			throw new RuntimeException("Wrong starting message exchange in incoming request from " + this.remotePeerInfo.getPeerID(), e);
		}
	}
}

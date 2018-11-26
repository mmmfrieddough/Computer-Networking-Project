package connection;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import behavior.RemotePeerInfo;

public class outcomingRequest implements Runnable{
	
	private RemotePeerInfo remotePeerInfo;
	Socket socket;
	BufferedInputStream in;
	BufferedOutputStream out;
	
	public outcomingRequest(RemotePeerInfo peerRemoteInfo) {
		this.remotePeerInfo = peerRemoteInfo;
	}
	@Override
	public void run() {
		try {
			peer.getPeerInstance().getLog().logTCPTo(this.remotePeerInfo.getPeerID());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("There is an outgoing request to peer " + this.remotePeerInfo.getPeerID());
		connectionPeer connectionpeer = new connectionPeer(this.remotePeerInfo);
		
		try {
			connectionpeer.startCommunicating();
		}
		catch(Exception e) {
			throw new RuntimeException("Wrong starting message exchange in outcoming request from " + this.remotePeerInfo.getPeerID(), e);
		}
	}
}

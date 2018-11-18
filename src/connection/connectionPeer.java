package connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.BitSet;
import java.util.Map;

import Message.handshake;
import Message.message;
import behavior.RemotePeerInfo;

public class connectionPeer {
	RemotePeerInfo remotePeer;
	Socket socket;
	handshake Handshake;
	BufferedOutputStream out;
	BufferedInputStream in;
	int receivedPiece;
	Long downloadStart;
	Long downloadEnd;
	boolean flag;
	
	public connectionPeer(RemotePeerInfo RemotePeer) {
		this.remotePeer = RemotePeer;
		this.socket = null;
		initSocket();
	}
	
	public connectionPeer(RemotePeerInfo RemotePeer, Socket socket) {
		this.remotePeer = RemotePeer;
		this.socket = socket;
		initSocket();
	}
	

	private void initSocket() {
		// TODO Auto-generated method stub
		try {
			if(this.socket==null) {
				this.socket = new Socket(InetAddress.getByName(this.remotePeer.getPeerAddress()), this.remotePeer.get_portNum());
			}
			this.out = new BufferedOutputStream(this.socket.getOutputStream());
			this.remotePeer.bufferedOutputStream = this.out;
			this.out.flush();
			this.in = new BufferedInputStream(this.socket.getInputStream());
			this.remotePeer.bufferedInputStream = this.in;
			
		}
		catch(IOException e) {
			throw new RuntimeException("Not able to open client socket", e);
		}
		
		this.Handshake = new handshake();   //TODO
		
		try {
			this.Handshake.sendHandshakeMSG(this.out);
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		try {
			this.Handshake.recieveHandshakeMSG(this.in);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void startCommunicating() throws Exception{
		message MSG = null;    //message
		byte[] pieceIndexField = null;
		if(Peer.getPeer);    //TODO
			
			
		while(true) {
			
		}
			
			
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

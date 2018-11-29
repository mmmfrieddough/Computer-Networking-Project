package Message;

import fileIO.config;
import Message.MessageUtil;
import behavior.RemotePeerInfo;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Arrays;


import com.sun.istack.internal.ByteArrayDataSource;

public class handshake {
	private  String header;
	private String zero_bit;
	private int peer_ID;
	private RemotePeerInfo remotePeerInfo;

	public handshake(int peer_ID, RemotePeerInfo remotePeerInfo) {
		this.header = config.HandShakeHeader; 
		this.zero_bit = config.zerobit;
		this.peer_ID = peer_ID;
		this.remotePeerInfo = remotePeerInfo;
	}
	
	public String handshakeMessage() {
		return this.header + this.zero_bit + String.valueOf(this.peer_ID);
	}
	
	public void sendHandshakeMSG(BufferedOutputStream out) throws IOException{
		System.out.println("Sent handshake to " + this.remotePeerInfo.getPeerID());
		byte[] handshakeMsg = MessageUtil.concatenateByteArrays(MessageUtil.concatenateByteArrays(this.header.getBytes(),
                        this.zero_bit.getBytes()), 
                MessageUtil.intToByteArray(this.peer_ID));
		
       out.write(handshakeMsg);
       out.flush();
	}
	
	

	public boolean recieveHandshakeMSG(BufferedInputStream in) throws IOException{
		byte[] b = new byte[32];
        in.read(b);
        byte[] copyOfRange = Arrays.copyOfRange(b, 28, 32);
        byte[] header = Arrays.copyOfRange(b, 0, 18);
        int peerId = MessageUtil.byteArrayToInt(copyOfRange);
        String s = new String(header);
        if(s.equals(config.HandShakeHeader)&&(this.remotePeerInfo.getPeerID()==peerId)){
        	System.out.println("Received handshake from " + peerId);
        	return true;
        }
        return false;
	}
	
	
}


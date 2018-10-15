package Message;

import behavior.constant;
import sun.misc.MessageUtils;
import behavior.RemotePeerInfo;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Arrays;


import com.sun.istack.internal.ByteArrayDataSource;

public class handshake {
	private  String header;
	private String zero_bit;
	private byte[] peer_ID;
	private RemotePeerInfo remotePeerInfo;
	
	
	public handshake() {
		this.header = constant.protocolID; 
		this.zero_bit = constant.zero_bit;
		this.peer_ID = constant.peer_ID;
		this.remotePeerInfo = remotePeerInfo;
	}
	
	
	public String handshakeMessage() {
		return this.header + this.zero_bit + String.valueOf(this.peer_ID);
		
	}
	
	public void sendHandshakeMSG(BufferedOutputStream out) throws IOException{
		byte[] handshakeMsg = MessageUtil.concatenateByteArrays(MessageUtil.concatenateByteArrays(this.header.getBytes(),
                        this.zero_bit.getBytes()), 
                this.peer_ID);
		
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
        if(s.equals(constant.protocolID)&&(this.remotePeerInfo.get_peer_ID()==peerId)){
        	return true;
        }
        return false;
	}
	
	
}


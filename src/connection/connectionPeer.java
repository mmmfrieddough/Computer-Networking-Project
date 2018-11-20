package connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.BitSet;
import java.util.Map;

import Message.MessageUtil;
import Message.handshake;
import Message.message;
import behavior.RemotePeerInfo;
import fileIO.dataFile;


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
		
		this.Handshake = new handshake(peer.getPeerInstance().getPeerID(), this.remotePeer);   //TODO
		
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
		if(!peer.getPeerInstance().getBitSet().isEmpty()){
			MSG = connectionPeerHelper.sendBitSetMSG(this.out);
		}
		
		while(true) {
			byte MSGType = connectionPeerHelper.getMSGType(this.in);
			byte[] msgPayloadReceived = connectionPeerHelper.getActualMessage(this.in);
			if(this.flag && MSGType!=(byte)7) {
				this.downloadStart = 0L;
			}
			if(MSGType==(byte) 7) {
				if(MSGType==(byte) 7 || MSGType==(byte) 4) {                  //redundant, but not sure
					pieceIndexField = new byte[4];
					for(int i=0;i<4;i++) {
						pieceIndexField[i] = msgPayloadReceived[i];
					}
				}
			}
			switch(MSGType) {
			
			//choke
			case (byte) 0:{
				while(this.in.available()==0) {
					break;
				}
			}
			
			//unchoke
			case (byte) 1:{
				int pieceIndex = MessageUtil.byteArrayToInt(connectionPeerHelper.getPieceIndex(this.remotePeer));
				if(pieceIndex!=-1) {
					MSG = connectionPeerHelper.sendRequestMSG(this.out, this.remotePeer);
					this.downloadStart = System.nanoTime();
					this.flag = true;
				}
				
				if(pieceIndex==1) {
					MSG = connectionPeerHelper.sendNotInterestedMSG(this.out);
				}
				break;
			}
			
			//interested
			case (byte) 2:{
				peer.getPeerInstance().peersInterested.putIfAbsent(this.remotePeer.getPeerID(), this.remotePeer);
				int haveIndexField = connectionPeerHelper.compare(peer.getPeerInstance().getBitSet(), this.remotePeer.getbitField());
				connectionPeerHelper.sendHaveMSG(this.out, haveIndexField);
				break;
			}
			
			//not interested
			case (byte) 3:{
				if(peer.getPeerInstance().peersInterested.containsKey(this.remotePeer.getPeerID())) {
					peer.getPeerInstance().peersInterested.remove(this.remotePeer.getPeerID());
				}
			}
			
			//have
			case (byte) 4:{
				if(peer.getPeerInstance().NeighborPreferred.containsKey(this.remotePeer)
						|| peer.getPeerInstance().getBest()==this.remotePeer) {
					connectionPeerHelper.sendRequestMSG(this.out, this.remotePeer);
				}
				else {
					connectionPeerHelper.sendNotInterestedMSG(this.out);
				}
			}
			
			//bitfield
			case (byte) 5:{
				BitSet bitfield = MessageUtil.fromByteArraytoBitSet(msgPayloadReceived);
				if(connectionPeerHelper.isInterested(bitfield, peer.getPeerInstance().getBitSet())) {
					MSG = connectionPeerHelper.sendInterestedMSG(this.out);
				}
				else {
					MSG = connectionPeerHelper.sendNotInterestedMSG(this.out);
				}
				break;	
			}
			
			//request
			case (byte) 6:{
				if(peer.getPeerInstance().NeighborPreferred.containsKey(this.remotePeer)
						|| peer.getPeerInstance().getBest() == this.remotePeer) {
					connectionPeerHelper.sendPieceMSG(this.out, MessageUtil.byteArrayToInt(msgPayloadReceived));
					this.downloadEnd = System.nanoTime();
					this.remotePeer.setDownloadRate(this.downloadEnd-this.downloadStart);
					break;
				}
			}
			
			//piece
			case (byte) 7:{
				dataFile.writeFilePiece(MessageUtil.byteArrayToInt(pieceIndexField), this.in);
				peer.getPeerInstance().getBitSet().set(MessageUtil.byteArrayToInt(pieceIndexField));
				int piecesNum = peer.getPeerInstance().getBitSet().cardinality();
				connectionPeerHelper.sendRequestMSG(this.out, this.remotePeer);
				break;
			}

			}
		}
		
	}
	
	
}

package connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;

import Message.MessageUtil;
import Message.handshake;
import Message.message;
import behavior.RemotePeerInfo;
import fileIO.dataFile;
import processes.peerProcess;

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
		try {
			if(this.socket == null) {
				this.socket = new Socket(InetAddress.getByName(this.remotePeer.getPeerAddress()), this.remotePeer.get_portNum());
			}
			this.out = new BufferedOutputStream(this.socket.getOutputStream());
			this.remotePeer.bufferedOutputStream = this.out;
			this.out.flush();
			this.in = new BufferedInputStream(this.socket.getInputStream());
			this.remotePeer.bufferedInputStream = this.in;
			
		}
		catch(IOException e) {
			//throw new RuntimeException("Not able to open client socket", e);
			e.printStackTrace();
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
		System.out.println("Started communicating with peer");
		message MSG = null;    //message
		byte[] pieceIndexField = null;
		if(!peer.getPeerInstance().getBitSet().isEmpty()){
			MSG = connectionPeerHelper.sendBitSetMSG(this.out);
		}
		
		while(true) {
			byte MSGType = connectionPeerHelper.getMSGType(this.in);
			byte[] msgPayloadReceived = null;
			this.downloadStart = 0L;
			//byte[] msgPayloadReceived = connectionPeerHelper.getActualMessage(this.in);
			if(this.flag && MSGType!=(byte)7) {
				this.downloadStart = 0L;
			}
			if (MSGType==(byte) 4 || MSGType==(byte) 5 || MSGType==(byte) 6 || MSGType==(byte) 7) {
				msgPayloadReceived = connectionPeerHelper.getActualMessage(this.in);
			}
			else {
				byte[] b = new byte[in.available()];
				in.read(b);
			}
			if(MSGType==(byte) 7 || MSGType==(byte) 4) {
				pieceIndexField = new byte[4];
				for(int i=0;i<4;i++) {
					pieceIndexField[i] = msgPayloadReceived[i];
				}
			}
			switch(MSGType) {
			
			//choke
			case (byte) 0:{
				System.out.println("Received choke");
				peer.getPeerInstance().getLog().logChoked(remotePeer.getPeerID());
//				while(this.in.available()==0) {
//					break;
//				}
				break;
			}
			
			//unchoke
			case (byte) 1:{
				System.out.println("Received unchoke");
				peer.getPeerInstance().getLog().logUnchoked(remotePeer.getPeerID());
				if(connectionPeerHelper.isInterested(peer.getPeerInstance().getBitSet(), this.remotePeer.getbitField())) {
					int pieceIndex = connectionPeerHelper.getFirstDifference(peer.getPeerInstance().getBitSet(), this.remotePeer.getbitField());
					MSG = connectionPeerHelper.sendRequestMSG(this.out, pieceIndex);
					this.downloadStart = System.nanoTime();
					this.flag = true;
				}
				else {
					MSG = connectionPeerHelper.sendNotInterestedMSG(this.out);
				}
				break;
			}
			
			//interested
			case (byte) 2:{
				System.out.println("Received interested");
				peer.getPeerInstance().getLog().logInterested(remotePeer.getPeerID());
				peer.getPeerInstance().peersInterested.putIfAbsent(this.remotePeer.getPeerID(), this.remotePeer);
//				if (connectionPeerHelper.compare(peer.getPeerInstance().getBitSet(), this.remotePeer.getbitField()) == -1) {
//					int haveIndexField = connectionPeerHelper.getFirstDifference(peer.getPeerInstance().getBitSet(), this.remotePeer.getbitField());
//					connectionPeerHelper.sendHaveMSG(this.out, haveIndexField);
//				}
				break;
			}
			
			//not interested
			case (byte) 3:{
				System.out.println("Received not interested");
				peer.getPeerInstance().getLog().logNotInterested(remotePeer.getPeerID());
				if(peer.getPeerInstance().peersInterested.containsKey(this.remotePeer.getPeerID())) {
					peer.getPeerInstance().peersInterested.remove(this.remotePeer.getPeerID());
				}
				break;
			}
			
			//have
			case (byte) 4:{
				System.out.println("Received have for piece " + MessageUtil.byteArrayToInt(pieceIndexField));
				remotePeer.setBitField(MessageUtil.byteArrayToInt(pieceIndexField));
				if (connectionPeerHelper.isInterested(peer.getPeerInstance().getBitSet(), this.remotePeer.getbitField())) {
					connectionPeerHelper.sendInterestedMSG(this.out);
				}
				else {
					connectionPeerHelper.sendNotInterestedMSG(this.out);
				}
				//remotePeer.setBitField(MessageUtil.fromByteArraytoBitSet(pieceIndexField | MessageUtil.(remotePeer.getbitField())));
//				peer.getPeerInstance().getLog().logHave(remotePeer.getPeerID(), MessageUtil.byteArrayToInt(pieceIndexField));
//				if(peer.getPeerInstance().NeighborPreferred.containsKey(this.remotePeer)
//						|| peer.getPeerInstance().getBest() == this.remotePeer) {
//					//connectionPeerHelper.sendRequestMSG(this.out, this.remotePeer);
//					connectionPeerHelper.sendRequestMSG(this.out, MessageUtil.byteArrayToInt(pieceIndexField));
//				}
//				else {
//					connectionPeerHelper.sendNotInterestedMSG(this.out);
//				}
				break;
			}
			
			//bitfield
			case (byte) 5:{
				System.out.println("Received bitfield");
				BitSet bitfield = MessageUtil.fromByteArraytoBitSet(msgPayloadReceived);
				System.out.println("Received: " + bitfield.toString());
				this.remotePeer.setBitSet(bitfield);
				if(connectionPeerHelper.isInterested(peer.getPeerInstance().getBitSet(), bitfield)) {
					MSG = connectionPeerHelper.sendInterestedMSG(this.out);
				}
				else {
					MSG = connectionPeerHelper.sendNotInterestedMSG(this.out);
				}
				break;	
			}
			
			//request
			case (byte) 6:{
				System.out.println("Received request for part " + MessageUtil.byteArrayToInt(msgPayloadReceived));
				if(peer.getPeerInstance().NeighborPreferred.containsKey(this.remotePeer)
						|| peer.getPeerInstance().getBest() == this.remotePeer) {
					connectionPeerHelper.sendPieceMSG(this.out, MessageUtil.byteArrayToInt(msgPayloadReceived));
					this.downloadEnd = System.nanoTime();
					this.remotePeer.setDownloadRate(this.downloadEnd-this.downloadStart);
					break;
				}
				break;
			}
			
			//piece
			case (byte) 7:{
				System.out.println("Received piece " + MessageUtil.byteArrayToInt(pieceIndexField));
				byte[] fileData = Arrays.copyOfRange(msgPayloadReceived, 4, msgPayloadReceived.length);
				dataFile.writeFilePiece(MessageUtil.byteArrayToInt(pieceIndexField), fileData);
				peer.getPeerInstance().getBitSet().set(MessageUtil.byteArrayToInt(pieceIndexField));
				peer.getPeerInstance().getLog().logDownloadedPiece(remotePeer.getPeerID(), MessageUtil.byteArrayToInt(pieceIndexField), peer.getPeerInstance().getBitSet().cardinality());
				if (peer.getPeerInstance().getBitSet().cardinality() == peer.getPeerInstance().getPieceCount()) {
					dataFile.mergeFile();
					peer.getPeerInstance().getLog().logDownloadCompletion();
				}
				if (connectionPeerHelper.isInterested(peer.getPeerInstance().getBitSet(), remotePeer.getbitField()) && true /* TODO Change later to unchoked*/) {
					MSG = connectionPeerHelper.sendRequestMSG(this.out, connectionPeerHelper.getFirstDifference(peer.getPeerInstance().getBitSet(), this.remotePeer.getbitField()));
				}
				connectionPeerHelper.sendHaveMSG(this.out, MessageUtil.byteArrayToInt(pieceIndexField));
				//connectionPeerHelper.sendRequestMSG(this.out, this.remotePeer);
				break;
			}

			}
			
			boolean newFlag = true;
			for (RemotePeerInfo remotePeerCheck : peer.getPeerInstance().getPeerConnectTo().values()) {
				if (remotePeerCheck.getbitField().cardinality() != peer.getPeerInstance().getPieceCount()) {
					newFlag = false;
				}
				System.out.println("Remote peer " + remotePeerCheck.getPeerID() + " has " + remotePeerCheck.getbitField().cardinality() + "/" + peer.getPeerInstance().getPieceCount());
			}
			for (RemotePeerInfo remotePeerCheck : peer.getPeerInstance().getPeerExpectConnectFrom().values()) {
				if (remotePeerCheck.getbitField().cardinality() != peer.getPeerInstance().getPieceCount()) {
					newFlag = false;
				}
				System.out.println("Remote peer " + remotePeerCheck.getPeerID() + " has " + remotePeerCheck.getbitField().cardinality() + "/" + peer.getPeerInstance().getPieceCount());
			}
			if (newFlag) {
				peerProcess.setFinished();
				break;
			}
		}
	}
}

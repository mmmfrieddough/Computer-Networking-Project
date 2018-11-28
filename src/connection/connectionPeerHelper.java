package connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.BitSet;

import Message.*;
import behavior.RemotePeerInfo;
import fileIO.dataFile;
import messageType.interest;

public class connectionPeerHelper {
	
	public static message sendBitSetMSG(BufferedOutputStream out) throws Exception {
		System.out.println("Sent bit");
		peer testPeer = peer.getPeerInstance();
		BitSet testBitset = testPeer.getBitSet();
		System.out.println(testBitset.toString());
		byte[] testBytearray = testBitset.toByteArray();
		message_process messageProcess = new message_process((byte) 5, peer.getPeerInstance().getBitSet().toByteArray());
		message Message = messageProcess.messageBuilder();
		System.out.println(Arrays.toString(Message.getMessageLength()) + " " + Message.getMessageType() + " " + Arrays.toString(Message.getMessagePayload()));
		byte[] messageToSend = MessageUtil.concatenateByteArrays(MessageUtil.concatenateByte(Message.getMessageLength(),
				Message.getMessageType()), Message.getMessagePayload());
		out.write(messageToSend);
		out.flush();
		
		return Message;
	}
	
	public static message sendInterestedMSG(BufferedOutputStream out) throws Exception {
		System.out.println("Sent interested");
		message_process messageProcess = new message_process((byte) 2);
		message Message = messageProcess.messageBuilder();
		byte[] messageToSend = MessageUtil.concatenateByte(Message.getMessageLength(),
				Message.getMessageType());
		out.write(messageToSend);
		out.flush();
		
		return Message;
	}
	
	
	public static message sendNotInterestedMSG(BufferedOutputStream out) throws Exception {
		System.out.println("Sent not interested");
		message_process messageProcess = new message_process((byte) 3);
		message Message = messageProcess.messageBuilder();
		byte[] messageToSend = MessageUtil.concatenateByte(Message.getMessageLength(),
				Message.getMessageType());
		out.write(messageToSend);
		out.flush();
		
		return Message;
	}
	
	public static message sendChokeMSG(BufferedOutputStream out) throws Exception {
		System.out.println("Sent choke");
		message_process messageProcess = new message_process((byte) 0);
		message Message = messageProcess.messageBuilder();
		byte[] messageToSend = MessageUtil.concatenateByte(Message.getMessageLength(),
				Message.getMessageType());
		out.write(messageToSend);
		out.flush();
		
		return Message;
	}
	
	public static message sendUnChokeMSG(BufferedOutputStream out) throws Exception {
		System.out.println("Sent unchoke");
		message_process messageProcess = new message_process((byte) 1);
		message Message = messageProcess.messageBuilder();
		byte[] messageToSend = MessageUtil.concatenateByte(Message.getMessageLength(),
				Message.getMessageType());
		out.write(messageToSend);
		out.flush();
		
		return Message;
	}
	
//	public static message sendRequestMSG(BufferedOutputStream out, RemotePeerInfo remote) throws Exception {
//		System.out.println("Sent request");
//		message_process messageProcess = new message_process((byte) 6,getPieceIndex(remote));
//		message Message = messageProcess.messageBuilder();
//		byte[] messageToSend = MessageUtil.concatenateByte(Message.getMessageLength(),
//				Message.getMessageType());
//		out.write(messageToSend);
//		out.flush();
//		
//		return Message;
//	}
	
	public static message sendRequestMSG(BufferedOutputStream out, int pieceIndex) throws Exception {
		System.out.println("Sent request");
		message_process messageProcess = new message_process((byte) 6,MessageUtil.intToByteArray(pieceIndex));
		message Message = messageProcess.messageBuilder();
		byte[] messageToSend = MessageUtil.concatenateByte(Message.getMessageLength(),
				Message.getMessageType());
		out.write(messageToSend);
		out.write(Message.getMessagePayload());
		out.flush();
		return Message;
	}

	public static message sendHaveMSG(BufferedOutputStream out, int receivedPieceIndex) throws Exception {
		System.out.println("Sent have");
		message_process messageProcess = new message_process((byte) 4, MessageUtil.intToByteArray(receivedPieceIndex));
		message Message = messageProcess.messageBuilder();
		byte[] messageToSend = MessageUtil.concatenateByte(Message.getMessageLength(),
				Message.getMessageType());
		out.write(messageToSend);
		out.write(Message.getMessagePayload());
		out.flush();
		
		return Message;
	}
	
	public static message sendPieceMSG(BufferedOutputStream out, int PieceIndex) throws Exception {
		System.out.println("Sent piece");
		File piece = dataFile.readFilePiece(PieceIndex);
		byte[] fileBytes = Files.readAllBytes(piece.toPath());
		byte[] payload = MessageUtil.concatenateByteArrays(MessageUtil.intToByteArray(PieceIndex), fileBytes);
		message_process messageProcess = new message_process((byte)7, payload);
		message Message = messageProcess.messageBuilder();
		byte[] messageToSend = MessageUtil.concatenateByte(Message.getMessageLength(), Message.getMessageType());
		out.write(messageToSend);
		out.write(Message.getMessagePayload());
		out.flush();
		return Message;
	}
	
	
	public static byte[] getActualMessage(BufferedInputStream in) {
		byte[] ByteLength = new byte[4];
		int byteReadin = -1;
		byte[] data = null; 
		try {
			byteReadin = in.read(ByteLength);
			if(byteReadin!=4) {
				System.out.println("The length of message in not corrent!");
			}
			int dataLength = MessageUtil.byteArrayToInt(ByteLength);
			byte[] messageType = new byte[1];
			in.read(messageType);
			int actualDataLength = dataLength - 1;
			data = new byte[actualDataLength];
			data = MessageUtil.readBytes(in, data, actualDataLength);
		}
		catch (IOException e){
			System.out.println("Not able to get the length of actual message");
			e.printStackTrace();
		}
		return data;
	}

	public static byte getMSGType(BufferedInputStream in) throws IOException {
		in.mark(5);
		byte[] ByteLengthAndMSGType = new byte[5];
		in.read(ByteLengthAndMSGType);
		in.reset();
		return ByteLengthAndMSGType[4];
	}
	
	
	//TODO  it might be wrong
	public static boolean isInterested(BitSet b1, BitSet b2) {
		for(int i=0;i<b1.length();i++) {
			if(b1.get(i)!=b2.get(i) && b1.get(i)==true && b2.get(i)==false) {
				return true;
			}
//			if(b1.get(i)!=b2.get(i)) {
//				return true;
//			}
		}
		return false;
	}
	
	public static byte[] getPieceIndex(RemotePeerInfo remote) {
		 BitSet b1 = remote.getbitField();
		 BitSet b2 = peer.getPeerInstance().getBitSet();
		 int pieceIndex = compare(b1, b2);
		 return MessageUtil.intToByteArray(pieceIndex);
	}
	
	public static int getFirstDifference(BitSet left, BitSet right) {
		BitSet tmp = (BitSet) left.clone();
		tmp.xor(right);
		tmp.and(left);
		return tmp.nextSetBit(0);
	}
	
	public static int compare(BitSet left, BitSet right) {
		if(left.equals(right)) {
			return 0;
		}
		BitSet xor = (BitSet) left.clone();
		xor.xor(right);
		int firstDifferent = xor.length()-1;
		if(firstDifferent==-1) {
			return 0;
		}
		return right.get(firstDifferent) ? 1:-1;
	}
}

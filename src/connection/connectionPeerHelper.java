package connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.BitSet;

import Message.*;
import behavior.RemotePeerInfo;
import messageType.interest;

public class connectionPeerHelper {
	
	public static message sendBitSetMSG(BufferedOutputStream out) throws Exception {
		message_process messageProcess = new message_process((byte) 5, peer.getPeerInstance().getBitSet().toByteArray());
		message Message = messageProcess.messageBuilder();
		System.out.println(Message.getMessageLength() + " " + Message.getMessageType() + " " + Message.getMessagePayload());
		byte[] messageToSend = MessageUtil.concatenateByteArrays(MessageUtil.concatenateByte(Message.getMessageLength(),
				Message.getMessageType()), Message.getMessagePayload());
		out.write(messageToSend);
		out.flush();
		
		return Message;
	}
	
	public static message sendInterestedMSG(BufferedOutputStream out) throws Exception {
		message_process messageProcess = new message_process((byte) 2);
		message Message = messageProcess.messageBuilder();
		byte[] messageToSend = MessageUtil.concatenateByte(Message.getMessageLength(),
				Message.getMessageType());
		out.write(messageToSend);
		out.flush();
		
		return Message;
	}
	
	
	public static message sendNotInterestedMSG(BufferedOutputStream out) throws Exception {
		message_process messageProcess = new message_process((byte) 3);
		message Message = messageProcess.messageBuilder();
		byte[] messageToSend = MessageUtil.concatenateByte(Message.getMessageLength(),
				Message.getMessageType());
		out.write(messageToSend);
		out.flush();
		
		return Message;
	}
	
	public static message sendChokeMSG(BufferedOutputStream out) throws Exception {
		message_process messageProcess = new message_process((byte) 0);
		message Message = messageProcess.messageBuilder();
		byte[] messageToSend = MessageUtil.concatenateByte(Message.getMessageLength(),
				Message.getMessageType());
		out.write(messageToSend);
		out.flush();
		
		return Message;
	}
	
	public static message sendUnChokeMSG(BufferedOutputStream out) throws Exception {
		message_process messageProcess = new message_process((byte) 1);
		message Message = messageProcess.messageBuilder();
		byte[] messageToSend = MessageUtil.concatenateByte(Message.getMessageLength(),
				Message.getMessageType());
		out.write(messageToSend);
		out.flush();
		
		return Message;
	}
	
	public static message sendRequestMSG(BufferedOutputStream out, RemotePeerInfo remote) throws Exception {
		message_process messageProcess = new message_process((byte) 6,getPieceIndex(remote));
		message Message = messageProcess.messageBuilder();
		byte[] messageToSend = MessageUtil.concatenateByte(Message.getMessageLength(),
				Message.getMessageType());
		out.write(messageToSend);
		out.flush();
		
		return Message;
	}

	public static message sendHaveMSG(BufferedOutputStream out, int receivedPieceIndex) throws Exception {
		message_process messageProcess = new message_process((byte) 6, MessageUtil.intToByteArray(receivedPieceIndex));
		message Message = messageProcess.messageBuilder();
		byte[] messageToSend = MessageUtil.concatenateByte(Message.getMessageLength(),
				Message.getMessageType());
		out.write(messageToSend);
		out.flush();
		
		return Message;
	}
	
	public static message sendPieceMSG(BufferedOutputStream out, int PieceIndex) throws Exception {
		File piece = FileManagerExecutor.getFilePart(pieceIndex);
		byte[] payload = Files.readAllBytes(piece.toPath());
		message_process messageProcess = new message_process((byte)7, payload);
		message Message = messageProcess.messageBuilder();
		byte[] messageToSend = MessageUtil.concatenateByte(Message.getMessageLength(), Message.getMessageType());
		out.write(messageToSend);
		out.flush();
		return Message;
	}
	
	/////////////////////////////////////////start from 92
	
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
			if(messageType[0]==(byte)5) {
				int actualDataLength = dataLength - 1;
				data = new byte[actualDataLength];
				data = MessageUtil.readBytes(in, data, actualDataLength);
			}
			else {
				System.out.println("Message type is not correct");
			}
		}
		catch (IOException e){
			System.out.println("Not able to get the length of actual message");
			e.printStackTrace();
		}
		return data;
	}

	public static byte getMSGType(BufferedInputStream in) throws IOException {
		byte[] ByteLengthAndMSGType = new byte[5];
		in.read(ByteLengthAndMSGType);
		return ByteLengthAndMSGType[4];
	}
	
	
	//TODO  it might be wrong    
	public static boolean isInterested(BitSet b1, BitSet b2) {
		for(int i=0;i<b2.length();i++) {
//			if(b1.get(i)!=b2.get(i) && b1.get(i)==false && b2.get(i)==true) {
//				return true;
//			}
			if(b1.get(i)!=b2.get(i)) {
				return true;
			}
		}
		return false;
	}
	
	//not sure if interested is mutual or not  
	public static byte[] getPieceIndex(RemotePeerInfo remote) {
		 BitSet b1 = remote.getbitField();
		 BitSet b2 = peer.getPeerInstance().getBitSet();
		 int pieceIndex = compare(b1, b2);
		 return MessageUtil.intToByteArray(pieceIndex);
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

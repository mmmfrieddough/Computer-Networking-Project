package connection;

import java.io.BufferedOutputStream;
import java.io.File;
import java.nio.file.Files;

import Message.*;
import behavior.RemotePeerInfo;

public class connectionPeerHelper {
	public message sendBitSetMSG(BufferedOutputStream out) throws Exception {
		message_process messageProcess = new message_process((byte) 5, peer.getPeerInstance().getBitSet().toByteArray());
		message Message = messageProcess.messageBuilder();
		System.out.println(Message.getMessageLength() + " " + Message.getMessageType() + " " + Message.getMessagePayload());
		byte[] messageToSend = MessageUtil.concatenateByteArrays(MessageUtil.concatenateByte(Message.getMessageLength(),
				Message.getMessageType()), Message.getMessagePayload());
		out.write(messageToSend);
		out.flush();
		
		return Message;
	}
	
	public message sendInterestedMSG(BufferedOutputStream out) throws Exception {
		message_process messageProcess = new message_process((byte) 2);
		message Message = messageProcess.messageBuilder();
		byte[] messageToSend = MessageUtil.concatenateByte(Message.getMessageLength(),
				Message.getMessageType());
		out.write(messageToSend);
		out.flush();
		
		return Message;
	}
	
	
	public message sendNotInterestedMSG(BufferedOutputStream out) throws Exception {
		message_process messageProcess = new message_process((byte) 3);
		message Message = messageProcess.messageBuilder();
		byte[] messageToSend = MessageUtil.concatenateByte(Message.getMessageLength(),
				Message.getMessageType());
		out.write(messageToSend);
		out.flush();
		
		return Message;
	}
	
	public message sendChokeMSG(BufferedOutputStream out) throws Exception {
		message_process messageProcess = new message_process((byte) 0);
		message Message = messageProcess.messageBuilder();
		byte[] messageToSend = MessageUtil.concatenateByte(Message.getMessageLength(),
				Message.getMessageType());
		out.write(messageToSend);
		out.flush();
		
		return Message;
	}
	
	public message sendUnChokeMSG(BufferedOutputStream out) throws Exception {
		message_process messageProcess = new message_process((byte) 1);
		message Message = messageProcess.messageBuilder();
		byte[] messageToSend = MessageUtil.concatenateByte(Message.getMessageLength(),
				Message.getMessageType());
		out.write(messageToSend);
		out.flush();
		
		return Message;
	}
	
	public message sendRequestMSG(BufferedOutputStream out, RemotePeerInfo remote) throws Exception {
		message_process messageProcess = new message_process((byte) 6,getPieceIndex(remote));
		message Message = messageProcess.messageBuilder();
		byte[] messageToSend = MessageUtil.concatenateByte(Message.getMessageLength(),
				Message.getMessageType());
		out.write(messageToSend);
		out.flush();
		
		return Message;
	}

	public message sendHaveMSG(BufferedOutputStream out, int receivedPieceIndex) throws Exception {
		message_process messageProcess = new message_process((byte) 6, MessageUtil.intToByteArray(receivedPieceIndex));
		message Message = messageProcess.messageBuilder();
		byte[] messageToSend = MessageUtil.concatenateByte(Message.getMessageLength(),
				Message.getMessageType());
		out.write(messageToSend);
		out.flush();
		
		return Message;
	}
	
	public message sendPieceMSG(BufferedOutputStream out, int PieceIndex) throws Exception {
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
	
	
	
}

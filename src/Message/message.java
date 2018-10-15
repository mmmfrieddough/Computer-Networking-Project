package Message;


public class message {
	
	private byte[] message_length;
	private byte message_type;
	private byte[] message_payload;
	
//	public message_consist() {
//		
//	}
	
	public message(byte message_type){
		this.message_type = message_type;
		this.message_length = MessageUtil.intToByteArray(1);
		this.message_payload = null;
	}
	
	public message(byte message_type, byte[] messagePayload) {
		this.message_type = message_type;
		this.message_payload = messagePayload;
		this.message_length = MessageUtil.intToByteArray(messagePayload.length+1);
	}
	
	
	
	public byte[] getMessageLength() {
		return message_length;
	}
	
	public byte getMessageType() {
		return message_type;
	}
	
	public byte[] getMessagePayload() {
		return message_payload;
	}
	
	
	
}

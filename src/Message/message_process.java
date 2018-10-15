package Message;
import messageType.*;

public class message_process {
	private byte messageType;
	private byte[] messagePayload;
	
	public message_process(byte messageType) {
		this.messageType = messageType;
	}
	
	public message_process(byte messageType, byte[] messagePayload) {
		this.messageType = messageType;
		this.messagePayload = messagePayload;
	}
	 
	public message messageBuilder() throws Exception{
		message msg;
		switch(this.messageType) {
		case (byte) 0:{
			msg = new choke();
			break;
		}
		
		case (byte) 1:{
			msg = new unchoke();
			break;
		}
		case (byte) 2:{
			msg = new interest();
			break;
		}
		case (byte) 3:{
			msg = new notInterest();
			break;
		}
		case (byte) 4:{
			msg = new have(this.messagePayload);
			break;
		}
		case (byte) 5:{
			msg = new bitField(this.messagePayload);
			break;
		}
		case (byte) 6:{
			msg = new request(this.messagePayload);
			break;
		}
		case (byte) 7:{
			msg = new piece(this.messagePayload);
			break;
		}
		default:{
			throw new Exception("wrong message type" + messageType);
		}
		
		
			
		
		}
		
		return msg;
	}
}

package messageType;
import Message.message;


public class piece extends message{
	public piece(byte[] payload_piece) {
		
		super((byte) 7, payload_piece);
	}
}

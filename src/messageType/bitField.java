package messageType;

import Message.message;

public class bitField extends message{
	
	private byte[] bitField;

    public bitField(byte[] bitField) {
        super((byte) 5, bitField);
        this.bitField = bitField;
    }
}

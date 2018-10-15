package messageType;

import Message.message;
 

public class have extends message{
	

    public have(byte[] piece_index) {
        super((byte) 4, piece_index);
    }
}






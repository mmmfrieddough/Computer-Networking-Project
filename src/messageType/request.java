package messageType;

import Message.message;
 

public class request extends message{
	

    public request(byte[] piece_index){
        super((byte) 6, piece_index);
    }
}






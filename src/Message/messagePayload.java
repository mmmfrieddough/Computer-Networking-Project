package Message;

public class messagePayload {
	private byte[] payload;
	
	public byte[] messagePayload(){
        return payload;
    }

    public messagePayload(byte[] payload) {
        this.payload = payload;
        
    }

    public messagePayload () {
        this.payload = null;
        
    }

	
}

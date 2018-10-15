package behavior;

public enum msgType {
	
	choke ((byte) 0), 
	unchoke ((byte) 1),
	interested ((byte) 2),
	notInterested ((byte) 3),
	have ((byte) 4),
	bitfield ((byte) 5),
	request ((byte) 6),
	piece ((byte) 7);
	
	
	private final byte val;
	
	private msgType(byte val) {
		this.val = val;
	}
	
}

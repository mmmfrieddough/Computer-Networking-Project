package fileIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class peerConfig {
	public int ID;
	public String host;
	public int port;
	public boolean hasFile;
	
	public peerConfig(int newID) throws FileNotFoundException {
		File file = new File("PeerInfo.cfg"); 
	    Scanner sc = new Scanner(file);
	    
	    while(sc.hasNext()) {
	    	if (sc.findInLine(Integer.toString(newID)) != null) {
	    		this.ID = newID;
	    		this.host = sc.next();
	    		this.port = sc.nextInt();
	    		this.hasFile = sc.nextInt() == 1;
	    		break;
	    	}
	    	sc.nextLine();
	    }
	}
}

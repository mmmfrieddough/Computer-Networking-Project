package fileIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class peerConfig {
	public int ID;
	public String host;
	public int port;
	public int hasFile;
	
	public peerConfig(int newID) throws FileNotFoundException {
		File file = new File("PeerInfo.cfg"); 
	    Scanner sc = new Scanner(file);
	    
	    System.out.println("Reading config for peer " + newID);
	    while(sc.hasNext()) {
	    	if (sc.findInLine(Integer.toString(newID)) != null) {
	    		this.ID = newID;
	    		this.host = sc.next();
	    		System.out.println("Read host as " + this.host);
	    		this.port = sc.nextInt();
	    		System.out.println("Read port as " + this.port);
	    		this.hasFile = sc.nextInt();
	    		System.out.println("Read hasFile as " + this.hasFile);
	    		break;
	    	}
	    	sc.nextLine();
	    }
	}
}

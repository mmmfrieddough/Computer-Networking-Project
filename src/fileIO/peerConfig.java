package fileIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import behavior.RemotePeerInfo;
import connection.peer;

public class peerConfig {
	public int ID;
	public String host;
	public int port;
	public int hasFile;
	
	public peerConfig(int newID, peer peer) throws FileNotFoundException {
		File file = new File("PeerInfo.cfg"); 
	    Scanner sc = new Scanner(file);
	    
	    System.out.println("Reading config for peer " + newID);
	    while(sc.hasNext()) {
	    	int readID = sc.nextInt();
	    	String readHost = sc.next();
	    	int readPort = sc.nextInt();
	    	int readHasFile = sc.nextInt();
	    	if (readID == newID) {
	    		// This is the peer we are looking for
	    		System.out.println("Found peer");
	    		this.ID = readID;
	    		this.host = readHost;
	    		System.out.println("Read host as " + this.host);
	    		this.port = readPort;
	    		System.out.println("Read port as " + this.port);
	    		this.hasFile = readHasFile;
	    		System.out.println("Read hasFile as " + this.hasFile);
	    	}
	    	else {
	    		// This is a different peer
	    		System.out.println("Found remote peer");
	    		RemotePeerInfo remotePeer = new RemotePeerInfo(readID, readHost, readPort, readHasFile);
	    		if (readID < newID) {
	    			peer.addPeerExpectConnectFrom(readID, remotePeer);
	    		}
	    		else {
	    			peer.addPeerConnectTo(readID, remotePeer);
	    		}
	    		
	    		peer.addConnectedPeer(remotePeer);
	    	}
	    	sc.nextLine();
	    }
	    sc.close();
	}
}

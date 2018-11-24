package processes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import connection.peer;
import fileIO.config;
import fileIO.dataFile;
import fileIO.peerConfig;

public class peerProcess {
	private static peer peer;
    private static boolean finished = false;
    
    public static void main(String[] args) {
    	// Create the peer object
    	peer = connection.peer.getPeerInstance();
    	
    	// Create executor that will run the client and server processes
    	ScheduledThreadPoolExecutor peerExecutor = new ScheduledThreadPoolExecutor(1);
    	
    	// Refresh common configuration values
    	try {
			config.getCommonConfig();
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
    	
    	// Read peer properties from file an set them
    	peerConfig config;
		try {
			config = new peerConfig(Integer.parseInt(args[0]), peer);
			peer.setPeerId(config.ID);
	    	peer.setHostName(config.host);
	    	peer.setPortNum(config.port);
	    	peer.setHasFile(config.hasFile);
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	if (peer.getHasFile() != 0) {
    		peer.setBitSet();
    		if (dataFile.findFile(peer.getPeerID())) {
    			System.out.println("Found file, splitting");
    			try {
					dataFile.splitFile(peer.getPeerID());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		else {
    			System.out.println("Couldn't find file");
    		}
    	}
    	else {
    		// Make new directory for partial files
    		System.out.println("Creating directory for files");
    		dataFile.createDirectory(peer.getPeerID());
    	}
    	
    	// Callable for creating server
    	Callable serverCreator = new Callable() {
    		public Object call() {
    			peerServer server = new peerServer();
    			new Thread(server).start();
    			return null;
    		}
    	};
    	
    	// Callable for creating client
    	Callable clientCreator = new Callable() {
    		public Object call() {
    			peerClient client = new peerClient(peer.getPeerConnectTo(), peer.getPeerConnectTo().size());
    			new Thread(client).start();
    			return null;
    		}
    	};
    	
    	// Schedule server thread to execute
    	peerExecutor.schedule(serverCreator, 0, TimeUnit.SECONDS);
    	
    	// Schedule client thread to execute
    	peerExecutor.schedule(clientCreator, 0, TimeUnit.SECONDS);
    	
    	// Send connection requests
    	System.out.println("Sending connection requests");
    	peer.preferredNeighbor();
    	peer.BestUnchokedNeighbor();
    }
    public static boolean getFinished() {
    	return finished;
    }
    public static void setFinished() {
    	finished = true;
    }
}

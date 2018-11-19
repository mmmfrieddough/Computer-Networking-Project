package processes;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import connection.peer;
import fileIO.config;
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
    	config.getCommonConfig();
    	
    	// Read peer properties from file an set them
    	peerConfig config = new peerConfig(Integer.parseInt(args[0]));
    	peer.setPeerId(config.ID);
    	peer.setHostName(config.host);
    	peer.setPortNum(config.port);
    	peer.setHasFile(config.hasFile);
    	
    	if (peer.getHasFile()) {
    		peer.setBitSet();
    		// TODO Check if file actually exists
    		// TODO Split file based on piece size
    	}
    	else {
    		// Make new directory for partial files
    		// TODO move into dataFile class
    		File directory = new File("/peer_" + String.valueOf(peer.getPeerID()));
    	    if (!directory.exists()) {
    	        directory.mkdir();
    	    }
    	}
    	
    	// Callable for creating server
    	Callable serverCreator = new Callable() {
    		public Object call() {
    			Server server = new Server();
    			new Thread(server).start();
    		}
    	};
    	
    	// Callable for creating client
    	Callable clientCreator = new Callable() {
    		public Object call() {
    			Client client = new Client();
    			new Thread(client).start();
    		}
    	};
    	
    	// Schedule server thread to execute
    	peerExecutor.schedule(serverCreator, 0, TimeUnit.SECONDS);
    	
    	// Schedule client thread to execute
    	peerExecutor.schedule(clientCreator, 0, TimeUnit.SECONDS);
    	
    	// Send connection requests
    	peer.preferredNeighbor();
    	peer.getBest();
    }
}

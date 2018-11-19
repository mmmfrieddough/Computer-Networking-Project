package processes;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import behavior.RemotePeerInfo;

public class peerClient implements Runnable {
	private Map<Integer, RemotePeerInfo> PeerConnectTo;
	
	private ExecutorService outThreadPool;
	private Thread clientThread;
    
	// Client is created with list of peers to connect to
    peerClient(Map<Integer, RemotePeerInfo> PeerConnectTo, int numPeerConnectTo) {
    	this.PeerConnectTo = PeerConnectTo;
    	this.outThreadPool = Executors.newFixedThreadPool(numPeerConnectTo);
    }
    
    public void run() {
    	// Synchronize so muliple clients do not get assigned the same thread
    	synchronized (this) {
    		// Assign the current thread to this process
    		this.clientThread = Thread.currentThread();
    	}
    	// Go through remote peers in list and start a connection process for each
    	for (RemotePeerInfo remotePeer : this.PeerConnectTo.values()) {
    		this.outThreadPool.execute(new remotePeerConnection(remotePeer));
    	}
    }
}
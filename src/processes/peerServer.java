package processes;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import connection.peer;

public class peerServer implements Runnable {
    private int port;
    private ServerSocket serverSocket;
    
    private ExecutorService inThreadPool;
    private Thread serverThread;
    
    peerServer() {
    	// Get port from peer instance
    	this.port = peer.getPeerInstance().getPortNum();
    	// Create enough threads for the peers that are expected to connect
    	inThreadPool = Executors.newFixedThreadPool(peer.getPeerInstance().getPeerExpectConnectFrom().size());
    }
    
    public void run() {
    	// Synchronize so multiple servers do not get assigned the same thread
    	synchronized (this) {
    		// Assign the current thread to this process
    		this.serverThread = Thread.currentThread();
    	}
    	
    	// Open socket with specified port number
    	this.serverSocket = new ServerSocket(this.port);
    	
    	// Go through peers until the process is finished
    	int currentID = peer.getPeerInstance().getPeerID();
    	while (!peerProcess.getFinished()) {
    		// Accept new connection on server socket
    		Socket clientSocket = serverSocket.accept();
    		
    		this.inThreadPool.execute(new IncomingRequestsHandler(clientSocket, peer.getPeerInstance().getPeerExpectConnectFrom().get(currentID)));
    		currentID++;
    	}
    	
    	// Shutdown because process is finished
    	this.inThreadPool.shutdown();
    	peerProcess.setFinished();
    	this.serverSocket.close();
    	
    	// Merge parts of file back into full file
    	dataFile.merge();
    }
}
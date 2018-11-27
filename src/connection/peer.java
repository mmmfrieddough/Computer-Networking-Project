package connection;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

import behavior.RemotePeerInfo;
import fileIO.config;
import fileIO.log;

public class peer {
	public static peer Peer;
	public volatile BitSet bitfield;
	private RemotePeerInfo bestUnchokedNeighbor;
	Map<Integer, RemotePeerInfo> PeerConnectTo;
	Map<Integer, RemotePeerInfo> PeerExpectConnectFrom;
	List<RemotePeerInfo> connectedPeer;
	volatile Map<RemotePeerInfo, BitSet> NeighborPreferred;
	Map<Integer, RemotePeerInfo> peersInterested;
	private log log;
	
	public int peerID;
	private String hostName;
	private int portNumber;
	private int hasFile;
	
	private int pieceSizeExcess;
	private int pieceCount;
	
	public log getLog() {
		return this.log;
	}
	
	public RemotePeerInfo getBest() {
		return bestUnchokedNeighbor;
	}
	
	public Map<Integer, RemotePeerInfo> getPeerConnectTo(){
		return PeerConnectTo;
	}
	
	public void addPeerConnectTo(int peerID, RemotePeerInfo peerInfo) {
		this.PeerConnectTo.put(peerID, peerInfo);
	}
	
	public Map<Integer, RemotePeerInfo> getPeerExpectConnectFrom(){
		return PeerExpectConnectFrom;
	}
	
	public void addPeerExpectConnectFrom(int peerID, RemotePeerInfo peerInfo) {
		this.PeerExpectConnectFrom.put(peerID, peerInfo);
	}
	
	public void addConnectedPeer(RemotePeerInfo peer) {
		this.connectedPeer.add(peer);
	}
	
	public Map<Integer, RemotePeerInfo> getpeersInterested(){
		return peersInterested;
	}
	
	public BitSet getBitSet() {
		return bitfield;
	}
	
	public int getPeerID() {
		return peerID;
	}
	
	public void setPeerId(int peerID) {
		this.peerID = peerID;
		try {
			this.log = new log(this.peerID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getHostName() {
		return hostName;
	}
	
	public int getPortNum() {
		return portNumber;
	}
	
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
	public void setPortNum(int portNum) {
		this.portNumber = portNum;
	}
	
	public int getHasFile() {
		return hasFile;
	}
	
	public boolean getBitField(int i) {
		return bitfield.get(i);
	}
	
	public void setHasFile(int hasFile) {
		this.hasFile = hasFile;
	}
	
	public int getPieceCount() {
		return pieceCount;
	}
	
	private void setBitField(int i) {
		this.bitfield.set(i);
	}
	
	public void setPieceCount() {
		int fileSize = config.getFileSize();
		int pieceSize = config.getPieceSize();
		this.pieceCount = (int) Math.ceil((double) fileSize/pieceSize);
	}
	
	private int getExcessPieceSize() {
		return pieceSizeExcess;
	}
	
	private void setExcessPieceSize(int excessPieceSize) {
		this.pieceSizeExcess = excessPieceSize;
	}
	
	public void setBitSet() {
		for(int i = 0; i < getPieceCount(); i++) {
			Peer.setBitField(i);
		}
	}
	
	private peer() {
		this.bitfield = new BitSet(this.getPieceCount());
		this.PeerConnectTo = Collections.synchronizedMap(new LinkedHashMap<>());
		this.PeerExpectConnectFrom = Collections.synchronizedMap(new LinkedHashMap<>());
		this.connectedPeer = Collections.synchronizedList(new ArrayList<>());
		this.peersInterested = Collections.synchronizedMap(new LinkedHashMap<>());
	}
	
	public static peer getPeerInstance() {
		if(Peer==null) {
			synchronized(peer.class) {
				if(Peer==null) Peer = new peer();
			}
		}
		return Peer;
	}
	
	public void BestUnchokedNeighbor() {
		System.out.println("Setting up timed task for setting unchoked neighbor");
		TimerTask repeatedTask = new TimerTask() {
			@Override
			public void run() {
				setBestUnchokedNeighbor();
			}
		};
		
		Timer BestTimer = new Timer();
		long delay = 0L;
		long period = (long) config.getOptimisticUnchokingInterval() * 1000;
		BestTimer.scheduleAtFixedRate(repeatedTask, delay, period);
	}
	
	private void setBestUnchokedNeighbor() {
		System.out.println("Finding best unchoked neighbor");
		this.bestUnchokedNeighbor = this.connectedPeer.get(ThreadLocalRandom.current().nextInt(this.connectedPeer.size()));
		try {
			this.log.logOptimisticallyUnchoked(this.bestUnchokedNeighbor.getPeerID());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void preferredNeighbor() {
		NeighborPreferred = Collections.synchronizedMap(new HashMap<>());
		
		TimerTask repeatedTask = new TimerTask() {
			@Override
			public void run() {
				setPreferredNeighbor();
			}
		};
		
		Timer prefTimer = new Timer();
		long delay = (long) config.getUnchokingInterval() *1000;
		long period = (long) config.getUnchokingInterval() *1000;
		prefTimer.scheduleAtFixedRate(repeatedTask, delay, period);
	}
	
	private synchronized void setPreferredNeighbor() {
		List<RemotePeerInfo> remotePeerList = new LinkedList<>(this.peersInterested.values());
		Queue<RemotePeerInfo> neighborsQueue = new PriorityBlockingQueue<>(config.getNumberOfPreferredNeighbors(), (o1, o2) -> Math.toIntExact(o1.getDownloadRate() - o2.getDownloadRate()));
		
		for(RemotePeerInfo remote : remotePeerList) {
			neighborsQueue.add(remote);
			remotePeerList.remove(remote);
		}
		
		RemotePeerInfo remote;
		
		this.NeighborPreferred.clear();
		
		int count = 0;
		
		while(!neighborsQueue.isEmpty()) {
			if (this.hasFile == 0) {
                remote = neighborsQueue.poll(); //like the pop in a stack
                if ((remote != null ? remote.getState() : null) == msgType.choke) {
					try {
						connectionPeerHelper.sendChokeMSG(remote.bufferedOutputStream);
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						throw new RuntimeException ("Could not send choke message from the peer class", e);
					}
                this.NeighborPreferred.put(remote, remote.getbitField());
                }
            }
			
			else{
                remote = this.connectedPeer.get(ThreadLocalRandom.current().nextInt(this.connectedPeer.size()));
                if (remote.getState() == msgType.choke || remote.getState() == null)
                	try {
						connectionPeerHelper.sendUnChokeMSG(remote.bufferedOutputStream);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						throw new RuntimeException ("Could not send choke message from the peer class", e);
					}

                this.NeighborPreferred.put(remote, remote.getbitField());
            }
			
			count++;
            if (count == config.getNumberOfPreferredNeighbors()) break;


		}
		
		try {
			this.log.logChangePreferedNeighbors(this.NeighborPreferred);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		while (!neighborsQueue.isEmpty()) {
        	remote = neighborsQueue.poll();
        	try {
        		connectionPeerHelper.sendUnChokeMSG(remote.bufferedOutputStream);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new RuntimeException ("Not able to send choke message", e);
			}
        }
	}
}

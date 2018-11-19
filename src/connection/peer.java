package connection;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

import behavior.RemotePeerInfo;

public class peer {
	public static peer Peer;
	public volatile BitSet bitfield;
	private RemotePeerInfo bestUnchokedNeighbor;
	Map<Integer, RemotePeerInfo> PeerConnectTo;
	Map<Integer, RemotePeerInfo> PeerExpectConnectFrom;
	List<RemotePeerInfo> connectedPeer;
	volatile Map<RemotePeerInfo, BitSet> NeighborPreferred;
	Map<Integer, RemotePeerInfo> peersInterested;
	public 
	
	private int peerID;
	private String hostName;
	private int portNumber;
	private boolean hasFile;
	
	private int pieceSizeExcess;
	private int pieceCount;
	
	public RemotePeerInfo getBest() {
		return bestUnchokedNeighbor;
	}
	
	public Map<Integer, RemotePeerInfo> getPeerConnectTo(){
		return PeerConnectTo;
	}
	
	public Map<Integer, RemotePeerInfo> getPeerExpectConnectFrom(){
		return PeerExpectConnectFrom;
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
	
	public boolean getHasFile() {
		return hasFile;
	}
	
	public boolean getBitField(int i) {
		return bitfield.get(i);
	}
	
	public void setHasFile(boolean hasFile) {
		this.hasFile = hasFile;
	}
	
	public int getPieceCount() {
		return pieceCount;
	}
	
	private void setBitField(int i) {
		this.bitfield.set(i);
	}
	
	public void setPieceCount() {
		int fileSize = constant.getFileSize();                                  //TODO
		int pieceSize = constant.getPieceSize();
		this.pieceCount = (int) Math.ceil((double) fileSize/pieceSize);
	}
	
	private int getExcessPieceSize() {
		return pieceSizeExcess;
	}
	
	private void setExcessPieceSize(int excessPieceSize) {
		this.pieceSizeExcess = excessPieceSize;
	}
	
	public void setBitSet() {
		for(int i=0;i<getPieceCount();i++) {
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
		if(Per==null) {
			synchronized(peer.class) {
				if(Peer==null) Peer = new peer();
			}
		}
		return Peer;
	}
	
	public void BestUnchokedNeighbor() {
		TimerTask repeatedTask = new TimerTask() {
			@Override
			public void run() {
				getBest();
			}
		};
		
		Timer BestTimer = new Timer();
		long delay = 0L;
		long period = (long) Constants.getBestUnchokedInterval() * 1000;    //TODO
		BestTimer.scheduleAtFixedRate(repeatedTask, delay, period);
		
	}
	
	private void setBestUnchokedNeighbor() {
		this.bestUnchokedNeighbor = this.connectedPeer.get(ThreadLocalRandom.current().nextInt(this.connectedPeer.size()));
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
		long delay = (long) Constants.getUnchokedInterval() *1000;
		long period = (long) Constants.getUnchokedInterval() *1000;
		prefTimer.scheduleAtFixedRate(repeatedTask, delay, period);
	}
	
	private synchronized void setPreferredNeighbor() {
		// TODO Auto-generated method stub
		List<RemotePeerInfo> remotePeerList = new LinkedList<>(this.peersInterested.values());
		Queue<RemotePeerInfo> neighborsQueue = new PriorityBlockingQueue<>(Constants.getNumberOfPreferredNeighbors(), (o1, o2) -> Math.toIntExact(o1.getDownload_rate() - o2.getDownload_rate()));
		
		
		for(RemotePeerInfo remote : remotePeerList) {
			neighborsQueue.add(remote);
			remotePeerList.remove(remote);
		}
		
		RemotePeerInfo remote;
		
		this.NeighborPreferred.clear();
		
		int count = 0;
		
		while(!neighborsQueue.isEmpty()) {
			if (this.hasFile != 1) {
                remote = neighborsQueue.poll();
                if ((remote != null ? remote.getState() : null) == msgType.choke) {
					try {
						PeerCommunicationHelper.sendChokeMsg(remote.bufferedOutputStream);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						throw new RuntimeException ("Could not send choke message from the peer class", e);
					}
                this.NeighborPreferred.put(remote, remote.getBitfield());
                }
            }
			
			else{
                remote = this.connectedPeer.get(ThreadLocalRandom.current().nextInt(this.connectedPeers.size()));
                if (remote.getState() == msgType.choke || remote.getState() == null)
                	try {
						PeerCommunicationHelper.sendChokeMsg(remote.bufferedOutputStream);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						throw new RuntimeException ("Could not send choke message from the peer class", e);
					}

                this.NeighborPreferred.put(remote, remote.getbitField());
            }
			
			count++;
            if (count == Constants.getNumberOfPreferredNeighbors()) break;


		}
	
		while (!neighborsQueue.isEmpty()) {
        	remote = neighborsQueue.poll();
        	try {
				PeerCommunicationHelper.sendChokeMsg(remote.bufferedOutputStream);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new RuntimeException ("Not able to send choke message", e);
			}

        }

	
	
	}
	
	
	
	
	
	
	

}

package fileIO;

import java.io.File;

public class dataFile {
	
	
	public static void createDirectory(int peerID) {
		File directory = new File("/peer_" + String.valueOf(peerID));
	    if (!directory.exists()) {
	        directory.mkdir();
	    }
	}
	
	public static boolean findFile(int peerID) {
		File file = new File("/peer_" + String.valueOf(peerID) + "/" + config.getFileName());
		return file.exists();
	}
}

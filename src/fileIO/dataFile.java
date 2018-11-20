package fileIO;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import connection.connectionPeerHelper;
import connection.peer;

public class dataFile {
	private static Map<Integer, File> piecesOfFile = new TreeMap<>();
	
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
	
	public static void splitFile(int peerID) throws IOException {
		File fullFile = new File("/peer_" + String.valueOf(peerID) + "/" + config.getFileName());
		FileInputStream inputStream = new FileInputStream(fullFile);
		
		int dataLeft = (int) fullFile.length();
		int pieceNumber = 0;
		while (dataLeft > 0) {
			byte[] pieceData = new byte[config.getPieceSize()];
			dataLeft -= inputStream.read(pieceData);
			File pieceFile = new File("/peer_" + String.valueOf(peerID) + "/" + config.getPartName() + String.valueOf(pieceNumber++));
			FileOutputStream outputStream = new FileOutputStream(pieceFile);
			outputStream.write(pieceData);
			outputStream.flush();
			outputStream.close();
		}
		inputStream.close();
	}
	
	public static void mergeFile() {
		File fullFile = new File("/peer_" + String.valueOf(peer.getPeerInstance().getPeerID()) + "/" + config.getFileName());
		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(fullFile);
		
			for (File piece : piecesOfFile.values()) {
				try {
					FileInputStream inputStream = new FileInputStream(piece);
					byte[] pieceData = new byte[(int) piece.length()];
					inputStream.read(pieceData);
					outputStream.write(pieceData);
					outputStream.flush();
					inputStream.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static void writeFilePiece(int pieceNumber, BufferedInputStream input) {
		File pieceFile = new File("/peer_" + String.valueOf(peer.getPeerInstance().getPeerID()) + "/" + config.getPartName() + String.valueOf(pieceNumber));
		try {
			FileOutputStream outputStream = new FileOutputStream(pieceFile);
			BufferedOutputStream outputStreamBuffered = new BufferedOutputStream(outputStream);
			outputStreamBuffered.write(connectionPeerHelper.getActualMessage(input));
			piecesOfFile.put(pieceNumber, pieceFile);
			outputStreamBuffered.flush();
			outputStreamBuffered.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

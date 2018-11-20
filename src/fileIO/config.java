package fileIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class config {
	public static Integer NumberOfPreferredNeighbors;
	public static Integer UnchokingInterval;
	public static Integer OptimisticUnchokingInterval;
	public static String FileName;
	public static Integer FileSize;
	public static Integer PieceSize;
	public static String partName = "part_";
	public static final String HandShakeHeader = "P2PFILESHARING";
	public static final String zerobit = "0000000000";

	public static void getCommonConfig() throws FileNotFoundException {
	    File file = new File("Common.cfg");
	    Scanner sc = new Scanner(file);

	    sc.next();
	    NumberOfPreferredNeighbors = sc.nextInt();
	    sc.next();
	    UnchokingInterval = sc.nextInt();
	    sc.next();
	    OptimisticUnchokingInterval = sc.nextInt();
	    sc.next();
	    FileName = sc.next();
	    sc.next();
	    FileSize = sc.nextInt();
	    sc.next();
	    PieceSize = sc.nextInt();
	}

	private  static Integer throwEI() {
		throw new RuntimeException("Already set");
	}

	private  static String throwES() {
		throw new RuntimeException("Already set");
	}

	public static int getNumberOfPreferredNeighbors() {
		return NumberOfPreferredNeighbors;
	}

	public static void setNumberOfPreferredNeighbors(int numberOfPreferredNeighbors) {
		NumberOfPreferredNeighbors = NumberOfPreferredNeighbors==null ? numberOfPreferredNeighbors:throwEI();
	}

	public static int getUnchokingInterval() {
		return UnchokingInterval;
	}

	public static void setUnchokingInterval(int unchokingInterval) {
		UnchokingInterval = UnchokingInterval==null ? unchokingInterval:throwEI();
	}

	public static int getOptimisticUnchokingInterval() {
		return OptimisticUnchokingInterval;
	}

	public static void setOptimisticUnchokingInterval(int optimisticUnchokingInterval) {
		OptimisticUnchokingInterval = OptimisticUnchokingInterval==null ? optimisticUnchokingInterval : throwEI();
	}

	public static String getFileName() {
		return FileName;
	}

	public static void setFileName(String fileName) {
		FileName = FileName==null ? fileName : throwES();
	}

	public static int getFileSize() {
		return FileSize;
	}

	public static void setFileSize(int fileSize) {
		FileSize = FileSize==null ? fileSize : throwEI();
	}

	public static int getPieceSize() {
		return PieceSize;
	}

	public static void setPieceSize(int pieceSize) {
		PieceSize = PieceSize==null ? pieceSize : throwEI();
	}

	public static String getPartName() {
		return partName;
	}
}

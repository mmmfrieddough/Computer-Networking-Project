package fileIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner; 

public class config {
	public int NumberOfPreferredNeighbors;
	public int UnchokingInterval;
	public int OptimisticUnchokingInterval;
	public String FileName;
	public int FileSize;
	public int PieceSize;
	
	public void getCommonConfig() throws FileNotFoundException {
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
}

package Message;

import java.io.BufferedInputStream;
import java.io.IOException;

public class MessageUtil {
	
	
	public static byte[] concatenateByteArrays (byte[] a, byte[] b)
    {
    	byte[] c = new byte[a.length + b.length];
    	System.arraycopy(a, 0, c, 0, a.length);
    	System.arraycopy(b, 0, c, a.length, b.length); 	
    	return c;	
    }
	
	public static byte[] concatenateByteArrays(byte[] a, int aLength, byte[] b, int bLength) {
		byte[] result = new byte[aLength + bLength];
		System.arraycopy(a, 0, result, 0, aLength);
		System.arraycopy(b, 0, result, aLength, bLength);
		return result;
	}
	
	public static int byteArrayToInt(byte[] b) 
	{
	    return   b[3] & 0xFF |
	            (b[2] & 0xFF) << 8 |
	            (b[1] & 0xFF) << 16 |
	            (b[0] & 0xFF) << 24;
	}
	
	public static byte[] concatenateByte(byte[] a, byte b) {
		 byte[] res = new byte[a.length+1];
		 System.arraycopy(a, 0, res, 0, a.length);
		 res[a.length] = b;
		 return res;
	 }
	
	//int to byte array
	public static byte[] intToByteArray(final int integer) {
	    byte[] result = new byte[4];

	    result[0] = (byte) ((integer & 0xFF000000) >> 24);
	    result[1] = (byte) ((integer & 0x00FF0000) >> 16);
	    result[2] = (byte) ((integer & 0x0000FF00) >> 8);
	    result[3] = (byte) (integer & 0x000000FF);

		return result;
		}
		 
	
	public static byte[] readBytes(BufferedInputStream in, byte[] byteArray, int length) throws IOException {
		int len = length;
		int index = 0;
		while (len != 0) {
			int dataAvailableLength = in.available();
			int read = Math.min(len, dataAvailableLength);
			byte[] dataRead = new byte[read];
			if (read != 0) {
				in.read(dataRead);
				byteArray = MessageUtil.concatenateByteArrays(byteArray, index, dataRead, read);
				index += read;
				len -= read;
			}
		}
		return byteArray;
	}
		 
		 
}

package Message;

public class MessageUtil {
	
	
	public static byte[] concatenateByteArrays (byte[] a, byte[] b)
    {
    	byte[] c = new byte[a.length + b.length];
    	System.arraycopy(a, 0, c, 0, a.length);
    	System.arraycopy(b, 0, c, a.length, b.length); 	
    	return c;	
    }
	
	public static int byteArrayToInt(byte[] b) 
	{
	    return   b[3] & 0xFF |
	            (b[2] & 0xFF) << 8 |
	            (b[1] & 0xFF) << 16 |
	            (b[0] & 0xFF) << 24;
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
}

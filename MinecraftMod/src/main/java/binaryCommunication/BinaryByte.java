package binaryCommunication;

public class BinaryByte extends BitArray{

	BinaryByte(byte b){
		super(b);
	}
	
	//string in form 00101010
	public BinaryByte(String s){
		super(s,false);
	}
	
	//for strings in form "1101N110101" etc.
	public static BinaryByte[] getBinaryByteArray(String binaryString,String separator){
		//divide string into bytes by seperator
		String [] strings = binaryString.split(separator);
		
		BinaryByte[] binaryBytes = new BinaryByte[strings.length];
		for(int i=0;i<strings.length;i++) 
		{
			binaryBytes[i]= new BinaryByte(strings[i]);
		}
		
		return binaryBytes;
	}
	
	
	public static BinaryByte getBinary(byte b) {
		return  new BinaryByte(b);
	}
	
	public static byte getByte(BinaryByte b) {
		Integer a = new Integer(BinaryByte.getInt(b));
		return a.byteValue();
	}
	
	public static BinaryByte[] getBinaryByteArray(byte [] b) {
		BinaryByte[] result= new BinaryByte[b.length];
		for(int i=0;i<b.length;i++) {
			result[i]=BinaryByte.getBinary(b[i]);
		}
		return result;
	}
	
	public static byte[] getByteArray(BinaryByte [] b) {
		byte[] result= new byte[b.length];
		
		for(int i=0;i<b.length;i++) {
			result[i]=BinaryByte.getByte(b[i]);
		}
		return result;
	}
	
	
	
}

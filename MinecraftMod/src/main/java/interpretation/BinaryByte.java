package interpretation;

public class BinaryByte {
	public static int BYTE_SIZE =8;
	//FROM MOST TO LEAST SIGNIFICANT BIT		2^7  2^6    2^5  2^4    2^3   2^2   2^1  2^0
	private boolean values [] = new boolean[] {false,false,false,false,false,false,false,false};

	BinaryByte(byte b){
		int value= Byte.toUnsignedInt(b);
		int counter=BYTE_SIZE-1;
		while(value!=0 && counter>=0)
		{
			this.values[counter]= (value%2 == 1);
			counter--;
			value=value/2;
		}
	}
	
	//string in form 00101010
	public BinaryByte(String s){
		if(s.length()>BYTE_SIZE) {return;}
		//remove begining zero's
		while(s.length()!=0  && s.charAt(0)=='0' ) {s=s.substring(1);}
		
		
		int c=(BYTE_SIZE-1)-(s.length()-1);
		for(int i=0;i<s.length();i++) {
			if(s.charAt(i)=='1') {this.values[c]=true;}
			c++;
		}
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
	
	public BinaryByte() {}
	

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
	
	
	public static int getInt(BinaryByte b) {return getInt(b,0,b.values.length-1);}
	
	public static int getInt(BinaryByte b,int indexStart,int indexEnd) {
		if(indexStart>indexEnd) {return -100;}
		
		int result=0;
		int counter=0;
		for(int i=indexEnd;i>=indexStart;i--) {
			if(b.values[i]==true) {result= (int) (result +Math.pow(2, counter));}		
			counter=counter+1;
		}
		return result;
	}
	
	
	public String toString() {
		String result="";
		for(int i=0;i<values.length;i++) {
			if(values[i]) {result=result+'1'; continue;}
			result=result+'0'; 
		}
		return result;
	}
	
	public char getASCII() {
		return (char) getInt(this);
	}
	
	public boolean bitAt(int index) {
		if(index>=BYTE_SIZE) {return false;}
		
		return this.values[index];
	}
}

package binaryCommunication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class BitArray {
	public static int BYTE_SIZE=8;
	public int size;
	//FROM MOST TO LEAST SIGNIFICANT BITex. 2^7  2^6    2^5  2^4    2^3   2^2   2^1  2^0
	public ArrayList<Boolean> values;
	
	
	public BitArray() {
		size=0;
		values=null;
	}
	
	//parse string in form ex. 0010101001 to bitArray 
	public BitArray(String s,boolean optimize){
		//remove zeros at the begining it it is to be optimized
		if(optimize) {while(s.length()!=0  && s.charAt(0)=='0' ){s=s.substring(1);}}
		
		this.size=s.length();
		//intialize values arrayList
		this.values =new ArrayList<Boolean>(Arrays.asList(new Boolean[this.size]));
		Collections.fill(values, Boolean.FALSE);
		
		for(int i=0;i<s.length();i++) {
			if(s.charAt(i)=='1') {this.values.set(i, true);}
		}
	}
	
	//convert byte[] java type to one BitArray
	public BitArray(byte[] bytes){
		this.size=bytes.length*BYTE_SIZE;
		values =new ArrayList<Boolean>(Arrays.asList(new Boolean[this.size]));
		Collections.fill(values, Boolean.FALSE);
		
		for(int i=0;i<bytes.length;i++) {
			int value= Byte.toUnsignedInt(bytes[i]);
			int counter=((i+1)*BYTE_SIZE)-1;
			while(value!=0 && counter>=0)
			{
				this.values.set(counter,(value%2 == 1));
				counter--;
				value=value/2;
			}
		}
	}
	
	//bit array from boolan
	public BitArray(boolean b) {
		this.size=1;
		values =new ArrayList<Boolean>(Arrays.asList(new Boolean[this.size]));
		Collections.fill(values, b);
	}
	
	//
	public static BitArray bitArrayFromInt(int i,int onBits){
		return new BitArray(i,onBits);
	}
	
	//convert byte java type to one BitArray
	public BitArray(int value,int onBits){
		if(onBits!=0) {
			this.size=onBits;
			this.values =new ArrayList<Boolean>(Arrays.asList(new Boolean[this.size]));
			Collections.fill(values, Boolean.FALSE);
			System.out.println("\nKoduje: "+value);
			int counter=this.size-1;
			while(value!=0 && counter>=0)
			{
				this.values.set(counter,value%2 == 1);
				counter--;
				value=value/2;
			}
			System.out.println("\nSize: "+size+"\nTablica: "+values.toString());
		}
	}
	
	
	
	//convert byte java type to one BitArray
	public BitArray(byte b){
		this.size=BYTE_SIZE;
		this.values =new ArrayList<Boolean>(Arrays.asList(new Boolean[this.size]));
		Collections.fill(values, Boolean.FALSE);
		
		//convert all the bytes to one singular decimal number
		int value= Byte.toUnsignedInt(b);
		
		int counter=this.size-1;
		while(value!=0 && counter>=0)
		{
			this.values.set(counter,value%2 == 1);
			counter--;
			value=value/2;
		}
	}
	
	public BitArray subBitArray(int indexStart,int indexEnd) {
		int sizeOfSub=(indexEnd-indexStart)+1;
		return bitArrayFromInt(this.getInt(indexStart, indexEnd), sizeOfSub);
	}
	
	
	public static int getInt(BitArray b) {return getInt(b,0,b.values.size()-1);}
	public int getInt() {return getInt(this,0,this.size-1);}
	public int getInt(int indexStart,int indexEnd) {return getInt(this,indexStart,indexEnd);}
	public static int getInt(BitArray b,int indexStart,int indexEnd) {
		if(indexStart>indexEnd) {return -100;}
		
		int result=0;
		int counter=0;
		for(int i=indexEnd;i>=indexStart;i--) {
			if(i<b.values.size()) 
			{
			if(b.values.get(i)){result= (int) (result +Math.pow(2, counter));}		
			counter=counter+1;
			}		
		}
		return result;
	}
	public byte[] getByteArray(){
		byte[] result;
		if(this.getSize()%8==0) {result= new byte[(this.getSize()/BYTE_SIZE)];}
		else {result= new byte[(this.getSize()/BYTE_SIZE)+1];}
		
		
		int index=0;
		int resultIndex=0;
		Integer a;
		while(index<this.getSize()-1)
		{
			a= new Integer(this.getInt(index,index+BYTE_SIZE-1));
			result[resultIndex]= a.byteValue();
			resultIndex++;
			index=index+BYTE_SIZE;
		}
		return result;
	}
	
	//adds the given bitArray to the end of this one
	public void concatenate(BitArray bitA) throws Exception {
		this.size+=bitA.getSize();
		for(int i=0;i<bitA.getSize();i++) {
			this.values.add(bitA.bitAt(i));
		}
	}

	
	public int getSize() {return this.values.size();}
	
	public String toString() {
		String result="";
		for(int i=0;i<values.size();i++) {
			if(values.get(i)) {result=result+'1'; continue;}
			result=result+'0'; 
		}
		return result;
	}
	
	public boolean bitAt(int index) throws Exception {
		if(index>=size) {throw new Exception("Index out of range!");}
		return this.values.get(index);
	}

	public char getASCII() {
		if(size<9) {return (char) getInt(this);}
		return '_';	
	}

	
	//get decimal, as fraction of max value of bitArray
	public double getDecimal() {
		return ((double) this.getInt()/(Math.pow(2, this.size))-1);
	}
	
}

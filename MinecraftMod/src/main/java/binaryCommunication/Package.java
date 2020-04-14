package binaryCommunication;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Package {
	
	/*STRUCTURE
     * BYTE         BYTE[0]        BYTE[1]     BYTE[2]     BYTE[3]     BYTE[4]     BYTE[5]     BYTE[6]     BYTE[7]    
     * 0            [P]            [T0]          [T1]        [T2]          [T3]      [S_ARG[0]]      [S_ARG[1]]      [S_ARG[2]]
     * 1+          [ARG_0[0]]                          ….                          [ARG_0[5]]             ….                
	 *	
	 *
	 *FIELDS	
     *[NAME]  [STANDS FOR]         [RANGE]           [USE]                                    
     *  P     PackageType           2**1 (2)           For specifying if it’s binary or ASCII.  0 for ASCII, 1 for Binary
     *  T     Order Type            2**3 (9)                            
     *  ARG   Argument              2**8 (256)
	*/
	
	/* ORDERS
	 * 
	 * [PLAYER_MOVEMENT]
	 * ARGUMENTS:
	 * 	 [1] 7 dimensional value of WSADUBS (W,A,S,D,Up,Below,SPRINTING)- toogles logical states basing on bits.
	 * 
	 * [CAMERA_MOVEMENT]
	 * ARGUMENTS:
	 * 
	 * 
	 * 
	 * */
		
	//public fields
	public static enum PackageType {NOTDETERMINED,ASCII,BINARY};
	public static enum OrderType {NOTDETERMINED,PLAYER_MOVEMENT,CAMERA_MOVEMENT,HOT_BAR,INVENTORY};
	public static String[] PLAYER_MOVEMENT_ARGUMENTS ={"W,A,S,D,Jump,Crouch,Sprint"};
	public static String[] CAMERA_MOVEMENT_ARGUMENTS ={"Yaw_Int","Yaw_Decimal","Pitch_isNegative","Pitch_Int","Pitch_Decimal"};
	public static String[] HOT_BAR_ARGUMENTS ={"Next,Previous"};
	
	//private fields
	byte rawData[] = null;
	//package info
	private PackageType packageType= Package.PackageType.NOTDETERMINED;
	private OrderType orderType= OrderType.NOTDETERMINED;
	private BitArray[] arguments = null;
	private int SPECIAL_ARGUMENT_INDEX=5;
	
	//CONSTRUCTORS
	//for parsing from raw data
	public Package(byte input[]) throws Exception{
		rawData=input;
		parsePackage();
	}
	
	//for creating raw data from Package
	public Package(PackageType pT,OrderType oT,BitArray arguments[]) throws Exception {
		this.packageType=pT;
		this.orderType=oT;
		this.arguments=arguments;
		

		BitArray temp= getPackageTypeAsBitArray();
		temp.concatenate(getOrderTypeAsBitArray());
		
		//for types in which special arguments are absent
		if(this.orderType!=orderType.CAMERA_MOVEMENT && this.orderType!=orderType.INVENTORY ) 
		{
			temp.concatenate(BitArray.bitArrayFromInt(0,3));
		}
		for(int i=0;i<arguments.length;i++) {temp.concatenate(arguments[i]);}

		rawData= temp.getByteArray();
	}
	
	
	private BitArray getPackageTypeAsBitArray() {
		if(this.packageType==packageType.ASCII) {return BitArray.bitArrayFromInt(1,1);}
		return BitArray.bitArrayFromInt(0,1);
	}
	
	private BitArray getOrderTypeAsBitArray() {
		switch(this.orderType) {
			case PLAYER_MOVEMENT: return BitArray.bitArrayFromInt(1,4);
			case CAMERA_MOVEMENT: return BitArray.bitArrayFromInt(2,4);
			case HOT_BAR: return BitArray.bitArrayFromInt(3,4);
			case INVENTORY: return BitArray.bitArrayFromInt(4,4);
			default: return BitArray.bitArrayFromInt(0,4);
		}
	}
	

	private void parsePackage() throws Exception {
		//check if there is any data to parse at all
		if(rawData==null) {throw new Exception("No data in package!");}

		parsePackageType();  //determine PackageType
		if(this.packageType!=PackageType.ASCII) { parseOrderType(); } //determine OrderType
		parseArguments();
	}
	
	
	private void parseArguments() throws Exception {
		//if it's an ASCII PACKAGE then all of its arguments are to be saved as BinaryBytes
		if(this.packageType==PackageType.ASCII) {
			this.arguments= new BitArray[rawData.length-1];
			for(int i=1;i<rawData.length;i++){ arguments[i-1]=new BinaryByte(rawData[i]);}
			return;
		}
		
		if(this.orderType==OrderType.PLAYER_MOVEMENT){
			this.arguments= new BitArray[1];
			arguments[0]=new BitArray(rawData).subBitArray(8,16);
			return;
		}
		
		//
		if(this.orderType==OrderType.CAMERA_MOVEMENT){
			//intiialize the list with 4 indecees
			this.arguments= new BitArray[5];
			//AROUND
			//around takes values between [0,360] 
			BitArray allArgs= new BitArray(rawData);
			System.out.println("\nDuza bitArray"+allArgs.toString());
			//because the one byte can only go up to 255, Special Arguments are used to make up the 9 bit value
			arguments[0]= allArgs.subBitArray(SPECIAL_ARGUMENT_INDEX,SPECIAL_ARGUMENT_INDEX+9); //the integer value
			arguments[1]= allArgs.subBitArray(14,23); //the fixed floating point
			
			//UPDOWN
			//updown  takes values [-90,90]
			arguments[2]= allArgs.subBitArray(24,25); //one bit argument (1 if it is a negative number)
			arguments[3]= allArgs.subBitArray(25,31);
			arguments[4]= allArgs.subBitArray(32,40);
			
			return;
		}
		
		//hotbar uses special Arguments as it's only arguments
		if(this.orderType==OrderType.HOT_BAR){
			this.arguments= new BitArray[1];
			this.arguments[0]= new BitArray(rawData).subBitArray(SPECIAL_ARGUMENT_INDEX,7);
			return;
		}
		
	}

	//First bit of the first byte determines package's type
	public PackageType parsePackageType() throws Exception {
		//if it starts with a one it's an ASCII package
		if(new BinaryByte(rawData[0]).bitAt(0)) {this.packageType=Package.PackageType.ASCII;}
		//else it's a binary one
		else{this.packageType=Package.PackageType.BINARY;}
	
		return this.packageType;
	}
	
	public OrderType parseOrderType() throws Exception{
		int typeAsInt =BitArray.getInt(new BinaryByte(rawData[0]), 1, 4);
		switch(typeAsInt) {
			case 0 :this.orderType= OrderType.PLAYER_MOVEMENT; break;
			case 1:this.orderType= OrderType.CAMERA_MOVEMENT; break;
			case 2:this.orderType= OrderType.HOT_BAR; break;
			case 3:this.orderType= OrderType.INVENTORY; break;
		}
		return this.orderType;
	}
	
	
	public OrderType getOrderType(){
		return this.orderType;
	}
	public BitArray[] getArguments() {
		return arguments;
	}
	public void setArguments(BinaryByte[] arguments) {
		this.arguments = arguments;
	}
	public void setPackageType(PackageType packageType) {
		this.packageType = packageType;
	}
	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}
	public byte[] getRawData() {return this.rawData;}

	@Override
	public String toString() {
		String argumentsString="";
		switch(this.orderType) {
			case PLAYER_MOVEMENT: for(int i=0;i<PLAYER_MOVEMENT_ARGUMENTS.length;i++){argumentsString+=PLAYER_MOVEMENT_ARGUMENTS[i]+": "+BitArray.getInt(arguments[i])+",";} break;
			case CAMERA_MOVEMENT: for(int i=0;i<CAMERA_MOVEMENT_ARGUMENTS.length;i++){argumentsString+=CAMERA_MOVEMENT_ARGUMENTS[i]+": "+arguments[i].toString()+",";} break;
			case HOT_BAR: for(int i=0;i<HOT_BAR_ARGUMENTS.length;i++){argumentsString+=HOT_BAR_ARGUMENTS[i]+": "+arguments[i].toString()+",";} break;			
			default: argumentsString= Arrays.toString(arguments);break;
		}
		
		
		return "Package [rawData=" + Arrays.toString(BinaryByte.getBinaryByteArray(rawData)) + ", packageType=" + packageType + ", orderType="
				+ orderType + ", arguments=" + argumentsString + "]";
	}

	public PackageType getPackageType() {
		return this.packageType;
	}
	
		
}

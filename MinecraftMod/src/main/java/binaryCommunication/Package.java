package binaryCommunication;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Package {
	
	/*STRUCTURE
     * BYTE         BYTE[0]        BYTE[1]     BYTE[2]     BYTE[3]     BYTE[4]     BYTE[5]     BYTE[6]     BYTE[7]    
     * 0            [P]            [T0]          [T1]        [T2]       [T3]       [S_ARG[0]]  [S_ARG[1]]  [S_ARG[2]]
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
	
	public static int PACKAGE_TYPE_LENGHT=1;
	public static int ORDER_TYPE_LENGHT=4;
	public static int SPECIAL_ARGUMENT_LENGHT=3;
	public static int YAW_WHOLE_PART_LENGHT=9;
	public static int YAW_DECIMAL_PART_LENGHT=9;
	public static int PITCH_WHOLE_PART_LENGHT=6;
	public static int PITCH_DECIMAL_PART_LENGHT=8;
	public static int HOT_BAR_ARGUMENT_LENGHT=2;
	
	
	
	//private fields
	byte rawData[] = null;
	//package info
	private PackageType packageType= Package.PackageType.NOTDETERMINED;
	private OrderType orderType= OrderType.NOTDETERMINED;
	private BitArray[] arguments = null;
	
	
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
		
		if(arguments!=null) {
			//for types in which special arguments are absent
			if(this.orderType==orderType.INVENTORY || this.orderType==orderType.PLAYER_MOVEMENT ) 
			{
				temp.concatenate(BitArray.bitArrayFromInt(0,SPECIAL_ARGUMENT_LENGHT));
			}
			for(int i=0;i<arguments.length;i++) {temp.concatenate(arguments[i]);}
		}
		
		rawData= temp.getByteArray();
	}
	
	
	public static Package createCameraMovementPackage(float yaw,float pitch) throws Exception {
		if(yaw>=360 || yaw<0) {throw new Exception("Yaw must be in <0-360)!");}
		if(pitch>90 || pitch<-90) {throw new Exception("Pitch must be in <(-90)-90)!");}
		
		//add header
		BitArray temp= BitArray.bitArrayFromInt(0,PACKAGE_TYPE_LENGHT);
		temp.concatenate(BitArray.bitArrayFromInt(2,ORDER_TYPE_LENGHT));
		
		//add arguments
		//YAW/AROUND
		int wholePart=(int) yaw;
		int decimalPart= (int) ((yaw-wholePart)*Math.pow(2,YAW_DECIMAL_PART_LENGHT));
		temp.concatenate(BitArray.bitArrayFromInt(wholePart,YAW_WHOLE_PART_LENGHT));
		temp.concatenate(BitArray.bitArrayFromInt(decimalPart,YAW_DECIMAL_PART_LENGHT));
		
		//UPDOWN/PITCH
		//check if it is negative
		if(pitch<0) {pitch*=-1; temp.concatenate(BitArray.bitArrayFromInt(1,1));}
		else {temp.concatenate(BitArray.bitArrayFromInt(0,1));}
		
		wholePart=(int) pitch;
		decimalPart= (int) ((pitch-wholePart)*Math.pow(2,PITCH_DECIMAL_PART_LENGHT));
		temp.concatenate(BitArray.bitArrayFromInt(wholePart,PITCH_WHOLE_PART_LENGHT));
		temp.concatenate(BitArray.bitArrayFromInt(decimalPart,PITCH_DECIMAL_PART_LENGHT));
		
		return new Package(temp.getByteArray());
	}
	
	
	private BitArray getPackageTypeAsBitArray() {
		if(this.packageType==packageType.ASCII) {return BitArray.bitArrayFromInt(0,PACKAGE_TYPE_LENGHT);}
		return BitArray.bitArrayFromInt(0,PACKAGE_TYPE_LENGHT);
	}
	
	private BitArray getOrderTypeAsBitArray() {
		switch(this.orderType) {
			case PLAYER_MOVEMENT: return BitArray.bitArrayFromInt(1,ORDER_TYPE_LENGHT);
			case CAMERA_MOVEMENT: return BitArray.bitArrayFromInt(2,ORDER_TYPE_LENGHT);
			case HOT_BAR: return BitArray.bitArrayFromInt(3,ORDER_TYPE_LENGHT);
			case INVENTORY: return BitArray.bitArrayFromInt(4,ORDER_TYPE_LENGHT);
			default: return BitArray.bitArrayFromInt(0,ORDER_TYPE_LENGHT);
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
			System.out.println("\nDEKODOWANIE\nDuza tablica"+ allArgs);
			//because the one byte can only go up to 255, Special Arguments are used to make up the 9 bit value
			int beginingIndex=PACKAGE_TYPE_LENGHT+ORDER_TYPE_LENGHT;
			arguments[0]= allArgs.subBitArray(beginingIndex,beginingIndex+YAW_WHOLE_PART_LENGHT-1); //the whole part
			beginingIndex+=YAW_WHOLE_PART_LENGHT;
			arguments[1]= allArgs.subBitArray(beginingIndex,beginingIndex+YAW_DECIMAL_PART_LENGHT-1); //the fixed floating point
			beginingIndex+=YAW_DECIMAL_PART_LENGHT;
			
			
			//UPDOWN
			//updown  takes values [-90,90]
			arguments[2]= allArgs.subBitArray(beginingIndex,beginingIndex); //one bit argument (1 if it is a negative number)
			beginingIndex+=1;
			arguments[3]= allArgs.subBitArray(beginingIndex,beginingIndex+PITCH_WHOLE_PART_LENGHT-1);
			beginingIndex+=PITCH_WHOLE_PART_LENGHT;
			arguments[4]= allArgs.subBitArray(beginingIndex,beginingIndex+PITCH_DECIMAL_PART_LENGHT-1);
			
			return;
		}
		
		//hotbar uses special Arguments as it's only arguments
		if(this.orderType==OrderType.HOT_BAR){
			this.arguments= new BitArray[1];
			this.arguments[0]= new BitArray(rawData).subBitArray(PACKAGE_TYPE_LENGHT+ORDER_TYPE_LENGHT,PACKAGE_TYPE_LENGHT+ORDER_TYPE_LENGHT+HOT_BAR_ARGUMENT_LENGHT);
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
		int typeAsInt =BitArray.getInt(new BinaryByte(rawData[0]), PACKAGE_TYPE_LENGHT,ORDER_TYPE_LENGHT);
		switch(typeAsInt) {
			case 1:this.orderType= OrderType.PLAYER_MOVEMENT; break;
			case 2:this.orderType= OrderType.CAMERA_MOVEMENT; break;
			case 3:this.orderType= OrderType.HOT_BAR; break;
			case 4:this.orderType= OrderType.INVENTORY; break;
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
			case PLAYER_MOVEMENT: for(int i=0;i<PLAYER_MOVEMENT_ARGUMENTS.length;i++){argumentsString+=PLAYER_MOVEMENT_ARGUMENTS[i]+": "+arguments[i]+",";} break;
			case CAMERA_MOVEMENT: for(int i=0;i<CAMERA_MOVEMENT_ARGUMENTS.length;i++){argumentsString+=CAMERA_MOVEMENT_ARGUMENTS[i]+": "+arguments[i].getInt()+",";} break;
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

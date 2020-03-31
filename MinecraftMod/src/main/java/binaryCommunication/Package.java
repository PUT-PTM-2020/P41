package binaryCommunication;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;



public class Package {
	
	/*STRUCTURE
     * BYTE         BYTE[0]        BYTE[1]     BYTE[2]     BYTE[3]     BYTE[4]     BYTE[5]     BYTE[6]     BYTE[7]    
     * 0                      [P]            [T0]          [T1]        [T2]          [T3]        [SUB0]      [SUB1]      [SUB2]
     * 1+         [ARG0_0]                       ….                    [ARG0_4]             ….                
	 *	
	 *
	 *FIELDS	
     *[NAME]  [STANDS FOR]         [RANGE]           [USE]                                    
     *  P     PackageType           2**1 (2)           For specifying if it’s binary or ASCII.  0 for ASCII, 1 for Binary
     *  T     Order Type            2**3 (9)                
     *  SUB   Order subType         2**3 (9)             
     *  ARG   Argument              2**8 (256)
	*/
	
	/* ORDERS
	 * 
	 * [PLAYER_MOVEMENT]
	 * 		VALUES - give 6 dimensional value of WSADUBS (W,A,S,D,Up,Below,SPRINTING) and toogle logical states basing on bits.
	 * 		INCREMENT - give 3 arguments each translated to Unsigned int x,y,z.
	 * 
	 * [CAMERA_MOVEMENT]
	 * 		VALUES - 
	 * 		INCREMENT-

	 * 
	 * 
	 * */
		
	//public fields
	public static enum PackageType {NOTDETERMINED,ASCII,BINARY};
	public static enum OrderType {NOTDETERMINED,PLAYER_MOVEMENT,CAMERA_MOVEMENT,TOOLBAR,INVENTORY};
	public static enum OrderSubType {NOTDETERMINED,VALUES,INCREMENT};
	
	//private fields
	BinaryByte rawData[] = null;
	//package info
	private PackageType packageType= Package.PackageType.NOTDETERMINED;
	private OrderType orderType= OrderType.NOTDETERMINED;
	private OrderSubType orderSubType= OrderSubType.NOTDETERMINED;
	private BinaryByte arguments[] = null;
	
	
	//CONSTRUCTORS
	//for parsing from raw data
	public Package(BinaryByte input[]) throws PackageException{
		rawData=input;
		parsePackage();
	}
	
	//for creating a package
	public Package(PackageType pT,OrderType oT,OrderSubType oST,BinaryByte arguments[]) {
		
	}
	
	private void parsePackage() throws PackageException {
		//check if there is any data to parse at all
		if(rawData==null) {throw new PackageException("No data in package!");}
		if(rawData.length<2) {throw new PackageException("Package must have a Header and some data!");}
		
		
		getPackageType();  //determine PackageType
		
		if(this.packageType!=PackageType.ASCII) 
		{
			getOrderType();    //determine OrderType
			getOrderSubType(); //determine SubType
		}
		
		//add Arguments
		this.arguments= new BinaryByte[rawData.length-1];
		for(int i=1;i<rawData.length;i++) {
			arguments[i-1]=rawData[i];
		}
	}
	
	
	//METHODS
	//First bit of the first byte determines package's type
	public PackageType getPackageType() {
		if(this.packageType!=Package.PackageType.NOTDETERMINED) {return this.packageType;}

		//if it starts with a zero it's an ASCII package
		if(rawData[0].bitAt(0)) {this.packageType=Package.PackageType.ASCII;}
		//else it's a binary one
		else{this.packageType=Package.PackageType.BINARY;}
	
		return this.packageType;
	}
	
	public OrderType getOrderType(){
		if(this.orderType!=Package.OrderType.NOTDETERMINED) {return this.orderType;}
		
		int typeAsInt =BinaryByte.getInt(rawData[0], 0, 4);
		switch(typeAsInt) {
			case 0 :this.orderType= OrderType.PLAYER_MOVEMENT; break;
			case 1:this.orderType= OrderType.CAMERA_MOVEMENT; break;
			case 2:this.orderType= OrderType.TOOLBAR; break;
			case 3:this.orderType= OrderType.INVENTORY; break;
		}
		return this.orderType;
	}
	
	public OrderSubType getOrderSubType(){
		if(this.orderSubType!=Package.OrderSubType.NOTDETERMINED) {return this.orderSubType;}

		int typeAsInt =BinaryByte.getInt(rawData[0], 4, 7);
		switch(typeAsInt) {
		case 0 : this.orderSubType= OrderSubType.VALUES; break;
		case 1:this.orderSubType= OrderSubType.INCREMENT; break;
		}
		
		return this.orderSubType;
	}
	public BinaryByte[] getArguments() {
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
	public void setOrderSubType(OrderSubType orderSubType) {
		this.orderSubType = orderSubType;
	}
	
	
	@Override
	public String toString() {
		return "Package [rawData=" + Arrays.toString(rawData) + ", packageType=" + packageType + ", orderType="
				+ orderType + ", orderSubType=" + orderSubType + ", arguments=" + Arrays.toString(arguments) + "]";
	}
	
		
}

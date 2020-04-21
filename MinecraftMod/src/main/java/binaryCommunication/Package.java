package binaryCommunication;

import java.util.Arrays;


/**
* @author      Mateusz Stelmasiak <mateusz.stelmasia@gmail.com>
* @version     1.2                
* @since       1.0   
*  
*/

public class Package{
	
	/*STRUCTURE
     * BYTE          BIT 0           BIT 1       BIT2       BIT3         BIT4     BIT5        BIT6        BIT7    
     * 0              [A]            [T0]        [T1]        [T2]        [T3]     [S_ARG[0]]  [S_ARG[1]]  [S_ARG[2]]
     * 1          [ARG_0[0]]                          ….                         [ARG_0[5]]                  ….                
	 *+			  [ARG_1[0]] ...
	 *
	 *FIELDS	
     *[NAME]  [STANDS FOR]         [RANGE]           [USE]                                    
     *  A     PackageType           2**1 (2)          For specifying if it’s binary or ASCII.  1 for ASCII, 0 for Binary.
     *  T     Order Type            2**4 (16)   
     *  S_ARG SpecialArgument                         Mainly used for packages that require just the header.                         
     *  ARG   Argument              
	*/
	
	/* ORDERS
	 * 
	 *[PLAYER_MOVEMENT]
	 * DESCRIPTION:
	 * 	For controlling player's movement. !DOESN'T INCLUDE LEFT/RIGH CLICKING!
	 * ARGUMENTS:
	 * 	 [1] 7bit value of WSADJCS(Forward,Backward,Left,Right,Jump,Croutch,Sprint)- toogles player's movement direction basing on bits.
	 * 	 ex.1000001- makes the player sprint forward
	 * 		
	 *[CAMERA_MOVEMENT]
	 * DESCRIPTION:
	 * 	For controlling player's camera on two axis yaw and pitch. 
	 *  Yaw controls player's rotation around their own axis, takes values in [0-360). 
	 *  Pitch controls player's head tilt, takes values in [-90-90]  !NEGATIVE VALUES CORRESPOND TO AN UPWARD TILT!
	 * ARGUMENTS:
	 * 	[1] Yaw (14 bits)
	 * 	[4] Pitch (13 bits)
	 * 
	 *[HOT_BAR]
	 * DESCRIPTION:
	 * 	For controlling the Item hotBar as well as handling left/right clicking.
	 * ARGUMENTS:
	 *
	 *[INVENTORY]
	 * DESCRIPTION:
	 * 	For crafting and managing the inventory widget.
	 * ARGUMENTS:
	 * 
	 * */
		
	//HELPER FIELDS
		public static enum PackageType {NOTDETERMINED,ASCII,BINARY};
		public static enum OrderType {NOTDETERMINED,PLAYER_MOVEMENT,CAMERA_MOVEMENT,HOT_BAR,INVENTORY};
		private static String[] PLAYER_MOVEMENT_ARGUMENTS ={"Forward","Backwards","Left","Right","Jump","Crouch","Sprint"};
		private static String[] CAMERA_MOVEMENT_ARGUMENTS ={"Yaw","Pitch"};
		private static String[] HOT_BAR_ARGUMENTS ={"Left/Right","Previous/Next"};
		
		private static int PACKAGE_TYPE_LENGHT=1;
		private static int ORDER_TYPE_LENGHT=4;
		private static int SPECIAL_ARGUMENT_LENGHT=3;
		private static int YAW_LENGHT=14;
		private static int PITCH_LENGHT=13;
		private static int HOT_BAR_ARGUMENT_LENGHT=2;
	
	
	
	//DATA FIELDS
		byte rawData[] = null;
		private PackageType packageType= Package.PackageType.NOTDETERMINED;
		private OrderType orderType= OrderType.NOTDETERMINED;
		private BitArray[] arguments = null;
	
	
	//PACKAGE PARSING FROM RAW DATA
		/**
		 * Basic constructor                      
		 * <p>
		 * For parsing out a package out of raw data.
		 * </p>
		 *
		 * @param  input byte array to be parsed.          
		 */
		public Package(byte input[]) throws Exception{
			if(input==null) {throw new Exception("No data in package!");}
			this.rawData=input;

			parsePackageType();  
			parseOrderType(); 
			parseArguments();
		}
		
		
		private PackageType parsePackageType() throws Exception {
			//if it starts with a one it's an ASCII package
			if(new BinaryByte(rawData[0]).bitAt(0)) {this.packageType=Package.PackageType.ASCII;}
			//else it's a binary one
			else{this.packageType=Package.PackageType.BINARY;} 
				
			return this.packageType;
		}
				
		private OrderType parseOrderType() throws Exception{
			//translate appropriate part of the header to int
			int typeAsInt =BitArray.getInt(new BinaryByte(rawData[0]), PACKAGE_TYPE_LENGHT,ORDER_TYPE_LENGHT);
			switch(typeAsInt) {
				case 1:this.orderType= OrderType.PLAYER_MOVEMENT; break;
				case 2:this.orderType= OrderType.CAMERA_MOVEMENT; break;
				case 3:this.orderType= OrderType.HOT_BAR; break;
				case 4:this.orderType= OrderType.INVENTORY; break;
				default:this.orderType= OrderType.NOTDETERMINED; break;
				}
			
			return this.orderType;
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
			
			if(this.orderType==OrderType.CAMERA_MOVEMENT){
				//intiialize the arguments list with 4 indecees
				this.arguments= new BitArray[2];
				
				//AROUND
				//around takes values between [0,360] 
				BitArray allArgs= new BitArray(rawData);

				int beginingIndex=PACKAGE_TYPE_LENGHT+ORDER_TYPE_LENGHT;
				arguments[0]= allArgs.subBitArray(beginingIndex,beginingIndex+YAW_LENGHT-1); //the whole part
				beginingIndex+=YAW_LENGHT;
				
				//UPDOWN
				//updown  takes values [-90,90]
				arguments[1]= allArgs.subBitArray(beginingIndex,beginingIndex+PITCH_LENGHT-1);
				
				return;
			}
			
			if(this.orderType==OrderType.HOT_BAR){
				this.arguments= new BitArray[1];
				//hotbar uses special Arguments as its only arguments
				this.arguments[0]= new BitArray(rawData).subBitArray(PACKAGE_TYPE_LENGHT+ORDER_TYPE_LENGHT,PACKAGE_TYPE_LENGHT+ORDER_TYPE_LENGHT+HOT_BAR_ARGUMENT_LENGHT);
				
				return;
			}
			
		}

	//PARSING RAW DATA FROM PACKAGE	
		/**
		 * Basic contructor                      
		 * <p>
		 * For creating rawData from package info.
		 * </p>
		 *
		 * @param  pT    
		 * @param  oT 
		 * @param argumnets       
		 */
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
			
			this.rawData= temp.getByteArray();
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
		
	
	    public static Package createCameraMovementPackage(float yaw,float pitch) throws Exception {
			if(yaw>=360 || yaw<0) {throw new Exception("Yaw must be in <0-360)!");}
			if(pitch>90 || pitch<-90) {throw new Exception("Pitch must be in <(-90)-90)!");}
			
			//add header
			BitArray temp= BitArray.bitArrayFromInt(0,PACKAGE_TYPE_LENGHT);
			temp.concatenate(BitArray.bitArrayFromInt(2,ORDER_TYPE_LENGHT));
			
			//ADD ARGUMENTS
			//yaw/around
			int forPackage= (int) ((float) (yaw/360)*Math.pow(2,YAW_LENGHT));
			temp.concatenate(BitArray.bitArrayFromInt(forPackage,YAW_LENGHT));
			
			//pitch/udDown
			//move pitch from taking -90-90 to 0-180
			pitch=pitch+90;
			forPackage= (int) ((float) ((pitch)/180)*Math.pow(2,PITCH_LENGHT));	
			temp.concatenate(BitArray.bitArrayFromInt(forPackage,PITCH_LENGHT));
			return new Package(temp.getByteArray());
		}
	
	

		
	//ACCESOR METHODS
		public void setArguments(BinaryByte[] arguments) { this.arguments = arguments;}
		public void setPackageType(PackageType packageType) { this.packageType = packageType;}
		public void setOrderType(OrderType orderType) { this.orderType = orderType;}

		public OrderType getOrderType(){ return this.orderType;}
		public BitArray[] getArguments() { return arguments;}
		public byte[] getRawData() {return this.rawData;}	
		public PackageType getPackageType() { return this.packageType;}

		@Override
		public String toString() {
			String argumentsString="";
			switch(this.orderType) {
				case PLAYER_MOVEMENT: 
					for(int i=0;i<PLAYER_MOVEMENT_ARGUMENTS.length;i++)
					{
						try 
						{
						//color arguments basing on their state
						if(arguments[0].bitAt(i)){argumentsString+="\u00A7c "+PLAYER_MOVEMENT_ARGUMENTS[i]+": OFF| ";}
						else{argumentsString+="\u00A7a "+PLAYER_MOVEMENT_ARGUMENTS[i]+": ON| ";}
						} 
						catch (Exception e) { return "Error: Index out of bounds";}
					} 
					break;
				case CAMERA_MOVEMENT: 
						argumentsString+=CAMERA_MOVEMENT_ARGUMENTS[0]+": "+(float) ((float) arguments[0].getInt()/Math.pow(2, arguments[0].getSize())*360);
						argumentsString+=CAMERA_MOVEMENT_ARGUMENTS[1]+": "+(float) (((float) arguments[1].getInt()/Math.pow(2, arguments[1].getSize())*90)-90);
						
					break;
				case HOT_BAR: for(int i=0;i<HOT_BAR_ARGUMENTS.length;i++){argumentsString+=HOT_BAR_ARGUMENTS[i]+": "+arguments[i].toString()+",";} break;			
				default: argumentsString= Arrays.toString(arguments);break;
			}
			
			
			return "Package [rawData=" + Arrays.toString(BinaryByte.getBinaryByteArray(rawData)) + ", packageType=" + packageType + ", orderType="
					+ orderType + ", arguments=" + argumentsString + "]";
		}

		
}

package interpretation;


import binaryCommunication.BinaryByte;
import binaryCommunication.Package;
import binaryCommunication.PackageException;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;

public class SerialMessageInterpreter {
	private static PlayerController pc = new PlayerController();

	
	public static void interpret(BinaryByte data[]) {
		try {
			Package pack = new Package(data);
			sendToPlayer(pack.toString());
			execute(pack);
		} catch (PackageException e) {
			sendToPlayer(e.toString());
		}	
	}
	

	public static void execute(Package pack) {
		//IF it's an ASCII package
		if(pack.getPackageType()==Package.PackageType.ASCII) 
		{
			BinaryByte bytes [] =pack.getArguments();
			
			String result ="";
			for(int i=0;i<bytes.length;i++) 
			{
				result=result+(char) BinaryByte.getInt(bytes[i]); 
			}
			sendToPlayer(result);		
		}
		
		//BINARY PACKAGES
		switch(pack.getOrderType()) {
			//PLAYER MOVEMENT
			case PLAYER_MOVEMENT:
				switch(pack.getOrderSubType())
				{
					case VALUES: movePlayer(pack.getArguments()[0]); break;
					default:
				}
				return;
				
			//CAMERA MOVEMENT
			case CAMERA_MOVEMENT:
				moveCamera(pack.getArguments());
				return;
			default: return;
		}
		
	}
	
	public static void movePlayer(BinaryByte movementClues)
	{
		//WSADUBS (W,A,S,D,Up,Below,SPRINTING)
		pc.setForward(movementClues.bitAt(0));
		pc.setBackward(movementClues.bitAt(1));
		pc.setLeft(movementClues.bitAt(2));
		pc.setRight(movementClues.bitAt(3));
		pc.jumping(movementClues.bitAt(4));
		pc.sneaking(movementClues.bitAt(5));
		pc.sprinting(movementClues.bitAt(6));
	}
	
	public static void moveCamera(BinaryByte [] arguments){
		//around takes values between [0,360] 
		//because first byte can only go up to 255, first bit of the next byte is used as the most significant bit
		float around= BinaryByte.getInt(arguments[0])+256*(arguments[1].bitAt(0)? 1 : 0);
		//the remaining 7 bits are used to make fix point decimal 
		around+=BinaryByte.getInt(arguments[1],1,7)/128;
		
		//updown  takes values [-90,90]
		float upDown= BinaryByte.getInt(arguments[2],1,7);
		if(arguments[2].bitAt(0)==true) {upDown*=-1;}
		//next byte used as fixed point decimal
		upDown+=BinaryByte.getInt(arguments[3])/256;
		pc.setCamera(around,upDown);
	}
	
	 public static void incrementCamera(BinaryByte movementClues) {

		/* if(movementClues.bitAt(0)) {moveCameraUp();}
		 if(movementClues.bitAt(1)) {player.rotationPitch=player.rotationPitch+CAMERA_PITCH_INCREMENT;}
		 if(movementClues.bitAt(2)) {player.rotationPitch=player.rotationPitch-CAMERA_PITCH_INCREMENT;}
		 if(movementClues.bitAt(3)) {player.rotationPitch=player.rotationPitch-CAMERA_PITCH_INCREMENT;}
		 if(movementClues.bitAt(4)) {player.rotationYaw=player.rotationYaw+CAMERA_YAW_INCREMENT ;}
		 if(movementClues.bitAt(5)) {player.rotationYaw=player.rotationYaw-CAMERA_YAW_INCREMENT ;}
		 */
	 }
	
	private static void sendRawDataToPlayer(BinaryByte data[]) {
		String packet="";
		for(int i=0;i<data.length;i++) {
			packet=packet+" | "+data[i].toString();
		}
		sendToPlayer(packet);
	}
	
	public static void sendToPlayer(String data) {
        //format and send to player
		StringTextComponent baseText= new StringTextComponent("");
		baseText.appendSibling(new StringTextComponent("\u00A73"+"Port: "));
		baseText.appendSibling(new StringTextComponent("\u00A7f"+data));
		Minecraft.getInstance().player.sendMessage(baseText);
	}
	
	
}

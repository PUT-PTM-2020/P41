package interpretation;


import binaryCommunication.BitArray;
import binaryCommunication.Package;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;

public class SerialMessageInterpreter {
	private static PlayerController pc = new PlayerController();

	
	public static void interpret(byte data[]) {
		try {
			Package pack = new Package(data);
			sendToPlayer(pack.toString());
			execute(pack);
		} catch (Exception e) {
			sendToPlayer(e.toString());
		}	
	}
	

	public static void execute(Package pack) throws Exception {
		//IF it's an ASCII package
		if(pack.getPackageType()==Package.PackageType.ASCII) 
		{
			BitArray[] bytes = pack.getArguments();
			String result ="";
			for(int i=0;i<bytes.length;i++) 
			{
				result=result+bytes[i].getASCII(); 
			}
			sendToPlayer(result);	
			return;
		}
		
		//BINARY PACKAGES
		switch(pack.getOrderType()) {
			case PLAYER_MOVEMENT:
				movePlayer(pack.getArguments()[0]); 
				return;
			case CAMERA_MOVEMENT:
				moveCamera(pack.getArguments());
				return;
			case CAMERA_RESET:
				sendResetCameraPackage();
				return;
			case HOT_BAR:
				selectItem(pack.getArguments()[0]);
				return;
			case LEFT_RIGHT_CLICK:
				toogleLeftRightClick(pack.getArguments());
				return;
			default: return;
		}
		
	}
	
	



	private static void sendResetCameraPackage() {
		// TODO Auto-generated method stub	
	}


	public static void movePlayer(BitArray movementClues) throws Exception
	{
		//WSADJCS (W,A,S,D,Jump,Crouch,Sprint)
		pc.setForward(movementClues.bitAt(0));
		pc.setBackward(movementClues.bitAt(1));
		pc.setLeft(movementClues.bitAt(2));
		pc.setRight(movementClues.bitAt(3));
		pc.jumping(movementClues.bitAt(4));
		pc.sneaking(movementClues.bitAt(5));
		pc.sprinting(movementClues.bitAt(6));
	}
	
	public static void selectItem(BitArray hotBarClues) throws Exception {
		if(hotBarClues.bitAt(0)) {pc.nextTool();return;}
		pc.prevTool();
	}
	
	private static void toogleLeftRightClick(BitArray[] arguments) throws Exception {
		pc.leftClicking(arguments[0].bitAt(0));
		pc.rightClicking(arguments[1].bitAt(0));
	}
	
	
	public static void moveCamera(BitArray[] arguments) throws Exception{
		float around= (float) ((float) arguments[0].getInt()/Math.pow(2, arguments[0].getSize())*360);
		float upDown= (float) ((float) arguments[1].getInt()/(Math.pow(2, arguments[1].getSize()))*180)-90;
		
		pc.setCamera(around,upDown);
		return;
	}
	
	
	public static void sendToPlayer(String data) {
        //format and send to player
		StringTextComponent baseText= new StringTextComponent("");
		baseText.appendSibling(new StringTextComponent("\u00A73"+"Port: "));
		baseText.appendSibling(new StringTextComponent("\u00A7f"+data));
		Minecraft.getInstance().player.sendMessage(baseText);
	}
	
	
}

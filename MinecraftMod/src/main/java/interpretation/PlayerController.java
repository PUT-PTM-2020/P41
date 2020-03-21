package interpretation;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;

public class PlayerController {
	//CONTROL
	// W S A D U B (W,S,A,D,Up,Below)
	// 1 0 0 0 0 0 -goes forward
	public int CAMERA_PITCH_INCREMENT=2;
	public int CAMERA_YAW_INCREMENT=2;
	
	private GameSettings gs = null;
	private ClientPlayerEntity player= null;
	byte currentItem = 0;
	int toolNumber = 10;
	
	PlayerController() 
	{ 
		gs= Minecraft.getInstance().gameSettings;
		this.player = Minecraft.getInstance().player;
	}
	
	

	//PLAYER MOVEMENT
	 //helper
	 public void setForward(boolean b){ KeyBinding.setKeyBindState(gs.keyBindForward.getKey(), b);}  
	 public void setBackward(boolean b) { KeyBinding.setKeyBindState(gs.keyBindBack.getKey(), b);}
	 public void setLeft(boolean b) { KeyBinding.setKeyBindState(gs.keyBindLeft.getKey(), b);}
	 public void setRight(boolean b) { KeyBinding.setKeyBindState(gs.keyBindRight.getKey(), b);} 
	 
	 //direction
	 public void startMovingForward() { setBackward(false); setForward(true);}
	 public void stopMovingForward()  { setForward(false);}
	 public void startMovingBackwards() { setForward(false); setBackward(true);}
	 public void stopMovingBackwards()  { setBackward(false);}
	 public void startMovingLeft() { setRight(false); setLeft(true);}
	 public void stopMovingLeft()  { setLeft(false);}
	 public void startMovingRight() { setLeft(false); setRight(true);}
	 public void stopMovingRight()  { setRight(false);}
	 
	 //movement speed
	 public void sprinting(boolean b) {KeyBinding.setKeyBindState(gs.keyBindSprint.getKey(), b);}
	 public void startSprinting() { KeyBinding.setKeyBindState(gs.keyBindSprint.getKey(), true);}
	 public void stopSprinting() {KeyBinding.setKeyBindState(gs.keyBindSprint.getKey(), false);}
	 
	 //jumping
	 public void jumping(boolean b) {KeyBinding.setKeyBindState(gs.keyBindJump.getKey(), b);}
	 public void startJumping() { KeyBinding.setKeyBindState(gs.keyBindJump.getKey(), true);}
	 public void stopJumping()  { KeyBinding.setKeyBindState(gs.keyBindJump.getKey(), false);}  
	 //sneaking
	 public void sneaking(boolean b) {/*is this even possible?*/}
	 
	//LEFT/RIGHT CLICK
	 //left
	 public void startLeftClicking() { KeyBinding.setKeyBindState(gs.keyBindUseItem.getKey(), true);}
	 public void stopLeftClicking() {KeyBinding.setKeyBindState(gs.keyBindUseItem.getKey(), false);}
	 public void leftClicking(boolean b) {KeyBinding.setKeyBindState(gs.keyBindUseItem.getKey(), b);}
	 //right
	 public void startRightClicking() { KeyBinding.setKeyBindState(gs.keyBindAttack.getKey(), true);}
	 public void stopRightClicking() {KeyBinding.setKeyBindState(gs.keyBindAttack.getKey(), false);}
	 public void rightClicking(boolean b) {KeyBinding.setKeyBindState(gs.keyBindAttack.getKey(), b);}
	    	
	//CAMERA  

	 
	//TOOLS
	 public void useInterface(String interfaceClues) {
		 if(interfaceClues.length()!=6) {return;}
		 /*
		 if(interfaceClues.charAt(0)=='1') {player.rotationPitch=player.rotationPitch+CAMERA_PITCH_INCREMENT;}
		 if(interfaceClues.charAt(1)=='1') {player.rotationPitch=player.rotationPitch+CAMERA_PITCH_INCREMENT;}
		 if(interfaceClues.charAt(2)=='1') {player.rotationPitch=player.rotationPitch-CAMERA_PITCH_INCREMENT;}
		 if(interfaceClues.charAt(3)=='1') {player.rotationPitch=player.rotationPitch-CAMERA_PITCH_INCREMENT;}
		 if(interfaceClues.charAt(4)=='1') {player.rotationYaw=player.rotationYaw+CAMERA_YAW_INCREMENT ;}
		 if(interfaceClues.charAt(5)=='1') {player.rotationYaw=player.rotationYaw-CAMERA_YAW_INCREMENT ;}
		 */
	 }
	 
	 public void selectTool(int toolIndex) {
		 currentItem= (byte) (toolIndex & 0x00);
		 updateSelection();
	 }
	 public void nextTool() {
		currentItem= (byte) ((currentItem & 0x01)%toolNumber);
		updateSelection();
	 }
	 public void prevTool() {
		 if(currentItem ==0x00) {currentItem= (byte) toolNumber; return;}
		 currentItem= (byte) (currentItem & -(0x01));
		 updateSelection();
	 }
	 public int getTool() {
		int result =  currentItem & 0xFF;
		return result;
	 } 
	 public void updateSelection(){
		 player.inventory.currentItem= (int) currentItem;
	 }


	 
	//EQ
	 
	 //CRAFTING
	    
	 public GameSettings getGs() {
			return gs;
		}

		public void setGs(GameSettings gs) {
			this.gs = gs;
		}

		public ClientPlayerEntity getPlayer() {
			return player;
		}

		public void setPlayer(ClientPlayerEntity player) {
			this.player = player;
		}

		public byte getCurrentItem() {
			return currentItem;
		}

		public void setCurrentItem(byte currentItem) {
			this.currentItem = currentItem;
		}

		public int getToolNumber() {
			return toolNumber;
		}

		public void setToolNumber(int toolNumber) {
			this.toolNumber = toolNumber;
		}
	     
}

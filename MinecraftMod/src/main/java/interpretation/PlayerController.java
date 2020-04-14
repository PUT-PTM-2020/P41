package interpretation;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

public class PlayerController {
	
	private GameSettings gs = null;
	private PlayerEntity player= null;
	
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
	public void setCamera(float around,float upDown) {setCameraAround(around);setCameraUpDown(upDown);}
	public void setCameraAround(float yaw) { this.player.rotationYaw=yaw;} //0-360 degrees
	public void setCameraUpDown(float pitch) {this.player.rotationPitch=pitch;} //-90 to 90

	 
	//TOOLS
	 public void nextTool() {
		 player.inventory.changeCurrentItem(10);
	 }
	 public void prevTool() {
		 player.inventory.changeCurrentItem(-10);
	 }

	 
	 //CRAFTING
	   
	 //GETTERS/SETTERS
	 public GameSettings getGs() {
		return gs;
	}

	public void setGs(GameSettings gs) {
		this.gs = gs;
	}

	public PlayerEntity getPlayer() {
		return player;
	}

	public void setPlayer(ClientPlayerEntity player) {
		this.player = player;
	}

}

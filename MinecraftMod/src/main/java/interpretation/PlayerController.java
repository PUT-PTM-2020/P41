package interpretation;

import InventoryControl.CraftingGUI;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;


public class PlayerController {
	
	private GameSettings gs = null;
	private PlayerEntity player= null;
	private boolean inventoryStatus=false;
	private CraftingGUI GUI=new CraftingGUI(false);
	
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
		 public void sneaking(boolean b) {}
	 
	//LEFT/RIGHT CLICK
		 //left
		 public void startLeftClicking() { KeyBinding.setKeyBindState(gs.keyBindUseItem.getKey(), true);}
		 public void stopLeftClicking() {KeyBinding.setKeyBindState(gs.keyBindUseItem.getKey(), false);}
		 public void leftClicking(boolean b) {System.out.print("LEFT");KeyBinding.setKeyBindState(gs.keyBindUseItem.getKey(), b);}
		 //right
		 public void startRightClicking() { KeyBinding.setKeyBindState(gs.keyBindAttack.getKey(), true);}
		 public void stopRightClicking() {KeyBinding.setKeyBindState(gs.keyBindAttack.getKey(), false);}
		 public void rightClicking(boolean b) {System.out.print("RIGHT");KeyBinding.setKeyBindState(gs.keyBindAttack.getKey(), b);}
	    	
	//CAMERA  
		public void setCamera(float around,float upDown) {setCameraAround(around);setCameraUpDown(upDown);}
		public void setCameraAround(float yaw) { this.player.rotationYaw=yaw;} //0-360 degrees
		public void setCameraUpDown(float pitch) {this.player.rotationPitch=pitch;} //-90 to 90

	 
	//TOOLS
	
		 public void nextTool() { player.inventory.changeCurrentItem(-10); }
		 public void prevTool() { player.inventory.changeCurrentItem(10); }

	 
	 //CRAFTING
		 public void toogleInventory() {

			 inventoryStatus=!inventoryStatus;
			 if(inventoryStatus) {openInventory(); return;}
			 closeInventory();
		 }
		 public void setOpenTo(boolean status) {if(this.inventoryStatus) {this.openInventory();}else{this.closeInventory();}}
		 public void openInventory() { Minecraft.getInstance().displayGuiScreen(GUI); }
		 public void closeInventory() {GUI.onClose(); }
		 
		 public void gesture(boolean gesture) {
			 //clockwise motion (right,down)
			 if(gesture) {
				 GUI.nextCategory();
				 return; 
			 }
			 //counterclockwise motion (left,down)
			 GUI.previousCategory();
		 }
	   
	//GETTERS/SETTERS
		public GameSettings getGs() { return gs;}
		public PlayerEntity getPlayer() { return player;}
		public void setPlayer(ClientPlayerEntity player) { this.player = player;}
		public void setGs(GameSettings gs) { this.gs = gs;}
		public void setInventoryStatus(boolean status) {this.inventoryStatus=status; this.setOpenTo(status);}
		public boolean getInventoryStatus() {return this.inventoryStatus;}
		
}

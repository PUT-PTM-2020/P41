package interpretation;

import InventoryControl.CraftingCategoryGUI;
import InventoryControl.CraftingGUI;
import InventoryControl.SerialGUI;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;


public class PlayerController {
	
	private GameSettings gs = null;
	private PlayerEntity player= null;
	ServerPlayerEntity splayer=Minecraft.getInstance().getIntegratedServer().getPlayerList().getPlayerByUUID(Minecraft.getInstance().player.getUniqueID());
	ServerWorld sworld=Minecraft.getInstance().getIntegratedServer().getWorld(DimensionType.OVERWORLD);
	private boolean inventoryStatus=false;
	private net.minecraft.client.multiplayer.PlayerController pc;
	private SerialGUI GUI;
	private Minecraft mc= Minecraft.getInstance();
	private LeftMouseButtonEmulator leftClick;
	private RightMouseButtonEmulator rightClick;
	
	
	PlayerController()
	{ 
		gs= Minecraft.getInstance().gameSettings;
		pc=Minecraft.getInstance().playerController;
		this.player = Minecraft.getInstance().player;
		this.rightClick=new RightMouseButtonEmulator();
		rightClick.setRun(false);
		this.leftClick=new LeftMouseButtonEmulator();
		leftClick.setRun(false);
		GUI= new CraftingGUI(false);
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
		 public void leftClicking(boolean b) {
			 if(b){startLeftClicking(); return;}
			 stopLeftClicking();
		}

		 public void startLeftClicking() {
			 this.leftClick=new LeftMouseButtonEmulator();
			 leftClick.setRun(true);
			 this.leftClick.start();
		 }
		 
		public void stopLeftClicking() {
			if(leftClick.getRun()) {
				//notify the thread that it's time to stop running
				 this.leftClick.setRun(false);
			}
			 
		 }
		 
		
		//right
		public void rightClicking(boolean b) {
			 if(b){startRightClicking(); return;}
			 stopRightClicking();
		}
		
		public void startRightClicking() {
			 this.rightClick=new RightMouseButtonEmulator();
			 rightClick.setRun(true);
			 this.rightClick.start();
		 }
		
		 public void stopRightClicking() {
			if(rightClick.getRun()) {
				 this.rightClick.setRun(false);
				 this.rightClick.stop();
				 KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKey(), false);
			}
		 }
		 
	    	
	//CAMERA  
		public void setCamera(float around,float upDown) {setCameraAround(around);setCameraUpDown(upDown);}
		public void setCameraAround(float yaw) { this.player.rotationYaw=yaw;} //0-360 degrees
		public void setCameraUpDown(float pitch) {this.player.rotationPitch=pitch;} //-90 to 90

	 
	//TOOLS
	
		 public void nextTool() { player.inventory.changeCurrentItem(-10); }
		 public void prevTool() { player.inventory.changeCurrentItem(10); }

	 
	 //CRAFTING
		 public void updateCraftingGUI() {
			 if(this.inventoryStatus) {openInventory(); return;}
			 closeInventory();
	     }
		 public void openInventory() {
			 GUI= new CraftingGUI(false);
			 mc.displayGuiScreen(GUI);}
		 public void closeInventory() {GUI.onClose(); }
		 
		 public void gestureUp() {
			 GUI.next();
		 }
		 public void gestureDown() {
			GUI.previous();
		 }
		 public void clickSelected() {
			 SerialGUI temp= GUI.rightClickFocusedButton();
			 if(temp!=null) {this.GUI=temp;}
		 }
	   
	//GETTERS/SETTERS
		public GameSettings getGs() { return gs;}
		public PlayerEntity getPlayer() { return player;}
		public void setPlayer(ClientPlayerEntity player) { this.player = player;}
		public void setGs(GameSettings gs) { this.gs = gs;}
		public void setInventoryStatus(boolean status) {
			this.inventoryStatus=status; updateCraftingGUI();
			}
		public boolean getInventoryStatus() {return this.inventoryStatus;}
		
}

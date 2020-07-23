package interpretation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.InputEvent.ClickInputEvent;

public class LeftMouseButtonEmulator extends Thread {

		private Minecraft mc= Minecraft.getInstance();
		private boolean run=true;
		
		LeftMouseButtonEmulator(){
			this.run=true;
		}
		
	    public void run(){
	    	//press the key
	   	    KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKey(), true);
	   	    System.out.println("LEFT CLICKING BEGIN");
	   	    //while key pressed
		    while(run) {
		    	System.out.println("IN LOOP");
		    	ClickInputEvent inputEvent = ForgeHooksClient.onClickInput(0, mc.gameSettings.keyBindAttack, Hand.MAIN_HAND);
		        if (!inputEvent.isCanceled()) {
		        	System.out.println("EVENT NOT CANCELLED");
		        	switch(mc.objectMouseOver.getType()) {
			        case ENTITY:
			        	mc.playerController.attackEntity(mc.player, ((EntityRayTraceResult)mc.objectMouseOver).getEntity());
			           break;
			        case BLOCK:
			           BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)mc.objectMouseOver;
			           BlockPos blockpos = blockraytraceresult.getPos();
			           if (!mc.world.isAirBlock(blockpos)) {
			        	  System.out.println("BUM");
			        	  mc.playerController.clickBlock(blockpos, blockraytraceresult.getFace());
			              break;
			           }
			        case MISS:
			        	mc.player.resetCooldown();
			           net.minecraftforge.common.ForgeHooks.onEmptyLeftClick(mc.player);
			        }
		        }    
		        if (inputEvent.shouldSwingHand()) { mc.player.swingArm(Hand.MAIN_HAND);}
		        
		        //sleep
		        try {
					Thread.sleep(60);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		    }
		    //unpress the key
		    KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKey(), false);
	    }
	    
	    public void setRun(boolean b) {this.run=b;}
	    public boolean getRun() {return this.run;}
}

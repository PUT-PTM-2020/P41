package interpretation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.InputEvent.ClickInputEvent;

public class RightMouseButtonEmulator extends Thread{
	private ClientPlayerEntity player= Minecraft.getInstance().player;
	private Minecraft mc= Minecraft.getInstance();
	private boolean run=true;
	
	RightMouseButtonEmulator(){
		this.run=true;
	}
	
	
    public void run(){
    	
    	//press the key
   	    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKey(), true);
   	    
	    while(run) {
	    	if (!mc.playerController.getIsHittingBlock()) {
	            if (!(this.player).isRowingBoat()) {
	               for(Hand hand : Hand.values()) {
	                  ClickInputEvent inputEvent = ForgeHooksClient.onClickInput(1, mc.gameSettings.keyBindUseItem, hand);
	                  if (inputEvent.isCanceled()) {
	                     if (inputEvent.shouldSwingHand()) this.player.swingArm(hand);
	                     return;
	                  }
	                  ItemStack itemstack = this.player.getHeldItem(hand);
	                  if (mc.objectMouseOver != null) {
	                     switch(mc.objectMouseOver.getType()) {
	                     case ENTITY:
	                        EntityRayTraceResult entityraytraceresult = (EntityRayTraceResult)mc.objectMouseOver;
	                        Entity entity = entityraytraceresult.getEntity();
	                        ActionResultType actionresulttype = mc.playerController.interactWithEntity(this.player, entity, entityraytraceresult, hand);
	                        if (!actionresulttype.func_226246_a_()) {
	                           actionresulttype = mc.playerController.interactWithEntity(this.player, entity, hand);
	                        }

	                        if (actionresulttype.func_226246_a_()) {
	                           if (actionresulttype.func_226247_b_()) {
	                              if (inputEvent.shouldSwingHand())
	                              this.player.swingArm(hand);
	                           }

	                           return;
	                        }
	                        break;
	                     case BLOCK:
	                        BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)mc.objectMouseOver;
	                        int i = itemstack.getCount();
	                        ActionResultType actionresulttype1 = mc.playerController.func_217292_a(this.player, mc.world, hand, blockraytraceresult);
	                        if (actionresulttype1.func_226246_a_()) {
	                           if (actionresulttype1.func_226247_b_()) {
	                              if (inputEvent.shouldSwingHand())
	                              this.player.swingArm(hand);
	                              if (!itemstack.isEmpty() && (itemstack.getCount() != i || mc.playerController.isInCreativeMode())) {
	                            	  mc.gameRenderer.itemRenderer.resetEquippedProgress(hand);
	                              }
	                           }

	                           return;
	                        }

	                        if (actionresulttype1 == ActionResultType.FAIL) {
	                           return;
	                        }
						case MISS:
							break;
						default:
							break;
	                     }
	                  }

	                  if (itemstack.isEmpty() && (mc.objectMouseOver == null || mc.objectMouseOver.getType() == RayTraceResult.Type.MISS))
	                      net.minecraftforge.common.ForgeHooks.onEmptyClick(this.player, hand);

	                  if (!itemstack.isEmpty()) {
	                     ActionResultType actionresulttype2 = mc.playerController.processRightClick(this.player, mc.world, hand);
	                     if (actionresulttype2.func_226246_a_()) {
	                        if (actionresulttype2.func_226247_b_()) {
	                           this.player.swingArm(hand);
	                        }

	                        mc.gameRenderer.itemRenderer.resetEquippedProgress(hand);
	                        return;
	                     }
	                  }
	               }

	            }
	         }
	  
	        //sleep
	        try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    }
		//unpress the key
   	    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKey(), false);
    }
    
    public void setRun(boolean b) {this.run=b;}
    public boolean getRun() {return this.run;}
}

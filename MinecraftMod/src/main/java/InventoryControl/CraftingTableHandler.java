package InventoryControl;


import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CraftingTableHandler {
    @SubscribeEvent
    public void tableOpen(RightClickBlock event) {
    	BlockState bs=Minecraft.getInstance().world.getBlockState(event.getPos());
    	if(bs.getBlock().equals(Blocks.CRAFTING_TABLE)) {
    		//stop normal menu from poping up
    		event.setUseBlock(Result.DENY);
    		//launch custom gui
    		interpretation.PlayerController.setInventoryStatus(true, true);
    	}
    		
    }
}

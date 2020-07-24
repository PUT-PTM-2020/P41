package InventoryControl;


import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.serial.serialmod.Serial;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.RecipeBookCategories;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CategoryButton extends Button {
	//instantialize all the icons
	public static final  Map<RecipeBookCategories,ResourceLocation> icons =Stream.of(new Object[][] { 
		    { RecipeBookCategories.REDSTONE, new ResourceLocation( Serial.MODID, "textures/gui/redstone_button.png") }, 
		    { RecipeBookCategories.SEARCH, new ResourceLocation( Serial.MODID,"textures/gui/searchbutton.png") }, 
		    { RecipeBookCategories.BUILDING_BLOCKS, new ResourceLocation( Serial.MODID,"textures/gui/buildingblocks_button.png") }, 
		    { RecipeBookCategories.EQUIPMENT, new ResourceLocation( Serial.MODID,"textures/gui/equipment_button.png") },
		    { RecipeBookCategories.MISC, new ResourceLocation( Serial.MODID,"textures/gui/misc_button.png") }, 
		    { RecipeBookCategories.FURNACE_BLOCKS, new ResourceLocation( Serial.MODID,"textures/gui/furnaceblocks_button.png") }, 
		    { RecipeBookCategories.FURNACE_FOOD, new ResourceLocation( Serial.MODID,"textures/gui/food_button.png") }, 
		    { RecipeBookCategories.SMOKER_FOOD, new ResourceLocation( Serial.MODID,"textures/gui/food_button.png") }, 
		    { RecipeBookCategories.CAMPFIRE, new ResourceLocation( Serial.MODID,"textures/gui/campfire_button.png") }, 
		}).collect(Collectors.toMap(data -> (RecipeBookCategories) data[0], data -> (ResourceLocation) data[1]));
	//this specific button's  icon
	private ResourceLocation icon;
		
	public CategoryButton(int xIn, int yIn, int widthIn, int heightIn, RecipeBookCategories category,Button.IPressable onPress) {
		super(xIn, yIn, widthIn, heightIn, category.name(), onPress);
		this.icon=icons.get(category);
	}

	@Override 
	public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
	
		Minecraft.getInstance().getTextureManager().bindTexture(icon);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	    RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        int j = 0;
        if (this.active) {
           j += this.width * 2;
        } else if (this.isHovered()) {
           j += this.width * 3;
        }
        
        
        this.blit(this.x, this.y, j, 0, this.width, this.height);
        
            
     } 


	

}

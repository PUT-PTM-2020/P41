package InventoryControl;


import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.serial.serialmod.Serial;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.RecipeBookCategories;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
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
	private CraftingCategoryGUI GUI;

	
	public CategoryButton(int xIn, int yIn, int buttonWidth, int buttonHeight, String cat,
			IPressable onPress, CraftingCategoryGUI GUI) {
			super(xIn, yIn, buttonWidth, buttonHeight, cat, onPress);
			this.GUI=GUI;
			this.icon=icons.get(cat);
	}

	public CraftingCategoryGUI getGUI() {
		return this.GUI;
	}

	@Override 
	public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
	  Minecraft minecraft = Minecraft.getInstance();
      FontRenderer fontrenderer = minecraft.fontRenderer;
      minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
      int i = this.getYImage(this.isHovered());
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      this.blit(this.x, this.y, 0,  i * 20, this.width / 2, this.height);
      this.blit(this.x + this.width / 2, this.y, 200 - this.width / 2,  i * 20, this.width / 2, this.height);
      this.renderBg(minecraft, p_renderButton_1_, p_renderButton_2_);
      int j = getFGColor();
      this.drawCenteredString(fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
     } 


	

}

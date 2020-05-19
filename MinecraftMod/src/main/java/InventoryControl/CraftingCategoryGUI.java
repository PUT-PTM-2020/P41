package InventoryControl;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import interpretation.SerialMessageInterpreter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class CraftingCategoryGUI extends Screen{
	private String category;
	private List<IRecipe<?>> craftable;
	//back button handling
	private final Screen lastScreen;
	//scroll handling
	private boolean isScrollPressed = false;
	private static final int scrollMax = 93;
	private int scrollPos; //up to 93
	//scroll texture
	public static final ResourceLocation GUI = new ResourceLocation("textures/gui/book.png");
	//itemTextures
	

	protected CraftingCategoryGUI(String category,List<IRecipe<?>> craftable,Screen lastScreen) {
		super(new StringTextComponent(category));
		this.category=category;
		this.craftable=craftable;
		this.lastScreen=lastScreen;
		scrollPos=0;
	}
	
	
	public void tick() { super.tick();}
	
	public void init() {
		super.init();
		
		//add back button
	     this.addButton(new Button(this.width/2 - 100, this.height / 4 -16, 200, 20,"<- back", (p_213070_1_) -> {
	          this.minecraft.displayGuiScreen(lastScreen);
	       }));
		
		
		//show all available recipes
		int i=1;
		for(IRecipe<?> recipe:craftable) {
				 this.addButton(new Button(this.width/2 - 100, this.height / 4 + (24*i) + -16, 200, 20, recipe.getRecipeOutput().getItem().toString(), (p_213055_1_) -> {
			         craftItem(recipe);
			      }));
				 i++;
			}
			
	}
	
	//when a recipe is chosen
	private void craftItem(IRecipe<?> recipe) {
		ServerPlayerEntity splayer=Minecraft.getInstance().getIntegratedServer().getPlayerList().getPlayerByUUID(Minecraft.getInstance().player.getUniqueID());
		
		//delete the ingredients from player's inventory
		
		NonNullList<Ingredient> ingredients=recipe.getIngredients();

		for(Ingredient ing:ingredients) 
		{
			//save current Stacks in temp array and iterate over it
			ItemStack [] currentStacks=ing.getMatchingStacks();
			for(ItemStack stack:currentStacks) 
			{
				if(!stack.isEmpty()) 
				{
					for(int i=0;i<splayer.inventory.getSizeInventory();i++)
					   {
						   ItemStack itemstack = splayer.inventory.getStackInSlot(i);
				           if (itemstack.isItemEqual(stack)) 
				           {
				        	   itemstack.setCount(itemstack.getCount()-stack.getCount());
				           }
					   }	
				}
			}
		
		}
			
		//so that the client sees it imediately
				Minecraft.getInstance().player.inventory.addItemStackToInventory(recipe.getRecipeOutput());
		//add crafted item
		splayer.inventory.addItemStackToInventory(recipe.getRecipeOutput());
		
	}
	
	public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
		  this.renderBackground();
		  //displace everything vertically by offset
		  p_render_2_=p_render_2_+getOffset();
		  
		  
		  this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 10, 16777215);
		  
		  this.renderScroll(p_render_2_);
		  
		  //draw only buttons in view (with offset)
	      for(int i = 0; i < this.buttons.size(); ++i) {
	          this.buttons.get(i).render(p_render_1_, p_render_2_, p_render_3_);
	       }
	 }
	
	private void renderScroll(int mouseY) {
		if (isScrollPressed) {
			scrollPos = mouseY - 7 - (height / 2 - 49);
			handleScrollPos();
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getInstance().getTextureManager().bindTexture(GUI);
		
	}
	
	
	private void handleScrollPos(){
		if(scrollPos < 0) { scrollPos = 0;}
		scrollPos=scrollPos%scrollMax;
	}
	
	private int getOffset(){
		int offset = Math.round((scrollPos * (craftable.size() - 5)) / scrollMax);
		if(offset < 0)
			return  0;
		return offset;
	}
	
	
}

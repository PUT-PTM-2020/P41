package InventoryControl;


import java.util.List;

import org.lwjgl.opengl.GL11;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
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
	
	//view handling
	private int currentItem;
	//private Widget Icon;
	//scroll texture
	public static final ResourceLocation GUI = new ResourceLocation("textures/gui/book.png");
	//itemTextures
	

	protected CraftingCategoryGUI(String category,List<IRecipe<?>> craftable,Screen lastScreen) {
		super(new StringTextComponent(category));
		this.category=category;
		this.craftable=craftable;
		this.lastScreen=lastScreen;
		currentItem=0;
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
			
		//add it on client side, so that the client sees it immediately
		Minecraft.getInstance().player.inventory.addItemStackToInventory(recipe.getRecipeOutput());
		//add crafted item
		splayer.inventory.addItemStackToInventory(recipe.getRecipeOutput());
		
	}
	
	public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
		  this.renderBackground();
		  this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 10, 16777215);
		  //render ICON
		  
		  
		  //render crafting recipe
		  
		  //draw buttons
		  super.render(p_render_1_,p_render_2_,p_render_3_);
	 }
	
	
	
	
}

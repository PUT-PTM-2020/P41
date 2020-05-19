package InventoryControl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import interpretation.SerialMessageInterpreter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.StringTextComponent;


public class CraftingGUI extends Screen {
	private List<IRecipe<?>> craftable;
	private Map<RecipeBookCategories,List<IRecipe<?>>> craftableByCategory;
	private ClientRecipeBook book;
	private ArrayList<ItemStack> inventory;
	private boolean launchedByCraftingTable=false;
	
	public CraftingGUI(boolean launchedByCraftingTable) {
		super(new StringTextComponent("crafting"));
		book=Minecraft.getInstance().player.getRecipeBook();
		craftable= new ArrayList<IRecipe<?>>();
		craftableByCategory= new HashMap<RecipeBookCategories, List<IRecipe<?>>>();
		this.launchedByCraftingTable=launchedByCraftingTable;
		this.inventory= new ArrayList<ItemStack>();
	}
	

	public void tick() { super.tick();}
	
	public void init() {
		this.buttons.clear();
		this.craftable.clear();
		this.craftableByCategory.clear();
		
		super.init();
		//update Craftable recipies
		try { updateCraftableRecipies(launchedByCraftingTable);} catch (Exception e) {e.printStackTrace();}
		//get all recipies categories
		updateCategories();
		if(!craftableByCategory.isEmpty())
		{
			
			//button for each category
			int i=1;
			for(RecipeBookCategories cat:craftableByCategory.keySet()) {
				 this.addButton(new Button(this.width/2 - 100, this.height / 4 + (24*i) + -16, 200, 20,cat.name(), (p_213055_1_) -> {
			         this.minecraft.displayGuiScreen(new CraftingCategoryGUI(cat.name(),craftableByCategory.get(cat), this));
			      }));
				 i++;
			}
			
		}
	}
	
	//for player to know which button is currently selected
	public void focusButton(int buttonIndex) {
		//defocus all the other buttons
		for(int i=0;i<this.buttons.size();i++) 
		{
			this.buttons.get(i).changeFocus(false);
		}
		this.buttons.get(buttonIndex).changeFocus(true);
		
	}
	
	public void rightClickFocusedButton() {
		for(int i=0;i<this.buttons.size();i++) 
		{
			if(this.buttons.get(i).isFocused()) {((Button) this.buttons.get(i)).onPress();}
		}
	}
	
	public void updateCategories() {
		if(craftable!=null) 
		{
			for(IRecipe<?> rec:craftable) 
			{
	
				 RecipeBookCategories s = getCategory(rec);
				 //get currently stored list
				 List<IRecipe<?>> recipeList = craftableByCategory.get(s);
				 //check for null pointer
				 if(recipeList==null) { recipeList = Arrays.asList(rec);}
				 else {	 
					 recipeList = new ArrayList<IRecipe<?>>(recipeList);
					 recipeList.add(rec); 
				 }	
				 craftableByCategory.put(s,recipeList);
			}
			SerialMessageInterpreter.sendToPlayer("\nKATEGORYZOWANIE UDANE!\nRozbito na "+Arrays.toString(craftableByCategory.keySet().toArray()));
		}
		
	}
	
	

	

	private  void updateCraftableRecipies(boolean launchedByCraftingTable) throws Exception{
		book=Minecraft.getInstance().player.getRecipeBook();
		List<RecipeList> allRecipies=book.getRecipes();
		SerialMessageInterpreter.sendToPlayer("WSZYSTKIE RECEPTURY W KSI¥¯CE "+allRecipies.size());
		int craftingwidth=2;
		int craftingheight=2;
		if(launchedByCraftingTable){craftingwidth=4;craftingheight=4;}
		
		
		//helper method
		this.inventory=getPlayerInventoryAsItemStacks();
		
		
		for(RecipeList rlist:allRecipies) 
		{
			Iterator<IRecipe<?>> it=rlist.getRecipes().iterator();
			IRecipe<?> temp=it.next();
		
			while(it.hasNext()) 
			{
				//check if recipe has been unlocked by the player
				if(book.isUnlocked(temp))
				{
					if(temp.canFit(craftingwidth, craftingheight)) {
						//check if player has the neccesary ingredients
						NonNullList<Ingredient> ingr=temp.getIngredients();

						boolean canBeCrafted=false;
						//as the same item can have multiple recipes they ought to be iterated over
						//printInventory();
						SerialMessageInterpreter.sendToPlayer("\nPróba zcraftowania " + temp.getRecipeOutput().toString());
						for(int i=0;i<ingr.size();i++) 
						{
							//save current Stacks in temp array and iterate over it
							ItemStack [] currentStacks=ingr.get(i).getMatchingStacks();
							canBeCrafted=false;
							
							SerialMessageInterpreter.sendToPlayer("\n[" +i+"]-czêœæ receptury\nPotrzebne sk³adniki: "+Arrays.toString(currentStacks));
							//in case the recipe requires an empty space 
							
							if(currentStacks.length==0) {canBeCrafted=true;}
							 for(int j=0;j<currentStacks.length;j++) 
							 {
								 int itemIndex=hasItemStack(currentStacks[j]);
								 if(itemIndex!=-1) 
								 {
									 SerialMessageInterpreter.sendToPlayer("\nTen sk³adnik mamy");
									 canBeCrafted=true;
									 decreaseStackByAmmount(itemIndex,currentStacks[j].getCount());
									 break;
								}  
							 }
							 if(!canBeCrafted) {break;}
						}
						if(canBeCrafted) 
						{
							SerialMessageInterpreter.sendToPlayer("\nSUKCES! Mo¿na craftowaæ "+temp.getRecipeOutput().toString());
							craftable.add(temp);break;
						}
					}	
				}		
				this.inventory=getPlayerInventoryAsItemStacks();
				temp=it.next();
			}
				
		}
			SerialMessageInterpreter.sendToPlayer("\nMOZNA ZCRAFTOWAC [ilosc]: "+craftable.size());
			SerialMessageInterpreter.sendToPlayer("\nJEST TO "+Arrays.toString(craftable.toArray()));
		}
	


	   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
		  this.renderBackground();
		  this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 10, 16777215);

	      super.render(p_render_1_, p_render_2_, p_render_3_);
	   }
	
	
	private RecipeBookCategories getCategory(IRecipe<?> recipe) {
	      IRecipeType<?> irecipetype = recipe.getType();
	      if (irecipetype == IRecipeType.SMELTING) {
	         if (recipe.getRecipeOutput().getItem().isFood()) {
	            return RecipeBookCategories.FURNACE_FOOD;
	         } else {
	            return recipe.getRecipeOutput().getItem() instanceof BlockItem ? RecipeBookCategories.FURNACE_BLOCKS : RecipeBookCategories.FURNACE_MISC;
	         }
	      } else if (irecipetype == IRecipeType.BLASTING) {
	         return recipe.getRecipeOutput().getItem() instanceof BlockItem ? RecipeBookCategories.BLAST_FURNACE_BLOCKS : RecipeBookCategories.BLAST_FURNACE_MISC;
	      } else if (irecipetype == IRecipeType.SMOKING) {
	         return RecipeBookCategories.SMOKER_FOOD;
	      } else if (irecipetype == IRecipeType.STONECUTTING) {
	         return RecipeBookCategories.STONECUTTER;
	      } else if (irecipetype == IRecipeType.CAMPFIRE_COOKING) {
	         return RecipeBookCategories.CAMPFIRE;
	      } else {
	         ItemStack itemstack = recipe.getRecipeOutput();
	         ItemGroup itemgroup = itemstack.getItem().getGroup();
	         if (itemgroup == ItemGroup.BUILDING_BLOCKS) {
	            return RecipeBookCategories.BUILDING_BLOCKS;
	         } else if (itemgroup != ItemGroup.TOOLS && itemgroup != ItemGroup.COMBAT) {
	            return itemgroup == ItemGroup.REDSTONE ? RecipeBookCategories.REDSTONE : RecipeBookCategories.MISC;
	         } else {
	            return RecipeBookCategories.EQUIPMENT;
	         }
	      }
	     
	   }
	
	private void printInventory(){
		SerialMessageInterpreter.sendToPlayer("\nINVENTARZ ");
		for(ItemStack item:inventory) {
			SerialMessageInterpreter.sendToPlayer("\n "+item.getItem().toString());
		}
		
	}
	
	 private ArrayList<ItemStack> getPlayerInventoryAsItemStacks() {
		 this.inventory.clear();
		 
		 ArrayList<ItemStack> temp= new ArrayList<ItemStack>();
		 PlayerInventory pInv= Minecraft.getInstance().player.inventory;
		 
		 for(int i=0;i<pInv.getSizeInventory();i++) {
			 ItemStack curritem=pInv.getStackInSlot(i);
			 if(!curritem.isEmpty()){ temp.add(curritem.copy());}
		 }
		return temp;
	}
	 
	 

	
	private void decreaseStackByAmmount(int index,int ammount) {
		ItemStack temp= this.inventory.get(index);
		if(temp.getCount()>=ammount) {
			temp.setCount(temp.getCount()-ammount);
		}
	}
	 
	public int hasItemStack(ItemStack itemStackIn) {
		   for(int i=0;i<this.inventory.size();i++)
		   {
			   ItemStack itemstack = inventory.get(i);
	            if (!itemstack.isEmpty() && itemstack.isItemEqual(itemStackIn) &&  itemstack.getCount()>=itemStackIn.getCount()) 
	            {
	            	//SerialMessageInterpreter.sendToPlayer("\nZnaleziono "+ itemStackIn.getItem().toString()+" w ekwipunku");
	                return i;
	            }
		   }
	           
	      return -1;
	   }



}

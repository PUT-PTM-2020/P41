package InventoryControl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import interpretation.SerialMessageInterpreter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.StringTextComponent;


public class CraftingGUI extends Screen {
	private int cwidth=800;
	private int cheight=800;
	private List<IRecipe<?>> craftable;
	private Map<RecipeBookCategories,List<IRecipe<?>>> craftableByCategory;
	private ClientRecipeBook book;
	private NonNullList<ItemStack> inventory;

	
	public CraftingGUI() {
		super(new StringTextComponent("crafting"));
		this.setSize(this.cwidth, this.cheight);
		book=Minecraft.getInstance().player.getRecipeBook();
		craftable= new ArrayList<IRecipe<?>>();
		craftableByCategory= new HashMap<RecipeBookCategories, List<IRecipe<?>>>();
	}
	
	public void init(boolean launchedByCraftingTable) throws Exception {
		//update Craftable recipies
		updateCraftableRecipies(launchedByCraftingTable);
		//get all recipies categories
		updateCategories();
		if(!craftableByCategory.isEmpty())
		{
			//button for each category
			for(RecipeBookCategories cat:craftableByCategory.keySet()) {
				SerialMessageInterpreter.sendToPlayer("\nDODAWANIE PRZYCISKU");
				 this.addButton(new Button(this.cwidth / 2 - 155, this.cheight / 6 + 48 - 6, 150, 20, cat.name(), (p_213055_1_) -> {
			         //this.minecraft.displayGuiScreen(new CraftingCategoryGUI(cat.name(),craftableByCategory.get(cat)));
			      }));
			}
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
					 recipeList = new ArrayList<>(recipeList);
					 recipeList.add(rec); 
					 }	
				 craftableByCategory.put(s,recipeList);
			}
			SerialMessageInterpreter.sendToPlayer("\nKATEGORYZOWANIE UDANE!\nRozbito na "+Arrays.toString(craftableByCategory.keySet().toArray()));
		}
		
	}
	
	

	public void displayGUI(boolean launchedByCraftingTable) throws Exception {
		//get all categorieshout
		init(launchedByCraftingTable);
		//this.minecraft.displayGuiScreen(this);
		this.render(10, 10, 10);
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
		
		for(RecipeList list:allRecipies) 
		{
			Iterator<IRecipe<?>> it=list.getRecipes().iterator();
			IRecipe<?> temp=it.next();
		
			while(it.hasNext()) 
			{
				//check if recipe has been unlocked by the player
				if(book.isUnlocked(temp))
				{
					if(temp.canFit(craftingwidth, craftingheight)) {
						//check if player has the neccesary ingredients
						NonNullList<Ingredient> ingr=temp.getIngredients();
						//flag variable, set as true by default
						boolean canBeCrafted=false;
						//as the same item can have multiple recipes they ought to be iterated over
						for(int i=0;i<ingr.size();i++) 
						{
							//save current Stacks in temp array and iterate over it
							ItemStack [] currentStacks=ingr.get(i).getMatchingStacks();
							canBeCrafted=false;
							SerialMessageInterpreter.sendToPlayer("\nCzêœæ receptury ["+i+"] Potrzebne sk³adniki: "+Arrays.toString(currentStacks));
							 for(int j=0;j<currentStacks.length;j++) 
							 {
								 int itemIndex=hasItemStack(currentStacks[j]);
								 if(itemIndex!=-1) 
								 {
									 canBeCrafted=true;
									 decreaseStackByAmmount(itemIndex,currentStacks[j].getCount());
									 break;
								}  
							 }
							 if(!canBeCrafted) {break;}
						}
						if(canBeCrafted) {craftable.add(temp);break;}
					}
					
				}	
				temp=it.next();
			}
				
		}
			SerialMessageInterpreter.sendToPlayer("\nMOZNA ZCRAFTOWAC [ilosc]: "+craftable.size());
			SerialMessageInterpreter.sendToPlayer("\nJEST TO "+Arrays.toString(craftable.toArray()));
		}
	


	public void tick() {
	      super.tick();
	   }

	   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
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
	
	 private NonNullList<ItemStack> getPlayerInventoryAsItemStacks() {
		 NonNullList<ItemStack> temp= NonNullList.withSize(36, ItemStack.EMPTY);
		 PlayerInventory pInv=Minecraft.getInstance().player.inventory;
		 for(int i=0;i<pInv.getSizeInventory();i++) {
			 ItemStack curritem=pInv.getStackInSlot(i);
			 if(curritem!=ItemStack.EMPTY) {temp.add(curritem);}
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
		   for(int i=0;i<this.inventory.size();i++) {
			   ItemStack itemstack = (ItemStack)inventory.get(i);
	            if (!itemstack.isEmpty() && itemstack.isItemEqual(itemStackIn) &&  itemstack.getCount()>=itemStackIn.getCount()) 
	            {
	                return i;
	            }
		   }
	           
	      return -1;
	   }



}

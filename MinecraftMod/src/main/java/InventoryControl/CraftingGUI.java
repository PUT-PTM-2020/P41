package InventoryControl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import interpretation.SerialMessageInterpreter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ServerRecipeBook;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.registries.ForgeRegistries;


public class CraftingGUI extends SerialGUI {
	private List<IRecipe<?>> craftable;
	private Map<RecipeBookCategories,List<IRecipe<?>>> craftableByCategory;
	private ClientRecipeBook book;
	private ArrayList<ItemStack> inventory;
	private boolean launchedByCraftingTable=false;
	private ServerPlayerEntity splayer=Minecraft.getInstance().getIntegratedServer().getPlayerList().getPlayerByUUID(Minecraft.getInstance().player.getUniqueID());
	
	public CraftingGUI(boolean launchedByCraftingTable) {
		super(new StringTextComponent("CRAFTING CATEGORIES"));
		book=Minecraft.getInstance().player.getRecipeBook();
		craftable= new ArrayList<IRecipe<?>>();
		craftableByCategory= new HashMap<RecipeBookCategories, List<IRecipe<?>>>();
		this.launchedByCraftingTable=launchedByCraftingTable;
		this.inventory= new ArrayList<ItemStack>();
		currentlySelected=0;
	}
	

	public void tick() { super.tick();}
	
	public void init() {
		this.buttons.clear();
		this.craftable.clear();
		this.craftableByCategory.clear();
		
		super.init();
		//update Craftable recipies
		try { updateCraftableRecipies();} catch (Exception e) {e.printStackTrace();}
		//get all recipies categories
		updateCategories();
		
		int buttonWidth=(int) (0.3*this.width);
		int buttonHeight=(int) (0.11*this.height);
		
		if(!craftableByCategory.isEmpty())
		{
			//button with all recepies
			CraftingCategoryGUI guiAll=new CraftingCategoryGUI("all",craftable, this);
			addButton(
					 new CategoryButton(this.width/2 -buttonWidth/2, (int) (this.height*0.26),buttonWidth,buttonHeight,"all", 
					 (p_213055_1_) -> {this.minecraft.displayGuiScreen(guiAll);},guiAll));
			//button for each category
			int i=1;
			for(RecipeBookCategories cat:craftableByCategory.keySet()) {
				 CraftingCategoryGUI gui=new CraftingCategoryGUI(cat.name(),craftableByCategory.get(cat), this);
				 this.addButton(
						 new CategoryButton(this.width/2 -buttonWidth/2, (int) (this.height*0.26+(buttonHeight*1.1)*i),buttonWidth,buttonHeight,cat.name(), 
						 (p_213055_1_) -> {this.minecraft.displayGuiScreen(gui);},gui));
				 i++;
			}
			this.focusButton(0);	
		}
	}
	
	
	@Override
	public SerialGUI rightClickFocusedButton() {
		for(int i=0;i<this.buttons.size();i++) 
		{
			if(this.buttons.get(i).isFocused()) {
				((CategoryButton) this.buttons.get(i)).onPress();
				return ((CategoryButton) this.buttons.get(i)).getGUI();
			}
		}
		return null;
	}
	
	public void updateCategories() {
		if(craftable!=null) 
		{
			//iterate over all craftable recipes
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
		}
		
	}

	
	public void updateCraftableRecipies() throws Exception{
		book = Minecraft.getInstance().player.getRecipeBook(); //gets all available recipes
		List<RecipeList> allRecipies= book.getRecipes();
		
		//for distinguishing between crafting in inventory or in a crafting table
		int craftingwidth=2; int craftingheight=2;
		if(launchedByCraftingTable){craftingwidth=3;craftingheight=3;}
		
		
		this.inventory=getPlayerInventoryAsItemStacks();
		//SerialMessageInterpreter.sendToPlayer("\nINVENTARZ: "+Arrays.deepToString(inventory.toArray()));
		//iterate over all available recipes
		for(RecipeList rlist:allRecipies) 
		{
			Iterator<IRecipe<?>> it=rlist.getRecipes().iterator();
			IRecipe<?> temp=it.next();
			while(it.hasNext()) 
			{
				//SerialMessageInterpreter.sendToPlayer("\n"+temp.getRecipeOutput().toString());
				//check if recipe has been unlocked by the player
				if(book.isUnlocked(temp))
				{
					if(temp.canFit(craftingwidth, craftingheight)) {
					//check if player has the neccesary ingredients
						NonNullList<Ingredient> ingr=temp.getIngredients();

						boolean canBeCrafted=false;
						boolean isAir=true;
						//as the same item can have multiple recipes they ought to be iterated over
						for(int i=0;i<ingr.size();i++) 
						{
							//save current Stacks in temp array and iterate over it
							ItemStack [] currentStacks=ingr.get(i).getMatchingStacks();
							canBeCrafted=false;
							
							//in case the recipe requires an empty space 
							if(currentStacks.length==0) {canBeCrafted=true;}
							 for(int j=0;j<currentStacks.length;j++) 
							 {
								 //method return -1 if player doesn't have the item, else return slot's index
								 int itemIndex=hasItemStack(currentStacks[j]);
								 if(itemIndex!=-1) 
								 {		
									 isAir=false;
									 canBeCrafted=true;
									 //decrease the ammount of the item 'used up' in this step of the recipe
									 decreaseStackByAmmount(itemIndex,currentStacks[j].getCount());
									 break;
								}  
							 }
						}
						if(canBeCrafted && !temp.getRecipeOutput().getItem().equals(Items.AIR)) {
							craftable.add(temp); break;
						}
						this.inventory=getPlayerInventoryAsItemStacks();
					}	
				}		
				temp=it.next();
			}
				
		}
	}
	


	   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
		  this.renderBackground();
		  
		  RenderSystem.pushMatrix();
		  float scaleFactor=1.6f;
		  RenderSystem.scalef(scaleFactor,scaleFactor, 1.0f);
		  this.drawCenteredString(this.font, this.title.getFormattedText(), (int) ((this.width/2)/scaleFactor), (int) ((this.height*0.1)/scaleFactor), 16777215);
		  RenderSystem.popMatrix();
		  
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
		 PlayerInventory pInv= splayer.inventory;
		 
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
	
	//source https://stackoverflow.com/questions/869033/how-do-i-copy-an-object-in-java
	public static Object cloneObject(Object obj){
        try{
            Object clone = obj.getClass().newInstance();
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if(field.get(obj) == null || Modifier.isFinal(field.getModifiers())){
                    continue;
                }
                if(field.getType().isPrimitive() || field.getType().equals(String.class)
                        || field.getType().getSuperclass().equals(Number.class)
                        || field.getType().equals(Boolean.class)){
                    field.set(clone, field.get(obj));
                }else{
                    Object childObj = field.get(obj);
                    if(childObj == obj){
                        field.set(clone, clone);
                    }else{
                        field.set(clone, cloneObject(field.get(obj)));
                    }
                }
            }
            return clone;
        }catch(Exception e){
            return null;
        }
    }



}

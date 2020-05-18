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
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.StringTextComponent;


public class CraftingGUI extends Screen {
	private int cwidth=300;
	private int cheight=100;
	private List<IRecipe<?>> craftable;
	private Map<String,List<IRecipe<?>>> craftableByCategory;
	private ClientRecipeBook book;

	
	public CraftingGUI() {
		super(new StringTextComponent("crafting"));
		this.setSize(this.cwidth, this.cheight);
		book=Minecraft.getInstance().player.getRecipeBook();
		craftableByCategory= new HashMap<String, List<IRecipe<?>>>();
	}
	
	public void init(boolean launchedByCraftingTable) throws Exception {
		//update Craftable recipies
		updateCraftableRecipies(launchedByCraftingTable);
		//get all recipies categories
		updateCategories();
		
		if(!craftableByCategory.isEmpty())
		{
			//buttons for each category
			for(String cat:craftableByCategory.keySet()) {
				 this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, I18n.format(cat), (p_213055_1_) -> {
			         this.minecraft.displayGuiScreen(new CraftingCategoryGUI(cat,craftableByCategory.get(cat)));
			      }));
			}
		}
		
	
	}
	
	public void updateCategories() {
		if(craftable!=null) 
		{
			for(IRecipe<?> rec:craftable) 
			{
				 String s = rec.getGroup();
				 //get currently stored list
				 List<IRecipe<?>> recipeList = craftableByCategory.get(s);
				 //check for null pointer
				 if(recipeList==null) { recipeList = Arrays.asList(rec); }
				 else {	 recipeList.add(rec); }	
				 craftableByCategory.put(s,recipeList);
			}
		}
	}
	
	

	public void displayGUI(boolean launchedByCraftingTable) throws Exception {
		//get all categories
		init(launchedByCraftingTable);
		this.minecraft.displayGuiScreen(this);
	}
	

	private  void updateCraftableRecipies(boolean launchedByCraftingTable) throws Exception{
		List<RecipeList> allRecipies=book.getRecipes();
		for(RecipeList list:allRecipies) 
		{
			Iterator<IRecipe<?>> it=list.getRecipes().iterator();
			//flag variable, set as true by default
			boolean canBeCrafted=true;
			IRecipe<?> temp=it.next();
			
			while(it.hasNext()) 
			{
				//check if recipe has been unlocked by the player
				if(book.isUnlocked(temp))
				{
					//check if player has the neccesary ingredients
					NonNullList<Ingredient> ingr=temp.getIngredients();
					//as the same item can have multiple recepies they ought to be iterated over
					for(int i=0;i<ingr.size();i++) 
					{
						ItemStack [] currentStacks=ingr.get(i).getMatchingStacks();
						//save current Stacks in temp array and iterate over it
						for(int j=0;j<currentStacks.length;j++) 
						{
							if(!Minecraft.getInstance().player.inventory.hasItemStack(currentStacks[j])) {canBeCrafted=false;break;}  
						}
						//1 way of crafting the item is enough to make it 'craftable'
						if(canBeCrafted) {craftable.add(temp);break;}
					}	
				}
				temp=it.next();
			}
		}
			SerialMessageInterpreter.sendToPlayer("\nMOZNA ZCRAFTOWAC [ilosc]: "+craftable.size());
		}



}

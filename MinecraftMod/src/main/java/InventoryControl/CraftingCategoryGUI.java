package InventoryControl;

import java.util.List;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class CraftingCategoryGUI extends Screen{
	private String category;
	private List<IRecipe<?>> craftable;

	protected CraftingCategoryGUI(String category,List<IRecipe<?>> craftable) {
		super(new StringTextComponent(category));
		this.category=category;
		this.craftable=craftable;
	}
	
	
	public void init(boolean launchedByCraftingTable) throws Exception {

	
	}
	
	
}

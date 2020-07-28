package InventoryControl;

import java.util.List;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.text.StringTextComponent;

public class SerialGUI extends Screen{
	protected int currentlySelected;
	
	
	SerialGUI(StringTextComponent stringTextComponent){
		super(stringTextComponent);
		currentlySelected=0;
	}
	
	public void next() {
		currentlySelected=(currentlySelected+1)%buttons.size();
		focusButton(currentlySelected);
	}
	
	public void previous() {
		currentlySelected--;
		if(currentlySelected<0) {currentlySelected=buttons.size()-1;}
		focusButton(currentlySelected);
	}
	
	//for player to know which button is currently selected
	public void focusButton(int buttonIndex) {
		if(buttonIndex<this.buttons.size()) {this.buttons.get(buttonIndex).changeFocus(true);}
	}
	
	
	public SerialGUI rightClickFocusedButton() {return null;}
}

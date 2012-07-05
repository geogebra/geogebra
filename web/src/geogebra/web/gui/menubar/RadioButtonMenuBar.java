package geogebra.web.gui.menubar;

import geogebra.common.gui.menubar.MenuInterface;
import geogebra.common.main.App;
import java.util.ArrayList;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RadioButton;

public class RadioButtonMenuBar extends MenuBar implements
        geogebra.common.gui.menubar.RadioButtonMenuBar {

	final private String menubarID;
	private ArrayList<RadioButton> radioButtons;
	private Object cmd;
	private String[] texts;
	private String[] commands;
	private MenuInterface listener;

	public RadioButtonMenuBar() {
		super(true);
		menubarID = DOM.createUniqueId();
		radioButtons = new ArrayList<RadioButton>();
	}

	public MenuItem addItem(String text, Command com) {
		RadioButton radioButton = new RadioButton(menubarID, text, true);		
		if (radioButtons.size()==0) radioButton.setValue(true);		
		radioButtons.add(radioButton);
		return super.addItem(radioButton.toString(), true, com);
	}

	public MenuItem addItem(String text, Command com, boolean selected) {
		RadioButton radioButton = new RadioButton(menubarID, text, true);		
		radioButton.setValue(selected);
		return super.addItem(radioButton.toString(), true, com);
	}

	
	public void setSelected_old(int itemIndex) {
//		boolean val;
//		for (int i = 0; i < radioButtons.size(); i++) {
//			val = (itemIndex == i) ? true : false;
//			radioButtons.get(itemIndex).setValue(val);
//			this.getItems().get(i).setHTML(radioButtons.get(i).toString());
//		} 
		
		

		radioButtons.get(itemIndex).setValue(true);
		MenuItem selectedItem;
		if (getSelectedItem() != null){
			selectedItem = this.getSelectedItem(); 
		} else {
			selectedItem = this.getItems().get(itemIndex);
		}
		selectedItem.setHTML(radioButtons.get(itemIndex).toString());
	}
	
	/**
	 * Create a set of radio buttons automatically.
	 * 
	 * @param al
	 * @param items
	 * @param actionCommands
	 * @param selectedPos
	 */
	public void addRadioButtonMenuItems(MenuInterface al,
			String[] items, final String[] actionCommands, int selectedPos) {
		texts = items;
		commands = actionCommands;
		listener = al;
		
		setSelected(selectedPos);
		

	}

	public void setSelected(int selectedPos) {
		clearItems();
		
		for (int i = 0; i < texts.length; i++) {
			if (texts[i] == "---") {
				addSeparator();
				radioButtons.add(null);
				
			} else {
				final int j=i;
				addItem(texts[i], new Command() {
					public void execute() {
						setSelected(j);
						OptionsMenuW.actionPerformed(commands[j]);
					}
				}, i == selectedPos);
				
				
			}
		}
	    
    }

}

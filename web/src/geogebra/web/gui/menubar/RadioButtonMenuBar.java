package geogebra.web.gui.menubar;

import geogebra.common.main.AbstractApplication;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RadioButton;

public class RadioButtonMenuBar extends MenuBar {

	final private String menubarID;
	private ArrayList<RadioButton> radioButtons;
	private Object cmd;

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

	public void setSelected(int itemIndex) {
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
	 * @param menu
	 * @param al
	 * @param items
	 * @param actionCommands
	 * @param selectedPos
	 */
	public void addRadioButtonMenuItems(OptionsMenu al,
			String[] items, final String[] actionCommands, int selectedPos) {
		MenuItem mi;
		
		for (int i = 0; i < items.length; i++) {
			if (items[i] == "---") {
				addSeparator();
				radioButtons.add(null);
			} else {
				final int j=i;
				addItem(items[i], new Command(){
						public void execute() {
				            setSelected(j);
							OptionsMenu.actionPerformed(actionCommands[j]);	
			            }
					});
				
				
			}
		}
		
		setSelected(selectedPos);
	}
}

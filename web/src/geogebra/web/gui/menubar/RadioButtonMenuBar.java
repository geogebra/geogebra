package geogebra.web.gui.menubar;

import java.util.ArrayList;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RadioButton;

public class RadioButtonMenuBar extends MenuBar {

	final private String menubarID;
	private ArrayList<RadioButton> radioButtons;

	public RadioButtonMenuBar() {
		super(true);
		menubarID = DOM.createUniqueId();
		radioButtons = new ArrayList<RadioButton>();
	}

	public MenuItem addItem(String text, RadioButtonCommand com) {
		RadioButton radioButton = new RadioButton(menubarID, text, true);		
		if (radioButtons.size()==0) radioButton.setValue(true);		
		radioButtons.add(radioButton);
		return super.addItem(radioButton.toString(), true, com);
	}

	public void setSelected(int itemIndex) {		
		boolean val;		
		radioButtons.get(itemIndex).setValue(true);
		this.getItems().get(itemIndex).setHTML(radioButtons.get(itemIndex).toString());
	}
}

package geogebra.web.gui.menubar;

import geogebra.common.gui.menubar.MyActionListener;
import geogebra.common.gui.menubar.RadioButtonMenuBar;
import geogebra.common.main.App;

import java.util.ArrayList;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * An implementation of a radio button menu bar. 
 * @author judit
 */
public class RadioButtonMenuBarW extends MenuBar implements RadioButtonMenuBar {

	final private String menubarID;
	private ArrayList<RadioButton> radioButtons;
	private String[] texts;
	private String[] commands;
	private MyActionListener listener;
	private App app;

	/**
	 * Creates a RadioButtonMenuBarW instance
	 * @param application Application instance
	 */
	public RadioButtonMenuBarW(App application) {
		super(true);
		menubarID = DOM.createUniqueId();
		radioButtons = new ArrayList<RadioButton>();
		app = application;
	}

	private MenuItem addItem(String text, Command com, boolean selected) {
		RadioButton radioButton = new RadioButton(menubarID, text, true);		
		radioButton.setValue(selected);
		radioButton.addStyleName("RadioButtonMenuItem");
		return super.addItem(radioButton.toString(), true, com);
	}
	
	public void addRadioButtonMenuItems(MyActionListener al,
			String[] items, final String[] actionCommands, int selectedPos, boolean changeText) {
		texts = items;
		if (changeText){
			for (int i=0; i<items.length; i++){
				texts[i] = app.getMenu(items[i]);
			}
		}
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
						listener.actionPerformed(commands[j]);
					}
				}, i == selectedPos);			
			}
		}	    
    }
}

package org.geogebra.web.full.gui.menubar;

import java.util.ArrayList;

import org.geogebra.common.gui.menubar.MyActionListener;
import org.geogebra.common.gui.menubar.RadioButtonMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * An implementation of a radio button menu bar. 
 * @author judit
 */
public class RadioButtonMenuBarW extends AriaMenuBar
		implements RadioButtonMenuBar {

	final private String menubarID;
	private ArrayList<RadioButton> radioButtons;
	private String[] texts;
	/** item commands */
	String[] commands;
	/** listener */
	MyActionListener listener;
	private AppW app;
	/** action side effect */
	Scheduler.ScheduledCommand itemSideEffect = null;

	/**
	 * Creates a RadioButtonMenuBarW instance
	 * 
	 * @param application
	 *            Application instance
	 * @param arrow
	 *            {@code true} if menu needs an arrow for a submenu
	 */
	public RadioButtonMenuBarW(AppW application, boolean arrow) {
		super();

		menubarID = DOM.createUniqueId();
		radioButtons = new ArrayList<>();
		app = application;

		if (app.isUnbundled()) {
			addStyleName("floating-Popup");
		}

		if (arrow) {
			MainMenu.addSubmenuArrow(this,
					app.isUnbundledOrWhiteboard());
		}

	}

	private AriaMenuItem addItem(String text, Command com, boolean selected) {
		RadioButton radioButton = new RadioButton(menubarID, text, true);
		radioButton.setValue(selected);
		radioButton.addStyleName("RadioButtonMenuItem");
		return super.addItem(radioButton.toString(), true, com);
	}
	
	@Override
	public void addRadioButtonMenuItems(MyActionListener al,
			String[] items, final String[] actionCommands, int selectedPos, boolean changeText) {
		texts = items;
		if (changeText) {
			for (int i = 0; i < items.length; i++) {
				texts[i] = app.getLocalization().getMenu(items[i]);
			}
		}
		commands = actionCommands;
		listener = al;
		setSelected(selectedPos);
	}

	@Override
	public void setSelected(int selectedPos) {
		clearItems();
		for (int i = 0; i < texts.length; i++) {
			if ("---".equals(texts[i])) {
				addSeparator();
				radioButtons.add(null);
				
			} else {
				final int j = i;
				addItem(texts[i], () -> {
					setSelected(j);
					listener.actionPerformed(commands[j]);
					if (itemSideEffect != null) {
						itemSideEffect.execute();
					}
				}, i == selectedPos);
			}
		}
	}

	/**
	 * @param sc
	 *            side effect
	 */
	public void registerItemSideEffect(Scheduler.ScheduledCommand sc) {
		itemSideEffect = sc;
	}

	@Override
	public int getItemCount() {
		return getItems().size();
	}

	@Override
	public void setEnabled(boolean value) {
	    for (RadioButton button: radioButtons) {
	    	if (button != null) {
	    		button.setEnabled(value);
	    	}
	    }
    }

	/**
	 * Wondering why they make protected methods if we can get them this way
	 * 
	 * @return MenuItem the selected item
	 */
	public AriaMenuItem getSelectedItemPublic() {
		return getSelectedItem();
	}
}

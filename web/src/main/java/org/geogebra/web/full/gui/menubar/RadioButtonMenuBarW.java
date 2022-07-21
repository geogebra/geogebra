package org.geogebra.web.full.gui.menubar;

import java.util.ArrayList;

import org.geogebra.common.gui.menubar.MyActionListener;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.components.radiobutton.ComponentRadioButton;
import org.geogebra.web.full.gui.components.radiobutton.RadioButtonData;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;

/**
 * An implementation of a radio button menu bar.
 */
public class RadioButtonMenuBarW extends AriaMenuBar {
	private final ArrayList<ComponentRadioButton<String>> radioButtons;
	private String[] texts;
	/** item commands */
	String[] commands;
	/** listener */
	MyActionListener listener;
	private final Localization loc;
	/** action side effect */
	Scheduler.ScheduledCommand itemSideEffect = null;

	/**
	 * Creates a RadioButtonMenuBarW instance
	 * @param loc - localization
	 */
	public RadioButtonMenuBarW(Localization loc) {
		super();

		radioButtons = new ArrayList<>();
		this.loc = loc;
	}

	private void addItem(String text, Command com, boolean selected) {
		RadioButtonData<String> data = new RadioButtonData<>(text, text);
		ComponentRadioButton<String> radioButton = new ComponentRadioButton<>(loc, data);
		radioButton.setSelected(selected);
		radioButton.addStyleName("RadioButtonMenuItem");
		super.addItem(radioButton.toString(), true, com);
	}

	/**
	 * @param al listener
	 * @param items items
	 * @param actionCommands commands
	 * @param selectedPos initial selected position
	 */
	public void addRadioButtonMenuItems(MyActionListener al,
			String[] items, final String[] actionCommands, int selectedPos) {
		texts = items;
		commands = actionCommands;
		listener = al;
		setSelected(selectedPos);
	}

	private void setSelected(int selectedPos) {
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
	 * @param sc - side effect
	 */
	public void registerItemSideEffect(Scheduler.ScheduledCommand sc) {
		itemSideEffect = sc;
	}

	/**
	 * Make all items enabled/disabled
	 * @param value whether to enable
	 */
	public void setEnabled(boolean value) {
		for (ComponentRadioButton<?> button: radioButtons) {
			if (button != null) {
				button.setDisabled(!value);
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

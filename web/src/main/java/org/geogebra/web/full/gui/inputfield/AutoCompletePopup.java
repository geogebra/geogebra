package org.geogebra.web.full.gui.inputfield;

import java.util.List;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;

public class AutoCompletePopup extends GPopupMenuW {

	/**
	 * constructor for the command autocomplete popup
	 * @param app - see {@link AppW}
	 */
	public AutoCompletePopup(AppW app) {
		super(app);
		getPopupPanel().addStyleName("autoCompletePopup");
		getPopupPanel().addStyleName("customScrollbar");
	}

	private void fillContent(final String curWord) {
		getApp().getParserFunctions().getCompletions(curWord).stream()
				.map(syntax -> syntax.split("\\(")[0])
				.forEach(this::addRow);
		List<String> cmdDict = getApp().getCommandDictionary()
				.getCompletions(curWord.toLowerCase());
		if (cmdDict != null) {
			cmdDict.forEach(this::addRow);
		}
	}

	private void addRow(String command) {
		AriaMenuItem menuItem = new AriaMenuItem(command, false, new AriaMenuBar());
		menuItem.addStyleName("no-image");
		menuItem.setFocusable(false);
		getPopupMenu().appendSubmenu(menuItem, MaterialDesignResources
				.INSTANCE.arrow_drop_right_black());
		getPopupMenu().addItem(menuItem);
	}

	/**
	 * fill popup with command list based on the user input
	 * @param curWord - user input
	 */
	public void fillAndShow(String curWord) {
		getPopupMenu().clear();
		fillContent(curWord);
		if (getPopupMenu().getWidgetCount() > 0) {
			popupPanel.show();
			getPopupMenu().selectItem(0);
		} else {
			popupPanel.hide();
		}
	}
}

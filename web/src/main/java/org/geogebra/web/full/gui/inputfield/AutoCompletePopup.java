package org.geogebra.web.full.gui.inputfield;

import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.main.localization.AutocompleteProvider;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteW;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.SharedResources;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AutoCompletePopup extends GPopupMenuW {

	private final AutocompleteProvider suggestions;
	private final AutoCompleteW component;
	private int prefixLength = 0;

	/**
	 * constructor for the command autocomplete popup
	 * @param app - see {@link AppW}
	 */
	public AutoCompletePopup(AppW app, boolean forCAS,
			AutoCompleteW component) {
		super(app);
		this.suggestions = new AutocompleteProvider(app, forCAS);
		this.component = component;
		getPopupPanel().addStyleName("autoCompletePopup");
		getPopupPanel().addStyleName("customScrollbar");
	}

	private void fillContent(final String curWord) {
		prefixLength = curWord.length();
		suggestions.getCompletions(curWord).forEach(this::addRow);
	}

	private void addRow(AutocompleteProvider.Completion cpl) {
		AriaMenuBar submenu = new AriaMenuBar();
		submenu.addStyleName("autocompleteSyntaxContent");
		submenu.addStyleName("customScrollbar");
		for (String line: cpl.syntaxes) {
			AriaMenuItem item = new AriaMenuItem(line.replaceAll("[<>]", ""),
					false, () -> {
				component.insertString(line);
				hide();
			});
			item.setFocusable(false);
			submenu.addItem(item);
		}
		AriaMenuItem menuItem = new AriaMenuItem(highlightSuffix(cpl.command),
				true, submenu);
		menuItem.setSubmenuHeading(buildSubmenuHeading(cpl));
		menuItem.addStyleName("no-image");
		menuItem.setFocusable(false);
		addItem(menuItem, false);
	}

	private Widget buildSubmenuHeading(AutocompleteProvider.Completion command) {
		FlowPanel heading = new FlowPanel();
		heading.addStyleName("autocompleteSyntaxHeading");
		heading.add(new Label(command.command));
		StandardButton w = new StandardButton(SharedResources.INSTANCE.icon_help_black(),
				null, 24);
		w.addFastClickHandler(ignore ->
			getApp().getGuiManager().openHelp(command.helpPage, command.helpType)
		);
		heading.add(w);
		return heading;
	}

	private String highlightSuffix(String command) {
		String prefix = command.substring(0, prefixLength);
		String suffix = command.substring(prefixLength);
		return prefix + "<strong>" + suffix + "</strong>";
	}

	/**
	 * fill popup with command list based on the user input
	 * @param curWord - user input
	 * @param left - left position
	 * @param top - top of input
	 * @param bottom - bottom of input
	 */
	private void fillAndShow(String curWord, int left, int top, int bottom) {
		getPopupMenu().clear();
		fillContent(curWord);
		popupPanel.hide();
		if (getPopupMenu().getWidgetCount() > 0) {
			removeSubPopup();
			positionAndShowPopup(left, top, bottom);
			getPopupMenu().selectItem(0);
		} else {
			popupPanel.hide();
		}
	}

	/**
	 * positioning logic for the autocomplete popup
	 * @param left - left position based on marble panel
	 * @param top - top of input
	 * @param bottom - bottom of input
	 */
	private void positionAndShowPopup(int left, int top, int bottom) {
		popupPanel.show();
		popupPanel.setHeight("100%");

		int popupTop = bottom;
		int distBottomKeyboardTop = (int) (getApp().getHeight() - bottom
				- getApp().getAppletFrame().getKeyboardHeight());
		// not enough place to show below input
		if (distBottomKeyboardTop < popupPanel.getOffsetHeight()) {
			popupTop = top - popupPanel.getOffsetHeight();
			// not enough place to show above input
			if (popupTop < 0) {
				// more place above input -> show above
				if (top > distBottomKeyboardTop) {
					popupPanel.setHeight(top - 16 + "px");
					popupTop = 0;
				} else {
					// show below input otherwise
					popupPanel.setHeight(distBottomKeyboardTop - 16 + "px");
					popupTop = bottom;
				}
			}
		}

		int finalPopupTop = popupTop;
		popupPanel.setPopupPositionAndShow((offsetWidth, offsetHeight) ->
				popupPanel.setPopupPosition(left, finalPopupTop));
	}

	public boolean isSuggesting() {
		return isMenuShown();
	}

	/**
	 * @return whether some syntax was inserted
	 */
	public boolean handleEnter() {
		if (openItem != null) {
			AriaMenuItem selectedItem = openItem.getSubMenu().getSelectedItem();
			if (selectedItem != null) {
				selectedItem.getScheduledCommand().execute();
				return true;
			}
			return false;
		}
		AriaMenuItem selectedItem = getPopupMenu().getSelectedItem();
		if (selectedItem != null && selectedItem.getSubMenu() != null) {
			openSubmenu(selectedItem);
			return true;
		}
		return false;
	}

	@Override
	protected void openSubmenu(AriaMenuItem item) {
		super.openSubmenu(item);
		item.getSubMenu().selectItem(0);
	}

	/**
	 * Show suggestions.
	 * @param left - left position
	 * @param top - top of input
	 * @param bottom - bottom of input
	 */
	public void popupSuggestions(int left, int top, int bottom) {
		String curWord = component.getCommand();
		if (curWord != null
				&& !"sqrt".equals(curWord)
				&& InputHelper.needsAutocomplete(curWord, getApp().getKernel())) {
			fillAndShow(curWord, left, top, bottom);
		} else {
			hide();
		}
	}

	/**
	 * @return whether enter should be consumed by suggestions
	 */
	public boolean needsEnterForSuggestion() {
		if (isSuggesting()) {
			return handleEnter();
		}
		return false;
	}
}

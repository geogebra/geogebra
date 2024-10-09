package org.geogebra.web.full.gui.inputfield;

import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.main.localization.AutocompleteProvider;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.util.MatchedString;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.Shades;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteW;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.SharedResources;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.InlineHTML;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

public class AutoCompletePopup extends GPopupMenuW {

	private final AutocompleteProvider suggestions;
	private final AutoCompleteW component;

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
		suggestions.getCompletions(curWord).forEach(this::addRow);
	}

	private void addRow(AutocompleteProvider.Completion cpl) {
		AriaMenuBar submenu = new AriaMenuBar();
		submenu.addStyleName("autocompleteSyntaxContent");
		submenu.addStyleName("customScrollbar");
		for (String line: cpl.syntaxes) {
			AriaMenuItem item = new AriaMenuItem(line.replaceAll("[<>]", ""),
					null, () -> {
				component.insertString(line);
				hide();
			});
			item.addStyleName(Shades.NEUTRAL_900.getFgColName());
			item.setFocusable(false);
			submenu.addItem(item);
		}
		AriaMenuItem menuItem = new AriaMenuItem(highlightSuffix(cpl.match),
				submenu);
		menuItem.setSubmenuHeading(buildSubmenuHeading(cpl));
		menuItem.addStyleName("no-image");
		menuItem.setFocusable(false);
		addItem(menuItem, false);
	}

	private Widget buildSubmenuHeading(AutocompleteProvider.Completion command) {
		FlowPanel heading = new FlowPanel();
		heading.addStyleName("autocompleteSyntaxHeading");
		heading.add(new Label(command.getCommand()));
		if (!GlobalScope.examController.isExamActive()) {
			heading.add(createHelpButton(command));
		}
		return heading;
	}

	private Widget createHelpButton(AutocompleteProvider.Completion command) {
		StandardButton button = new StandardButton(SharedResources.INSTANCE.icon_help_black(),
				null, 24);
		button.addFastClickHandler(ignore ->
				getApp().getGuiManager().openHelp(command.helpPage, command.helpType));
		return button;
	}

	private InlineHTML highlightSuffix(MatchedString command) {
		String[] parts = command.getParts();
		return new InlineHTML(parts[0] + "<strong>" + parts[1] + "</strong>" + parts[2]);
	}

	/**
	 * fill popup with command list based on the user input
	 * @param curWord - user input
	 * @param left - left position
	 * @param top - top of input
	 * @param height - height of input
	 */
	private void fillAndShow(String curWord, int left, int top, int height) {
		getPopupMenu().clear();
		fillContent(curWord);
		popupPanel.hide();
		if (getPopupMenu().getWidgetCount() > 0) {
			removeSubPopup();
			positionAndShowPopup(left, top, height);
			getPopupMenu().selectItem(0);
		} else {
			popupPanel.hide();
		}
	}

	/**
	 * positioning logic for the autocomplete popup
	 * @param left - left position based on marble panel
	 * @param inputTop - inputTop of input
	 * @param inputHeight - inputHeight of input
	 */
	private void positionAndShowPopup(int left, int inputTop, int inputHeight) {
		popupPanel.show();
		popupPanel.setHeight("100%");

		int scaledTop = scaledY(inputTop);
		int scaledBottom = scaledTop + inputHeight;
		int popupTop = getPopupTop(scaledTop, scaledBottom);

		popupPanel.setPopupPositionAndShow((offsetWidth, offsetHeight) ->
				popupPanel.setPopupPosition(left, popupTop));
	}

	private int scaledY(double y) {
		return (int) (y / getScaleY());
	}

	private int getPopupTop(int inputTop, int inputBottom) {
		int popupTop = inputBottom;
		int distBottomKeyboardTop = (int) (getApp().getHeight() - inputBottom
				- getApp().getAppletFrame().getKeyboardHeight());
		// not enough place to show below input
		if (distBottomKeyboardTop < popupPanel.getOffsetHeight()) {
			popupTop = inputTop - popupPanel.getOffsetHeight();
			// not enough place to show above input
			if (popupTop < 0) {
				// more place above input -> show above
				if (inputTop > distBottomKeyboardTop) {
					popupPanel.setHeight(inputTop - 16 + "px");
					popupTop = 0;
				} else {
					// show below input otherwise
					popupPanel.setHeight(distBottomKeyboardTop - 16 + "px");
					popupTop = inputBottom;
				}
			}
		}
		return popupTop;
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
		item.getSubMenu().getItemAt(0).addStyleName("fakeFocus");
	}

	/**
	 * Show suggestions.
	 * @param left - left position
	 * @param top - top of input
	 * @param height - height of input
	 */
	public void popupSuggestions(int left, int top, int height) {
		String curWord = component.getCommand();
		if (curWord != null
				&& !"sqrt".equals(curWord)
				&& InputHelper.needsAutocomplete(curWord, getApp().getKernel())) {
			fillAndShow(curWord, left, top, height);
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

package org.geogebra.web.full.gui.view.algebra;

import java.util.List;

import org.geogebra.common.contextmenu.InputContextMenuItem;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.keyboard.base.KeyboardType;
import org.geogebra.web.full.gui.contextmenu.ImageMap;
import org.geogebra.web.full.gui.keyboard.KeyboardManager;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.gwtproject.user.client.ui.Widget;

/**
 * Class for Plus menu for AV Input to select input method (expression, text or
 * image) and get help.
 * 
 * @author Laszlo Gal
 *
 */
public class ContextMenuAVPlus implements SetLabels {
	/** The popup itself */
	protected GPopupMenuW wrappedPopup;
	/** Localization */
	protected Localization loc;
	/** Application */
	AppWFull app;
	/** The AV item associated the menu with */
	RadioTreeItem item;
	/** On-Screen Keyboard instance to switch tabs if needed */
	KeyboardManager kbd;

	/**
	 * Creates new context menu
	 * 
	 * @param item
	 *            application
	 */
	ContextMenuAVPlus(RadioTreeItem item) {
		app = item.getApplication();
		loc = app.getLocalization();
		this.item = item;
		kbd = app.getKeyboardManager();
		wrappedPopup = new GPopupMenuW(app);

		buildGUI();
	}

	private void buildGUI() {
		wrappedPopup.clearItems();
		List<InputContextMenuItem> items = GlobalScope.contextMenuFactory
				.makeInputContextMenu(true, hasImageItem());
		for (InputContextMenuItem item: items) {
			wrappedPopup.addItem(new AriaMenuItem(item.getLocalizedTitle(loc),
					ImageMap.get(item.getIcon()), () -> execute(item)));
		}
	}

	protected boolean hasImageItem() {
		return GlobalScope.examController.isIdle() && app.getGuiManager().toolbarHasImageMode();
	}

	private void execute(InputContextMenuItem item) {
		switch (item) {
		case Expression:
			addExpression();
			break;
		case Text:
			addText();
			break;
		case Help:
			showHelp();
			break;
		case Image:
			addImage();
			break;
		}
	}

	private void addExpression() {
		item.getController().setInputAsText(false);
		item.ensureEditing();
		kbd.selectTab(KeyboardType.NUMBERS);
	}

	private void addText() {
		item.getController().setInputAsText(true);
		item.ensureEditing();
		kbd.selectTab(KeyboardType.ABC);
	}
	
	void addImage() {
		item.getController().setInputAsText(false);
		app.getImageManager().setPreventAuxImage(true);

		app.getGuiManager().loadImage(null,
				null, false, app.getActiveEuclidianView());
	}

	/**
	 * Show popup menu at (x, y) screen coordinates.
	 * 
	 * @param x
	 *            y coordinate.
	 * @param y
	 *            y coordinate.
	 */
	public void show(Widget widget, int x, int y) {
		wrappedPopup.show(widget, x, y);
	}

	@Override
	public void setLabels() {
		buildGUI();
	}
	
	/**
	 * Shows command help dialog for the item.
	 */
	void showHelp() {
		if (MarblePanel.checkError(item)) {
			return;
		}
		MarblePanel.showDeferred(item);
	}
}


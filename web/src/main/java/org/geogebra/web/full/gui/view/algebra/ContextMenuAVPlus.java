package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.keyboard.base.KeyboardType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.keyboard.KeyboardManager;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.SharedResources;
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
		addExpressionItem();
		if (app.getActiveEuclidianView().getViewID() != App.VIEW_EUCLIDIAN3D) {
			addTextItem();
			if (GlobalScope.examController.isIdle() && app.getGuiManager().toolbarHasImageMode()) {
				addImageItem();
			}
		}
		addHelpItem();
	}

	private void addExpressionItem() {
		SVGResource img = MaterialDesignResources.INSTANCE.description();
		AriaMenuItem mi =
				MainMenu.getMenuBarItem(img, loc.getMenu("Expression"), () -> {
					item.getController().setInputAsText(false);
					item.ensureEditing();
					kbd.selectTab(KeyboardType.NUMBERS);
				});
		wrappedPopup.addItem(mi);
	}

	private void addTextItem() {
		SVGResource img = MaterialDesignResources.INSTANCE.icon_quote_black();
		AriaMenuItem mi =
				MainMenu.getMenuBarItem(img, loc.getMenu("Text"), () -> {
					item.getController().setInputAsText(true);
					item.ensureEditing();
					kbd.selectTab(KeyboardType.ABC);
				});
		wrappedPopup.addItem(mi);
	}
	
	void addImageItem() {
		SVGResource img = MaterialDesignResources.INSTANCE.insert_photo_black();
		AriaMenuItem mi =
				MainMenu.getMenuBarItem(img, loc.getMenu("Image"), () -> {
					item.getController().setInputAsText(false);
					app.getImageManager().setPreventAuxImage(true);

					app.getGuiManager().loadImage(null,
							null, false, app.getActiveEuclidianView());
				});
		wrappedPopup.addItem(mi);
	}

	private void addHelpItem() {
		SVGResource img = SharedResources.INSTANCE.icon_help_black();
		AriaMenuItem mi =
				MainMenu.getMenuBarItem(img, loc.getMenu("Help"), this::showHelp);
		wrappedPopup.addItem(mi);
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


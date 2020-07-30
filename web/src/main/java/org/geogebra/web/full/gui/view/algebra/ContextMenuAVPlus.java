package org.geogebra.web.full.gui.view.algebra;

import java.util.Vector;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.toolbar.ToolbarItem;
import org.geogebra.common.gui.toolcategorization.AppType;
import org.geogebra.common.gui.toolcategorization.ToolCollection;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.keyboard.base.KeyboardType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.images.StyleBarResources;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.gui.util.VirtualKeyboardGUI;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.SharedResources;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;

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
	VirtualKeyboardGUI kbd;

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
		kbd = app.getKeyboardManager().getOnScreenKeyboard();
		wrappedPopup = new GPopupMenuW(app);
		if (app.isUnbundled()) {
			wrappedPopup.getPopupPanel().addStyleName("matMenu");
		} else {
			wrappedPopup.getPopupPanel().addStyleName("mioMenu");
		}
		buildGUI();
	}

	private void buildGUI() {
		wrappedPopup.clearItems();
		addExpressionItem();
		if (app.getActiveEuclidianView().getViewID() != App.VIEW_EUCLIDIAN3D) {
			addTextItem();

			if (app.showToolBar() && toolbarHasImageMode()) {
				addImageItem();
			}
		}
		addHelpItem();
	}

	private boolean toolbarHasImageMode() {
		if (app.getConfig().getToolbarType().equals(AppType.CLASSIC)) {
			Vector<ToolbarItem> toolbarItems =
					ToolBar.parseToolbarString(app.getGuiManager().getToolbarDefinition());

			for (ToolbarItem toolbarItem : toolbarItems) {
				if (toolbarItem.getMode() == null) {
					if (toolbarItem.getMenu().contains(EuclidianConstants.MODE_IMAGE)) {
						return true;
					}
				} else {
					if (toolbarItem.getMode() == EuclidianConstants.MODE_IMAGE) {
						return true;
					}
				}
			}
		} else {
			ToolCollection toolCollection =
					app.createToolCollectionFactory().createToolCollection();
			return toolCollection.contains(EuclidianConstants.MODE_IMAGE);
		}

		return false;
	}

	private void addExpressionItem() {
		ImageResource img = StyleBarResources.INSTANCE.description();
		AriaMenuItem mi = new AriaMenuItem(
				MainMenu.getMenuBarHtml(img,
						loc.getMenu("Expression")),
				true,
				new Command() {
					
					@Override
					public void execute() {
						item.getController().setInputAsText(false);
						item.ensureEditing();
						kbd.selectTab(KeyboardType.NUMBERS);
					}
				});
		wrappedPopup.addItem(mi);
	}

	private void addTextItem() {
		SVGResource img = MaterialDesignResources.INSTANCE.icon_quote_black();
		AriaMenuItem mi = new AriaMenuItem(
				MainMenu.getMenuBarHtml(img, loc.getMenu("Text")), true,
				new Command() {
					
					@Override
					public void execute() {
						item.getController().setInputAsText(true);
						item.ensureEditing();
						kbd.selectTab(KeyboardType.ABC);
					}
				});
		wrappedPopup.addItem(mi);
	}
	
	void addImageItem() {
		SVGResource img = MaterialDesignResources.INSTANCE.insert_photo_black();
		AriaMenuItem mi = new AriaMenuItem(
				MainMenu.getMenuBarHtml(img, loc.getMenu("Image")), true,
				new Command() {
					
					@Override
					public void execute() {
						item.getController().setInputAsText(false);
						app.getImageManager().setPreventAuxImage(true);
						
						app.getGuiManager().loadImage(null,
								null, false, app.getActiveEuclidianView());
					}
				});
		wrappedPopup.addItem(mi);
	}

	private void addHelpItem() {
		SVGResource img = SharedResources.INSTANCE.icon_help_black();
		AriaMenuItem mi = new AriaMenuItem(
				MainMenu.getMenuBarHtml(img, loc.getMenu("Help")),
				true, new Command() {
					
					@Override
					public void execute() {
						showHelp();
					}
				});
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
	public void show(int x, int y) {
		wrappedPopup.show(x, y);
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


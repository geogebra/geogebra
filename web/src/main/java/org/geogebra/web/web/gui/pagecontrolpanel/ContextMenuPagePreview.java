package org.geogebra.web.web.gui.pagecontrolpanel;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.javax.swing.GPopupMenuW;
import org.geogebra.web.web.main.AppWapplet;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;

/**
 * Context Menu of Page Preview Cards
 * 
 * @author Alicia Hofstaetter
 *
 */
public class ContextMenuPagePreview
		implements SetLabels {

	/** visible component */
	protected GPopupMenuW wrappedPopup;
	private Localization loc;
	private AppW app;
	private GeoGebraFrameBoth frame;
	private PagePreviewCard card;

	/**
	 * @param app
	 *            application
	 * @param card
	 *            associated preview card
	 */
	public ContextMenuPagePreview(AppW app, PagePreviewCard card) {
		this.app = app;
		this.card = card;
		loc = app.getLocalization();
		frame = ((AppWapplet) app).getAppletFrame();
		initGUI();
	}

	private void initGUI() {
		wrappedPopup = new GPopupMenuW(app);
		wrappedPopup.getPopupPanel().addStyleName("matMenu mowMatMenu");
		addDeleteItem();
		addDuplicateItem();
	}

	private void addDeleteItem() {
		String img = MaterialDesignResources.INSTANCE.delete_black()
				.getSafeUri().asString();
		AriaMenuItem mi = new AriaMenuItem(
				MainMenu.getMenuBarHtml(img, loc.getMenu("Delete"), true), true,
				new Command() {
					@Override
					public void execute() {
						onDelete();
					}
				});
		wrappedPopup.addItem(mi);
	}

	private void addDuplicateItem() {
		String img = MaterialDesignResources.INSTANCE.duplicate_black()
				.getSafeUri().asString();
		AriaMenuItem mi = new AriaMenuItem(
				MainMenu.getMenuBarHtml(img, loc.getMenu("Duplicate"), true),
				true,
				new Command() {
					@Override
					public void execute() {
						onDuplicate();
					}
				});
		wrappedPopup.addItem(mi);
	}

	/**
	 * execute delete action
	 */
	protected void onDelete() {
		hide();
		frame.getPageControlPanel().removePage(card.getPageIndex());
	}

	/**
	 * execute duplicate action
	 */
	protected void onDuplicate() {
		hide();
		frame.getPageControlPanel().duplicatePage(card);
	}

	@Override
	public void setLabels() {
		initGUI();
	}

	/**
	 * @param x
	 *            screen x-coordinate
	 * @param y
	 *            screen y-coordinate
	 */
	public void show(int x, int y) {
		wrappedPopup.show(new GPoint(x, y));
		focusDeferred();
		wrappedPopup.setMenuShown(true);
	}

	/**
	 * hides the context menu
	 */
	public void hide() {
		wrappedPopup.hideMenu();
		wrappedPopup.setMenuShown(false);
	}

	/**
	 * @return if context menu is showing
	 */
	public boolean isShowing() {
		return wrappedPopup.isMenuShown();
	}

	private void focusDeferred() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				wrappedPopup.getPopupMenu().getElement().focus();
			}
		});
	}
}

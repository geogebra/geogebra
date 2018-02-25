package org.geogebra.web.full.gui.pagecontrolpanel;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.MyToggleButton;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;

/**
 * Context Menu of Page Preview Cards
 * 
 * @author Alicia Hofstaetter
 *
 */
public class ContextMenuPagePreview extends MyToggleButton
		implements SetLabels, CloseHandler<GPopupPanel> {

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
		super(getImage(MaterialDesignResources.INSTANCE.more_vert_black()),
				app);
		this.app = app;
		this.card = card;
		loc = app.getLocalization();
		frame = ((AppWFull) app).getAppletFrame();
		initButton();
	}

	private void initButton() {
		Image hoveringFace = getImage(
				MaterialDesignResources.INSTANCE.more_vert_mebis());
		getUpHoveringFace().setImage(hoveringFace);
		getDownHoveringFace().setImage(hoveringFace);
		addStyleName("mowMoreButton");

		ClickStartHandler.init(this, new ClickStartHandler(true, true) {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				if (isShowing()) {
					hide();
				} else {
					show();
				}
			}
		});
	}

	private static Image getImage(SVGResource res) {
		return new NoDragImage(res, 24, 24);
	}

	private void initPopup() {
		wrappedPopup = new GPopupMenuW(app);
		wrappedPopup.getPopupPanel().addCloseHandler(this);
		wrappedPopup.getPopupPanel().addAutoHidePartner(this.getElement());
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
		initPopup();
		setAltText(loc.getMenu("Options"));
	}

	/**
	 * @return true if context menu is showing
	 */
	protected boolean isShowing() {
		return wrappedPopup.isMenuShown();
	}

	/**
	 * show the context menu
	 */
	protected void show() {
		if (wrappedPopup == null) {
			initPopup();
		}
		wrappedPopup.show(
				new GPoint(getAbsoluteLeft() - 122, getAbsoluteTop() + 36));
		focusDeferred();
		wrappedPopup.setMenuShown(true);
		toggleIcon(true);
	}

	/**
	 * hide the context menu
	 */
	public void hide() {
		wrappedPopup.hide();
		wrappedPopup.setMenuShown(false);
		toggleIcon(false);
	}

	@Override
	public void onClose(CloseEvent<GPopupPanel> event) {
		if (event.isAutoClosed()) {
			wrappedPopup.setMenuShown(false);
		}
		toggleIcon(false);
	}

	/**
	 * @param toggle
	 *            true if active
	 */
	protected void toggleIcon(boolean toggle) {
		if (toggle) {
			getUpFace().setImage(getImage(
					MaterialDesignResources.INSTANCE.more_vert_mebis()));
			addStyleName("active");
		} else {
			getUpFace().setImage(getImage(
					MaterialDesignResources.INSTANCE.more_vert_black()));
			removeStyleName("active");
		}
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

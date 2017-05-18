package org.geogebra.web.web.gui.toolbarpanel;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.web.gui.layout.DockSplitPaneW;
import org.geogebra.web.web.gui.layout.panels.ToolbarDockPanelW;
import org.geogebra.web.web.main.AppWFull;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;

/**
 * 
 * @author Laszlo Gal
 *
 */
public class ToolbarPanel extends FlowPanel {
	private static final int CLOSED_WIDTH = 56;

	private Header header;
	private App app;
	private Integer lastOpenWidth = null;

	private class Header extends FlowPanel {
		private static final int PADDING = 12;
		private ToggleButton btnMenu;
		private ToggleButton btnAlgebra;
		private ToggleButton btnTools;
		private ToggleButton btnClose;
		private boolean open = true;
		private Image imgClose;
		private Image imgOpen;
		private FlowPanel contents;
		private FlowPanel center;
		public Header() {
			contents = new FlowPanel();
			contents.addStyleName("contents");
			add(contents);


			createMenuButton();
			createCloseButton();
			createCenter();

		}

		private void createCenter() {
			btnAlgebra = new ToggleButton(new Image(
					MaterialDesignResources.INSTANCE.toolbar_algebra_dummy()));
			btnAlgebra.addStyleName("flatButton");

			btnTools = new ToggleButton(new Image(
					MaterialDesignResources.INSTANCE.toolbar_tools_dummy()));
			btnTools.addStyleName("flatButton");
			center = new FlowPanel();
			center.addStyleName("center");
			center.add(btnAlgebra);
			center.add(btnTools);
			contents.add(center);

		}

		private void createCloseButton() {
			imgClose = new Image(MaterialDesignResources.INSTANCE.toolbar_close_white());
			imgOpen = new Image(MaterialDesignResources.INSTANCE.toolbar_open_white());

			btnClose = new ToggleButton();
			btnClose.addStyleName("flatButton");
			btnClose.addStyleName("close");
			contents.add(btnClose);

			ClickStartHandler.init(btnClose, new ClickStartHandler() {

				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					if (isOpen()) {
						setLastOpenWidth(getOffsetWidth());
					}

					setOpen(!isOpen());
					getFrame().showKeyBoard(false, null, true);

				}
			});
		}

		private void createMenuButton() {
			btnMenu = new ToggleButton(new Image(MaterialDesignResources.INSTANCE.toolbar_menu_white()));
			btnMenu.addStyleName("flatButton");
			btnMenu.addStyleName("menu");
			contents.add(btnMenu);

			ClickStartHandler.init(btnMenu, new ClickStartHandler() {

				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					toggleMenu();
				}
			});
		}

		public boolean isOpen() {
			return open;
		}

		public void setOpen(boolean value) {
			this.open = value;
			if (open) {
				removeStyleName("header-close");
				addStyleName("header-open");
				btnClose.getUpFace().setImage(imgClose);
			} else {
				removeStyleName("header-open");
				addStyleName("header-close");
				btnClose.getUpFace().setImage(imgOpen);
			}

			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				public void execute() {
					updateCenterSize();
				}
			});
			updateWidth();
			showKeyboardButtonDeferred(isOpen());
		}

		void updateCenterSize() {
			if (open) {
				return;
			}

			int h = getOffsetHeight() - btnMenu.getOffsetHeight()
					- btnClose.getOffsetHeight() - 2 * PADDING;
			center.setHeight(h + "px");

		}
	}

	/**
	 * 
	 * @param app
	 *            .
	 */
	public ToolbarPanel(App app) {
		this.app = app;
		initGUI();
	}

	private void initGUI() {
		clear();
		addStyleName("toolbar");
		header = new Header();
		add(header);
		add(new Label("Here comes the contents..."));
		open();
	}

	/**
	 * Opens the toolbar.
	 */
	public void open() {
		if (header.isOpen()) {
			return;
		}
		header.setOpen(true);
	}

	/**
	 * Closes the toolbar.
	 */
	public void close() {
		if (!header.isOpen()) {
			return;
		}
		header.setOpen(false);
	}

	/**
	 * updates panel width according to its state.
	 */
	public void updateWidth() {

		ToolbarDockPanelW dockPanel = getToolbarDockPanel();
		DockSplitPaneW dockParent = dockPanel != null ? dockPanel.getParentSplitPane() : null;
		if (dockPanel != null && getLastOpenWidth() != null) {
			if (header.isOpen()) {
				dockParent.setWidgetSize(dockPanel,
						getLastOpenWidth().intValue());
				dockParent.removeStyleName("hide-HDragger");
			} else {
				dockParent.setWidgetSize(dockPanel, CLOSED_WIDTH);
				dockParent.addStyleName("hide-HDragger");
			}
			dockPanel.deferredOnResize();
		}

	}

	/**
	 * @return algebra dock panel
	 */
	ToolbarDockPanelW getToolbarDockPanel() {
		return (ToolbarDockPanelW) app.getGuiManager().getLayout().getDockManager().getPanel(App.VIEW_ALGEBRA);
	}

	/**
	 * @return if toolbar is open or not.
	 */
	public boolean isOpen() {
		return header.isOpen();
	}

	/**
	 * @return the frame with casting.
	 */
	GeoGebraFrameBoth getFrame() {
		return ((GeoGebraFrameBoth) ((AppWFull) app).getAppletFrame());
	}

	/**
	 * @param b
	 *            To show or hide keyboard button.
	 */
	void showKeyboardButtonDeferred(final boolean b) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				getFrame().showKeyboardButton(b);

			}
		});
	}

	/**
	 * 
	 * @return the last width when toolbar was open.
	 */
	Integer getLastOpenWidth() {
		return lastOpenWidth;
	}

	/**
	 * 
	 * @param value
	 *            to set.
	 */
	void setLastOpenWidth(Integer value) {
		this.lastOpenWidth = value;
	}

	/**
	 * Opens and closes Burger Menu
	 */
	void toggleMenu() {
		((AppW) app).toggleMenu();
	}
}

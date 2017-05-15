package org.geogebra.web.web.gui.toolbarpanel;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
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


public class ToolbarPanel extends FlowPanel {
	private static final int CLOSED_WIDTH = 56;

	private Header header;
	private App app;
	private Integer lastOpenWidth = null;
	private class Header extends FlowPanel {
		private ToggleButton btnMenu;
		private ToggleButton btnClose;
		private boolean open = true;
		private Image imgClose;
		private Image imgOpen;
		private FlowPanel contents;
		public Header() {
			contents = new FlowPanel();
			contents.addStyleName("contents");
			add(contents);

			createCloseButton();
			createMenuButton();
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
					if (open) {
						lastOpenWidth = getOffsetWidth();
					}

					setOpen(!open);
					((GeoGebraFrameBoth) ((AppWFull) app).getAppletFrame()).showKeyBoard(false, null, true);

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

			updateWidth();
			showKeyboardButtonDeferred(open);
		}
	}

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

	public void open() {
		if (header.isOpen()) {
			return;
		}
		header.setOpen(true);
	}

	public void close() {
		if (!header.isOpen()) {
			return;
		}
		header.setOpen(false);
	}

	public void updateWidth() {

		ToolbarDockPanelW dockPanel = getToolbarDockPanel();
		DockSplitPaneW dockParent = dockPanel != null ? dockPanel.getParentSplitPane() : null;
		if (dockPanel != null && lastOpenWidth != null) {
			dockParent.setWidgetSize(dockPanel,
					header.isOpen() ? lastOpenWidth.intValue() : CLOSED_WIDTH);
			dockPanel.deferredOnResize();
		}

	}

	/**
	 * @return algebra dock panel
	 */
	ToolbarDockPanelW getToolbarDockPanel() {
		return (ToolbarDockPanelW) app.getGuiManager().getLayout().getDockManager().getPanel(App.VIEW_ALGEBRA);
	}

	public boolean isOpen() {
		return header.isOpen();
	}

	private void showKeyboardButtonDeferred(final boolean show) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				((GeoGebraFrameBoth) ((AppWFull) app).getAppletFrame()).showKeyboardButton(show);

			}
		});
	}
}

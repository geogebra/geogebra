package org.geogebra.web.web.gui.toolbarpanel;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.Presistable;
import org.geogebra.web.web.gui.toolbarpanel.ToolbarPanel.TabIds;
import org.geogebra.web.web.gui.util.StandardButton;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * header of toolbar
 *
 */
class Header extends FlowPanel {

	/**
	 * Parent tool panel
	 */
	final ToolbarPanel toolbarPanel;
	private static final int PADDING = 12;

	private static class PresistableToggleButton extends ToggleButton
			implements Presistable {

		public PresistableToggleButton(Image image) {
			super(image);
		}

	}

	private static class PresistablePanel extends FlowPanel
			implements Presistable {

		public PresistablePanel() {
			super();
		}
	}

	private PresistableToggleButton btnMenu;
	private ToggleButton btnAlgebra;
	private ToggleButton btnTools;
	private ToggleButton btnClose;
	private StandardButton btnContextMenu;
	private boolean open = true;
	private Image imgClose;
	private Image imgOpen;

	private Image imgMenu;
	private FlowPanel contents;
	private FlowPanel center;
	private FlowPanel rightSide;
	/**
	 * panel containing undo and redo
	 */
	PresistablePanel undoRedoPanel;
	private ToggleButton btnUndo;
	private ToggleButton btnRedo;
	private ContextMenuAlgebra cmAlgebra = null;
	private ContextMenuTools cmTools;

	/**
	 * @param toolbarPanel
	 *            - panel containing the toolbar
	 */
	public Header(ToolbarPanel toolbarPanel) {
		this.toolbarPanel = toolbarPanel;
		contents = new FlowPanel();
		contents.addStyleName("contents");
		add(contents);

		createMenuButton();
		createRightSide();
		createCenter();
		addUndoRedoButtons();

	}

	private void createCenter() {
		btnAlgebra = new ToggleButton(new Image(
				MaterialDesignResources.INSTANCE.toolbar_algebra()));
		btnAlgebra.addStyleName("tabButton");
		ClickStartHandler.init(btnAlgebra, new ClickStartHandler() {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {

				Header.this.toolbarPanel.openAlgebra();
				Header.this.toolbarPanel.setMoveMode();
				Header.this.toolbarPanel.getFrame().showKeyboardButton(true);
			}
		});

		btnTools = new ToggleButton(new Image(
				MaterialDesignResources.INSTANCE.toolbar_tools()));
		btnTools.addStyleName("tabButton");
		ClickStartHandler.init(btnTools,
				new ClickStartHandler(false, true) {

					@Override
					public void onClickStart(int x, int y,
							PointerEventType type) {

						Header.this.toolbarPanel.getFrame().keyBoardNeeded(false, null);
						Header.this.toolbarPanel.getFrame().showKeyboardButton(false);
						Header.this.toolbarPanel.openTools();
					}
				});

		center = new FlowPanel();
		center.addStyleName("center");
		center.addStyleName("indicatorLeft");
		center.getElement().setInnerHTML(center.getElement().getInnerHTML()
				+ "<div class=\"indicator\"></div>");
		center.add(btnAlgebra);
		center.add(btnTools);
		contents.add(center);
	}

	/**
	 * Switch to algebra panel
	 */
	void selectAlgebra() {
		center.removeStyleName("indicatorRight");
		center.addStyleName("indicatorLeft");
		btnAlgebra.addStyleName("selected");
		btnTools.removeStyleName("selected");
		this.toolbarPanel.setSelectedTab(TabIds.ALGEBRA);
	}

	/**
	 * Switch to tools panel
	 */
	void selectTools() {
		center.removeStyleName("indicatorLeft");
		center.addStyleName("indicatorRight");
		btnAlgebra.removeStyleName("selected");
		btnTools.addStyleName("selected");
		this.toolbarPanel.setSelectedTab(TabIds.TOOLS);
	}

	private void createRightSide() {
		imgClose = new Image();
		imgOpen = new Image();
		imgMenu = new Image();
		updateButtonImages();
		btnClose = new ToggleButton();
		btnClose.addStyleName("flatButton");
		btnClose.addStyleName("close");

		ClickStartHandler.init(btnClose, new ClickStartHandler() {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				if (isOpen()) {
					if (Header.this.toolbarPanel.isPortrait()) {
						Header.this.toolbarPanel.header.getParent().getParent().getParent()
								.addStyleName("closePortrait");
						Header.this.toolbarPanel.setLastOpenHeight(
								Header.this.toolbarPanel.app.getActiveEuclidianView().getHeight());
					} else {
						Header.this.toolbarPanel.header.getParent().getParent().getParent()
								.addStyleName("closeLandscape");
						Header.this.toolbarPanel.setLastOpenWidth(getOffsetWidth());
					}
					Header.this.toolbarPanel.setMoveMode();
					Header.this.toolbarPanel.setClosedByUser(true);
				} else {
					Header.this.toolbarPanel.header.getParent().getParent().getParent()
							.removeStyleName("closePortrait");
					Header.this.toolbarPanel.header.getParent().getParent().getParent()
							.removeStyleName("closeLandscape");
					Header.this.toolbarPanel.setClosedByUser(false);
				}

				Header.this.toolbarPanel.getFrame().showKeyBoard(false, null, true);
				setOpen(!isOpen());

			}
		});
		
		btnContextMenu = new StandardButton(MaterialDesignResources.INSTANCE
				.more_vert_white());
		btnContextMenu.addStyleName("flatButton");
		btnContextMenu.addStyleName("contextMenu");
		
		FastClickHandler handler = new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				openContextMenu();
			}
		};

		btnContextMenu.addFastClickHandler(handler);
		rightSide = new FlowPanel();
		rightSide.add(btnClose);
		// remove context menu
		// rightSide.add(btnContextMenu);
		rightSide.addStyleName("rightSide");
		contents.add(rightSide);

	}

	/**
	 * context menu button handler
	 */
	protected void openContextMenu() {
		int x = btnContextMenu.getAbsoluteLeft();
		int y = btnContextMenu.getAbsoluteTop() + 6;

		if (this.toolbarPanel.isAlgebraViewActive()) {
			if (cmAlgebra == null) {
				cmAlgebra = new ContextMenuAlgebra((AppW) this.toolbarPanel.app);
			}
			cmAlgebra.show(x, y);
		} else {
			if (cmTools == null) {
				cmTools = new ContextMenuTools((AppW) this.toolbarPanel.app,
						this.toolbarPanel);
			}
			cmTools.getSubToolFilter().update();
			cmTools.show(x, y);
		}
	}

	private void updateButtonImages() {
		if (this.toolbarPanel.isPortrait()) {
			imgOpen.setResource(MaterialDesignResources.INSTANCE
					.toolbar_open_portrait_white());
			imgClose.setResource(MaterialDesignResources.INSTANCE
					.toolbar_close_portrait_white());
			imgMenu.setResource(
					MaterialDesignResources.INSTANCE.toolbar_menu_black());
		} else {
			imgOpen.setResource(MaterialDesignResources.INSTANCE
					.toolbar_open_landscape_white());
			imgClose.setResource(MaterialDesignResources.INSTANCE
					.toolbar_close_landscape_white());
			imgMenu.setResource(
					MaterialDesignResources.INSTANCE.toolbar_menu_white());
		}
	}

	private void createMenuButton() {
		btnMenu = new PresistableToggleButton(new Image(
				MaterialDesignResources.INSTANCE.toolbar_menu_black()));
		btnMenu.addStyleName("flatButton");
		btnMenu.addStyleName("menu");

		this.toolbarPanel.getFrame().add(btnMenu);

		ClickStartHandler.init(btnMenu, new ClickStartHandler() {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				Header.this.toolbarPanel.toggleMenu();
			}
		});
	}

	private void addUndoRedoButtons() {
		undoRedoPanel = new PresistablePanel();
		undoRedoPanel.addStyleName("undoRedoPanel");
		addUndoButton(undoRedoPanel);
		addRedoButton(undoRedoPanel);
		this.toolbarPanel.getFrame().add(undoRedoPanel);
	}

	/**
	 * update position of undo+redo panel
	 */
	public void updateUndoRedoPosition() {
		final EuclidianView ev = ((AppW) this.toolbarPanel.app).getEuclidianView1();
		if (ev != null) {
			int evTop = ev.getAbsoluteTop();
			int evLeft = ev.getAbsoluteLeft();
			if ((evLeft == 0) && !this.toolbarPanel.isPortrait()) {
				return;
			}
			int move = this.toolbarPanel.isPortrait() ? 48 : 0;
			undoRedoPanel.getElement().getStyle().setTop(evTop, Unit.PX);
			undoRedoPanel.getElement().getStyle().setLeft(evLeft + move,
					Unit.PX);
		}
	}

	/**
	 * update style of undo+redo buttons
	 */
	public void updateUndoRedoActions() {
		if (this.toolbarPanel.app.getKernel().undoPossible()) {
			btnUndo.addStyleName("buttonActive");
			btnUndo.removeStyleName("buttonInactive");
		} else {
			btnUndo.removeStyleName("buttonActive");
			btnUndo.addStyleName("buttonInactive");
		}

		if (this.toolbarPanel.app.getKernel().redoPossible()) {
			btnRedo.removeStyleName("hideButton");
		} else {
			btnRedo.addStyleName("hideButton");
		}
	}

	private void addUndoButton(final FlowPanel panel) {
		btnUndo = new ToggleButton(
				new Image(MaterialDesignResources.INSTANCE.undo_black()));
		btnUndo.addStyleName("flatButton");

		ClickStartHandler.init(btnUndo, new ClickStartHandler() {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				Header.this.toolbarPanel.app.getGuiManager().undo();
			}
		});

		panel.add(btnUndo);
	}

	private void addRedoButton(final FlowPanel panel) {
		btnRedo = new ToggleButton(
				new Image(MaterialDesignResources.INSTANCE.redo_black()));
		btnRedo.addStyleName("flatButton");
		btnRedo.addStyleName("buttonActive");

		ClickStartHandler.init(btnRedo, new ClickStartHandler() {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				Header.this.toolbarPanel.app.getGuiManager().redo();
			}
		});

		panel.add(btnRedo);
	}
	
	/**
	 * @return - true if toolbar is open
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * @param value
	 *            - true if toolbar should be open
	 */
	public void setOpen(boolean value) {
		this.open = value;
		updateStyle();
		
		if (this.toolbarPanel.isPortrait()) {
			this.toolbarPanel.updateHeight();
		} else {

			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {
					updateCenterSize();
				}
			});
			this.toolbarPanel.updateWidth();

		}
		

		this.toolbarPanel.showKeyboardButtonDeferred(
				isOpen() && this.toolbarPanel.getSelectedTab() != TabIds.TOOLS);

	}

	/**
	 * update style of toolbar
	 */
	void updateStyle() {
		this.toolbarPanel.updateStyle();
		removeStyleName("header-open-portrait");
		removeStyleName("header-close-portrait");
		removeStyleName("header-open-landscape");
		removeStyleName("header-close-landscape");
		updateButtonImages();
		String orientation = this.toolbarPanel.isPortrait() ? "portrait" : "landscape";
		if (open) {
			addStyleName("header-open-" + orientation);
			btnClose.getUpFace().setImage(imgClose);
			btnMenu.removeStyleName("landscapeMenuBtn");
		} else {
			addStyleName("header-close-" + orientation);
			btnClose.getUpFace().setImage(imgOpen);
			if (!this.toolbarPanel.isPortrait()) {
				btnMenu.addStyleName("landscapeMenuBtn");
			} else {
				btnMenu.removeStyleName("landscapeMenuBtn");
			}
		}

		if (this.toolbarPanel.isPortrait()) {
			btnMenu.addStyleName("portraitMenuBtn");
		} else {
			btnMenu.removeStyleName("portraitMenuBtn");
		}

		btnMenu.getUpFace().setImage(imgMenu);
		updateUndoRedoPosition();
		updateUndoRedoActions();

	}

	/**
	 * update center posiotion by resize
	 */
	void updateCenterSize() {
		int h = 0;
		if (open) {
			h = ToolbarPanel.OPEN_HEIGHT;
		} else {
			h = getOffsetHeight() - btnMenu.getOffsetHeight()
					- btnClose.getOffsetHeight() - 2 * PADDING;

		}

		if (h > 0) {
			center.setHeight(h + "px");
		}

	}

	/**
	 * handle resize of toolbar
	 */
	public void resize() {
		updateCenterSize();
		updateStyle();

	}
}
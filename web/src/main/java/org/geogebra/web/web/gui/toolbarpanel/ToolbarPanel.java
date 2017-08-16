package org.geogebra.web.web.gui.toolbarpanel;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.MyModeChangedListener;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.toolcategorization.ToolCategorization.AppType;
import org.geogebra.common.gui.toolcategorization.ToolCategorization.ToolsetLevel;
import org.geogebra.common.io.layout.PerspectiveDecoder;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW.ToolTipLinkType;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.web.gui.layout.DockSplitPaneW;
import org.geogebra.web.web.gui.layout.panels.ToolbarDockPanelW;
import org.geogebra.web.web.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.web.gui.view.algebra.LatexTreeItemController;
import org.geogebra.web.web.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.web.main.AppWFull;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Laszlo Gal
 *
 */
public class ToolbarPanel extends FlowPanel implements MyModeChangedListener {
	private static final int HDRAGGER_WIDTH = 8;

	private static final int TAB_COUNT = 2;

	private static final int OPEN_ANIM_TIME = 200;

	/** Closed width of header in landscape mode */
	public static final int CLOSED_WIDTH_LANDSCAPE = 56;

	/** Closed height of header in portrait mode */
	public static final int CLOSED_HEIGHT_PORTRAIT = 56;

	/** Application */
	App app;

	/**
	 * Tab ids.
	 */
	enum TabIds {
		/** tab one */
		ALGEBRA,

		/** tab two */
		TOOLS
	}

	/** Header of the panel with buttons and tabs */
	Header header;

	private FlowPanel main;
	private StandardButton moveBtn;
	private Integer lastOpenWidth = null;
	private AlgebraTab tabAlgebra = null;
	private ToolsTab tabTools = null;
	private TabIds selectedTab;
	private boolean closedByUser = false;
	private ScheduledCommand deferredOnRes = new ScheduledCommand() {

		public void execute() {
			resize();
		}
	};
	/**
	 * Selects MODE_MOVE as mode and changes visual settings accordingly of
	 * this.
	 */
	public void setMoveMode() {
		tabTools.setMoveMode();
	}

	/**
	 * Changes visual settings of selected mode.
	 * 
	 * @param mode
	 *            the mode will be selected
	 */
	public void setMode(int mode) {
		tabTools.setMode(mode);
	}

	/**
	 * Updates the style of undo and redo buttons accordingly of they are active
	 * or inactive
	 */
	public void updateUndoRedoActions() {
		header.updateUndoRedoActions();
	}

	/**
	 * Updates the position of undo and redo buttons
	 */
	public void updateUndoRedoPosition() {
		header.updateUndoRedoPosition();
	}

	private class ToolbarTab extends ScrollPanel {
		public ToolbarTab() {
			setSize("100%", "100%");
			setAlwaysShowScrollBars(false);

		}

		@Override
		public void onResize() {
			setWidth(ToolbarPanel.this.getTabWidth() + "px");
			setHeight("100%");
		}
	}

	private class AlgebraTab extends ToolbarTab {
		SimplePanel simplep;
		AlgebraViewW aview = null;

		private int savedScrollPosition;

		public AlgebraTab() {
			if (app != null) {
				setAlgebraView((AlgebraViewW) app.getAlgebraView());
				aview.setInputPanel();
			}
		}

		public void setAlgebraView(final AlgebraViewW av) {
			if (av != aview) {
				if (aview != null && simplep != null) {
					simplep.remove(aview);
					remove(simplep);
				}

				simplep = new SimplePanel(aview = av);
				add(simplep);
				simplep.addStyleName("algebraSimpleP");
				addStyleName("algebraPanel");
				addStyleName("matDesign");
				addDomHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						int bt = simplep.getAbsoluteTop()
								+ simplep.getOffsetHeight();
						if (event.getClientY() > bt) {
							app.getSelectionManager().clearSelectedGeos();
							av.resetItems(true);
						}
					}
				}, ClickEvent.getType());
			}
		}

		@Override
		public void onResize() {
			super.onResize();
			if (aview != null) {
				aview.resize();
			}
		}

		public void scrollToActiveItem() {

			final RadioTreeItem item = aview == null ? null
					: aview.getActiveTreeItem();
			if (item == null) {
				return;
			}

			if (item.isInputTreeItem()) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					@Override
					public void execute() {

						scrollToBottom();
					}
				});
				return;
			}
			doScrollToActiveItem();
		}

		public void saveScrollPosition() {
			savedScrollPosition = getVerticalScrollPosition();
		}

		private void doScrollToActiveItem() {
			final RadioTreeItem item = aview.getActiveTreeItem();

			int spH = getOffsetHeight();

			int top = item.getElement().getOffsetTop();

			int relTop = top - savedScrollPosition;

			if (spH < relTop + item.getOffsetHeight()) {
				int pos = top + item.getOffsetHeight() - spH;
				setVerticalScrollPosition(pos);
			}
		}

	}

	/**
	 * tab of tools
	 */
	class ToolsTab extends ToolbarTab {
	
		/**
		 * panel containing the tools
		 */
		Tools toolsPanel;

		/**
		 * button to get more tools
		 */
		StandardButton moreBtn;
		
		/**
		 * button to get less tools
		 */
		StandardButton lessBtn;

		/**
		 * tab containing the tools
		 */
		public ToolsTab() {
			createContents();
			handleMoreLessButtons();
		}
		
		private void handleMoreLessButtons() {
			createMoreLessButtons();
			addMoreLessButtons();
		}
		
		private void createMoreLessButtons() {
			moreBtn = new StandardButton(
					app.getLocalization().getMenu("Tools.More"), app);
			moreBtn.addStyleName("moreLessBtn");
			moreBtn.removeStyleName("button");
			lessBtn = new StandardButton(
					app.getLocalization().getMenu("Tools.Less"), app);
			lessBtn.addStyleName("moreLessBtn");
			lessBtn.removeStyleName("button");
			moreBtn.addFastClickHandler(new FastClickHandler() {
				
				@Override
				public void onClick(Widget source) {
					ToolsetLevel level = app.getSettings().getToolbarSettings().getToolsetLevel();
					if (level.equals(ToolsetLevel.EMPTY_CONSTRUCTION)) { 
						app.getSettings().getToolbarSettings().setToolsetLevel(ToolsetLevel.STANDARD);
					} else if (level.equals(ToolsetLevel.STANDARD)) {
						app.getSettings().getToolbarSettings().setToolsetLevel(ToolsetLevel.ADVANCED);
					}
					updateContent();
				}
			});
			lessBtn.addFastClickHandler(new FastClickHandler() {
				
				@Override
				public void onClick(Widget source) {
					ToolsetLevel level = app.getSettings().getToolbarSettings().getToolsetLevel();
					AppType type = app.getSettings().getToolbarSettings().getType();
					if (level.equals(ToolsetLevel.ADVANCED)) { 
						app.getSettings().getToolbarSettings().setToolsetLevel(ToolsetLevel.STANDARD);
					} else if (level.equals(ToolsetLevel.STANDARD) && type.equals(AppType.GEOMETRY_CALC)) {
						app.getSettings().getToolbarSettings().setToolsetLevel(ToolsetLevel.EMPTY_CONSTRUCTION);
					} else {
						app.getSettings().getToolbarSettings().setToolsetLevel(ToolsetLevel.STANDARD);
					}
					updateContent();
				}
			});
		}

		/**
		 * add more or less button to tool panel
		 */
		public void addMoreLessButtons() {
			AppType type = app.getSettings().getToolbarSettings().getType();
			ToolsetLevel level = app.getSettings().getToolbarSettings().getToolsetLevel();
			
			if (type.equals(AppType.GRAPHING_CALCULATOR)) {
				switch (level) {
				case STANDARD:
					toolsPanel.add(moreBtn);
					break;
					
				case ADVANCED:
					toolsPanel.add(lessBtn);
				
				default:
					break;
				}
			} else if (type.equals(AppType.GEOMETRY_CALC)) {
				switch (level) {
				case EMPTY_CONSTRUCTION:
					toolsPanel.add(moreBtn);
					break;
				case STANDARD:
					toolsPanel.add(lessBtn);
					toolsPanel.add(moreBtn);
					break;
					
				case ADVANCED:
					toolsPanel.add(lessBtn);
				
				default:
					break;
				}
			}
		}
		
		private void createContents() {
			toolsPanel = new Tools((AppW) ToolbarPanel.this.app);
			add(toolsPanel);
		}
		
		/**
		 * update the content of tool panel
		 */
		public void updateContent() {
			toolsPanel.removeFromParent();
			toolsPanel = new Tools((AppW) ToolbarPanel.this.app);
			add(toolsPanel);
			handleMoreLessButtons();
		}

		/**
		 * Selects MODE_MOVE as mode and changes visual settings accordingly of
		 * this.
		 */
		void setMoveMode() {
			toolsPanel.setMoveMode();
		}

		/**
		 * Changes visual settings of selected mode..
		 * 
		 * @param mode
		 *            the mode will be selected
		 */
		void setMode(int mode) {
			toolsPanel.setMode(mode);
		}

	}
	/**
	 * 
	 * @param app
	 *            .
	 */
	public ToolbarPanel(App app) {
		this.app = app;
		app.getActiveEuclidianView().getEuclidianController()
				.setModeChangeListener(this);
		initGUI();

		initClickStartHandler();
	}

	/**
	 * 
	 * @return width of one tab.
	 */
	public int getTabWidth() {
		int w = header.getOffsetWidth();
		if (isAnimating() && !app.isPortrait()) {
			w -= HDRAGGER_WIDTH;
		}
		Log.debug("TAB width: " + w);
		return w > 0 ? w : 0;
	}

	private void initClickStartHandler() {
		ClickStartHandler.init(this, new ClickStartHandler() {
			@Override
			public void onClickStart(final int x, final int y,
					PointerEventType type) {

				app.getActiveEuclidianView().getEuclidianController()
						.closePopups(x, y, type);

			}
		});
	}

	/**
	 * Init gui, don't open any panels
	 */
	private void initGUI() {
		clear();
		addStyleName("toolbar");
		header = new Header(this, (AppW) app);
		add(header);
		main = new FlowPanel();
		main.addStyleName("main");
		tabAlgebra = new AlgebraTab();
		tabTools = new ToolsTab();
		main.add(tabAlgebra);
		main.add(tabTools);
		addMoveBtn();
		add(main);
		hideDragger();
	}

	private int getOpenHeightInPortrait() {
		double h = app.getHeight();
		int kh = 0;
		// getFrame().isKeyboardShowing()
		// ? (int) (getFrame().getKeyboardHeight()) : 0;

		return (int) (Math.round(h * PerspectiveDecoder.portraitRatio(h))) + kh;
	}

	/**
	 * resets toolbar
	 */
	public void reset() {
		lastOpenWidth = null;
		hideDragger();
		header.reset();
		resizeTabs();
	}

	private void addMoveBtn() {
		moveBtn = new StandardButton(
				MaterialDesignResources.INSTANCE.mode_move(), app);
		moveBtn.setStyleName("moveFloatingBtn");
		main.add(moveBtn);
		FastClickHandler moveBtnHandler = new FastClickHandler() {
			
			@Override
			public void onClick(Widget source) {
				setMoveMode();
				if (!Browser.isMobile()) {
					ToolTipManagerW.sharedInstance().setBlockToolTip(false);
					ToolTipManagerW.sharedInstance().showBottomInfoToolTip(
							app.getToolTooltipHTML(
									EuclidianConstants.MODE_MOVE),
							((GuiManagerW) app.getGuiManager()).getTooltipURL(
									EuclidianConstants.MODE_MOVE),
							ToolTipLinkType.Help, (AppW) app,
							((AppW) app).getAppletFrame().isKeyboardShowing());
					ToolTipManagerW.sharedInstance().setBlockToolTip(true);
				}
			}
		};
		moveBtn.addFastClickHandler(moveBtnHandler);
	}

	private void hideDragger() {
		ToolbarDockPanelW dockPanel = getToolbarDockPanel();
		final DockSplitPaneW dockParent = dockPanel != null
				? dockPanel.getParentSplitPane() : null;
		if (dockPanel != null) {
			final Widget opposite = dockParent.getOpposite(dockPanel);
			// AnimationCallback animCallback = null;
			dockParent.addStyleName("hide-Dragger");
			opposite.addStyleName("hiddenHDraggerRightPanel");
		}
	}

	/**
	 * Sets last height and width
	 * 
	 * @param force
	 *            override values even they are not null.
	 */
	void setLastSize(boolean force) {
		// if (app.isPortrait()) {
		// if (force || lastOpenHeight == null) {
		// lastOpenHeight = app.getActiveEuclidianView().getViewHeight();
		// }
		// } else {
		// if (force || lastOpenWidth == null) {
		// lastOpenWidth = getOffsetWidth();
		// }
		// }

	}
	/**
	 * Opens the toolbar.
	 */
	public void doOpen() {
		if (header.isOpen()) {
			return;
		}
		setClosedByUser(false);
		header.setOpen(true);
		// setLastSize(false);

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
	 * updates panel width according to its state in landscape mode.
	 */
	public void updateWidth() {
		if (app.isPortrait()) {
			return;
		}

		final ToolbarDockPanelW dockPanel = getToolbarDockPanel();
		final DockSplitPaneW dockParent = dockPanel != null
				? dockPanel.getParentSplitPane() : null;
		if (dockPanel != null && getLastOpenWidth() != null) {
			final Widget opposite = dockParent.getOpposite(dockPanel);
			AnimationCallback animCallback = null;
			dockParent.addStyleName("hide-Dragger");
			opposite.addStyleName("hiddenHDraggerRightPanel");
			if (header.isOpen()) {

				dockParent.setWidgetSize(dockPanel,
						getLastOpenWidth().intValue());
				animCallback = new LandscapeAnimationCallback(header,
						CLOSED_WIDTH_LANDSCAPE, getLastOpenWidth());

			} else {
				lastOpenWidth = getOffsetWidth();
				dockParent.setWidgetMinSize(dockPanel, CLOSED_WIDTH_LANDSCAPE);
				dockParent.setWidgetSize(dockPanel, CLOSED_WIDTH_LANDSCAPE);
				animCallback = new LandscapeAnimationCallback(header,
						getLastOpenWidth(),
						CLOSED_WIDTH_LANDSCAPE) {

					@Override
					public void onEnd() {
						super.onEnd();
						dockParent.addStyleName("hide-HDragger");
						opposite.addStyleName("hiddenHDraggerRightPanel");
						dockParent.onResize();
					}
				};

			}

			dockParent.animate(OPEN_ANIM_TIME, animCallback);
			// dockPanel.deferredOnResize();
		}

	}

	private void setMinimumSize() {
		ToolbarDockPanelW dockPanel = getToolbarDockPanel();
		DockSplitPaneW dockParent = dockPanel != null
				? dockPanel.getParentSplitPane() : null;
		if (dockPanel != null) {
			dockParent.setWidgetMinSize(dockPanel, CLOSED_WIDTH_LANDSCAPE);

		}

	}

	// /**
	// * updates panel height according to its state in portrait mode.
	// */
	// public void updateHeight() {
	// if (!app.isPortrait()) {
	// return;
	// }
	//
	// ToolbarDockPanelW dockPanel = getToolbarDockPanel();
	// final DockSplitPaneW dockParent = dockPanel != null
	// ? dockPanel.getParentSplitPane() : null;
	//
	// AnimationCallback animationCallback = null;
	// if (dockPanel != null) {
	// final Widget opposite = dockParent.getOpposite(dockPanel);
	// int h = 0;
	// if (header.isOpen()) {
	// h = getOpenHeightInPortrait();
	// // dockParent.setDividerLocation(h);
	// dockParent.updateDividerLocation(200, 0);
	// dockParent.removeStyleName("hide-VDragger");
	// animationCallback = new PortraitAnimationCallback(header, h + 1,
	// h);
	// } else {
	// final int closedEVHeight = (int) (app.getHeight() -
	// CLOSED_HEIGHT_PORTRAIT
	// - 8);
	// if (closedEVHeight > 0) {
	// dockParent.updateDividerLocation(700, 0);
	// // closedEVHeight / app.getHeight());
	//
	// // dockParent.setWidgetSize(opposite, closedEVHeight);
	// dockParent.addStyleName("hide-VDragger");
	// animationCallback = new PortraitAnimationCallback(header,
	// closedEVHeight, closedEVHeight + 1) {
	// @Override
	// protected void onEnd() {
	// super.onEnd();
	// // dockParent.deferredOnResize();
	// }
	//
	// };
	// }
	// h = closedEVHeight;
	// }
	//
	// if (h > 0) {
	// dockParent.animate(OPEN_ANIM_TIME, animationCallback);
	// }
	// }
	//
	// }
	//

	/**
	 * updates panel height according to its state in portrait mode.
	 */
	public void updateHeight() {
		if (!app.isPortrait()) {
			return;
		}

		ToolbarDockPanelW dockPanel = getToolbarDockPanel();
		final DockSplitPaneW dockParent = dockPanel != null
				? dockPanel.getParentSplitPane() : null;

		if (dockPanel != null) {
			Widget evPanel = dockParent.getOpposite(dockPanel);
			if (header.isOpen()) {
				dockParent.setWidgetSize(evPanel, getOpenHeightInPortrait());
				dockParent.removeStyleName("hide-VDragger");
			} else {
				dockParent.setWidgetSize(evPanel,
						app.getHeight() - header.getOffsetHeight());
				dockParent.addStyleName("hide-VDragger");
			}

			dockParent.animate(OPEN_ANIM_TIME,
					new PortraitAnimationCallback(header) {
						@Override
						protected void onEnd() {
							super.onEnd();
							dockParent.forceLayout();
						}
					});
		}

	}


	/**
	 * @return algebra dock panel
	 */
	ToolbarDockPanelW getToolbarDockPanel() {
		return (ToolbarDockPanelW) app.getGuiManager().getLayout().getDockManager().getPanel(App.VIEW_ALGEBRA);
	}

	/**
	 * @return mode floating action button
	 */
	public StandardButton getMoveBtn() {
		return moveBtn;
	}

	@Override
	public void onModeChange(int mode) {
		updateMoveButton(mode);
	}

	private void updateMoveButton(int mode) {
		if (mode == EuclidianConstants.MODE_MOVE) {
			hideMoveFloatingButton();
		} else {
			showMoveFloatingButton();
		}
	}

	/**
	 * Show move floating action button
	 */
	void showMoveFloatingButton() {
		if (moveBtn == null) {
			return;
		}
		moveBtn.addStyleName("showMoveBtn");
		moveBtn.removeStyleName("hideMoveBtn");
	}

	/**
	 * Hide move floating action button
	 */
	void hideMoveFloatingButton() {
		if (moveBtn == null) {
			return;
		}

		moveBtn.addStyleName("hideMoveBtn");
		moveBtn.removeStyleName("showMoveBtn");
	}

	/**
	 * @param ttLeft
	 *            - tooltip left
	 * @param width
	 *            - width
	 * @param isSmall
	 *            - is small tooltip
	 * @return true if was moved
	 */
	public boolean moveMoveFloatingButtonUpWithTooltip(int ttLeft, int width,
			boolean isSmall) {
		if (moveBtn != null) {

			int mLeft = moveBtn.getAbsoluteLeft();
			int mRight = mLeft + 48;
			int ttRight = ttLeft + width;
			if ((ttLeft < mRight && ttRight > mRight)
					|| (ttRight > mLeft && ttLeft < mLeft)) {
				if (isSmall) {
					moveBtn.removeStyleName("moveMoveBtnDownSmall");
					moveBtn.addStyleName("moveMoveBtnUpSmall");
				} else {
					moveBtn.removeStyleName("moveMoveBtnDown");
					moveBtn.addStyleName("moveMoveBtnUp");
				}
				return true; // button was moved
			}
		}
		return false; // button was not moved
	}

	/**
	 * @param isSmall
	 *            - is small tooltip
	 * @param wasMoved
	 *            - true if was moved
	 */
	public void moveMoveFloatingButtonDownWithTooltip(boolean isSmall,
			boolean wasMoved) {
		if (moveBtn != null && wasMoved) {

			if (isSmall) {
				moveBtn.addStyleName("moveMoveBtnDownSmall");
				moveBtn.removeStyleName("moveMoveBtnUpSmall");
			} else {
				moveBtn.addStyleName("moveMoveBtnDown");
				moveBtn.removeStyleName("moveMoveBtnUp");
			}
		}
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

	/**
	 * Opens algebra tab.
	 */
	public void openAlgebra() {
		header.selectAlgebra();
		open();
		main.addStyleName("algebra");
		main.removeStyleName("tools");
	}

	/**
	 * Opens tools tab.
	 */
	public void openTools() {
		ToolTipManagerW.hideAllToolTips();
		header.selectTools();

		open();
		main.removeStyleName("algebra");
		main.addStyleName("tools");


	}

	/**
	 * select tools tab
	 */
	public void selectTools() {
		header.selectTools();
		main.removeStyleName("algebra");
		main.addStyleName("tools");
	}

	/**
	 * @return tool tab
	 */
	public ToolsTab getTabTools() {
		return tabTools;
	}

	private void open() {

		if (!isOpen()) {
			doOpen();
		}
		onOpen();
	}

	/**
	 * Called after open.
	 */
	protected void onOpen() {
		resizeTabs();
		main.getElement().getStyle().setProperty("height", "calc(100% - 56px)");
		main.getElement().getStyle().setProperty("width",
				(TAB_COUNT * 100) + "%");
	}

	/**
	 * Resize tabs.
	 */
	public void resize() {
		int w = getOffsetWidth();
		if (w == 0) {
			return;
		}

		header.resize();
		resizeTabs();
	}

	private void resizeTabs() {
		// main.setWidth(TAB_COUNT * getTabWidth() + "px");

		if (tabAlgebra != null) {
			tabAlgebra.onResize();
		}

		if (tabTools != null) {
			tabTools.onResize();
		}
	}
	/**
	 * Shows/hides full toolbar.
	 */
	void updateStyle() {
		setMinimumSize();
		if (header.isOpen()) {
			main.removeStyleName("hidden");
		} else {
			main.addStyleName("hidden");

		}
	}

	/**
	 * 
	 * @return true if AV is selected and ready to use.
	 */
	public boolean isAlgebraViewActive() {
		return tabAlgebra != null && selectedTab == TabIds.ALGEBRA;
	}

	/**
	 * Scrolls to currently edited item, if AV is active.
	 */
	public void scrollToActiveItem() {
		if (isAlgebraViewActive()) {
			tabAlgebra.scrollToActiveItem();
		}
	}

	/**
	 * 
	 * @return the selected tab id.
	 */
	public TabIds getSelectedTab() {
		return selectedTab;
	}

	/**
	 * 
	 * @param selectedTab
	 *            to set.
	 */
	public void setSelectedTab(TabIds selectedTab) {
		this.selectedTab = selectedTab;
	}

	/**
	 * 
	 * @return The height that AV should have minimally in portrait mode.
	 */
	public double getMinVHeight() {
		return 3 * header.getOffsetHeight();
				
	}

	/**
	 * Saves the scroll position of algebra view
	 */
	public void saveAVScrollPosition() {
		tabAlgebra.saveScrollPosition();
	}

	/**
	 * Scrolls to the bottom of AV.
	 */
	public void scrollAVToBottom() {
		if (tabAlgebra != null) {
			tabAlgebra.scrollToBottom();
		}
	}

	/**
	 * @return keyboard listener of AV.
	 * 
	 */
	public MathKeyboardListener getKeyboardListener() {
		if (tabAlgebra == null
				|| app.getInputPosition() != InputPosition.algebraView) {
			return null;
		}
		return ((AlgebraViewW) app.getAlgebraView()).getActiveTreeItem();
	}

	/**
	 * @param ml
	 *            to update.
	 * @return the updated listener.
	 */
	public MathKeyboardListener updateKeyboardListener(
			MathKeyboardListener ml) {
		if (tabAlgebra.aview != null
				&& tabAlgebra.aview.getInputTreeItem() != ml) {
			return ml;
		}

		// if no retex editor yet
		if (!(ml instanceof RadioTreeItem)) {
			return ml;
		}
		LatexTreeItemController itemController = ((RadioTreeItem) ml)
				.getLatexController();
		itemController.initAndShowKeyboard(false);
		return itemController.getRetexListener();
	}

	/**
	 * 
	 * @return true if toolbar is closed by user with close button, and not by
	 *         code.
	 */
	boolean isClosedByUser() {
		return closedByUser;
	}

	/**
	 * Sets if user closed the toolbar.
	 * 
	 * @param value
	 *            to set
	 */
	void setClosedByUser(boolean value) {
		this.closedByUser = value;
	}

	/**
	 * 
	 * @return if toolbar is animating or not.
	 */
	public boolean isAnimating() {
		return header.isAnimating();
	}

	/**
	 * Resize in a deferred way.
	 */
	public void deferredOnResize() {
		Scheduler.get().scheduleDeferred(deferredOnRes);
	}

	public void updateHeader() {
		header.updateStyle();
	}

	/**
	 * Called when app changes orientation.
	 */
	public void onOrientationChange() {
		header.onOrientationChange();

	}
}

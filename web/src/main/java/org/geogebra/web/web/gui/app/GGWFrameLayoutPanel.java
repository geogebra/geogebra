package org.geogebra.web.web.gui.app;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.keyboard.UpdateKeyBoardListener;
import org.geogebra.web.html5.util.keyboard.VirtualKeyboard;
import org.geogebra.web.keyboard.OnScreenKeyBoard;
import org.geogebra.web.web.gui.laf.GLookAndFeel;
import org.geogebra.web.web.gui.layout.DockGlassPaneW;
import org.geogebra.web.web.gui.layout.DockManagerW;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.web.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.web.main.AppWFull;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Frame for app
 */
public class GGWFrameLayoutPanel extends LayoutPanel implements
        UpdateKeyBoardListener {

	private boolean menuClosed = true;

	private FlowPanel menuContainer;
	private GuiManagerInterfaceW guiManagerW;

	private GGWToolBar ggwToolBar;
	private GGWCommandLine ggwCommandLine;
	private GGWMenuBar ggwMenuBar;
	private EuclidianDockPanelW ggwGraphicView;
	/** dock panel */
	MyDockPanelLayout dockPanel;
	private MyDockPanelLayout mainPanel;
	private SimplePanel spaceForKeyboard;
	/** Whether keyboard is visible */
	boolean keyboardShowing = false;
	/** KB button */
	ShowKeyboardButton showKeyboardButton;
	
	private DockGlassPaneW glassPane;

	private boolean algebraBottom = false;
	/** application */
	AppW app;

	/**
	 * Create new frame
	 */
	public GGWFrameLayoutPanel() {
		super();

		dockPanel = new MyDockPanelLayout(Style.Unit.PX);
		ggwGraphicView = new EuclidianDockPanelW(true);
		glassPane = new DockGlassPaneW(null);
		mainPanel = new MyDockPanelLayout(Style.Unit.PX);
		spaceForKeyboard = new SimplePanel();
		mainPanel.addSouth(spaceForKeyboard, 0);
		mainPanel.add(dockPanel);

		ClickStartHandler.init(dockPanel, new ClickStartHandler() {
			@Override
			public void onClickStart(final int x, final int y,
					final PointerEventType type) {
				((AppWFull) app).updateAVStylebar();

				if (!CancelEventTimer.cancelKeyboardHide()) {
					Timer timer = new Timer() {
						@Override
						public void run() {
							confirmAVInput(x, y);
						}
					};
					timer.schedule(0);
				}
			}
		});

		dockPanel.addDomHandler(new TouchMoveHandler() {
			@Override
			public void onTouchMove(TouchMoveEvent event) {
				// prevent zooming
				if (event.getTouches().length() > 1) {
					event.preventDefault();
					event.stopPropagation();
				}
			}
		}, TouchMoveEvent.getType());

		add(glassPane);
		add(mainPanel);
	}

	/**
	 * 
	 * @param x
	 *            click x-coordinate
	 * @param y
	 *            click y-coordinate
	 */
	protected void confirmAVInput(int x, int y) {
		boolean focusLost = true;
		if (app.getGuiManager() != null && app.has(Feature.INPUT_BAR_PREVIEW)
				&& app.getGuiManager().getLayout().getDockManager() != null) {
			DockPanelW panel = ((DockManagerW) app.getGuiManager().getLayout()
					.getDockManager()).getPanelForKeyboard();
			MathKeyboardListener kl = panel
					.getKeyboardListener();
			if (kl != null) {
				String text = kl.getText();
				if (text != null && !text.isEmpty()) {
					focusLost = !((x > panel.getAbsoluteLeft()
							- app.getAbsLeft())
							&& (x < panel.getAbsoluteLeft() - app.getAbsLeft()
									+ panel.getWidth())
							&& (y > panel.getAbsoluteTop() - app.getAbsTop())
							&& (y < panel.getAbsoluteTop() - app.getAbsTop()
									+ panel.getHeight()));
					kl.onEnter(!focusLost);

				} else {
					kl.setFocus(false, false);
				}
			}

		}
		if (focusLost) {
			keyBoardNeeded(false, null);
		}
	}

	/**
	 * @param app
	 *            application
	 */
	public void setLayout(final AppW app) {
		this.guiManagerW = app.getGuiManager();
		this.app = app;
		glassPane.setArticleElement(app.getArticleElement());
		dockPanel.clear();
		dockPanel.addNorth(getToolBar(), GLookAndFeelI.TOOLBAR_HEIGHT);
		if(app.showAlgebraInput()){
			switch (app.getInputPosition()) {
			case top:
				dockPanel.addNorth(getCommandLine(), GLookAndFeelI.COMMAND_LINE_HEIGHT);
				break;
			case bottom:
				dockPanel.addSouth(getCommandLine(), GLookAndFeelI.COMMAND_LINE_HEIGHT);
				break;
			case algebraView:
				// done at the end
				break;
			default: 
				break;
			}
		}
		if (app.getGuiManager().hasAlgebraView()) {
			((AlgebraViewW) app.getAlgebraView()).setShowAlgebraInput(app
				.showAlgebraInput()
				&& app.getInputPosition() == InputPosition.algebraView);
		}
		if (app.getGuiManager().getRootComponent() != null) {
			dockPanel.add(app.getGuiManager().getRootComponent());
			app.getGuiManager().getRootComponent().setStyleName("ApplicationPanel");
		}

		// keyboard is visible and material with input bar is opened -> hide
		// keyboard
		if (app.getInputPosition() != InputPosition.algebraView
				&& keyboardShowing) {
			keyboardShowing = false;
			this.mainPanel.clear();
			this.mainPanel.add(this.dockPanel);
		}

		onResize();

		Timer timer = new Timer() {
			@Override
			public void run() {
				// show the keyboard button
				if (app.getGuiManager() != null
						&& app.getGuiManager().hasAlgebraView()
						&& !keyboardShowing
						&& app.getInputPosition() == InputPosition.algebraView) {
					showKeyboardButton(true,
							((AlgebraViewW) app.getAlgebraView())
									.getInputTreeItem());
				} else {
					if (showKeyboardButton != null) {
						showKeyboardButton.show(false, null);
					}
				}
			}
		};
		timer.schedule(0);

	}

	/**
	 * for Tablets it shows/hides the keyboard. For Web it shows a button to
	 * open the {@link OnScreenKeyBoard}
	 * 
	 * @param show
	 *            whether to show keyboard
	 * @param textField
	 *            text field receiving the text from keyboard
	 */
	@Override
	public void keyBoardNeeded(boolean show, MathKeyboardListener textField) {

		if ((app.getLAF().isTablet())
		        || keyboardShowing // if keyboard is already
		                           // showing, we don't have
		                           // to handle the showKeyboardButton
				|| app.getGuiManager().getOnScreenKeyboard(textField, this)
						.shouldBeShown()) {
			doShowKeyBoard(show, textField);
		} else {
			showKeyboardButton(show, textField);
		}
	}
	
	/**
	 * used for Web. Shows a button at the left lower corner to open the
	 * {@link OnScreenKeyBoard}.
	 * 
	 * @param show
	 *            whether to show the keyboard
	 * @param textField
	 *            text field receiving the text from keyboard
	 */
	public void showKeyboardButton(boolean show,
			final MathKeyboardListener textField) {
		if (!(app.showAlgebraInput() && app.getInputPosition() == InputPosition.algebraView)) {
			return;
		}
		if (showKeyboardButton == null) {
			DockManagerW dm = (DockManagerW) guiManagerW.getLayout()
					.getDockManager();
			DockPanelW dockPanelKB = dm.getPanelForKeyboard();

			if (dockPanelKB != null) {
				showKeyboardButton = new ShowKeyboardButton(this, dm,
						dockPanelKB);
				dockPanelKB.setKeyBoardButton(showKeyboardButton);
			}



		}
		showKeyboardButton.show(show || app.isKeyboardNeeded(), textField);
	}

	/**
	 * Resize dockpanel when keyboard shown/hidden
	 */
	public void updateKeyboardHeight() {
		VirtualKeyboard keyboard = app.getGuiManager().getOnScreenKeyboard(
				null, this);
		if (spaceForKeyboard != null
				&& spaceForKeyboard.getParent() == this.mainPanel) {
		this.mainPanel.setWidgetSize(spaceForKeyboard,
				keyboard.getOffsetHeight());
		} else if (spaceForKeyboard != null) {
			Log.debug("Unexpected keyboard parent: "
					+ spaceForKeyboard.getParent());
		} else {
			Log.debug("Keyboard is null.");
		}

		// necessary to prevent lag when resizing panels/widgets
		this.mainPanel.forceLayout();

		Timer timer = new Timer() {
			@Override
			public void run() {
				onResize();
				dockPanel.onResize();
				// scrollToInputField();
			}
		};
		app.getGuiManager().focusScheduled(false, false, false);
		timer.schedule(500);
	}

	/**
	 * Shows or hides keyboard. In case keyboard state changed, it rebuilds the
	 * DOM in the process so it may steal focus from currently selected element.
	 */
	public void doShowKeyBoard(boolean show,
	        final MathKeyboardListener textField) {
		if (app == null) {
			return;
		}
		// make sure the main part of this method is called ONLY WHEN NECESSARY
		if (this.keyboardShowing == show) {
			app.getGuiManager().setOnScreenKeyboardTextField(textField);
			return;
		}
		this.keyboardShowing = show;

		final int pos = textField == null ? 0 : textField.asWidget()
				.getElement().getScrollLeft();

		VirtualKeyboard keyBoard = app.getGuiManager().getOnScreenKeyboard(
				textField, this);
		if (show && textField != null) {
			keyBoard.show();
			CancelEventTimer.keyboardSetVisible();
			this.mainPanel.setWidgetSize(spaceForKeyboard,
					keyBoard.getOffsetHeight());
			spaceForKeyboard.add(keyBoard);
			
			if (showKeyboardButton != null) {
				showKeyboardButton.hide();
			}
		} else {
			if (app.getGuiManager().getLayout().getDockManager() != null) {
				this.mainPanel.setWidgetSize(spaceForKeyboard, 0);
				spaceForKeyboard.remove(keyBoard);
				showKeyboardButton(true, textField);
			}

			keyBoard.resetKeyboardState();
		}

		// necessary to prevent lag when resizing panels/widgets
		this.mainPanel.forceLayout();

		// necessary in Internet Explorer, should not do harm in other browsers
		// although we can add browser check here if necessary, but it may be slower?
		//if (Browser.isIE())// also might not cover every exception
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				if (textField != null) {
					textField.asWidget().getElement().setScrollLeft(pos);
				}
			}
		});

		Timer timer = new Timer() {
			@Override
			public void run() {
				onResize();
				dockPanel.onResize();
				// scrollToInputField();
				if(textField!= null){
					textField.setFocus(true, true);
					textField.ensureEditing();
				}
				// necessary in Internet Explorer, should not do harm in other browsers
				// although we can add browser check here if necessary, but it may be slower?
				//if (Browser.isIE())// also might not cover every exception
				if (((AlgebraViewW) app.getAlgebraView()).getInputTreeItem() != null) {
					((AlgebraViewW) app.getAlgebraView()).getInputTreeItem()
							.getElement().setScrollLeft(pos);
				}
			}
		};
		app.getGuiManager().focusScheduled(false, false, false);
		timer.schedule(500);
	}

	/**
	 * this should be extedns MyDockLayoutPanel to get out somehow the
	 * overflow:hidden to show the toolbar.
	 */
	class MyDockPanelLayout extends DockLayoutPanel {
		/**
		 * @param unit
		 *            CSS unit
		 */
		public MyDockPanelLayout(Unit unit) {
			super(unit);
			addStyleName("ggbdockpanelhack");
			addStyleName("wholePanel");
		}

		// protected -> public
		@Override
        public double getCenterWidth() {
			return super.getCenterWidth();
		}

		// protected -> public
		@Override
        public double getCenterHeight() {
			return super.getCenterHeight();
		}
	}

	/**
	 * @return dock panel width
	 */
	public double getCenterWidth() {
		return dockPanel.getCenterWidth();
	}

	/**
	 * @return dock panel height
	 */
	public double getCenterHeight() {
		return dockPanel.getCenterHeight();
	}

	/**
	 * @return toolbar
	 */
	public GGWToolBar getToolBar() {
		if (ggwToolBar == null) {
			ggwToolBar = newGGWToolBar();
		}
		return ggwToolBar;
	}
	
	/**
	 * 
	 * @return toolbar
	 */
	protected GGWToolBar newGGWToolBar(){
		return new GGWToolBar();
	}

	/**
	 * @return inputbar
	 */
	public GGWCommandLine getCommandLine() {
		if (ggwCommandLine == null) {
			ggwCommandLine = new GGWCommandLine();
		}
		return ggwCommandLine;
	}

	/**
	 * @return menubar
	 */
	public GGWMenuBar getMenuBar() {
		if (ggwMenuBar == null) {
			ggwMenuBar = new GGWMenuBar();
		}
		return ggwMenuBar;
	}
	
	/**
	 * @return EV dock panel
	 */
	public EuclidianDockPanelW getGGWGraphicsView() {
		return ggwGraphicView;
	}

	/**
	 * @return glass pane for view moving
	 */
	public DockGlassPaneW getGlassPane() {
		return glassPane;
	}
	
	/**
	 * @return switch menu on / off
	 */
	public boolean toggleMenu() {
		boolean needsUpdate = menuContainer != null;
		if(menuContainer == null){
			createMenuContainer();
		}
		
		if (this.menuClosed) {
			this.menuClosed = false;
			this.add(this.menuContainer);
			this.menuContainer.setVisible(true);
			if (needsUpdate) {
				guiManagerW.updateMenubar();
			}
			updateSize();
			guiManagerW.updateStyleBarPositions(true);
		} else {
			hideMenu();
		}
		return !menuClosed;
	}

	/**
	 * Hide the menu
	 */
	public void hideMenu() {
		if (menuContainer == null || menuClosed) {
			return;
		}
		this.menuClosed = true;
		guiManagerW.updateStyleBarPositions(false);
		this.remove(this.menuContainer);

	}

	private void createMenuContainer() {
	    menuContainer = new FlowPanel();
	    menuContainer.addStyleName("menuContainer");
	    menuContainer.add(getMenuBar());
	    updateSize();
    }

	/**
	 * @return true iff the menu is open
	 */
	public boolean isMenuOpen() {
		return !menuClosed;
	}
	
	@Override
	public void onResize() {
		super.onResize();
		if (this.menuContainer != null && this.getWidgetIndex(this.menuContainer) != -1) {
			updateSize();
		}
	}

	/**
	 * updates height of the menu
	 * @param showAlgebraInput boolean
	 */
	public void setMenuHeight(boolean showAlgebraInput) {
		this.algebraBottom = showAlgebraInput;
	    updateSize();
    }
	
	private void updateSize() {
		if (this.menuContainer != null) {
			int height = 0;
	    	if (this.algebraBottom) {
		    	height = (int) (this.app.getHeight() - GLookAndFeelI.TOOLBAR_HEIGHT - GLookAndFeelI.COMMAND_LINE_HEIGHT);
		    	
	    	} else {
	    		height = (int) (this.app.getHeight() - GLookAndFeelI.TOOLBAR_HEIGHT);
	    	}
			this.menuContainer.setHeight(height + "px");
			this.menuContainer.setWidth(GLookAndFeel.MENUBAR_WIDTH + "px");

			this.ggwMenuBar.updateHeight(height);
	    }
	}

	/**
	 * @return 0 when keyboard hidden, keyboard height otherwise
	 */
	public double getKeyboardHeight() {
		if (!keyboardShowing || spaceForKeyboard == null) {
			return 0;
		}
		return spaceForKeyboard.getOffsetHeight();
	}
}

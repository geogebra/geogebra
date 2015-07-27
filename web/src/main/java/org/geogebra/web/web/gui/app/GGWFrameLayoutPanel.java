package org.geogebra.web.web.gui.app;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPositon;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.keyboard.UpdateKeyBoardListener;
import org.geogebra.web.html5.util.keyboard.VirtualKeyboard;
import org.geogebra.web.web.gui.laf.GLookAndFeel;
import org.geogebra.web.web.gui.layout.DockGlassPaneW;
import org.geogebra.web.web.gui.layout.DockManagerW;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.layout.panels.AlgebraDockPanelW;
import org.geogebra.web.web.gui.layout.panels.AlgebraStyleBarW;
import org.geogebra.web.web.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.web.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.web.util.keyboard.OnScreenKeyBoard;

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
import com.google.gwt.user.client.ui.Widget;

public class GGWFrameLayoutPanel extends LayoutPanel implements
        UpdateKeyBoardListener {

	private boolean menuClosed = true;

	private FlowPanel menuContainer;
	private GuiManagerInterfaceW guiManagerW;

	GGWToolBar ggwToolBar;
	GGWCommandLine ggwCommandLine;
	GGWMenuBar ggwMenuBar;
	EuclidianDockPanelW ggwGraphicView;
	MyDockPanelLayout dockPanel;
	MyDockPanelLayout mainPanel;
	SimplePanel spaceForKeyboard;
	boolean keyboardShowing = false;
	ShowKeyboardButton showKeyboardButton;
	
	private DockGlassPaneW glassPane;

	private boolean algebraBottom = false;

	AppW app;

	public AppW getAppW() {
		return app;
	}

	public GGWFrameLayoutPanel() {
		super();

		dockPanel = new MyDockPanelLayout(Style.Unit.PX);
		ggwGraphicView = new EuclidianDockPanelW(true);
		glassPane = new DockGlassPaneW();
		mainPanel = new MyDockPanelLayout(Style.Unit.PX);
		spaceForKeyboard = new SimplePanel();
		mainPanel.addSouth(spaceForKeyboard, 0);
		mainPanel.add(dockPanel);

		ClickStartHandler.init(dockPanel, new ClickStartHandler() {
			@Override
			public void onClickStart(int x, int y, final PointerEventType type) {
				AlgebraStyleBarW styleBar = ((AlgebraViewW) app
						.getView(App.VIEW_ALGEBRA)).getStyleBar(false);
				if (styleBar != null) {
					styleBar.update(null);
				}

				if (!CancelEventTimer.cancelKeyboardHide()) {
					Timer timer = new Timer() {
						@Override
						public void run() {
							keyBoardNeeded(false, null);
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
				&& app.getInputPosition() == InputPositon.algebraView);
		}
		if (app.getGuiManager().getRootComponent() != null) {
			dockPanel.add(app.getGuiManager().getRootComponent());
			app.getGuiManager().getRootComponent().setStyleName("ApplicationPanel");
		}

		// keyboard is visible and material with input bar is opened -> hide
		// keyboard
		if (app.getInputPosition() != InputPositon.algebraView
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
						&& app.getInputPosition() == InputPositon.algebraView) {
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

		if (app.getLAF().isTablet()
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
		if (!(app.showAlgebraInput() && app.getInputPosition() == InputPositon.algebraView)) {
			return;
		}
		if (showKeyboardButton == null) {
			if (app.has(Feature.CAS_EDITOR)) {
				DockManagerW dm = (DockManagerW) guiManagerW.getLayout()
						.getDockManager();
				DockPanelW dockPanelKB = dm.getPanelForKeyboard();

				if (dockPanelKB != null) {
					showKeyboardButton = new ShowKeyboardButton(this, dm,
							dockPanelKB);
					dockPanelKB.setKeyBoardButton(showKeyboardButton);
				}

			} else {
				showInAlgebra(textField);
			}

		}
		showKeyboardButton.show(show || app.isKeyboardNeeded(), textField);
	}

	private void showInAlgebra(final MathKeyboardListener textField) {
		DockPanel algebraDockPanel = guiManagerW.getLayout().getDockManager()
				.getPanel(App.VIEW_ALGEBRA);

		if (algebraDockPanel != null) {
			showKeyboardButton = new ShowKeyboardButton(this,
					(DockManagerW) guiManagerW.getLayout().getDockManager(),
					((Widget) algebraDockPanel));
			if (algebraDockPanel instanceof AlgebraDockPanelW) {
				((AlgebraDockPanelW) algebraDockPanel)
						.setKeyBoardButton(showKeyboardButton);
			}
		}
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

			this.mainPanel.setWidgetSize(spaceForKeyboard, keyBoard.getOffsetHeight());
			spaceForKeyboard.add(keyBoard);
			
			if (showKeyboardButton != null) {
				showKeyboardButton.hide();
			}
		} else {
			if (app.has(Feature.CAS_EDITOR)) {
				if (app.getGuiManager().getLayout().getDockManager() != null) {

					this.mainPanel.setWidgetSize(spaceForKeyboard, 0);
					spaceForKeyboard.remove(keyBoard);
					showKeyboardButton(true, textField);
				}
			} else {
				showKBButtonInAlgebra(textField, keyBoard);
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
				scrollToInputField();
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

	private void showKBButtonInAlgebra(MathKeyboardListener textField,
			VirtualKeyboard keyBoard) {
		if (app.getAlgebraView() != null) {

			this.mainPanel.setWidgetSize(spaceForKeyboard, 0);
			spaceForKeyboard.remove(keyBoard);
			MathKeyboardListener tf = textField != null ? textField
					: ((AlgebraViewW) app.getAlgebraView())
							.getInputTreeItem();
			showKeyboardButton(true, tf);
		}

	}

	@Override
	public void showInputField() {
		Timer timer = new Timer() {
			@Override
			public void run() {
				scrollToInputField();
			}
		};
		timer.schedule(0);
	}

	/**
	 * Scroll to the input-field, if the input-field is in the algebraView.
	 */
	void scrollToInputField(){
		if (app.showAlgebraInput()
		        && app.getInputPosition() == InputPositon.algebraView) {
			((AlgebraDockPanelW) (app.getGuiManager().getLayout()
			        .getDockManager()
			        .getPanel(App.VIEW_ALGEBRA)))
			        .scrollToBottom();
		}
	}

	//this should be extedns MyDockLayoutPanel to get out somehow the overflow:hidden to show the toolbar.
	class MyDockPanelLayout extends DockLayoutPanel {
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

	public double getCenterWidth() {
		return dockPanel.getCenterWidth();
	}

	public double getCenterHeight() {
		return dockPanel.getCenterHeight();
	}

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

	public GGWCommandLine getCommandLine() {
		if (ggwCommandLine == null) {
			ggwCommandLine = new GGWCommandLine();
		}
		return ggwCommandLine;
	}

	public GGWMenuBar getMenuBar() {
		if (ggwMenuBar == null) {
			ggwMenuBar = new GGWMenuBar();
		}
		return ggwMenuBar;
	}
	
	public EuclidianDockPanelW getGGWGraphicsView() {
		return ggwGraphicView;
	}

	public DockGlassPaneW getGlassPane() {
		return glassPane;
	}
	
	public boolean toggleMenu() {
		if(menuContainer == null){
			createMenuContainer();
		}
		
		if (this.menuClosed) {
			this.menuClosed = false;
			this.add(this.menuContainer);
			this.menuContainer.setVisible(true);
			guiManagerW.updateMenubar();
			updateSize();
			guiManagerW.updateStyleBarPositions(true);
		} else {
			hideMenu();
		}
		return !menuClosed;
	}

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
}

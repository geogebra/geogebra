package geogebra.web.gui.app;

import geogebra.common.euclidian.event.PointerEventType;
import geogebra.common.gui.layout.DockPanel;
import geogebra.common.main.App;
import geogebra.common.main.App.InputPositon;
import geogebra.html5.gui.GuiManagerInterfaceW;
import geogebra.html5.gui.laf.GLookAndFeelI;
import geogebra.html5.gui.util.CancelEventTimer;
import geogebra.html5.gui.util.ClickStartHandler;
import geogebra.html5.gui.view.algebra.MathKeyboardListener;
import geogebra.html5.main.AppW;
import geogebra.web.gui.laf.GLookAndFeel;
import geogebra.web.gui.layout.DockGlassPaneW;
import geogebra.web.gui.layout.panels.AlgebraDockPanelW;
import geogebra.web.gui.layout.panels.AlgebraStyleBarW;
import geogebra.web.gui.layout.panels.EuclidianDockPanelW;
import geogebra.web.gui.view.algebra.AlgebraViewWeb;
import geogebra.web.util.keyboard.OnScreenKeyBoard;
import geogebra.web.util.keyboard.UpdateKeyBoardListener;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
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
	boolean keyboardShowing = false;
	ShowKeyboardButton showKeyboardButton;
	
	private DockGlassPaneW glassPane;

	private boolean algebraBottom = false;

	AppW app;

	public GGWFrameLayoutPanel() {
		super();

		dockPanel = new MyDockPanelLayout(Style.Unit.PX);
		ggwGraphicView = new EuclidianDockPanelW(true);
		glassPane = new DockGlassPaneW();
		mainPanel = new MyDockPanelLayout(Style.Unit.PX);
		mainPanel.add(dockPanel);

		ClickStartHandler.init(dockPanel, new ClickStartHandler() {
			@Override
			public void onClickStart(int x, int y, final PointerEventType type) {
				AlgebraStyleBarW styleBar = ((AlgebraViewWeb) app
						.getView(App.VIEW_ALGEBRA)).getStyleBar();
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
		((AlgebraViewWeb) app.getAlgebraView()).setShowAlgebraInput(app.showAlgebraInput() && app.getInputPosition() == InputPositon.algebraView);

		if (app.getGuiManager().getRootComponent() != null) {
			dockPanel.add(app.getGuiManager().getRootComponent());
			app.getGuiManager().getRootComponent().setStyleName("ApplicationPanel");
		}

		onResize();

		Timer timer = new Timer() {
			@Override
			public void run() {
				// show the keyboard button
				if (app.getAlgebraView() != null
						&& !keyboardShowing
						&& app.getInputPosition() == InputPositon.algebraView) {
					showKeyboardButton(true,
							((AlgebraViewWeb) app.getAlgebraView())
									.getInputTreeItem());
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
		        || OnScreenKeyBoard.getInstance(textField, this, app)
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
		if (showKeyboardButton == null) {
			DockPanel algebraDockPanel = guiManagerW.getLayout()
					.getDockManager()
					.getPanel(App.VIEW_ALGEBRA);

			if (algebraDockPanel != null) {
				showKeyboardButton = new ShowKeyboardButton(this, textField,
						((Widget) algebraDockPanel));
				if (algebraDockPanel instanceof AlgebraDockPanelW) {
					((AlgebraDockPanelW) algebraDockPanel)
							.setKeyBoardButton(showKeyboardButton);
				}
			}
		}
		showKeyboardButton.show(app.isKeyboardNeeded(), textField);
	}

	/**
	 * Shows or hides keyboard. In case keyboard state changed, it rebuilds the
	 * DOM in the process so it may steal focus from currently selected element.
	 */
	public void doShowKeyBoard(boolean show,
	        final MathKeyboardListener textField) {
		// make sure the main part of this method is called ONLY WHEN NECESSARY
		if (this.keyboardShowing == show) {
			return;
		}
		this.keyboardShowing = show;
		this.mainPanel.clear();
		OnScreenKeyBoard keyBoard = OnScreenKeyBoard.getInstance(textField,
		        this, app);
		if (show && textField != null) {
			keyBoard.show();
			CancelEventTimer.keyboardSetVisible();
			this.mainPanel.addSouth(keyBoard, keyBoard.getOffsetHeight());
			if (showKeyboardButton != null) {
				showKeyboardButton.hide();
			}
		} else {
			if (app.getAlgebraView() != null) {
				showKeyboardButton(
						true,
						textField != null ? textField : ((AlgebraViewWeb) app
								.getAlgebraView()).getInputTreeItem());
			}
			keyBoard.resetKeyboardState();
		}
		this.mainPanel.add(this.dockPanel);

		Timer timer = new Timer() {
			@Override
			public void run() {
				onResize();
				dockPanel.onResize();
				scrollToInputField();
				textField.setFocus(true);
				textField.ensureEditing();
			}
		};
		timer.schedule(500);
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
			        .getPanel(geogebra.common.main.App.VIEW_ALGEBRA)))
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
			this.menuClosed = true;
			guiManagerW.updateStyleBarPositions(false);
			this.remove(this.menuContainer);
		}
		return !menuClosed;
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

	@Override
	public void closePopups() {
		app.closePopups();
	}
}

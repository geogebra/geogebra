package geogebra.web.gui.app;

import geogebra.common.main.App.InputPositon;
import geogebra.html5.gui.GuiManagerInterfaceW;
import geogebra.html5.gui.laf.GLookAndFeelI;
import geogebra.html5.main.AppW;
import geogebra.web.gui.laf.GLookAndFeel;
import geogebra.web.gui.layout.DockGlassPaneW;
import geogebra.web.gui.layout.panels.EuclidianDockPanelW;
import geogebra.web.gui.view.algebra.AlgebraViewWeb;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;

public class GGWFrameLayoutPanel extends LayoutPanel implements RequiresResize {

	private boolean menuClosed = true;

	private FlowPanel menuContainer;
	private GuiManagerInterfaceW guiManagerW;

	GGWToolBar ggwToolBar;
	GGWCommandLine ggwCommandLine;
	GGWMenuBar ggwMenuBar;
	EuclidianDockPanelW ggwGraphicView;
	MyDockPanelLayout dockPanel;
	
	private DockGlassPaneW glassPane;

	private boolean algebraBottom = false;

	public GGWFrameLayoutPanel() {
		super();

		dockPanel = new MyDockPanelLayout(Style.Unit.PX);
		ggwGraphicView = new EuclidianDockPanelW(true);
		glassPane = new DockGlassPaneW();
		
		add(glassPane);
		add(dockPanel);
	}

	public void setLayout(final AppW app) {
		this.guiManagerW = app.getGuiManager();
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
	}

	//this should be extedns MyDockLayoutPanel to get out somehow the overflow:hidden to show the toolbar.
	class MyDockPanelLayout extends DockLayoutPanel {
		public MyDockPanelLayout(Unit unit) {
			super(unit);
			addStyleName("ggbdockpanelhack");
		}

		public double getCenterWidth() {
			return super.getCenterWidth();
		}

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
			this.menuContainer.setWidth(GLookAndFeel.MENUBAR_WIDTH + "px");
			this.add(this.menuContainer);
			this.menuContainer.setVisible(true);
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
		if (this.menuContainer != null) {
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
		    	height = Window.getClientHeight() - GLookAndFeelI.TOOLBAR_HEIGHT - GLookAndFeelI.COMMAND_LINE_HEIGHT;
		    	
	    	} else {
	    		height = Window.getClientHeight() - GLookAndFeelI.TOOLBAR_HEIGHT;
	    	}
			this.menuContainer.setHeight(height + "px");
			this.ggwMenuBar.updateHeight(height);
	    }
	}
}

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
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;

public class GGWFrameLayoutPanel extends LayoutPanel implements RequiresResize {

	private int menuBarWidth = 0;
	int menuBarTop = 0;
	private boolean menuClosed = true;

	private FlowPanel menuPanel;
	private GuiManagerInterfaceW guiManagerW;

	GGWToolBar ggwToolBar;
	GGWCommandLine ggwCommandLine;
	GGWMenuBar ggwMenuBar;
	EuclidianDockPanelW ggwGraphicView;
	MyDockPanelLayout dockPanel;
	
	private DockGlassPaneW glassPane;

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

		this.menuBarTop = 0;

		// if(app.showToolBar()){
		dockPanel.addNorth(getToolBar(), GLookAndFeelI.TOOLBAR_HEIGHT);
		// }
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
		/*if(ggwMenuBar!=null){
			ggwMenuBar.getMenubar().onResize(null);
		}*/

		Timer timer = new Timer(){
			@Override
            public void run() {
				if(app.getInputPosition() == InputPositon.top ){
					menuBarTop = getCommandLine().getOffsetHeight();
				}
				ggwMenuBar.getElement().getStyle().setTop(menuBarTop, Unit.PX);
            }			
		};
		timer.schedule(0);
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
		
		if(menuPanel == null){
			menuPanel =  new FlowPanel();
			menuPanel.addStyleName("menuPanel");
			menuPanel.add(ggwMenuBar);
		}

		if (this.menuClosed) {
			add(menuPanel);
			this.menuBarWidth = GLookAndFeel.MENUBAR_WIDTH;
			ggwMenuBar.setWidth(this.menuBarWidth + "px");
			ggwMenuBar.getElement().getStyle().setTop(menuBarTop, Unit.PX);
			ggwMenuBar.getElement().getStyle().setPosition(Position.RELATIVE);
			ggwMenuBar.focus();
			this.menuClosed = false;
			guiManagerW.updateStyleBarPositions(true);
		} else {
			//close menu
			remove(menuPanel);
			this.menuBarWidth = 0;
			this.menuClosed = true;
			guiManagerW.updateStyleBarPositions(false);
			return false;
		}
		return !menuClosed;
	}

	/**
	 * @return true iff the menu is open
	 */
	public boolean isMenuOpen() {
		return !menuClosed;
	}

}

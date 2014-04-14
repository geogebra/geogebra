package geogebra.web.gui.app;

import geogebra.html5.gui.laf.GLookAndFeel;
import geogebra.web.gui.layout.DockGlassPaneW;
import geogebra.web.gui.layout.panels.EuclidianDockPanelW;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;

public class GGWFrameLayoutPanel extends LayoutPanel implements RequiresResize {


	private int MENUBAR_WIDTH = 0;
	private boolean menuClosed = true;
	

	public static final int MINUS_FROM_HEIGHT = GLookAndFeel.COMMAND_LINE_HEIGHT + GLookAndFeel.MENUBAR_HEIGHT + GLookAndFeel.TOOLBAR_HEIGHT;

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

	public void setLayout(AppW app) {
		glassPane.setArticleElement(app.getArticleElement());
		dockPanel.clear();
		
		// if(app.showToolBar()){
		dockPanel.addNorth(getToolBar(), GLookAndFeel.TOOLBAR_HEIGHT);
		// }
		if (app.showInputTop()) {
			dockPanel.addNorth(getCommandLine(), GLookAndFeel.COMMAND_LINE_HEIGHT);
		} else {
			dockPanel.addSouth(getCommandLine(), GLookAndFeel.COMMAND_LINE_HEIGHT);
		}
		
		dockPanel.addEast(getMenuBar(), MENUBAR_WIDTH);

		if (app.getGuiManager().getLayout().getRootComponent() != null) {
			dockPanel.add(app.getGuiManager().getLayout().getRootComponent());
			app.getGuiManager().getLayout().getRootComponent().setStyleName("ApplicationPanel");
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
		
		if (this.menuClosed) {
			//open menu
			this.MENUBAR_WIDTH = GLookAndFeel.MENUBAR_WIDTH_MAX;
			this.dockPanel.setWidgetSize(ggwMenuBar, MENUBAR_WIDTH);
			this.dockPanel.forceLayout();
			ggwMenuBar.focus();
			this.menuClosed = false;
		} else {
			//close menu
			this.MENUBAR_WIDTH = 0;
			this.dockPanel.setWidgetSize(ggwMenuBar, MENUBAR_WIDTH);
			this.dockPanel.forceLayout();
			this.menuClosed = true;
			return false;
		}
		return !menuClosed;
	}

}

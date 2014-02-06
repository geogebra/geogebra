package geogebra.web.gui.app;

import geogebra.web.gui.layout.DockGlassPaneW;
import geogebra.web.gui.layout.panels.EuclidianDockPanelW;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;

public class GGWFrameLayoutPanel extends LayoutPanel implements RequiresResize {

	public static final int COMMAND_LINE_HEIGHT = 43;
	public static final int MENUBAR_HEIGHT = 35;
	public static final int TOOLBAR_HEIGHT = 61;

	public static final int MINUS_FROM_HEIGHT = COMMAND_LINE_HEIGHT + MENUBAR_HEIGHT + TOOLBAR_HEIGHT;

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
		
		dockPanel.clear();

		dockPanel.addNorth(getMenuBar(), MENUBAR_HEIGHT);
		
		// if(app.showToolBar()){
		dockPanel.addNorth(getToolBar(), TOOLBAR_HEIGHT);
		// }
		if (app.showInputTop()) {
			dockPanel.addNorth(getCommandLine(), COMMAND_LINE_HEIGHT);
		} else {
			dockPanel.addSouth(getCommandLine(), COMMAND_LINE_HEIGHT);
		}

		if (app.getGuiManager().getLayout().getRootComponent() != null) {
			dockPanel.add(app.getGuiManager().getLayout().getRootComponent());
			app.getGuiManager().getLayout().getRootComponent().setStyleName("ApplicationPanel");
		}

		onResize();
	}

	class MyDockPanelLayout extends DockLayoutPanel {
		public MyDockPanelLayout(Unit unit) {
			super(unit);
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
			ggwToolBar = new GGWToolBar();
		}
		return ggwToolBar;
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

}

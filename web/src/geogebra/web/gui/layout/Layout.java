package geogebra.web.gui.layout;

import java.util.ArrayList;


import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.gui.toolbar.ToolBar;
import geogebra.common.io.layout.DockPanelData;
import geogebra.common.io.layout.DockSplitPaneData;
import geogebra.common.io.layout.Perspective;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.SettingListener;
import geogebra.web.main.Application;

public class Layout extends geogebra.common.gui.Layout implements SettingListener {
	
	private boolean isInitialized = false;
	
	private Application app;
	
	/**
	 * instantiates layout for Web
	 */
	public Layout() {
		initializeDefaultPerspectives();
		
		this.perspectives = new ArrayList<Perspective>(defaultPerspectives.length);
	}

	private void initializeDefaultPerspectives() {
		defaultPerspectives = new Perspective[5];
		
		DockPanelData[] dpData;
		DockSplitPaneData[] spData;
		
		String defToolbar;

		// algebra & graphics (default settings of GeoGebra < 3.2)
		dpData = new DockPanelData[4];
		dpData[0] = new DockPanelData(AbstractApplication.VIEW_EUCLIDIAN, null, true, false, false, new geogebra.web.awt.Rectangle(100, 100, 600, 400), "1", 500);
		dpData[1] = new DockPanelData(AbstractApplication.VIEW_ALGEBRA, null, true, false, false, new geogebra.web.awt.Rectangle(100, 100, 250, 400), "3", 200);
		dpData[2] = new DockPanelData(AbstractApplication.VIEW_SPREADSHEET, null, false, false, false, new geogebra.web.awt.Rectangle(100, 100, 600, 400), "1,1", 300);
		dpData[3] = new DockPanelData(AbstractApplication.VIEW_CAS, null, false, false, false, new geogebra.web.awt.Rectangle(100, 100, 600, 400), "1,3", 300);

		spData = new DockSplitPaneData[1];
		spData[0] = new DockSplitPaneData("", 0.25, /*AGJSplitPane.HORIZONTAL_SPLIT*/1);

		defToolbar = ToolBar.getAllToolsNoMacros();

		defaultPerspectives[0] = new Perspective("AlgebraAndGraphics", spData, dpData, defToolbar, true, false, true, true, true, false);
		
		// basic geometry - just the euclidian view
		dpData = new DockPanelData[4];
		dpData[0] = new DockPanelData(AbstractApplication.VIEW_EUCLIDIAN, null, true, false, false, new geogebra.web.awt.Rectangle(100, 100, 600, 400), "1", 500);
		dpData[1] = new DockPanelData(AbstractApplication.VIEW_ALGEBRA, null, false, false, false, new geogebra.web.awt.Rectangle(100, 100, 250, 400), "3", 200);
		dpData[2] = new DockPanelData(AbstractApplication.VIEW_SPREADSHEET, null, false, false, false, new geogebra.web.awt.Rectangle(100, 100, 600, 400), "1,1", 300);
		dpData[3] = new DockPanelData(AbstractApplication.VIEW_CAS, null, false, false, false, new geogebra.web.awt.Rectangle(100, 100, 600, 400), "1,3", 300);
		
		defaultPerspectives[1] = new Perspective("BasicGeometry", spData, dpData, "0 | 40 | 1 | 19 | 15 | 2 | 10 | 3 | 4 | 5 | 16 | 64 | 70 | 51 | 17 | 36 | 30 | 32 ", true, false, false, false, false, false);
		defaultPerspectives[1].setUnitAxesRatio(true);
		
		// geometry - like basic geometry but with less toolbar entries
		defaultPerspectives[2] = new Perspective("Geometry", spData, dpData, defToolbar, true, false, false, false, true, false);
		
		// Table & Graphics - spreadsheet and euclidian view
		spData = new DockSplitPaneData[1];
		spData[0] = new DockSplitPaneData("", 0.45, /*JSplitPane.HORIZONTAL_SPLIT*/1);
		
		dpData = new DockPanelData[4];
		dpData[0] = new DockPanelData(AbstractApplication.VIEW_EUCLIDIAN, null, true, false, false, new geogebra.web.awt.Rectangle(100, 100, 600, 400), "1", 500);
		dpData[1] = new DockPanelData(AbstractApplication.VIEW_ALGEBRA, null, false, false, false, new geogebra.web.awt.Rectangle(100, 100, 250, 400), "3,3", 200);
		dpData[2] = new DockPanelData(AbstractApplication.VIEW_SPREADSHEET, null, true, false, false, new geogebra.web.awt.Rectangle(100, 100, 600, 400), "3", 300);
		dpData[3] = new DockPanelData(AbstractApplication.VIEW_CAS, null, false, false, false, new geogebra.web.awt.Rectangle(100, 100, 600, 400), "3,1", 300);
		
		defaultPerspectives[3] = new Perspective("TableAndGraphics", spData, dpData, defToolbar, true, false, true, false, true, false);
		
		// CAS & Graphics - cas and euclidian view
		dpData = new DockPanelData[4];
		dpData[0] = new DockPanelData(AbstractApplication.VIEW_EUCLIDIAN, null, true, false, false, new geogebra.web.awt.Rectangle(100, 100, 600, 400), "1", 500);
		dpData[1] = new DockPanelData(AbstractApplication.VIEW_ALGEBRA, null, false, false, false, new geogebra.web.awt.Rectangle(100, 100, 250, 400), "3,3", 200);
		dpData[2] = new DockPanelData(AbstractApplication.VIEW_SPREADSHEET, null, false, false, false, new geogebra.web.awt.Rectangle(100, 100, 600, 400), "3,1", 300);
		dpData[3] = new DockPanelData(AbstractApplication.VIEW_CAS, null, true, false, false, new geogebra.web.awt.Rectangle(100, 100, 600, 400), "3", 300);
		
		defaultPerspectives[4] = new Perspective("CASAndGraphics", spData, dpData, defToolbar, true, false, true, false, true, false);
    }

	public void settingsChanged(AbstractSettings settings) {
		// TODO Auto-generated method stub

	}
	
	/*Many of this not implemented yet, later we can make it togehter*/
	@Override
    protected void applyPerspective(Perspective perspective) {
		// ignore axes & grid settings for the document perspective
				if(!perspective.getId().equals("tmp")) {
					EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();

					if (app.getEuclidianView1() == ev)
						app.getSettings().getEuclidian(1).setShowAxes(perspective.getShowAxes(), perspective.getShowAxes());
					else if (!app.hasEuclidianView2EitherShowingOrNot())
						ev.setShowAxes(perspective.getShowAxes(), false);
					else if (app.getEuclidianView2() == ev)
						app.getSettings().getEuclidian(2).setShowAxes(perspective.getShowAxes(), perspective.getShowAxes());
					else
						ev.setShowAxes(perspective.getShowAxes(), false);

					if (app.getEuclidianView1() == ev)
						app.getSettings().getEuclidian(1).showGrid(perspective.getShowGrid());
					else if (!app.hasEuclidianView2EitherShowingOrNot())
						ev.showGrid(perspective.getShowGrid());
					else if (app.getEuclidianView2() == ev)
						app.getSettings().getEuclidian(2).showGrid(perspective.getShowGrid());
					else
						ev.showGrid(perspective.getShowGrid());

					//ev.setUnitAxesRatio(perspective.isUnitAxesRatio());
				}
				
				app.getGuiManager().setToolBarDefinition(perspective.getToolbarDefinition());
				
				app.setShowToolBarNoUpdate(perspective.getShowToolBar());
				//AGapp.setShowAlgebraInput(perspective.getShowInputPanel(), false);
				//AGapp.setShowInputTop(perspective.getShowInputPanelOnTop(), false);
				
				// change the dock panel layout
				//AGdockManager.applyPerspective(perspective.getSplitPaneData(), perspective.getDockPanelData());
				
				if(!app.isIniting()) {
					app.updateToolBar();
					app.updateMenubar();
					//AGapp.updateContentPane();
				}
	   
    }

	public void initialize(AbstractApplication app) {
		if(isInitialized)
			return;
		
		isInitialized = true;
		
		this.app = (Application) app;
		this.settings = app.getSettings().getLayout();
		this.settings.addListener(this);
		//this.dockManager = new DockManager(this);
    }

}

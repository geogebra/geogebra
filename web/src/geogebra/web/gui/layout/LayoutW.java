package geogebra.web.gui.layout;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.gui.Layout;
import geogebra.common.io.layout.Perspective;
import geogebra.common.main.App;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.SettingListener;
import geogebra.web.main.AppW;

import java.util.ArrayList;

public class LayoutW extends Layout implements SettingListener {
	
	private boolean isInitialized = false;
	
	private AppW app;
	
	/**
	 * instantiates layout for Web
	 */
	public LayoutW() {
		initializeDefaultPerspectives(false, true);
		
		this.perspectives = new ArrayList<Perspective>(defaultPerspectives.length);
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

	public void initialize(App app) {
		if(isInitialized)
			return;
		
		isInitialized = true;
		
		this.app = (AppW) app;
		this.settings = app.getSettings().getLayout();
		this.settings.addListener(this);
		//this.dockManager = new DockManager(this);
    }

	@Override
    public void getXml(StringBuilder sb, boolean asPreference) {
		App.debug("unimplemented");
	}

	@Override
    public boolean isOnlyVisible(int viewEuclidian) {
		App.debug("unimplemented");
	    return false;
    }

}

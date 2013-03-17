package geogebra.web.gui.layout;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.gui.Layout;
import geogebra.common.io.layout.Perspective;
import geogebra.common.main.App;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.SettingListener;
import geogebra.web.main.AppW;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Widget;

public class LayoutW extends Layout implements SettingListener {
	
	private boolean isInitialized = false;
	
	private AppW app;

	private DockManagerW dockManager;
	
	/**
	 * instantiates layout for Web
	 */
	public LayoutW() {
		initializeDefaultPerspectives(false, true);
		
		this.perspectives = new ArrayList<Perspective>(defaultPerspectives.length);
	}

	/**
	 * Initialize the layout component.
	 * 
	 * @param app
	 */
	public void initialize(App app) {
		if(isInitialized)
			return;
		
		isInitialized = true;
		
		this.app = (AppW) app;
		this.settings = app.getSettings().getLayout();
		this.settings.addListener(this);
		this.dockManager = new DockManagerW(this);
    }

		
	/**
	 * Add a new dock panel to the list of known panels.
	 * 
	 * Attention: This method has to be called as early as possible in the application
	 * life cycle (e.g. before loading a file, before constructing the ViewMenu). 
	 * 
	 * @param dockPanel
	 */
	public void registerPanel(DockPanelW dockPanel) {
		dockManager.registerPanel(dockPanel);
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
				
				app.getGuiManager().setToolBarDefinition(App.VIEW_EUCLIDIAN,perspective.getToolbarDefinition());
				
				app.setShowToolBarNoUpdate(perspective.getShowToolBar());
				app.setShowAlgebraInput(perspective.getShowInputPanel(), false);
				app.setShowInputTop(perspective.getShowInputPanelOnTop(), false);
				
				// change the dock panel layout
				dockManager.applyPerspective(perspective.getSplitPaneData(), perspective.getDockPanelData());
				
				if(!app.isIniting()) {
					app.updateToolBar();
					app.updateMenubar();
					//app.updateContentPane();
				}
	   
    }

	
	/**
	 * Apply a new perspective using its id. 
	 * 
	 * This is a wrapper for #applyPerspective(Perspective) to simplify the loading of default
	 * perspectives by name. 
	 * 
	 * @param id The ID of the perspective. For default perspectives the hard-coded ID is used, ie
	 * 			 the translation key, for all other perspectives the ID chosen by the user is
	 * 			 used.
	 * @throws IllegalArgumentException If no perspective with the given name could be found.
	 */
	public void applyPerspective(String id) throws IllegalArgumentException {
		Perspective perspective = getPerspective(id);
		
		if(perspective != null) {
			applyPerspective(perspective);
		} else {
			throw new IllegalArgumentException("Could not find perspective with the given name.");
		}		
	}
	
	
	/**
	 * Get all current perspectives as array.
	 * 
	 * @return all current perspectives as array.
	 */
	public Perspective[] getPerspectives() {
		Perspective[] array = new Perspective[perspectives.size()];
		return perspectives.toArray(array);
	}

	/**
	 * @param index
	 * @return perspective at given index
	 */
	public Perspective getPerspective(int index) {
		if(index >= perspectives.size())
			throw new IndexOutOfBoundsException();
		
		return perspectives.get(index);
	}
	
	/**
	 * @param id name of the perspective
	 * @return perspective with 'id' as name or null 
	 */
	public Perspective getPerspective(String id) {
		for(int i = 0; i < defaultPerspectives.length; ++i) {
			if(id.equals(defaultPerspectives[i].getId())) {
				return defaultPerspectives[i];
			}
		}
		
		for(Perspective perspective : perspectives) {
			if(id.equals(perspective.getId())) {
				return perspective;
			}
		}
		
		return null;
	}
	
	/**
	 * Add a new perspective to the list of available perspectives.
	 * 
	 * @param perspective
	 */
	public void addPerspective(Perspective perspective) {
		perspectives.add(perspective);
	}
	
	/**
	 * Remove a perspective identified by the object.
	 * 
	 * @param perspective
	 */
	public void removePerspective(Perspective perspective) {
		if(perspectives.contains(perspective)) {
			perspectives.remove(perspective);
		}
	}
	
	/**
	 * Remove a perspective identified by the index.
	 * 
	 * @param index
	 */
	public void removePerspective(int index) {
		if(index >= 0 && index < perspectives.size()) {
			perspectives.remove(index);
		} else {
			App.debug("Invalid perspective index: " + index);
		}
	}
	
	
	public void settingsChanged(AbstractSettings settings) {
		// TODO Auto-generated method stub

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

	

	public AppW getApplication() {
	    return app;
    }
	
	/**
	 * @return The management class for the docking behavior.
	 */
	public DockManagerW getDockManager() {
		return dockManager;
	}

	public Widget getRootComponent() {
		if(dockManager == null) {
			return null;
		}		
		return dockManager.getRoot();
	}

}

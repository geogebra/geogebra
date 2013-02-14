package geogebra.web.gui;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.geos.FromMeta;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.web.main.AppW;

import java.util.ArrayList;
import java.util.TreeSet;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class ContextMenuChooseGeoW extends ContextMenuGeoElementW {


	/**
	 * 
	 */
	protected EuclidianView view;
	
	/**
	 * polygons/polyhedra parents of segments, polygons, ...
	 */
	private TreeSet<GeoElement> metas;
	
	private ArrayList<GeoElement> selectedGeos;
	private geogebra.common.awt.GPoint loc;
	private MenuBar selectAnotherMenu;
	
	/**
	 * 
	 * @param app application
	 * @param view view
	 * @param selectedGeos selected geos
	 * @param geos geos
	 * @param location place to show
	 */
	public ContextMenuChooseGeoW(AppW app, EuclidianView view, 
			ArrayList<GeoElement> selectedGeos,
			ArrayList<GeoElement> geos, GPoint location, geogebra.common.awt.GPoint invokerLocation) {
		super(app, selectedGeos, location);
		
		//return if just one geo, or if first geos more than one
		if (geos.size()<2 || selectedGeos.size()>1) {
			justOneGeo = false;
			return;
		}

		justOneGeo = true;

		//section to choose a geo
		//addSeparator();
		addSelectAnotherMenu();
		
		
		this.loc = invokerLocation;
		this.selectedGeos = selectedGeos;
		this.geos = geos;
		this.view = view;
		
		GeoElement geoSelected = selectedGeos.get(0);
		
		
		//add geos
		metas = new TreeSet<GeoElement>();
		
		for (GeoElement geo : geos){
			if (geo!=geoSelected){//don't add selected geo
				addGeo(geo);
			}
			
			if (geo.isFromMeta()){
				GeoElement meta = ((FromMeta) geo).getMeta();
				if (!metas.contains(meta)){
					addGeo(meta);
				}
			}
		}
	}
	
	private class MyMouseOverListener implements EventListener {
		
		private GeoElement geo;
		
		public MyMouseOverListener(GeoElement geo) {
	        this.geo = geo;
        }

		public void onBrowserEvent(Event event) {
			view.getEuclidianController().doSingleHighlighting(geo);
			App.debug("view.getEuclidianController().doSingleHighlighting(geo) called");
        }
		
	}

	private void addGeo(GeoElement geo) {
	    GeoAction chooser = new GeoAction(geo);
	    MenuItem mi = new MenuItem(getDescription(geo), chooser);
	    DOM.setEventListener(mi.getElement(), new MyMouseOverListener(geo));
	    DOM.sinkEvents(mi.getElement(), Event.ONMOUSEOVER);
	    
	    selectAnotherMenu.addItem(mi);
	    metas.add(geo);
	    
	    
    }
	
	private class GeoAction implements Command {
		
		private GeoElement geo;
		
		public GeoAction(GeoElement geo) {
			this.geo = geo;
		}
		
		public void execute() {
			geoActionCmd(this.geo,selectedGeos,geos,view, loc);
        }
		
	}

	private void addSelectAnotherMenu() {
	    selectAnotherMenu = new MenuBar(true);
	    MenuItem selectAnotherMenuItem = new MenuItem(app.getMenu("SelectAnother"), selectAnotherMenu);
	    wrappedPopup.addItem(selectAnotherMenuItem);
    }
}

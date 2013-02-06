package geogebra.mobile;

import geogebra.mobile.gui.TabletGUI;
import geogebra.mobile.gui.TubeSearchUI;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;

public interface ClientFactory
{
	EventBus getEventBus();
	PlaceController getPlaceController();
	TabletGUI getTabletGui();
	TubeSearchUI getTubeSearchUI();
	
	// TODO: add new views here!
}

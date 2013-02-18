package geogebra.mobile;

import geogebra.mobile.gui.TabletGUI;
import geogebra.mobile.gui.TubeSearchUI;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;

public class ClientFactory
{
	private static final EventBus eventBus = new SimpleEventBus();
	private static final PlaceController placeController = new PlaceController(eventBus);

	private static final TabletGUI tabletGui = new TabletGUI();
	private static final TubeSearchUI tubeSearchUI = new TubeSearchUI();

	public static EventBus getEventBus()
	{
		return ClientFactory.eventBus;
	}

	public static PlaceController getPlaceController()
	{
		return ClientFactory.placeController;
	}

	public static TabletGUI getTabletGui()
	{
		return ClientFactory.tabletGui;
	}

	public static TubeSearchUI getTubeSearchUI()
	{
		return ClientFactory.tubeSearchUI;
	}

}

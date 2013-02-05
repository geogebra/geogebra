package geogebra.mobile;

import geogebra.mobile.gui.TabletGUI;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;

public class ClientFactoryImpl implements ClientFactory
{
	private static final EventBus eventBus = new SimpleEventBus();
	private static final PlaceController placeController = new PlaceController(eventBus);
	private static final TabletGUI tabletGui = new TabletGUI();
	
	@Override
	public EventBus getEventBus()
	{
		return ClientFactoryImpl.eventBus;
	}
	@Override
	public PlaceController getPlaceController()
	{
		return ClientFactoryImpl.placeController;
	}
	@Override
	public TabletGUI getTabletGui()
	{
		return ClientFactoryImpl.tabletGui;
	}

	

}

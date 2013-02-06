package geogebra.mobile;

import geogebra.mobile.gui.TabletGUI;
import geogebra.mobile.gui.TubeSearchUI;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;

public class ClientFactoryImpl implements ClientFactory
{
	private static final EventBus eventBus = new SimpleEventBus();
	private static final PlaceController placeController = new PlaceController(eventBus);

	private static TabletGUI tabletGui;
	private static final TubeSearchUI tubeSearchUI = new TubeSearchUI();

	public ClientFactoryImpl(){
		tabletGui = TabletGUI.getInstance(); 
		MobileApp app = new MobileApp(tabletGui); 
		app.start(); 
	}
	
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

	public static void setTabletGui(TabletGUI gui){
		tabletGui = gui; 
	}
	
	@Override
	public TabletGUI getTabletGui()
	{
		return ClientFactoryImpl.tabletGui;
	}

	@Override
	public TubeSearchUI getTubeSearchUI()
	{
		return ClientFactoryImpl.tubeSearchUI;
	}

}

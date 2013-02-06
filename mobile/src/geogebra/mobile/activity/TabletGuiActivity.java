package geogebra.mobile.activity;

import geogebra.mobile.ClientFactory;
import geogebra.mobile.gui.Presenter;
import geogebra.mobile.gui.TabletGUI;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class TabletGuiActivity extends AbstractActivity implements Presenter
{
	// Used to obtain views, eventBus, placeController
	// Alternatively, could be injected via GIN
	private ClientFactory clientFactory;

	public TabletGuiActivity(ClientFactory clientFactory)
	{
		this.clientFactory = clientFactory;
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus)
	{
		TabletGUI gui = this.clientFactory.getTabletGui();
		gui.setPresenter(this);
		containerWidget.setWidget(gui.asWidget());
	}

	/**
	 * Ask user before stopping this activity
	 */
	@Override
	public String mayStop()
	{
		return null;
	}

	/**
	 * Navigate to a new Place in the browser
	 */
	@Override
	public void goTo(Place place)
	{
		this.clientFactory.getPlaceController().goTo(place);
	}

}

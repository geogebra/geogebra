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
	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus)
	{
		TabletGUI gui = ClientFactory.getTabletGui();
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
		ClientFactory.getPlaceController().goTo(place);
	}

}

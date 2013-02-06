package geogebra.mobile.mvp;

import geogebra.mobile.ClientFactory;
import geogebra.mobile.activity.TabletGuiActivity;
import geogebra.mobile.activity.TubeSearchActivity;
import geogebra.mobile.place.TabletGuiPlace;
import geogebra.mobile.place.TubeSearchPlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class AppActivityMapper implements ActivityMapper
{

	private ClientFactory clientFactory;

	/**
	 * AppActivityMapper associates each Place with its corresponding
	 * {@link Activity}
	 * 
	 * @param clientFactory
	 *          Factory to be passed to activities
	 */
	public AppActivityMapper(ClientFactory clientFactory)
	{
		super();
		this.clientFactory = clientFactory;
	}

	/**
	 * Map each Place to its corresponding Activity. This would be a great use for
	 * GIN.
	 */
	@Override
	public Activity getActivity(Place place)
	{
		// This is begging for GIN
		if (place instanceof TabletGuiPlace)
		{
			return new TabletGuiActivity(this.clientFactory);
		}
		else if (place instanceof TubeSearchPlace)
		{
			return new TubeSearchActivity(this.clientFactory);
		}
		// TODO: add other place-tyes here!

		return null;
	}

}

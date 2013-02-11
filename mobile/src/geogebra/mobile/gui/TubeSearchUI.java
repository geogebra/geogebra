package geogebra.mobile.gui;

import geogebra.mobile.ClientFactory;
import geogebra.mobile.utils.ggtapi.GeoGebraTubeAPI;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.googlecode.mgwt.dom.client.event.orientation.OrientationChangeEvent;
import com.googlecode.mgwt.dom.client.event.orientation.OrientationChangeHandler;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTStyle;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.MSearchBox;

/**
 * Coordinates the GUI of the tablet.
 * 
 */
public class TubeSearchUI extends LayoutPanel implements AcceptsOneWidget, Presenter
{
	private ClientFactory clientFactory;

	private MSearchBox searchBox;

	/**
	 * Sets the viewport and other settings, creates a link element at the end of
	 * the head, appends the css file and initializes the GUI elements.
	 */
	public TubeSearchUI()
	{

		// set viewport and other settings for mobile
		MGWT.applySettings(MGWTSettings.getAppSetting());

		// this will create a link element at the end of head
		MGWTStyle.getTheme().getMGWTClientBundle().getMainCss().ensureInjected();

		// append your own css as last thing in the head
		MGWTStyle.injectStyleSheet("TubeSearchUI.css");

		// Handle orientation changes
		MGWT.addOrientationChangeHandler(new OrientationChangeHandler()
		{
			@Override
			public void onOrientationChanged(OrientationChangeEvent event)
			{
				// TODO update whatever is shown right now
			}
		});

		this.searchBox = new MSearchBox();
		this.searchBox.addValueChangeHandler(new ValueChangeHandler<String>()
		{

			@Override
			public void onValueChange(ValueChangeEvent<String> event)
			{

				event.getValue();

				// Make JSON Request
				GeoGebraTubeAPI.getInstance().search(event.getValue(), GeoGebraTubeAPI.STANDARD_RESULT_QUANTITY);
			}
		});

		this.add(this.searchBox);
	}

	@Override
	public void setWidget(IsWidget w)
	{
		add(w.asWidget());
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

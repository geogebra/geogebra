package geogebra.mobile.gui;

import java.util.List;

import geogebra.mobile.ClientFactory;
import geogebra.mobile.utils.ggtapi.GeoGebraTubeAPI;
import geogebra.mobile.utils.ggtapi.JSONparserGGT;
import geogebra.mobile.utils.ggtapi.Material;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.googlecode.mgwt.dom.client.event.orientation.OrientationChangeEvent;
import com.googlecode.mgwt.dom.client.event.orientation.OrientationChangeHandler;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTStyle;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.MSearchBox;
import com.googlecode.mgwt.ui.client.widget.MTextArea;
import com.googlecode.mgwt.ui.client.widget.MTextBox;

/**
 * Coordinates the GUI of the tablet.
 * 
 */
public class TubeSearchUI extends LayoutPanel implements AcceptsOneWidget, Presenter
{
	private ClientFactory clientFactory;

	private MSearchBox searchBox;
	protected MTextArea materialTextArea;

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
				GeoGebraTubeAPI.getInstance().search(event.getValue(), new RequestCallback()
				{
					@Override
					public void onResponseReceived(com.google.gwt.http.client.Request request, Response response)
					{
						List<Material> materialList = JSONparserGGT.parseResponse(response.getText());

						if (materialList != null)
						{
							StringBuffer sb = new StringBuffer();

							for (Material m : materialList)
							{
								sb.append(m.toString());
								sb.append("\n\n");
							}
							TubeSearchUI.this.materialTextArea.setText(sb.toString());				
						}
			
					}

					@Override
					public void onError(com.google.gwt.http.client.Request request, Throwable exception)
					{
						// TODO Handle error!
						exception.printStackTrace();
					}
				});

			}
		});

		this.materialTextArea = new MTextArea();
		this.materialTextArea.setReadOnly(true);
		this.materialTextArea.addStyleDependentName("materialTextArea");


		this.add(this.searchBox);
		this.add(this.materialTextArea);
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

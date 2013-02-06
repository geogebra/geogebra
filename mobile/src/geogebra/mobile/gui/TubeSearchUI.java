package geogebra.mobile.gui;

import geogebra.common.kernel.Kernel;
import geogebra.mobile.ClientFactory;
import geogebra.mobile.controller.MobileController;
import geogebra.mobile.gui.algebra.AlgebraViewPanel;
import geogebra.mobile.gui.elements.header.TabletHeaderPanel;
import geogebra.mobile.gui.elements.header.TabletHeaderPanelLeft;
import geogebra.mobile.gui.elements.header.TabletHeaderPanelRight;
import geogebra.mobile.gui.elements.stylingbar.StylingBar;
import geogebra.mobile.gui.elements.toolbar.ToolBar;
import geogebra.mobile.gui.euclidian.EuclidianViewPanel;
import geogebra.mobile.model.MobileModel;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.dom.client.event.orientation.OrientationChangeEvent;
import com.googlecode.mgwt.dom.client.event.orientation.OrientationChangeHandler;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTStyle;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;

/**
 * Coordinates the GUI of the tablet.
 * 
 */
public class TubeSearchUI extends LayoutPanel implements AcceptsOneWidget, Presenter
{
	LayoutPanel background = new LayoutPanel();
	private ClientFactory clientFactory;

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

		// this.add();
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

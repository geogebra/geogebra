package geogebra.mobile.gui;

import org.vectomatic.dom.svg.ui.SVGResource;

import geogebra.mobile.gui.elements.AlgebraViewPanel;
import geogebra.mobile.gui.elements.EuclidianViewPanel;
import geogebra.mobile.gui.elements.toolbar.ToolButton;

import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTStyle;

public class IconTestGUI implements GeoGebraMobileGUI
{
	private RootPanel rootPanel;

	public IconTestGUI()
	{

		// set viewport and other settings for mobile
		MGWT.applySettings(MGWTSettings.getAppSetting());

		// this will create a link element at the end of head
		MGWTStyle.getTheme().getMGWTClientBundle().getMainCss().ensureInjected();

		// append your own css as last thing in the head
		MGWTStyle.injectStyleSheet("TabletGUI.css");

		this.rootPanel = RootPanel.get();
		
		for (ResourcePrototype r : CommonResourcesIconTest.INSTANCE.getResources())
		{
			r.getName();
			this.rootPanel.add(new ToolButton((SVGResource) r));
		}
		
	}

	@Override
	public EuclidianViewPanel getEuclidianViewPanel()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AlgebraViewPanel getAlgebraViewPanel()
	{
		// TODO Auto-generated method stub
		return null;
	}

}

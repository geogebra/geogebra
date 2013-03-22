package geogebra.touch.gui;

import geogebra.common.kernel.Kernel;
import geogebra.touch.gui.algebra.AlgebraViewPanel;
import geogebra.touch.gui.elements.ToolButton;
import geogebra.touch.gui.euclidian.EuclidianViewPanel;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.user.client.Element;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTStyle;

public class IconTestGUI implements GeoGebraTouchGUI
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
		return null;
	}

	@Override
	public AlgebraViewPanel getAlgebraViewPanel()
	{
		return null;
	}

	@Override
	public void initComponents(Kernel kernel)
	{
	}

	@Override
	public Element getElement() {
		return this.rootPanel.getElement();
	}

}

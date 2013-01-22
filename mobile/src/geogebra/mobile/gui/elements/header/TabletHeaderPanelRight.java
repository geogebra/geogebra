package geogebra.mobile.gui.elements.header;

import geogebra.mobile.gui.CommonResources;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;

/**
 * ButtonBar for the buttons on the right side of the HeaderPanel.
 * 
 * @author Thomas Krismayer
 * 
 */
public class TabletHeaderPanelRight extends HorizontalPanel
{

	/**
	 * Generates the {@link HeaderButton buttons} for the right HeaderPanel.
	 */
	public TabletHeaderPanelRight()
	{
		this.addStyleName("rightHeader");

		HeaderImageButton[] button = new HeaderImageButton[2];
		button[0] = new HeaderImageButton();
		SVGResource icon = CommonResources.INSTANCE.undo();
		button[0].setText(icon.getSafeUri().asString());

		icon = CommonResources.INSTANCE.redo();
		button[1] = new HeaderImageButton();
		button[1].setText(icon.getSafeUri().asString());

		for (int i = 0; i < button.length; i++)
		{
			this.add(button[i]);
		}

	}

}

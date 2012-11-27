package geogebra.mobile.gui.elements.header;

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

		//TODO: add again 
//		this.add(new ArrowLeftButton());
//		this.add(new ArrowRightButton());

	}

}

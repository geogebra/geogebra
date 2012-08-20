package geogebra.mobile.gui.elements;

import com.googlecode.mgwt.ui.client.widget.buttonbar.ArrowLeftButton;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ArrowRightButton;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;

/**
 * ButtonBar for the buttons on the right side of the HeaderPanel
 * 
 * @author Thomas Krismayer
 * 
 */
public class TabletHeaderPanelRight extends ButtonBar
{

	public TabletHeaderPanelRight()
	{
		this.addStyleName("rightHeader");

		this.add(new ArrowLeftButton());
		this.add(new ArrowRightButton());

	}

}

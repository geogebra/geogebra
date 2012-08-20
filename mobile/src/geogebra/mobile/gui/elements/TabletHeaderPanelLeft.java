package geogebra.mobile.gui.elements;

import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;

/**
 * ButtonBar for the buttons on the left side of the HeaderPanel
 * 
 * @author Thomas Krismayer
 * 
 */
public class TabletHeaderPanelLeft extends ButtonBar
{

	public TabletHeaderPanelLeft()
	{
		this.addStyleName("leftHeader");

		HeaderButton[] left = new HeaderButton[3];

		left[0] = new HeaderButton();
		left[0].setText("new");

		left[1] = new HeaderButton();
		left[1].setText("open");

		left[2] = new HeaderButton();
		left[2].setText("save");

		for (int i = 0; i < left.length; i++)
		{
			this.add(left[i]);
		}
	}

}
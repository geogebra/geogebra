package geogebra.mobile.gui.elements.header;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;

/**
 * ButtonBar for the buttons on the left side of the HeaderPanel.
 * 
 * @author Thomas Krismayer
 * 
 */
public class TabletHeaderPanelLeft extends HorizontalPanel
{

	/**
	 * Generates the {@link HeaderButton buttons} for the left HeaderPanel.
	 */
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
		
		left[0].addTapHandler(new TapHandler() {
			
			@Override
			public void onTap(TapEvent event) {
				//TODO Clear model, don't reload everything
				Window.Location.reload();
				
			}
		});
	}

}
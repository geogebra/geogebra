package geogebra.mobile.gui.elements.header;

import geogebra.common.kernel.Kernel;
import geogebra.mobile.model.GuiModel;

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
	public TabletHeaderPanelLeft(final Kernel kernel, final GuiModel guiModel)
	{
		this.addStyleName("leftHeader");

		// TODO: set array-length to 3
		HeaderButton[] left = new HeaderButton[1];

		left[0] = new HeaderButton();
		left[0].setText("new");

		// TODO: add again
//		left[1] = new HeaderButton();
//		left[1].setText("open");
//
//		left[2] = new HeaderButton();
//		left[2].setText("save");

		for (int i = 0; i < left.length; i++)
		{
			this.add(left[i]);
		}
		
		left[0].addTapHandler(new TapHandler() {
			
			@Override
			public void onTap(TapEvent event) {
				//TODO 
				guiModel.closeOptions(); 
				kernel.clearConstruction(); 
				kernel.notifyRepaint(); 				
			}
		});
	}

}
package geogebra.mobile.gui.elements.header;

import geogebra.common.kernel.Kernel;
import geogebra.mobile.gui.CommonResources;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
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
	public TabletHeaderPanelRight(final Kernel kernel)
	{
		this.addStyleName("rightHeader");

		HeaderImageButton[] button = new HeaderImageButton[2];
		button[0] = new HeaderImageButton();
		SVGResource icon = CommonResources.INSTANCE.undo();
		button[0].setText(icon.getSafeUri().asString());

		button[0].addTapHandler(new TapHandler()
		{			
			@Override
			public void onTap(TapEvent event)
			{
				kernel.undo(); 
			}
		}); 
		
		icon = CommonResources.INSTANCE.redo();
		button[1] = new HeaderImageButton();
		button[1].setText(icon.getSafeUri().asString());

		button[1].addTapHandler(new TapHandler()
		{			
			@Override
			public void onTap(TapEvent event)
			{
				kernel.redo(); 
			}
		}); 
		
		for (int i = 0; i < button.length; i++)
		{
			this.add(button[i]);
		}

	}

}

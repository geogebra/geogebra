package geogebra.touch.gui.elements.header;

import geogebra.common.kernel.Kernel;
import geogebra.touch.gui.CommonResources;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;

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
		HeaderImageButton[] button = new HeaderImageButton[2];
		button[0] = new HeaderImageButton();

		SVGResource icon = CommonResources.INSTANCE.undo();
		button[0].setText(icon.getSafeUri().asString());

		button[0].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				kernel.undo();
			}
		}, ClickEvent.getType());

		icon = CommonResources.INSTANCE.redo();
		button[1] = new HeaderImageButton();
		button[1].setText(icon.getSafeUri().asString());

		button[1].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				kernel.redo();
			}
		}, ClickEvent.getType());

		for (int i = 0; i < button.length; i++)
		{
			this.add(button[i]);
		}
	}
}
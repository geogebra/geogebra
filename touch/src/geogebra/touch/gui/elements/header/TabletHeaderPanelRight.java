package geogebra.touch.gui.elements.header;

import geogebra.common.kernel.Kernel;
import geogebra.touch.gui.CommonResources;
import geogebra.touch.gui.elements.StandardImageButton;

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
		StandardImageButton[] button = new StandardImageButton[2];

		button[0] = new StandardImageButton(CommonResources.INSTANCE.undo());
		button[0].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				kernel.undo();
			}
		}, ClickEvent.getType());

		button[1] = new StandardImageButton(CommonResources.INSTANCE.redo());
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
package geogebra.mobile.gui.elements;

import geogebra.mobile.gui.CommonResources;
import geogebra.mobile.gui.elements.toolbar.ToolBarButton;
import geogebra.mobile.model.GuiModel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;

/**
 * 
 * @author Thomas Krismayer
 * 
 */
public class StylingBar extends RoundPanel
{

	public StylingBar(final GuiModel guiModel)
	{
		this.addStyleName("stylingbar");

		ToolBarButton[] button = new ToolBarButton[3];
		button[0] = new ToolBarButton(
				CommonResources.INSTANCE.show_or_hide_the_axes(), guiModel);
		button[0].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				guiModel.closeOptions();
			}
		}, ClickEvent.getType());

		button[1] = new ToolBarButton(
				CommonResources.INSTANCE.show_or_hide_the_grid(), guiModel);
		button[1].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				guiModel.closeOptions();
			}
		}, ClickEvent.getType());

		button[2] = new ToolBarButton(
				CommonResources.INSTANCE.point_capturing(), guiModel);
		button[2].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				guiModel.closeOptions();
			}
		}, ClickEvent.getType());

		for (int i = 0; i < button.length; i++)
		{
			add(button[i]);
		}
	}
}

package geogebra.touch.gui.elements.stylingbar;

import geogebra.common.awt.GColor;
import geogebra.touch.model.TouchModel;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A {@link VerticalPanel} which contains the different color-choices.
 */
public class Colors extends VerticalPanel
{
	StylingBar stylingBar;
	TouchModel touchModel;

	public Colors(StylingBar stylingBar, TouchModel touchModel)
	{
		this.stylingBar = stylingBar;
		this.touchModel = touchModel;
	}

	protected void drawColorChoice(List<GColor> listOfColors)
	{
		this.clear();
		for (GColor color : listOfColors)
		{
			addColorButton(color);
		}
	}

	private void addColorButton(final GColor color)
	{
		PushButton button = new PushButton();
		button.setPixelSize(128, 64);
		button.getElement().getStyle().setBackgroundImage("initial");

		button.getElement().getStyle().setBackgroundColor(GColor.getColorString(color));

		button.addDomHandler(new ClickHandler()
		{

			@Override
			public void onClick(ClickEvent event)
			{
				Colors.this.stylingBar.updateColor(GColor.getColorString(color));
				Colors.this.touchModel.getGuiModel().setColor(color);
				if (Colors.this.touchModel.lastSelected() != null && Colors.this.touchModel.isColorChangeAllowed()
				    && StyleBarStatic.applyColor(Colors.this.touchModel.getSelectedGeos(), color))
				{
					Colors.this.touchModel.lastSelected().updateRepaint();
				}

				Colors.this.touchModel.storeOnClose();
			}
		}, ClickEvent.getType());

		this.add(button);
	}
}

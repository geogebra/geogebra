package geogebra.mobile.gui.elements.stylingbar;

import geogebra.common.awt.GColor;
import geogebra.mobile.model.MobileModel;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;

/**
 * A {@link VerticalPanel} which contains the different color-choices.
 */
public class Colors extends VerticalPanel
{
	StylingBar stylingBar;
	MobileModel mobileModel;

	public Colors(StylingBar stylingBar, MobileModel mobileModel)
	{
		this.stylingBar = stylingBar;
		this.mobileModel = mobileModel;
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
		RoundPanel button = new RoundPanel();

		button.addStyleName("colorChoiceButton");
		button.getElement().getStyle().setBackgroundColor(GColor.getColorString(color));

		button.addDomHandler(new ClickHandler()
		{

			@Override
			public void onClick(ClickEvent event)
			{
				Colors.this.stylingBar.updateColor(GColor.getColorString(color));
				Colors.this.mobileModel.getGuiModel().setColor(color);
				if (Colors.this.mobileModel.lastSelected() != null && Colors.this.mobileModel.isColorChangeAllowed()
				    && StyleBarStatic.applyColor(Colors.this.mobileModel.getSelectedGeos(), color))
				{
					Colors.this.mobileModel.lastSelected().updateRepaint();
				}

				Colors.this.mobileModel.storeOnClose();
			}
		}, ClickEvent.getType());

		this.add(button);
	}
}

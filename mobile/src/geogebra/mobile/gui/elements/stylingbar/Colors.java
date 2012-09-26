package geogebra.mobile.gui.elements.stylingbar;

import geogebra.common.awt.GColor;
import geogebra.mobile.model.MobileModel;
import geogebra.web.awt.GColorW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.mgwt.ui.client.widget.Button;

/**
 * A {@link VerticalPanel} which contains the different color-choices.
 */
public class Colors extends VerticalPanel
{

	StylingBar stylingBar;
	MobileModel mobileModel;
	String buttonColor;

	public Colors(StylingBar stylingBar, MobileModel mobileModel)
	{
		this.stylingBar = stylingBar;
		this.mobileModel = mobileModel;
		addDummyButton();
		addColorButton(GColor.BLACK);
		addColorButton(new GColorW(153, 51, 0));
		addColorButton(GColor.MAGENTA);
		addColorButton(GColor.BLUE);
		addColorButton(GColor.CYAN);
		addColorButton(GColor.GREEN);
		addColorButton(GColor.YELLOW);
		addColorButton(GColor.RED);
		addColorButton(GColor.WHITE);
		addColorButton(GColor.LIGHT_GRAY);
		addColorButton(GColor.GRAY);
		addColorButton(GColor.DARK_GRAY);
		addDummyButton();
	}

	private void addColorButton(final GColor color)
	{
		Button button = new Button();
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
			}

		}, ClickEvent.getType());
		add(button);
	}

	private void addDummyButton()
	{
		Button button = new Button();
		button.addStyleName("dummyButton");
		add(button);
	}
}

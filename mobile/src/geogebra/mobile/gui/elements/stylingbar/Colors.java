package geogebra.mobile.gui.elements.stylingbar;

import geogebra.common.awt.GColor;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.mgwt.ui.client.widget.Button;

/**
 * A {@link VerticalPanel} which contains the different
 * color-choices.
 */
public class Colors extends VerticalPanel
{

	StylingBar stylingBar;
	String buttonColor;

	public Colors (StylingBar stylingBar)
	{
		this.stylingBar = stylingBar;
		addDummyButton();
		addColorButton(GColor.BLACK);
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

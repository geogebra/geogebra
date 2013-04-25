package geogebra.touch.gui.elements.stylingbar;

import geogebra.common.awt.GColor;
import geogebra.touch.model.TouchModel;
import geogebra.web.awt.GColorW;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A {@link LyoutPanel} with a {@link ScrollPanel}.
 */
public class ColorBar extends VerticalPanel
{
	ScrollPanel scrollPanel;
	protected Colors colors;
	List<GColor> listOfColors;

	/**
	 * Initializes the {@link ScrollPanel} and adds the different
	 * {@link geogebra.touch.gui.elements.stylingbar.Colors color-choices} to it.
	 */
	public ColorBar(StylingBar stylingBar, TouchModel touchModel)
	{
		this.addStyleName("colorBar");

		this.scrollPanel = new ScrollPanel();
		this.scrollPanel.addStyleName("colorScrollPanel");
		
		//TODO get button height to show
		this.scrollPanel.setHeight("128px");

		this.listOfColors = new ArrayList<GColor>();
		initColors();
		this.colors = new Colors(stylingBar, touchModel);
		this.colors.drawColorChoice(this.listOfColors);

		this.scrollPanel.add(this.colors);

		initEndlessColorWheel();

		this.add(this.scrollPanel);
	}

	private void initEndlessColorWheel()
	{
		// TODO make colorwheel endless
	}

	private void initColors()
	{
		this.listOfColors.add(GColor.BLACK);
		this.listOfColors.add(new GColorW(153, 51, 0));
		this.listOfColors.add(GColor.MAGENTA);
		this.listOfColors.add(GColor.BLUE);
		this.listOfColors.add(GColor.CYAN);
		this.listOfColors.add(GColor.GREEN);
		this.listOfColors.add(GColor.YELLOW);
		this.listOfColors.add(GColor.RED);
		this.listOfColors.add(GColor.WHITE);
		this.listOfColors.add(GColor.LIGHT_GRAY);
		this.listOfColors.add(GColor.GRAY);
		this.listOfColors.add(GColor.DARK_GRAY);
	}
}
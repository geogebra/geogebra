package geogebra.touch.gui.elements.stylingbar;

import geogebra.common.awt.GColor;
import geogebra.touch.model.TouchModel;
import geogebra.web.awt.GColorW;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * A {@link LyoutPanel} with a {@link ScrollPanel}.
 */
public class ColorBar extends LayoutPanel
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
		this.listOfColors = new ArrayList<GColor>();
		initColors();
		this.colors = new Colors(stylingBar, touchModel);
		this.colors.drawColorChoice(this.listOfColors);
		this.scrollPanel.add(this.colors);
		this.add(this.scrollPanel);
		this.setVisible(true);
		initEndlessColorWheel();
	}

	private void initEndlessColorWheel()
	{
		//TODO make colorwheel endless
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
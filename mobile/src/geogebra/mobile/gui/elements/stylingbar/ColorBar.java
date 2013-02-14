package geogebra.mobile.gui.elements.stylingbar;

import java.util.ArrayList;

import geogebra.common.awt.GColor;
import geogebra.mobile.gui.CommonResources;
import geogebra.mobile.model.MobileModel;
import geogebra.web.awt.GColorW;

import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;
import com.googlecode.mgwt.ui.client.widget.event.scroll.ScrollMoveEvent;
import com.googlecode.mgwt.ui.client.widget.event.scroll.ScrollStartEvent;

/**
 * A {@link LyoutPanel} with a {@link ScrollPanel}.
 */
public class ColorBar extends LayoutPanel
{
	ScrollPanel scrollPanel;
	protected Colors colors;
	ArrayList<GColor> listOfColors;

	/**
	 * Initializes the {@link ScrollPanel} and adds the different
	 * {@link geogebra.mobile.gui.elements.stylingbar.Colors color-choices} to it.
	 */
	public ColorBar(StylingBar stylingBar, MobileModel mobileModel)
	{
		this.addStyleName("colorBar");
		this.scrollPanel = new ScrollPanel();
		this.scrollPanel.addStyleName("colorScrollPanel");
		initColors();
		this.colors = new Colors(stylingBar, mobileModel);
		this.colors.drawColorChoice(this.listOfColors);
		this.scrollPanel.add(this.colors);
		this.add(this.scrollPanel);
		this.setVisible(true);
		initEndlessColorWheel();
	}

	private void initEndlessColorWheel()
	{
		this.scrollPanel.addScrollStartHandler(new ScrollStartEvent.Handler()
		{
			@Override
			public void onScrollStart(ScrollStartEvent event)
			{
				if (ColorBar.this.scrollPanel.getMaxScrollY() >= ColorBar.this.scrollPanel.getY() - 20)
				{
					changeListOrder(-20, 7); // shift = 7; because 7 visible colors
				}
			}
		});

		this.scrollPanel.addScrollMoveHandler(new ScrollMoveEvent.Handler()
		{
			@Override
			public void onScrollMove(ScrollMoveEvent event)
			{
				if (ColorBar.this.scrollPanel.getMaxScrollY() >= ColorBar.this.scrollPanel.getY() - 20)
				{
					changeListOrder(0, 7); // shift = 7; because 7 visible colors
				}
				else if (ColorBar.this.scrollPanel.getY() > 3)
				{
					changeListOrder(-333, 5); // shift = 5; because 5 visible colors
				}
			}
		});
		this.getElement().getStyle().setBackgroundImage("url(" + CommonResources.INSTANCE.colorBarBackground().getSafeUri().asString() + ")");
	}

	void changeListOrder(int scrollPosition, int shift)
	{
		ArrayList<GColor> list = new ArrayList<GColor>();
		for (int i = 0; i < ColorBar.this.listOfColors.size(); i++)
		{
			// %12 - because 12 different colors
			list.add(ColorBar.this.listOfColors.get((i + shift) % 12));
		}
		ColorBar.this.listOfColors = list;
		ColorBar.this.colors.drawColorChoice(ColorBar.this.listOfColors);
		ColorBar.this.scrollPanel.refresh();
		ColorBar.this.scrollPanel.scrollTo(0, scrollPosition);
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
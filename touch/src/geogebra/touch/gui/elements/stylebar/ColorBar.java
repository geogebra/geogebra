package geogebra.touch.gui.elements.stylebar;

import geogebra.common.awt.GColor;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.touch.model.TouchModel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * A {@link LyoutPanel} with a {@link ScrollPanel}.
 */
public class ColorBar extends FlowPanel {
	HorizontalPanel contentPanel;
	protected Colors colors;
	List<GColor> listOfColors;

	/**
	 * Initializes the {@link ScrollPanel} and adds the different
	 * {@link geogebra.touch.gui.elements.stylebar.Colors color-choices} to it.
	 */
	public ColorBar(StyleBar stylingBar, TouchModel touchModel) {
		this.addStyleName("colorBar");
		this.contentPanel = new HorizontalPanel();

		this.listOfColors = new ArrayList<GColor>();
		this.initColors();
		this.colors = new Colors(stylingBar, touchModel);
		this.colors.drawColorChoice(this.listOfColors);

		this.contentPanel.add(this.colors);

		this.add(this.contentPanel);
	}

	private void initColors() {
		this.listOfColors.add(GColor.BLACK);
		this.listOfColors.add(GeoGebraColorConstants.BROWN);
		this.listOfColors.add(GeoGebraColorConstants.ORANGE);
		this.listOfColors.add(GColor.YELLOW);

		this.listOfColors.add(GColor.BLUE);
		this.listOfColors.add(GColor.CYAN);
		this.listOfColors.add(GColor.GREEN);
		this.listOfColors.add(GeoGebraColorConstants.DARKGREEN);

		this.listOfColors.add(GeoGebraColorConstants.LIGHTBLUE);
		this.listOfColors.add(GeoGebraColorConstants.LIGHTVIOLET);
		this.listOfColors.add(GColor.MAGENTA);
		this.listOfColors.add(GColor.RED);

		this.listOfColors.add(GColor.DARK_GRAY);
		this.listOfColors.add(GColor.GRAY);
		this.listOfColors.add(GColor.LIGHT_GRAY);
		this.listOfColors.add(GColor.WHITE);
	}
}
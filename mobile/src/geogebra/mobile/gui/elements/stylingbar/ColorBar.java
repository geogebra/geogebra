package geogebra.mobile.gui.elements.stylingbar;


import geogebra.mobile.gui.CommonResources;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;

/**
 * A {@link LyoutPanel} with a {@link ScrollPanel}.
 */
public class ColorBar extends LayoutPanel
{
	private ScrollPanel scrollPanel;
	protected Colors colors;
	
	/**
	 * Initializes the {@link ScrollPanel} and adds the different 
	 * {@link geogebra.mobile.gui.elements.stylingbar.Colors color-choices} to it.
	 */
	public ColorBar(StylingBar stylingBar)
  {
		addStyleName("colorBar");
		this.colors = new Colors(stylingBar);
		this.scrollPanel = new ScrollPanel(this.colors);
		this.scrollPanel.addStyleName("colorScrollPanel");
		//this.scrollPanel.setVerticalScrollPosition(10);
		
		add(this.scrollPanel);
		setVisible(true);
		
		
		this.getElement().getStyle().setBackgroundImage("url("+CommonResources.INSTANCE.colorBarBackground().getSafeUri().asString()+")");
  }
}

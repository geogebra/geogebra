package geogebra.mobile.gui.elements.stylingbar;


import com.google.gwt.user.client.ui.ScrollPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;

/**
 * A {@link LyoutPanel} with a {@link ScrollPanel}.
 */
public class ColorBar extends LayoutPanel
{
	private ScrollPanel scrollPanel;
	private Colors colors;
	
	/**
	 * Initializes the {@link ScrollPanel} and adds the different 
	 * {@link geogebra.mobile.gui.elements.stylingbar.Colors color-choices} to it.
	 */
	public ColorBar()
  {
		addStyleName("colorBar");
		System.out.println("ColorBar");

		this.colors = new Colors();
		this.scrollPanel = new ScrollPanel(this.colors);
		this.scrollPanel.addStyleName("colorScrollPanel");
		//this.scrollPanel.setVerticalScrollPosition(10);
		
		add(this.scrollPanel);
		setVisible(true);
  }
}

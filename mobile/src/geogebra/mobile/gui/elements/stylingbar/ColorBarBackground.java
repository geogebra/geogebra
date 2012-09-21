package geogebra.mobile.gui.elements.stylingbar;

import com.googlecode.mgwt.ui.client.animation.AnimationHelper;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;

/**
 * Contains the {@link ColorBar}.
 */
public class ColorBarBackground extends ButtonBar
{
	private final AnimationHelper animationHelper;
	private ColorBar colorBar;
	
	public ColorBarBackground(StylingBar stylingBar)
	{				
		addStyleName("colorBarBackground");
		this.colorBar = new ColorBar(stylingBar);

		this.animationHelper = new AnimationHelper();
		add(this.animationHelper);	
		add(this.colorBar);
	}
}

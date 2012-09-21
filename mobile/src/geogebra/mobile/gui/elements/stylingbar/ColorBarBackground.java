package geogebra.mobile.gui.elements.stylingbar;

import geogebra.mobile.model.MobileModel;

import com.googlecode.mgwt.ui.client.animation.AnimationHelper;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;

/**
 * Contains the {@link ColorBar}.
 */
public class ColorBarBackground extends ButtonBar
{
	//private final AnimationHelper animationHelper;
	private ColorBar colorBar;
	
	public ColorBarBackground(StylingBar stylingBar, MobileModel mobileModel)
	{				
		addStyleName("colorBarBackground");
		this.colorBar = new ColorBar(stylingBar, mobileModel);
		add(this.colorBar);
		
		//TO DO implement animationHelper
		//this.animationHelper = new AnimationHelper();
		//add(this.animationHelper);
	}
}

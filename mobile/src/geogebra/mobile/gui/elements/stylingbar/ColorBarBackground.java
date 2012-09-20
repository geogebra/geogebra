package geogebra.mobile.gui.elements.stylingbar;

import geogebra.mobile.gui.CommonResources;

import com.googlecode.mgwt.mvp.client.Animation;
import com.googlecode.mgwt.ui.client.animation.AnimationHelper;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;

/**
 * Contains the {@link ColorBar}.
 */
public class ColorBarBackground extends ButtonBar
{
	private final AnimationHelper animationHelper;
	private ColorBar colorBar;

	public ColorBarBackground()
	{
		addStyleName("colorBarBackground");
		this.colorBar = new ColorBar();

		this.animationHelper = new AnimationHelper();
		add(this.animationHelper);
	}

	public void show()
	{
		this.getElement().getStyle().setBackgroundImage("url("+CommonResources.INSTANCE.colorBarBackground().getSafeUri().asString()+")");
		//this.animationHelper.goTo(this.colorBar, Animation.SWAP_REVERSE);
		add(this.colorBar); 
	}

}

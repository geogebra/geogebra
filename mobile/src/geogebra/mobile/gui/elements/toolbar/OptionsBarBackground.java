package geogebra.mobile.gui.elements.toolbar;

import geogebra.mobile.utils.ToolBarCommand;

import com.googlecode.mgwt.mvp.client.Animation;
import com.googlecode.mgwt.ui.client.animation.AnimationHelper;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;

/**
 * Each {@link ToolBarButton ToolBarButton} has its own options. 
 * @author Thomas Krismayer
 * @see ButtonBar
 */
public class OptionsBarBackground extends ButtonBar
{

	private OptionsBar optionsBar;
	private final AnimationHelper animationHelper;

	/**
	 * Initialize the {@link OptionsBar optionsBar} with the specific menu entries and
	 * add an {@link AnimationHelper}.
	 * @param menuEntries
	 * @param ancestor
	 */
	public OptionsBarBackground(ToolBarCommand[] menuEntries, OptionsClickedListener ancestor)
	{
		this.addStyleName("toolBarOptionsBackground");
		this.optionsBar = new OptionsBar(menuEntries, ancestor);

		this.animationHelper = new AnimationHelper();
		add(this.animationHelper);
	}

	public void show()
	{
		this.animationHelper.goTo(this.optionsBar, Animation.SLIDE_UP);
	}

}

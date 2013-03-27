package geogebra.touch.gui.elements.toolbar;

import com.google.gwt.user.client.ui.HorizontalPanel;
import geogebra.touch.utils.ToolBarCommand;

/**
 * Each {@link ToolBarButton ToolBarButton} has its own options.
 * 
 * @author Thomas Krismayer
 * @see ButtonBar
 */
public class OptionsBarBackground extends HorizontalPanel
{
	private OptionsBar optionsBar;

	/**
	 * Initialize the {@link OptionsBar optionsBar} with the specific menu entries
	 * and add an {@link AnimationHelper}.
	 * 
	 * @param menuEntries
	 *          the ToolBarCommands that will be shown
	 * @param ancestor
	 *          the OptionsClickedListener (f.e. a ToolBarButton) that was clicked
	 */
	public OptionsBarBackground(ToolBarCommand[] menuEntries, OptionsClickedListener ancestor)
	{
		this.addStyleDependentName("toolBarOptionsBackground");
		this.optionsBar = new OptionsBar(menuEntries, ancestor);
	}

	public void show()
	{
		this.optionsBar.setVisible(true);
	}
}

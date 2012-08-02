package geogebra.mobile.gui.elements.toolbar;

import geogebra.mobile.utils.ToolBarCommand;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;

/**
 * The base of the submenu
 * 
 * @author Thomas Krismayer
 * 
 */
public class OptionsBar extends ButtonBar
{

	/**
	 * 
	 * @param entries
	 *          : the buttons in the submenu
	 * @param ancestor
	 *          : the button responsible for the submenu
	 */
	OptionsBar(ToolBarCommand[] entries, ToolBarButtonInterface ancestor)
	{
		OptionButton[] options = new OptionButton[entries.length];

		for (int i = 0; i < options.length; i++)
		{
			options[i] = new OptionButton(entries[i], ancestor);
			this.add(options[i]);
		}

	}
}

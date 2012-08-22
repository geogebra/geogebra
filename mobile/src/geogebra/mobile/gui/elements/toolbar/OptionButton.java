package geogebra.mobile.gui.elements.toolbar;

import geogebra.mobile.utils.ToolBarCommand;

import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;

/**
 * Buttons of the options-menu
 * 
 * @author Thomas Krismayer
 *
 */
public class OptionButton extends ToolButton
{

	OptionsClickedListener ancestor;

	/**
	 * Initializes the button and adds a {@link TapHandler}.
	 * @param cmd ToolBarCommand
	 * @param ancestor OptionsClickedListener
	 * @see geogebra.mobile.gui.elements.toolbar.ToolButton ToolButton
	 */
	public OptionButton(ToolBarCommand cmd, OptionsClickedListener ancestor)
	{
		super(cmd);

		this.ancestor = ancestor;

		this.addTapHandler(new TapHandler()
		{
			@Override
			public void onTap(TapEvent event)
			{
				OptionButton.this.ancestor.optionClicked(OptionButton.this.getCmd());
			}
		});
	}

}

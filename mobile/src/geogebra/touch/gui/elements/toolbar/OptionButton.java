package geogebra.touch.gui.elements.toolbar;

import geogebra.touch.gui.elements.ToolButton;
import geogebra.touch.utils.ToolBarCommand;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * Buttons of the options-menu.
 * 
 * @author Thomas Krismayer
 * @see geogebra.touch.gui.elements.ToolButton ToolButton
 */
public class OptionButton extends ToolButton
{

	OptionsClickedListener ancestor;

	/**
	 * Initializes the button of the options-menu and adds a {@link TapHandler}.
	 * 
	 * @param cmd
	 *          ToolBarCommand
	 * @param ancestor
	 *          OptionsClickedListener
	 */
	public OptionButton(ToolBarCommand cmd, OptionsClickedListener ancestor)
	{
		super(cmd);

		this.ancestor = ancestor;

		this.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.preventDefault();
				OptionButton.this.ancestor.optionClicked(OptionButton.this.getCmd());
			}
		}, ClickEvent.getType());

		// if addDomHandler works, this is not used
		// this.addTapHandler(new TapHandler()
		// {
		// @Override
		// public void onTap(TapEvent event)
		// {
		// OptionButton.this.ancestor.optionClicked(OptionButton.this.getCmd());
		// }
		// });
	}

}

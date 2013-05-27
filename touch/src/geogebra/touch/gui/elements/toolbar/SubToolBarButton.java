package geogebra.touch.gui.elements.toolbar;

import geogebra.touch.utils.ToolBarCommand;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * Buttons of the options-menu.
 * 
 * @author Thomas Krismayer
 * @see geogebra.touch.gui.elements.toolbar.ToolButton ToolButton
 */
public class SubToolBarButton extends ToolButton
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
	public SubToolBarButton(ToolBarCommand cmd, OptionsClickedListener ancestor)
	{
		super(cmd);

		this.ancestor = ancestor;

		this.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.preventDefault();
				SubToolBarButton.this.ancestor.optionClicked(SubToolBarButton.this.getCmd());
			}
		}, ClickEvent.getType());
	}

}

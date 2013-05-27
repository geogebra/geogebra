package geogebra.touch.gui.elements.toolbar;

import geogebra.touch.model.GuiModel;
import geogebra.touch.utils.OptionType;
import geogebra.touch.utils.ToolBarCommand;
import geogebra.touch.utils.ToolBarMenu;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * 
 * A button for the main-toolBar.
 * 
 * @author Thomas Krismayer
 * 
 */
public class ToolBarButton extends ToolButton implements OptionsClickedListener
{

	protected ToolBarCommand[] menuEntries;
	protected GuiModel model;

	/**
	 * Each ToolBarButton belongs to a {@link ToolBarMenu}.
	 * 
	 * @param menu
	 *          : the button to be placed
	 * @param guiModel
	 *          : the ToolBar it is placed on
	 */
	public ToolBarButton(ToolBarMenu menu, GuiModel guiModel)
	{
		super(menu.getCommand());

		this.menuEntries = menu.getEntries();
		this.model = guiModel;

		this.addDomHandler(new ClickHandler()
		{

			@Override
			public void onClick(ClickEvent event)
			{
				event.preventDefault();
				if (ToolBarButton.this.model.getCommand() == ToolBarButton.this.getCmd()
				    && ToolBarButton.this.model.getOptionTypeShown() == OptionType.ToolBar)
				{
					ToolBarButton.this.model.closeOptions();
				}
				else
				{
					showOptions();
				}
			}
		}, ClickEvent.getType());
	}

	public ToolBarButton(SVGResource svgResource, GuiModel guiModel)
	{
		super(guiModel.getCommand());
		super.setIcon(svgResource);
		this.model = guiModel;
	}

	@Override
	public void optionClicked(ToolBarCommand cmd)
	{
		super.setCmd(cmd);
		this.model.buttonClicked(this);
	}

	protected void showOptions()
	{
		if (this.menuEntries.length != 0)
		{
			SubToolBar options = new SubToolBar(this.menuEntries, this);
			this.model.showOption(options, OptionType.ToolBar, this);
		}
		this.model.setActive(this);
	}
}

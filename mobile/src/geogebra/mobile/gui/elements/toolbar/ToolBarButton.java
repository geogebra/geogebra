package geogebra.mobile.gui.elements.toolbar;

import geogebra.mobile.model.GuiModel;
import geogebra.mobile.utils.ToolBarCommand;
import geogebra.mobile.utils.ToolBarMenu;

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
	 *            : the button to be placed
	 * @param guiModel
	 *            : the ToolBar it is placed on
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
				if (ToolBarButton.this.model.getCommand() == ToolBarButton.this
						.getCmd() && ToolBarButton.this.model.optionsShown())
				{
					ToolBarButton.this.model.closeOptions();
				} else
				{
					showOptions();
				}
			}
		}, ClickEvent.getType());
	}

	public ToolBarButton(SVGResource svg, GuiModel guiModel)
	{
		super(svg);
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
		OptionsBarBackground options = new OptionsBarBackground(
				this.menuEntries, this);
		this.model.showOptions(options);
		if (this.menuEntries.length != 0)
		{
			options.show();
		}
		this.model.setActive(this);
	}

}

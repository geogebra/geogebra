package geogebra.mobile.gui.elements.toolbar;

import java.util.Arrays;

import geogebra.mobile.gui.elements.ToolButton;
import geogebra.mobile.model.GuiModel;
import geogebra.mobile.utils.OptionType;
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
	private SVGResource svg;

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
		super(svgResource);
		this.model = guiModel;
		this.svg = svgResource;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(this.menuEntries);
		result = prime * result + ((this.model == null) ? 0 : this.model.hashCode());
		result = prime * result + ((this.svg == null) ? 0 : this.svg.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ToolBarButton other = (ToolBarButton) obj;
		if (!Arrays.equals(this.menuEntries, other.menuEntries))
			return false;
		if (this.model == null)
		{
			if (other.model != null)
				return false;
		}
		else if (!this.model.equals(other.model))
			return false;
		if (this.svg == null)
		{
			if (other.svg != null)
				return false;
		}
		else if (!this.svg.equals(other.svg))
			return false;
		return true;
	}

	@Override
	public void optionClicked(ToolBarCommand cmd)
	{
		super.setCmd(cmd);
		this.model.buttonClicked(this);
	}

	protected void showOptions()
	{
		OptionsBarBackground options = new OptionsBarBackground(this.menuEntries, this);
		this.model.showOption(options, OptionType.ToolBar);
		if (this.menuEntries.length != 0)
		{
			options.show();
		}
		this.model.setActive(this);
	}
}

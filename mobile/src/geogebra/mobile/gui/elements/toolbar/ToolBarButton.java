package geogebra.mobile.gui.elements.toolbar;

import geogebra.mobile.gui.elements.GuiModel;
import geogebra.mobile.utils.ToolBarCommand;
import geogebra.mobile.utils.ToolBarMenu;

import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;

/**
 * 
 * A button for the main-toolBar
 * 
 * @author Thomas Krismayer
 * 
 */
public class ToolBarButton extends ToolButton implements OptionsClickedListener
{

	ToolBarCommand[] menuEntries;
	GuiModel model;

	/**
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

		this.addTapHandler(new TapHandler()
		{
			@Override
			public void onTap(TapEvent event)
			{
				if (ToolBarButton.this.model.getCommand() == ToolBarButton.this.getCmd() && ToolBarButton.this.model.optionsShown())
				{
					ToolBarButton.this.model.closeOptions();
				}
				else
				{
					showOptions();
				}
			}
		});
	}

	@Override
	public void optionClicked(ToolBarCommand cmd)
	{
		super.setCmd(cmd);
		this.model.buttonClicked(this);
	}

	protected void showOptions()
	{
		this.model.closeOptions();
		OptionsBarBackground options = new OptionsBarBackground(this.menuEntries, this);
		this.model.setOptions(options);
		RootPanel.get().add(options);
		options.show(); 
		this.model.setOptionsShown(true); 
		
		this.model.setActive(this);
	}

}

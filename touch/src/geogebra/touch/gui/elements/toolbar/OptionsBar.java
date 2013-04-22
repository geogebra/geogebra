package geogebra.touch.gui.elements.toolbar;

import geogebra.common.awt.GColor;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.utils.ToolBarCommand;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Each {@link ToolBarButton ToolBarButton} has its own options.
 * 
 * @author Thomas Krismayer
 * @see ButtonBar
 */
public class OptionsBar extends PopupPanel
{
	private HorizontalPanel contentPanel;
	private HorizontalPanel optionsBar;

	/**
	 * Initialize the {@link OptionsBar optionsBar} with the specific menu entries
	 * and add an {@link AnimationHelper}.
	 * 
	 * @param menuEntries
	 *          the ToolBarCommands that will be shown
	 * @param ancestor
	 *          the OptionsClickedListener (f.e. a ToolBarButton) that was clicked
	 */
	public OptionsBar(ToolBarCommand[] menuEntries, OptionsClickedListener ancestor)
	{
		this.contentPanel = new HorizontalPanel();

		this.getElement().getStyle().setBackgroundColor(TabletGUI.getBackgroundColor().toString());
		this.getElement().getStyle().setBorderColor(GColor.BLACK.toString());
		this.getElement().getStyle().setBorderWidth(TabletGUI.FOOTER_BORDER_WIDTH, Unit.PX);
		this.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);

		this.optionsBar = new HorizontalPanel();

		OptionButton[] options = new OptionButton[menuEntries.length];

		for (int i = 0; i < options.length; i++)
		{
			options[i] = new OptionButton(menuEntries[i], ancestor);
			this.optionsBar.add(options[i]);
		}

		this.contentPanel.add(this.optionsBar);
		this.setWidget(this.contentPanel);
	}
}

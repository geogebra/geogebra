package geogebra.touch.gui.elements.toolbar;

import geogebra.touch.gui.CommonResources;
import geogebra.touch.utils.ToolBarCommand;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Each {@link ToolBarButton ToolBarButton} has its own options.
 * 
 * @author Thomas Krismayer
 * @see ButtonBar
 */
public class SubToolBar extends PopupPanel
{
	private VerticalPanel contentPanel;
	private HorizontalPanel subToolBarPanel;
	private LayoutPanel arrowPanel;

	/**
	 * Initialize the {@link OptionsBar optionsBar} with the specific menu entries
	 * and add an {@link AnimationHelper}.
	 * 
	 * @param menuEntries
	 *          the ToolBarCommands that will be shown
	 * @param ancestor
	 *          the OptionsClickedListener (f.e. a ToolBarButton) that was clicked
	 */
	public SubToolBar(ToolBarCommand[] menuEntries, OptionsClickedListener ancestor)
	{
		this.setStyleName("subToolBar");
		
		this.contentPanel = new VerticalPanel();

		this.subToolBarPanel = new HorizontalPanel();
		this.subToolBarPanel.setStyleName("subToolBarButtonPanel");

		SubToolBarButton[] options = new SubToolBarButton[menuEntries.length];

		for (int i = 0; i < options.length; i++)
		{
			options[i] = new SubToolBarButton(menuEntries[i], ancestor);
			this.subToolBarPanel.add(options[i]);
		}

		this.contentPanel.add(this.subToolBarPanel);
		this.setWidget(this.contentPanel);
		
		this.arrowPanel = new LayoutPanel();
		String html = "<img src=\"" + CommonResources.INSTANCE.subToolBarArrow().getSafeUri().asString() + "\" />";
		this.arrowPanel.getElement().setInnerHTML(html);
		this.contentPanel.add(this.arrowPanel);
		this.arrowPanel.setStyleName("subToolBarArrow");
	}
}

package geogebra.touch.gui.elements.toolbar;

import geogebra.common.awt.GColor;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.utils.ToolBarCommand;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * Each {@link ToolBarButton ToolBarButton} has its own options.
 * 
 * @author Thomas Krismayer
 * @see ButtonBar
 */
public class OptionsBar extends HorizontalPanel 
{
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
	public OptionsBar(ToolBarCommand[] menuEntries, OptionsClickedListener ancestor, int offset)
	{
		this.setWidth(Window.getClientWidth() + "px"); 
		
		this.getElement().getStyle().setBackgroundColor(GColor.LIGHT_GRAY.toString()); 
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
		 
		this.add(this.optionsBar); 
		
		this.getElement().getStyle().setPosition(Position.ABSOLUTE);   
		this.getElement().getStyle().setLeft(0, Unit.PX);  
		this.getElement().getStyle().setTop(Window.getClientHeight() - 2*offset - TabletGUI.FOOTER_BORDER_WIDTH, Unit.PX);  
		
		
	}

}

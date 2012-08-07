package geogebra.mobile.gui.elements.toolbar;

import geogebra.mobile.utils.ToolBarCommand;

import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;

public class OptionsBarBackground extends ButtonBar
{

	public OptionsBarBackground(ToolBarCommand[] menuEntries, OptionsClickedListener ancestor)
  {
		this.addStyleName("toolBarOptionsBackground"); 
		this.add(new OptionsBar(menuEntries, ancestor)); 
  }

}

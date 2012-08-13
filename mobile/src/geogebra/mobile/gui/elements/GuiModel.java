package geogebra.mobile.gui.elements;

import com.google.gwt.user.client.ui.RootPanel;

import geogebra.mobile.gui.elements.toolbar.OptionsBarBackground;
import geogebra.mobile.gui.elements.toolbar.ToolBarButton;
import geogebra.mobile.utils.ToolBarCommand;

public class GuiModel
{

	private ToolBarButton activeButton; 
	private OptionsBarBackground optionsBackground;

	public ToolBarCommand getCommand()
	{
		return this.activeButton == null ? null : this.activeButton.getCmd(); 
	}
	
	public void buttonClicked(ToolBarButton tbb){
		closeOptions(); 		
		setActive(tbb); 
	}

	public void closeOptions()
  {
		if (this.optionsBackground != null)
		{
			RootPanel.get().remove(this.optionsBackground);
		} 
  }

	public void setActive(ToolBarButton toolBarButton)
  {
		if(this.activeButton != null){
			this.activeButton.removeStyleName("button-active"); 
		}
		this.activeButton = toolBarButton; 
		this.activeButton.addStyleName("button-active");
  }

	public void setOptions(OptionsBarBackground options)
  {
		this.optionsBackground = options;
  }
}

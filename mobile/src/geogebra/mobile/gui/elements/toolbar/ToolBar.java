package geogebra.mobile.gui.elements.toolbar;

import geogebra.mobile.utils.ToolBarCommand;
import geogebra.mobile.utils.ToolBarMenu;

import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;

/**
 * @see ButtonBar
 * 
 * @author Matthias Meisinger
 * 
 */
public class ToolBar extends ButtonBar implements ToolBarInterface
{

	private OptionsBarBackground optionsBackground;
	private	ToolBarButton[] b; 
	private ToolBarCommand activeCmd; 
	
	public ToolBar()
	{
		this.addStyleName("toolbar");
	}

	public void makeTabletToolBar()
	{
		this.b = new ToolBarButton[10];

		this.b[0] = new ToolBarButton(ToolBarMenu.Point, this);
		this.b[1] = new ToolBarButton(ToolBarMenu.Line, this);
		this.b[2] = new ToolBarButton(ToolBarMenu.SpecialLine, this);
		this.b[3] = new ToolBarButton(ToolBarMenu.Polygon, this);
		this.b[4] = new ToolBarButton(ToolBarMenu.CircleAndArc, this);
		this.b[5] = new ToolBarButton(ToolBarMenu.ConicSection, this);
		this.b[6] = new ToolBarButton(ToolBarMenu.Mesurement, this);
		this.b[7] = new ToolBarButton(ToolBarMenu.Transformation, this);
		this.b[8] = new ToolBarButton(ToolBarMenu.SpecialObject, this);
		this.b[9] = new ToolBarButton(ToolBarMenu.ActionObject, this);

		for (int i = 0; i < this.b.length; i++)
		{
			this.add(this.b[i]);
		}
	}

	@Override
  public void setOptions(OptionsBarBackground optionsBackground)
	{
		this.optionsBackground = optionsBackground;
	}

	@Override
  public void closeOptions()
	{
		if (this.optionsBackground != null)
		{
			RootPanel.get().remove(this.optionsBackground);
		}
	}

	@Override
  public void setActive(ToolBarButton toolBarButton)
  {
	  for(ToolBarButton tbb : this.b){
	  	tbb.removeStyleName("button-active"); 
	  }
	  
	  toolBarButton.addStyleName("button-active"); 
	  
	  this.activeCmd = toolBarButton.getCmd(); 
  }

	public ToolBarCommand getCommand(){
		return this.activeCmd; 
	}
}
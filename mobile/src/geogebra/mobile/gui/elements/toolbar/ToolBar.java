package geogebra.mobile.gui.elements.toolbar;

import geogebra.mobile.gui.elements.GuiModel;
import geogebra.mobile.utils.ToolBarCommand;
import geogebra.mobile.utils.ToolBarMenu;

import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;

/**
 * @see ButtonBar
 * 
 * @author Matthias Meisinger
 * 
 */
public class ToolBar extends ButtonBar 
{

	private	ToolBarButton[] b; 
	private ToolBarCommand activeCmd; 
	
	public ToolBar()
	{
		this.addStyleName("toolbar");
	}

	public void makeTabletToolBar(GuiModel model)
	{
		this.b = new ToolBarButton[10];

		this.b[0] = new ToolBarButton(ToolBarMenu.Point, model);
		this.b[1] = new ToolBarButton(ToolBarMenu.Line, model);
		this.b[2] = new ToolBarButton(ToolBarMenu.SpecialLine, model);
		this.b[3] = new ToolBarButton(ToolBarMenu.Polygon, model);
		this.b[4] = new ToolBarButton(ToolBarMenu.CircleAndArc, model);
		this.b[5] = new ToolBarButton(ToolBarMenu.ConicSection, model);
		this.b[6] = new ToolBarButton(ToolBarMenu.Mesurement, model);
		this.b[7] = new ToolBarButton(ToolBarMenu.Transformation, model);
		this.b[8] = new ToolBarButton(ToolBarMenu.SpecialObject, model);
		this.b[9] = new ToolBarButton(ToolBarMenu.ActionObject, model);

		for (int i = 0; i < this.b.length; i++)
		{
			this.add(this.b[i]);
		}
		
		model.setActive(this.b[0]); 
	}

	public ToolBarCommand getCommand(){
		return this.activeCmd; 
	}
}
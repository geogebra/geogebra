package geogebra.mobile.gui.elements.toolbar;

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

	public ToolBar()
	{
		this.addStyleName("toolbar");
	}

	public void makeTabletToolBar()
	{

		ToolBarButton[] b = new ToolBarButton[10];

		b[0] = new ToolBarButton(ToolBarMenu.Point, this);
		b[1] = new ToolBarButton(ToolBarMenu.Line, this);
		b[2] = new ToolBarButton(ToolBarMenu.SpecialLine, this);
		b[3] = new ToolBarButton(ToolBarMenu.Polygon, this);
		b[4] = new ToolBarButton(ToolBarMenu.CircleAndArc, this);
		b[5] = new ToolBarButton(ToolBarMenu.ConicSection, this);
		b[6] = new ToolBarButton(ToolBarMenu.Mesurement, this);
		b[7] = new ToolBarButton(ToolBarMenu.Transformation, this);
		b[8] = new ToolBarButton(ToolBarMenu.SpecialObject, this);
		b[9] = new ToolBarButton(ToolBarMenu.ActionObject, this);

		for (int i = 0; i < b.length; i++)
		{
			this.add(b[i]);
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

}
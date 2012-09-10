package geogebra.mobile.gui.elements.toolbar;

import geogebra.mobile.controller.MobileAlgebraController;
import geogebra.mobile.gui.CommonResources;
import geogebra.mobile.gui.elements.InputDialog;
import geogebra.mobile.model.GuiModel;
import geogebra.mobile.utils.ToolBarCommand;
import geogebra.mobile.utils.ToolBarMenu;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;

/**
 * @see ButtonBar
 * 
 * @author Matthias Meisinger
 * 
 */
public class ToolBar extends ButtonBar
{

	protected ToolBarButton[] b;
	private ToolBarCommand activeCmd;
	protected InputDialog input;

	public ToolBar()
	{
		this.addStyleName("toolbar");
	}

	/**
	 * Fill the toolBar with the default {@link ToolBarButton ToolBarButtons}
	 * and sets the default button to active.
	 * 
	 * @param model
	 *            GuiModel
	 * @see GuiModel
	 * 
	 * @param algebraController
	 *            AlgebraController responsible for handling the events
	 */
	public void makeTabletToolBar(final GuiModel model,
			final MobileAlgebraController algebraController)
	{
		this.b = new ToolBarButton[12];

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
		this.b[10] = new ToolBarButton(ToolBarMenu.ManipulateObjects, model);

		// inputBar
		this.b[11] = new ToolBarButton(
				CommonResources.INSTANCE.show_input_bar(), model);
		this.b[11].addStyleName("rightButton");
		this.b[11].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				ToolBar.this.input = new InputDialog(new ClickHandler()
				{
					@Override
					public void onClick(ClickEvent e)
					{
						algebraController.newInput(ToolBar.this.input.getText());
						ToolBar.this.input.hide();
					}
				});
			}
		}, ClickEvent.getType());

		for (int i = 0; i < this.b.length; i++)
		{
			this.add(this.b[i]);
		}

		model.setActive(this.b[0]);
	}

	public ToolBarCommand getCommand()
	{
		return this.activeCmd;
	}
}
package geogebra.mobile.gui.elements.toolbar;

import geogebra.mobile.gui.CommonResources;
import geogebra.mobile.gui.elements.InputDialog;
import geogebra.mobile.gui.elements.InputDialog.InputCallback;
import geogebra.mobile.model.GuiModel;
import geogebra.mobile.model.MobileModel;
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
		this.addStyleDependentName("toolbar");
	}

	/**
	 * Fill the toolBar with the default {@link ToolBarButton ToolBarButtons} and
	 * sets the default button to active.
	 * 
	 * @param model
	 *          GuiModel
	 * @see GuiModel
	 * 
	 * @param mobileModel
	 *          MobileModel responsible for handling the events
	 */
	public void makeTabletToolBar(final MobileModel mobileModel)
	{
		this.b = new ToolBarButton[12];

		this.b[0] = new ToolBarButton(ToolBarMenu.Point, mobileModel.getGuiModel());
		this.b[1] = new ToolBarButton(ToolBarMenu.Line, mobileModel.getGuiModel());
		this.b[2] = new ToolBarButton(ToolBarMenu.SpecialLine, mobileModel.getGuiModel());
		this.b[3] = new ToolBarButton(ToolBarMenu.Polygon, mobileModel.getGuiModel());
		this.b[4] = new ToolBarButton(ToolBarMenu.CircleAndArc, mobileModel.getGuiModel());
		this.b[5] = new ToolBarButton(ToolBarMenu.ConicSection, mobileModel.getGuiModel());
		this.b[6] = new ToolBarButton(ToolBarMenu.Mesurement, mobileModel.getGuiModel());
		this.b[7] = new ToolBarButton(ToolBarMenu.Transformation, mobileModel.getGuiModel());
		this.b[8] = new ToolBarButton(ToolBarMenu.SpecialObject, mobileModel.getGuiModel());
		this.b[9] = new ToolBarButton(ToolBarMenu.ActionObject, mobileModel.getGuiModel());
		this.b[10] = new ToolBarButton(ToolBarMenu.ManipulateObjects, mobileModel.getGuiModel());

		// inputBar
		this.b[11] = new ToolBarButton(CommonResources.INSTANCE.show_input_bar(), mobileModel.getGuiModel());
		this.b[11].addStyleDependentName("rightButton");
		this.b[11].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				ToolBar.this.input = new InputDialog("Input", new InputCallback()
				{
					@Override
					public void onOk()
					{
						mobileModel.newInput(ToolBar.this.input.getText());
					}

					@Override
					public void onCancel()
					{
						ToolBar.this.input.close();
					}
				});

				ToolBar.this.input.show();
			}
		}, ClickEvent.getType());

		for (int i = 0; i < this.b.length; i++)
		{
			this.add(this.b[i]);
		}

		mobileModel.getGuiModel().setActive(this.b[0]);
	}

	public ToolBarCommand getCommand()
	{
		return this.activeCmd;
	}
}
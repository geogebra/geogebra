package geogebra.touch.gui.elements.toolbar;

import geogebra.touch.gui.CommonResources;
import geogebra.touch.gui.elements.InputDialog;
import geogebra.touch.gui.elements.InputDialog.InputCallback;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.ToolBarMenu;

import java.util.ArrayList;
import java.util.List;

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

	protected List<ToolBarButton> tools;
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
	 * @param touchModel
	 *          TouchModel responsible for handling the events
	 */
	public void makeTabletToolBar(final TouchModel touchModel)
	{
		this.tools = new ArrayList<ToolBarButton>();

		this.tools.add(new ToolBarButton(ToolBarMenu.ManipulateObjects, touchModel.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.Point, touchModel.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.Line, touchModel.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.SpecialLine, touchModel.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.Polygon, touchModel.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.CircleAndArc, touchModel.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.ConicSection, touchModel.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.Mesurement, touchModel.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.Transformation, touchModel.getGuiModel()));
		// TODO: this.b[8] = new ToolBarButton(ToolBarMenu.SpecialObject,
		// touchModel.getGuiModel());
		// TODO: this.b[9] = new ToolBarButton(ToolBarMenu.ActionObject,
		// touchModel.getGuiModel());

		// inputBar
		ToolBarButton inputBarButton = new ToolBarButton(CommonResources.INSTANCE.show_input_bar(), touchModel.getGuiModel());
		inputBarButton.addStyleDependentName("tool-rightButton");
		inputBarButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				ToolBar.this.input = new InputDialog("Input", new InputCallback()
				{
					@Override
					public void onOk()
					{
						touchModel.newInput(ToolBar.this.input.getText());
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

		this.tools.add(inputBarButton);

		for (ToolBarButton b : this.tools)
		{
			this.add(b);
		}

		touchModel.getGuiModel().setActive(this.tools.get(0));
	}
}
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
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * @see ButtonBar
 * 
 * @author Matthias Meisinger
 * 
 */
public class ToolBar extends HorizontalPanel
{

	protected List<ToolBarButton> tools;
	private HorizontalPanel toolPanel, inputButtonPanel;

	protected InputDialog input;

	public ToolBar(final TouchModel touchModel)
	{
		this.setWidth(Window.getClientWidth() + "px");
		this.toolPanel = new HorizontalPanel();
		this.inputButtonPanel = new HorizontalPanel();

		makeTabletToolBar(touchModel);
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
	private void makeTabletToolBar(final TouchModel touchModel)
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
						ToolBar.this.input.hide();
					}
				});

				ToolBar.this.input.show();
			}
		}, ClickEvent.getType());

		for (ToolBarButton b : this.tools)
		{
			this.toolPanel.add(b);
		}

		this.add(this.toolPanel);
		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		this.inputButtonPanel.add(inputBarButton);
		this.add(this.inputButtonPanel);

		touchModel.getGuiModel().setActive(this.tools.get(0));
	}

	public void onResize(ResizeEvent event)
	{
		this.setWidth(event.getWidth() + "px");
	}
}
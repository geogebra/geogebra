package geogebra.mobile.gui.elements.toolbar;

import geogebra.mobile.gui.CommonResources;
import geogebra.mobile.gui.elements.InputDialog;
import geogebra.mobile.gui.elements.InputDialog.InputCallback;
import geogebra.mobile.model.MobileModel;
import geogebra.mobile.utils.ToolBarMenu;

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
	 * @param mobileModel
	 *          MobileModel responsible for handling the events
	 */
	public void makeTabletToolBar(final MobileModel mobileModel)
	{
		this.tools = new ArrayList<ToolBarButton>();

		this.tools.add(new ToolBarButton(ToolBarMenu.ManipulateObjects, mobileModel.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.Point, mobileModel.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.Line, mobileModel.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.SpecialLine, mobileModel.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.Polygon, mobileModel.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.CircleAndArc, mobileModel.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.ConicSection, mobileModel.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.Mesurement, mobileModel.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.Transformation, mobileModel.getGuiModel()));
		// TODO: this.b[8] = new ToolBarButton(ToolBarMenu.SpecialObject,
		// mobileModel.getGuiModel());
		// TODO: this.b[9] = new ToolBarButton(ToolBarMenu.ActionObject,
		// mobileModel.getGuiModel());

		// inputBar
		ToolBarButton inputBarButton = new ToolBarButton(CommonResources.INSTANCE.show_input_bar(), mobileModel.getGuiModel());
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

		this.tools.add(inputBarButton);

		for (ToolBarButton b : this.tools)
		{
			this.add(b);
		}

		mobileModel.getGuiModel().setActive(this.tools.get(0));
	}
}
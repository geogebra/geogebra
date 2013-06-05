package geogebra.touch.gui.elements.toolbar;

import geogebra.touch.TouchApp;
import geogebra.touch.gui.CommonResources;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.dialogs.InputDialog;
import geogebra.touch.gui.dialogs.InputDialog.DialogType;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.model.GuiModel;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.ToolBarMenu;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;

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
	TextBox inputBox = new TextBox();
	
	protected TouchModel touchModel;

	public ToolBar(final TouchModel touchModel, TouchApp app,TabletGUI gui)
	{
		this.setStyleName("toolbar");
		
		this.input = new InputDialog(app, DialogType.InputField, gui);
		this.setWidth(Window.getClientWidth() + "px");
		
		this.toolPanel = new HorizontalPanel();
		this.inputButtonPanel = new HorizontalPanel();
		
		this.toolPanel.setStyleName("toolbarButtonPanel");
		this.inputButtonPanel.setStyleName("inputbarPanel");

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
	 * @param model
	 *          TouchModel responsible for handling the events
	 */
	private void makeTabletToolBar(final TouchModel model)
	{
		this.touchModel = model;
		this.tools = new ArrayList<ToolBarButton>();

		this.tools.add(new ToolBarButton(ToolBarMenu.ManipulateObjects, model.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.Point, model.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.Line, model.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.SpecialLine, model.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.Polygon, model.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.CircleAndArc, model.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.ConicSection, model.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.Mesurement, model.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.Transformation, model.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.SpecialObject, model.getGuiModel()));
		// TODO: this.b[9] = new ToolBarButton(ToolBarMenu.ActionObject,
		// touchModel.getGuiModel());

		// inputBar <----- Old version of input button
		/*StandardImageButton inputBarButton = new StandardImageButton(CommonResources.INSTANCE.show_input_bar());

		inputBarButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				ToolBar.this.input.show();
			}
		}, ClickEvent.getType());

		this.input.addCloseHandler(new CloseHandler<PopupPanel>()
		{
			@Override
			public void onClose(CloseEvent<PopupPanel> event)
			{
				ToolBar.this.touchModel.newInput(ToolBar.this.input.getInput());
			}
		});*/
		
		// new Inputbar (Stefanie Bogner)
		this.inputBox.setWidth(Window.getClientWidth()*0.2-20 + "px");
		this.inputBox.setText("Input");
		this.inputBox.addFocusHandler(new FocusHandler()
		{

			@Override
			public void onFocus(FocusEvent event)
			{
				ToolBar.this.input.show();
				ToolBar.this.inputBox.setFocus(false);
			}
		});
		
		this.input.addCloseHandler(new CloseHandler<PopupPanel>()
				{
					@Override
					public void onClose(CloseEvent<PopupPanel> event)
					{
						ToolBar.this.touchModel.newInput(ToolBar.this.input.getInput());
					}
				});
		

		for (ToolBarButton b : this.tools)
		{
			this.toolPanel.add(b);
		}
		
		this.add(this.toolPanel);
		
		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		this.setVerticalAlignment(ALIGN_MIDDLE);
		//this.inputButtonPanel.add(inputBarButton);
		this.inputButtonPanel.add(this.inputBox);
		this.add(this.inputButtonPanel);

		model.getGuiModel().setActive(this.tools.get(0));
	}

}
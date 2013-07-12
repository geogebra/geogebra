package geogebra.touch.gui.elements.toolbar;

import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.dialogs.InputDialog;
import geogebra.touch.gui.dialogs.InputDialog.DialogType;
import geogebra.touch.model.GuiModel;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.ToolBarMenu;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @see ButtonBar
 * 
 * @author Matthias Meisinger
 * 
 */
public class ToolBar extends HorizontalPanel
{

	protected List<ToolBarButton> tools;
	private HorizontalPanel toolPanel;
	private VerticalPanel inputButtonPanel;
	Panel underline;

	protected InputDialog input;
	TextBox inputBox = new TextBox();

	protected TouchModel touchModel;

	public ToolBar(final TouchModel touchModel, TouchApp app, TabletGUI gui)
	{
		this.setStyleName("toolbar");

		this.input = new InputDialog(app, DialogType.InputField, gui, touchModel.getGuiModel());
		this.setWidth(Window.getClientWidth() + "px");

		this.toolPanel = new HorizontalPanel();
		this.inputButtonPanel = new VerticalPanel();

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

		// new Inputbar (Stefanie Bogner)
		this.inputButtonPanel.setWidth(Window.getClientWidth() * 0.2 + "px");
		
		// fx background icon
		this.inputBox.getElement().setAttribute("style", "background: url(" + TouchEntryPoint.getLookAndFeel().getIcons().icon_fx().getSafeUri().asString() + ") top right no-repeat;");
		
		this.inputBox.setText(this.touchModel.getKernel().getApplication().getLocalization().getMenu("InputField"));
		this.inputBox.setReadOnly(true);
		
	this.inputButtonPanel.addDomHandler(new ClickHandler()
	{
		
		@Override
		public void onClick(ClickEvent event)
		{
			ToolBar.this.underline.removeStyleName("inactive");
			ToolBar.this.underline.addStyleName("active");
			ToolBar.this.input.show();
		}
	}, ClickEvent.getType());
	
	this.input.addCloseHandler(new CloseHandler<PopupPanel>()
			{
				@Override
				public void onClose(CloseEvent<PopupPanel> event)
				{
					ToolBar.this.underline.removeStyleName("active");
					ToolBar.this.underline.addStyleName("inactive");
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
	
	//Input Underline for Android
	this.underline = new LayoutPanel();
	this.underline.setStyleName("inputUnderline");
	this.underline.addStyleName("inactive");
	this.inputButtonPanel.add(this.underline);
	
	this.add(this.inputButtonPanel);

	model.getGuiModel().setActive(this.tools.get(0));
	}

	public void setLabels()
	{
		this.inputBox.setText(this.touchModel.getKernel().getApplication().getLocalization().getMenu("InputField"));
	}

}
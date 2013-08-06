package geogebra.touch.gui.elements.toolbar;

import geogebra.common.gui.InputHandler;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.dialogs.InputDialog;
import geogebra.touch.gui.dialogs.InputDialog.DialogType;
import geogebra.touch.gui.elements.ArrowImageButton;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.model.GuiModel;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.ToolBarMenu;

import java.util.ArrayList;
import java.util.List;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @see ButtonBar
 * 
 * @author Matthias Meisinger
 * 
 */
public class ToolBar extends FlowPanel {

	protected List<ToolBarButton> tools;
	FlowPanel toolPanel;
	private final FlowPanel inputButtonPanel;
	private final HorizontalPanel inputPanel;
	StandardImageButton showHideClosed;
	StandardImageButton showHideOpened;
	private final SVGResource iconFx = TouchEntryPoint.getLookAndFeel().getIcons().icon_fx();
	Panel underline;

	boolean toolBarOpen = false;
	boolean openNeeded = false;
	static final int toolBarButtonWidth = 56;

	protected InputDialog input;
	TextBox inputBox = new TextBox();

	protected TouchModel touchModel;

	public ToolBar(final TouchModel touchModel, TouchApp app, TabletGUI gui) {
		this.setStyleName("toolbar");

		this.input = new InputDialog(app, DialogType.InputField, gui, touchModel.getGuiModel());
		// this.setWidth(Window.getClientWidth() - 20 + "px");

		this.toolPanel = new FlowPanel();
		this.inputButtonPanel = new FlowPanel();
		this.inputPanel = new HorizontalPanel();

		this.toolPanel.setStyleName("toolbarButtonPanel");
		this.inputButtonPanel.setStyleName("inputbarPanel");

		this.showHideClosed = new ArrowImageButton(TouchEntryPoint.getLookAndFeel().getIcons().triangle_left());
		this.showHideOpened = new ArrowImageButton(TouchEntryPoint.getLookAndFeel().getIcons().triangle_left());
		this.showHideClosed.setVisible(this.openNeeded);
		this.showHideOpened.setVisible(false);

		this.makeTabletToolBar(touchModel);
	}

	public void closeToolBar() {
		this.removeStyleName("visible");
		this.showHideOpened.setVisible(false);
		this.showHideClosed.setVisible(this.openNeeded);
		this.toolPanel.setWidth(Window.getClientWidth() - 60 + "px");
		this.toolBarOpen = false;
	}

	public boolean isOpen() {
		return this.toolBarOpen;
	}

	/**
	 * Fill the toolBar with the default {@link ToolBarButton ToolBarButtons}
	 * and sets the default button to active.
	 * 
	 * @param model
	 *            GuiModel
	 * @see GuiModel
	 * 
	 * @param model
	 *            TouchModel responsible for handling the events
	 */
	private void makeTabletToolBar(final TouchModel model) {
		this.touchModel = model;
		this.touchModel.getGuiModel().setToolBar(this);
		this.tools = new ArrayList<ToolBarButton>();

		ToolBarButton manipulateObjects = new ToolBarButton(ToolBarMenu.ManipulateObjects, model.getGuiModel());
		this.touchModel.getGuiModel().setDefaultButton(manipulateObjects);
		this.tools.add(manipulateObjects);
		this.tools.add(new ToolBarButton(ToolBarMenu.Point, model.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.Line, model.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.SpecialLine, model.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.Polygon, model.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.CircleAndArc, model.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.ConicSection, model.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.Mesurement, model.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.Transformation, model.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.SpecialObject, model.getGuiModel()));
		this.tools.add(new ToolBarButton(ToolBarMenu.ActionObject, model.getGuiModel()));

		this.openNeeded = Window.getClientWidth() < ToolBar.toolBarButtonWidth * this.tools.size() + 270;
		this.showHideClosed.setVisible(this.openNeeded);

		// new Inputbar (Stefanie Bogner)

		this.inputPanel.setStyleName("inputPanel");
		this.inputPanel.add(this.inputBox);

		// fx background icon
		// this.inputBox.getElement().setAttribute("style",
		// "background-image: url("
		// +
		// TouchEntryPoint.getLookAndFeel().getIcons().icon_fx().getSafeUri().asString()
		// +
		// "); background-position: top right; background-repeat: no-repeat;");
		final Panel iconPanel = new LayoutPanel();
		final String html = "<img src=\"" + this.iconFx.getSafeUri().asString() + "\" />";
		iconPanel.getElement().setInnerHTML(html);
		iconPanel.setStyleName("iconPanel");
		this.inputPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		this.inputPanel.add(iconPanel);

		this.inputBox.setText(this.touchModel.getKernel().getApplication().getLocalization().getMenu("InputField"));
		this.inputBox.setReadOnly(true);

		this.inputButtonPanel.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ToolBar.this.underline.removeStyleName("inactive");
				ToolBar.this.underline.addStyleName("active");
				ToolBar.this.input.show();
			}
		}, ClickEvent.getType());

		this.input.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				ToolBar.this.underline.removeStyleName("active");
				ToolBar.this.underline.addStyleName("inactive");

			}
		});
		this.input.setInputHandler(new InputHandler() {

			@Override
			public boolean processInput(String inputString) {
				return ToolBar.this.touchModel.newInput(inputString);
			}

		});

		for (final ToolBarButton b : this.tools) {
			this.toolPanel.add(b);
		}

		this.add(this.showHideClosed);
		this.toolPanel.add(this.showHideOpened);

		if (!this.toolBarOpen) {
			if (this.openNeeded) {
				this.toolPanel.setWidth(Window.getClientWidth() - 60 + "px");
			} else {
				this.toolPanel.setWidth(ToolBar.toolBarButtonWidth * this.tools.size() + "px");
			}
		}
		this.add(this.toolPanel);

		this.showHideClosed.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// toolbar is going to be shown
				ToolBar.this.addStyleName("visible");
				ToolBar.this.showHideOpened.setVisible(true);
				ToolBar.this.showHideClosed.setVisible(false);
				ToolBar.this.toolPanel.setWidth("100%");
				ToolBar.this.toolBarOpen = true;
				ToolBar.this.touchModel.getGuiModel().closeOnlyOptions();
			}
		});

		this.showHideOpened.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ToolBar.this.touchModel.getGuiModel().closeOptions();
				ToolBar.this.closeToolBar();
			}
		});

		this.inputButtonPanel.add(this.inputPanel);

		// Input Underline for Android
		this.underline = new LayoutPanel();
		this.underline.setStyleName("inputUnderline");
		this.underline.addStyleName("inactive");
		this.inputButtonPanel.add(this.underline);

		this.add(this.inputButtonPanel);

		model.getGuiModel().setActive(this.tools.get(0));

		final LayoutPanel clearPanel = new LayoutPanel();
		clearPanel.setStyleName("clear");
		this.add(clearPanel);
	}

	public void onResize() {
		if (!this.toolBarOpen) {
			if (this.openNeeded) {
				this.toolPanel.setWidth(Window.getClientWidth() - 60 + "px");
			} else {
				this.toolPanel.setWidth(ToolBar.toolBarButtonWidth * this.tools.size() + "px");
			}
		} else {
			this.toolPanel.setWidth("100%");
		}

		this.openNeeded = Window.getClientWidth() < ToolBar.toolBarButtonWidth * this.tools.size() + 270;
		this.showHideClosed.setVisible(this.openNeeded);
	}

	public boolean openNeeded() {
		return this.openNeeded;
	}

	public void setLabels() {
		this.inputBox.setText(this.touchModel.getKernel().getApplication().getLocalization().getMenu("InputField"));
	}

}
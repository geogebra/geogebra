package geogebra.touch.gui.elements.toolbar;

import geogebra.common.gui.InputHandler;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.ResizeListener;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.algebra.events.FastClickHandler;
import geogebra.touch.gui.dialogs.InputDialog;
import geogebra.touch.gui.dialogs.InputDialog.DialogType;
import geogebra.touch.gui.elements.StandardButton;
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
public class ToolBar extends FlowPanel implements ResizeListener {

	private List<ToolBarButton> tools;
	private FlowPanel toolPanel;
	private FlowPanel inputButtonPanel;
	private HorizontalPanel inputPanel;
	private StandardButton showHideClosed;
	private StandardButton showHideOpened;
	private final SVGResource iconFx = TouchEntryPoint.getLookAndFeel()
			.getIcons().icon_fx();
	private Panel underline;
	private boolean openNeeded = false;
	private static final int toolBarButtonWidth = 56;
	private InputDialog input;
	private final TextBox inputBox = new TextBox();
	private TouchModel touchModel;
	private boolean openClicked = false;
	private TouchApp app;

	public ToolBar(final TouchModel touchModel, final TouchApp app) {
		this.touchModel = touchModel;
		this.setStyleName("toolbar");
		this.app = app;
		this.input = new InputDialog(app, DialogType.InputField, touchModel);
		initShowHideButtons();
		addToolPanel();
		addInputPanel();
		addClearPanel();

		((TabletGUI) app.getTouchGui()).addResizeListener(this);
	}

	private void addToolPanel() {
		final GuiModel guiModel = this.touchModel.getGuiModel();
		this.toolPanel = new FlowPanel();
		this.toolPanel.setStyleName("toolbarButtonPanel");
		this.tools = new ArrayList<ToolBarButton>();

		for (ToolBarMenu t : ToolBarMenu.values()) {
			final ToolBarButton b = new ToolBarButton(t, guiModel, this.app);

			b.addFastClickHandler(new FastClickHandler() {
				@Override
				public void onClick() {
					b.onToolBarButton();
				}
			});
			this.tools.add(b);
			this.toolPanel.add(b);
		}

		guiModel.setDefaultButton(this.tools.get(0));
		guiModel.setActive(this.tools.get(0));

		this.toolPanel.add(this.showHideOpened);
		this.add(this.toolPanel);
	}

	private void initShowHideButtons() {
		this.showHideClosed = new StandardButton(TouchEntryPoint
				.getLookAndFeel().getIcons().triangle_left());
		this.showHideClosed.setStyleName("arrowLeft");
		this.showHideClosed.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick() {
				onExpandToolBar();

			}
		});

		this.add(this.showHideClosed);

		this.showHideOpened = new StandardButton(TouchEntryPoint
				.getLookAndFeel().getIcons().triangle_left());
		this.showHideOpened.setStyleName("arrowLeft");
		this.showHideOpened.setVisible(false);
		this.showHideOpened.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick() {
				onCollapseToolBar();
			}
		});
	}

	void onExpandToolBar() {
		this.openClicked = true;
		this.addStyleName("visible");
		this.showHideOpened.setVisible(true);
		this.showHideClosed.setVisible(false);
		this.toolPanel.setWidth("100%");
		this.touchModel.getGuiModel().closeOnlyOptions();
		((TabletGUI) this.app.getTouchGui()).updateViewSizes();
		this.openClicked = false;
	}

	void onCollapseToolBar() {
		this.touchModel.getGuiModel().closeOptions();
		this.closeToolBar();
		((TabletGUI) this.app.getTouchGui()).updateViewSizes();
	}

	private void addInputPanel() {
		this.inputButtonPanel = new FlowPanel();
		this.inputButtonPanel.setStyleName("inputbarPanel");

		this.inputPanel = new HorizontalPanel();
		this.inputPanel.setStyleName("inputPanel");
		this.inputPanel.add(this.inputBox);

		final Panel iconPanel = new LayoutPanel();
		iconPanel.getElement().setInnerHTML(
				"<img src=\"" + this.iconFx.getSafeUri().asString() + "\" />");
		iconPanel.setStyleName("iconPanel");
		this.inputPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		this.inputPanel.add(iconPanel);
		this.inputButtonPanel.add(this.inputPanel);

		this.inputBox.setText(this.touchModel.getKernel().getApplication()
				.getLocalization().getMenu("InputField"));
		this.inputBox.setReadOnly(true);

		// Input Underline for Android
		this.underline = new LayoutPanel();
		this.underline.setStyleName("inputUnderline");
		this.underline.addStyleName("inactive");
		this.inputButtonPanel.add(this.underline);

		this.add(this.inputButtonPanel);

		this.inputButtonPanel.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				onInputFieldClicked();
			}
		}, ClickEvent.getType());

		this.input.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(final CloseEvent<PopupPanel> event) {
				onInputFieldClosed();
			}
		});

		this.input.setInputHandler(new InputHandler() {
			@Override
			public boolean processInput(String inputString) {
				return onProcessInput(inputString);
			}
		});
	}

	void onInputFieldClicked() {
		this.underline.removeStyleName("inactive");
		this.underline.addStyleName("active");
		this.input.show();
	}

	void onInputFieldClosed() {
		this.underline.removeStyleName("active");
		this.underline.addStyleName("inactive");
	}

	boolean onProcessInput(String inputString) {
		return this.touchModel.newInput(inputString);
	}

	private void addClearPanel() {
		final LayoutPanel clearPanel = new LayoutPanel();
		clearPanel.setStyleName("clear");
		this.add(clearPanel);
	}

	private void closeToolBar() {
		this.removeStyleName("visible");
		this.showHideOpened.setVisible(false);
		this.showHideClosed.setVisible(this.openNeeded);
		this.toolPanel.setWidth(Window.getClientWidth() - 60 + "px");
	}

	public void setLabels() {
		this.inputBox.setText(this.touchModel.getKernel().getApplication()
				.getLocalization().getMenu("InputField"));
	}

	@Override
	public void onResize() {
		this.openNeeded = Window.getClientWidth() < ToolBar.toolBarButtonWidth
				* this.tools.size() + 270;
		if (!this.openClicked) {
			this.closeToolBar();
		}
	}
}
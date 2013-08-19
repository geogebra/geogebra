package geogebra.touch.gui.elements.header;

import geogebra.touch.FileManagerT;
import geogebra.touch.TouchApp;
import geogebra.touch.gui.ResizeListener;
import geogebra.touch.gui.dialogs.InfoDialog;
import geogebra.touch.gui.dialogs.InfoDialog.InfoType;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.TitleChangedListener;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Extends from {@link HeaderPanel}.
 */
public class TabletHeaderPanel extends HorizontalPanel implements
		ResizeListener {
	private final TabletHeaderPanelLeft leftHeader;
	private final VerticalPanel titlePanel;
	private Panel underline;
	private TextBox worksheetTitle;
	private final TabletHeaderPanelRight rightHeader;
	private InfoDialog infoOverrideDialog;
	private TouchApp app;
	private FileManagerT fm;

	public TabletHeaderPanel(final TouchApp app, final TouchModel touchModel) {
		this.setStyleName("headerbar");

		this.app = app;
		this.fm = this.app.getFileManager();
		this.leftHeader = new TabletHeaderPanelLeft(app, touchModel, this);
		this.leftHeader.setStyleName("headerLeft");
		this.infoOverrideDialog = new InfoDialog(this.app,
				touchModel.getGuiModel(), InfoType.Override);

		this.titlePanel = new VerticalPanel();

		this.worksheetTitle = new TextBox();
		this.worksheetTitle.setText(app.getConstructionTitle());

		this.app.addTitleChangedListener(new TitleChangedListener() {
			@Override
			public void onTitleChange(final String title) {
				setConstructionTitle(title);
			}
		});

		this.rightHeader = new TabletHeaderPanelRight(app, this, touchModel);
		this.rightHeader.setStyleName("headerRight");
		this.worksheetTitle.setStyleName("worksheetTitle");

		this.worksheetTitle.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(final KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					onChangeTitle();
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					onCancel();
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_TAB) {
					event.preventDefault();
					return;
				}
			}
		});

		this.worksheetTitle.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(final MouseOutEvent event) {
				onCancel();
			}
		});

		this.worksheetTitle.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(final FocusEvent event) {
				Scheduler.get().scheduleDeferred(
						new Scheduler.ScheduledCommand() {
							@Override
							public void execute() {
								onFocusTitle();
							}
						});
			}
		});

		this.worksheetTitle.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(final BlurEvent event) {
				onBlurTitle();
			}
		});

		this.titlePanel.add(this.worksheetTitle);

		// Input Underline for Android
		this.underline = new LayoutPanel();
		this.underline.setStyleName("inputUnderline");
		this.underline.addStyleName("inactive");
		this.titlePanel.getElement().setAttribute("width", "100%");

		this.titlePanel.add(this.underline);

		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		this.add(this.leftHeader);

		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.add(this.titlePanel);

		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		this.add(this.rightHeader);

	}

	protected void onChangeTitle() {
		if (this.fm.hasFile(this.worksheetTitle.getText())) {
			this.infoOverrideDialog.setConsTitle(this.worksheetTitle.getText());
			this.infoOverrideDialog.show();
		} else if (this.worksheetTitle.getText().equals("")) {
			this.worksheetTitle.setText(this.app.getConstructionTitle());
			this.worksheetTitle.setFocus(false);
		} else {
			this.app.setConstructionTitle(this.worksheetTitle.getText());
			this.fm.saveFile(this.app);
			this.worksheetTitle.setFocus(false);
		}
	}

	protected void onCancel() {
		this.worksheetTitle.setText(this.app.getConstructionTitle());
		this.worksheetTitle.setFocus(false);
	}

	protected void onFocusTitle() {
		this.worksheetTitle.setFocus(true);
		this.worksheetTitle.selectAll();
		this.underline.removeStyleName("inactive");
		this.underline.addStyleName("active");
	}

	protected void onBlurTitle() {
		this.worksheetTitle.setFocus(false);
		this.underline.removeStyleName("active");
		this.underline.addStyleName("inactive");
	}

	public void editTitle() {
		this.worksheetTitle.setFocus(true);
		this.worksheetTitle.selectAll();
	}

	/**
	 * Enable or disable the buttons redo, undo and save.
	 * 
	 */
	public void enableDisableButtons() {
		this.leftHeader.enableDisableSave();
		this.rightHeader.enableDisableRedo();
		this.rightHeader.enableDisableUndo();
	}

	public String getConstructionTitle() {
		return this.worksheetTitle.getText();
	}

	protected void setConstructionTitle(final String title) {
		this.worksheetTitle.setText(title);
	}
	
	public TabletHeaderPanelLeft getLeftHeader() {
		return this.leftHeader;
	}

	public TabletHeaderPanelRight getRightHeader() {
		return this.rightHeader;
	}

	@Override
	public void onResize() {
		this.setWidth(Window.getClientWidth() + "px");
	}

	public void setLabels() {
		this.leftHeader.setLabels();
		this.infoOverrideDialog.setLabels();
	}
}
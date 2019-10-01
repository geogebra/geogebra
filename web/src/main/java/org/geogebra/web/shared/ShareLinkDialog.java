package org.geogebra.web.shared;

import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.laf.VendorSettings;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Alicia
 *
 *         get share link dialog
 */
public class ShareLinkDialog extends DialogBoxW implements FastClickHandler {
	private FlowPanel mainPanel;
	private FlowPanel linkPanel;
	private Label linkLabel;
	/** textbox providing share url */
	protected TextBox linkBox;
	/** true if linkBox is focused */
	protected boolean linkBoxFocused = true;
	private StandardButton copyBtn;
	private Label shareHelp;
	// button panel
	private FlowPanel buttonPanel;
	private StandardButton printBtn;
	private StandardButton exportImgBtn;
	private StandardButton cancelBtn;
	// share link
	private String shareURL;
	/** parent widget */
	protected Widget anchor;

	/**
	 * @param app
	 *            application
	 * @param shareURL
	 *            sharing url of material
	 * @param anchor
	 *            parent widget
	 */
	public ShareLinkDialog(AppW app, String shareURL, Widget anchor) {
		super(false, true, null, app.getPanel(), app);
		this.app = app;
		this.shareURL = shareURL;
		this.anchor = anchor;
		initGui();
		DialogUtil.hideOnLogout(app, this);
	}

	private void initGui() {
        VendorSettings vendorSettings = ((AppW) app).getVendorSettings();
        addStyleName(vendorSettings.getStyleName("shareLink"));
		setAutoHideEnabled(true);
		setGlassEnabled(false);
		addCloseHandler(new CloseHandler<GPopupPanel>() {

			@Override
			public void onClose(CloseEvent<GPopupPanel> event) {
				if (anchor != null) {
					anchor.removeStyleName("selected");
				}
			}
		});
		mainPanel = new FlowPanel();
		// panel with link text field
		linkPanel = new FlowPanel();
		linkPanel.setStyleName("linkPanel");
		linkLabel = new Label();
		linkLabel.setStyleName("linkLabel");
		linkBox = new TextBox();
		linkBox.setReadOnly(true);
		linkBox.setText(this.shareURL);
		linkBox.setStyleName("linkBox");
		addLinkBoxHandlers();
		// build and add copy button
		copyBtn = new StandardButton(localize("Copy"),
				app);
		copyBtn.setStyleName("copyButton");
		mainPanel.add(linkLabel);
		linkPanel.add(linkBox);
		linkPanel.add(copyBtn);
		mainPanel.add(linkPanel);
		// share help text
		shareHelp = new Label();
		shareHelp.addStyleName("shareHelpTxt");
        if (app.isMebis()) {
			mainPanel.add(shareHelp);
		}
		// build button panel (print prev, export img)
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("buttonPanel");
		printBtn = new StandardButton(
				SharedResources.INSTANCE.print_white(),
				localize("Print"), 24, app);
		printBtn.setStyleName("roundButton");
		printBtn.addFastClickHandler(this);
		exportImgBtn = new StandardButton(
				SharedResources.INSTANCE.file_download_white(),
				localize("exportImage"), 24, app);
		exportImgBtn.setStyleName("roundButton");
		exportImgBtn.addFastClickHandler(this);
		cancelBtn = new StandardButton(localize("Cancel"), app);
		cancelBtn.addFastClickHandler(this);
        if (app.isMebis()) {
			buttonPanel.setStyleName("DialogButtonPanel");
			buttonPanel.add(cancelBtn);
		} else {
			buttonPanel.add(printBtn);
			buttonPanel.add(exportImgBtn);
		}
		mainPanel.add(buttonPanel);
		add(mainPanel);
		setLabels();
		copyBtn.addFastClickHandler(this);
	}

	private void addLinkBoxHandlers() {
		linkBox.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				focusLinkBox();
			}
		});
		linkBox.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				if (linkBoxFocused) {
					linkBox.setFocus(true);
					linkBox.setSelectionRange(0, 0);
				}
				linkBoxFocused = false;
			}
		});
	}

	/**
	 * focus textBox and select text
	 */
	protected void focusLinkBox() {
		linkBox.setFocus(true);
		linkBox.setSelectionRange(0, 0);
		linkBox.selectAll();
		linkBoxFocused = true;
	}

	@Override
	public void onClick(Widget source) {
		if (source == copyBtn) {
			linkBoxFocused = false;
			app.copyTextToSystemClipboard(linkBox.getText());
			focusLinkBox();
			hide();
		} else if (source == printBtn) {
			app.getDialogManager().showPrintPreview();
			hide();
		} else if (source == exportImgBtn) {
			app.getDialogManager().showExportImageDialog(null);
			hide();
		} else if (source == cancelBtn) {
			hide();
		}
	}

	@Override
	protected void onWindowResize() {
		if (anchor == null) {
			super.onWindowResize();
		} else {
			setPopupPosition(anchor.getAbsoluteLeft() - 474,
							anchor.getAbsoluteTop() - 27);
		}
	}

	/**
	 * set button labels and dialog title
	 */
	public void setLabels() {
		// dialog title
		getCaption().setText(
                localize(app.isMebis() ? "shareByLink" : "Share"));
		linkLabel.setText(localize("Link"));
		copyBtn.setText(localize("Copy"));
		printBtn.setText(localize("Print"));
		exportImgBtn.setText(localize("exportImage"));
		cancelBtn.setText(localize("Cancel"));

        VendorSettings vendorSettings = ((AppW) app).getVendorSettings();
        shareHelp.setText(localize(vendorSettings.getMenuLocalizationKey("SharedLinkHelpTxt")));
	}

	private String localize(String id) {
		return app.getLocalization().getMenu(id);
	}

	@Override
	public void show() {
		super.show();
		if (anchor != null) {
			anchor.addStyleName("selected");
		}
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				linkBox.selectAll();
				linkBox.setFocus(true);
			}
		});
	}

	@Override
	public void center() {
		super.center();
		if (anchor != null) {
			setPopupPosition(anchor.getAbsoluteLeft() - 474,
					anchor.getAbsoluteTop() - 27);
		}
	}

	@Override
	public void onBrowserEvent(Event event) {
		if (event.getTypeInt() != Event.ONMOUSEMOVE) {
			super.onBrowserEvent(event);
		}
	}
}

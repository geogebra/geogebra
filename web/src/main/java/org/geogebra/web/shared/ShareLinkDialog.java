package org.geogebra.web.shared;

import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.ComponentDialog;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 *   Share link dialog
 */
public class ShareLinkDialog extends ComponentDialog implements FastClickHandler {
	private FlowPanel contentPanel;
	/** textbox providing share url */
	protected TextBox linkBox;
	/** true if linkBox is focused */
	protected boolean linkBoxFocused = true;
	private StandardButton copyBtn;

	private StandardButton printBtn;
	private StandardButton exportImgBtn;

	/** parent widget */
	protected Widget anchor;

	/**
	 * @param app application
	 * @param data dialog tanskeys
	 * @param shareURL sharing url of material
	 * @param anchor parent widget
	 */
	public ShareLinkDialog(AppW app, DialogData data, String shareURL, Widget anchor) {
		super(app, data, true, false);
		this.app = app;
		this.anchor = anchor;
		addStyleName(app.getVendorSettings().getStyleName("shareLink"));
		buildContent(shareURL);
		DialogUtil.hideOnLogout(app, this);
	}

	private void buildContent(String shareURL) {
		addCloseHandler(event -> {
			if (anchor != null) {
				anchor.removeStyleName("selected");
			}
		});

		contentPanel = new FlowPanel();

		FlowPanel linkPanel = new FlowPanel();
		linkPanel.setStyleName("linkPanel");
		Label linkLabel = new Label(localize("Link"));
		linkLabel.setStyleName("linkLabel");
		linkBox = new TextBox();
		linkBox.setReadOnly(true);
		linkBox.setText(shareURL);
		linkBox.setStyleName("linkBox");
		addLinkBoxHandlers();

		copyBtn = new StandardButton(localize("Copy"),
				app);
		copyBtn.setStyleName("copyButton");
		copyBtn.addFastClickHandler(this);

		contentPanel.add(linkLabel);
		linkPanel.add(linkBox);
		linkPanel.add(copyBtn);
		contentPanel.add(linkPanel);

		Label shareHelp = new Label(localize(((AppW) app).getVendorSettings()
				.getMenuLocalizationKey("SharedLinkHelpTxt")));
		shareHelp.addStyleName("shareHelpTxt");

		FlowPanel buttonPanel = new FlowPanel();
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
		buttonPanel.add(printBtn);
		buttonPanel.add(exportImgBtn);

		contentPanel.add(buttonPanel);
		addDialogContent(contentPanel);
	}

	private void addLinkBoxHandlers() {
		linkBox.addClickHandler(event -> focusLinkBox());
		linkBox.addBlurHandler(event -> {
			if (linkBoxFocused) {
				linkBox.setFocus(true);
				linkBox.setSelectionRange(0, 0);
			}
			linkBoxFocused = false;
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
		} else if (source == printBtn) {
			app.getDialogManager().showPrintPreview();
		} else if (source == exportImgBtn) {
			app.getDialogManager().showExportImageDialog(null);
		}
		hide();
	}

	@Override
	public void onResize(ResizeEvent resizeEvent) {
		if (anchor == null) {
			super.onResize(resizeEvent);
		} else {
			setPopupPosition(anchor.getAbsoluteLeft() - (this.getOffsetWidth()
							- anchor.getOffsetWidth()), anchor.getAbsoluteTop() - 27);
		}
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
		Scheduler.get().scheduleDeferred(() -> {
			linkBox.selectAll();
			linkBox.setFocus(true);
		});
	}

	@Override
	public void center() {
		super.center();
		if (anchor != null) {
			setPopupPosition(anchor.getAbsoluteLeft() - (this.getOffsetWidth()
					- anchor.getOffsetWidth()), anchor.getAbsoluteTop() - 27);
		}
	}

	@Override
	public void onBrowserEvent(Event event) {
		if (event.getTypeInt() != Event.ONMOUSEMOVE) {
			super.onBrowserEvent(event);
		}
	}
}
package org.geogebra.web.shared;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.html5.gui.laf.VendorSettings;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.core.client.Scheduler;
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
public class ShareLinkDialog extends DialogBoxW {
	private Label linkLabel;
	/** textbox providing share url */
	protected TextBox linkBox;
	private StandardButton copyBtn;
	private Label shareHelp;
	private StandardButton printBtn;
	private StandardButton embedBtn;
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
		setStyleName("MaterialDialogBox"); // even in classic
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
		addCloseHandler(event -> {
			if (anchor != null) {
				anchor.removeStyleName("selected");
			}
		});
		// panel with link text field
		FlowPanel linkPanel = new FlowPanel();
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
		FlowPanel mainPanel = new FlowPanel();
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
		// button panel
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("buttonPanel");

		printBtn = roundButton(
				SharedResources.INSTANCE.print_white(), "Print");
		printBtn.addFastClickHandler(source -> {
			app.getDialogManager().showPrintPreview();
			hide();
		});

		embedBtn = roundButton(
				SharedResources.INSTANCE.code_white(), "Embed");
		embedBtn.setStyleName("roundButton");
		embedBtn.addFastClickHandler(source -> {
			copyEmbedCode();
			hide();
		});

		exportImgBtn = roundButton(
				SharedResources.INSTANCE.file_download_white(), "exportImage");
		exportImgBtn.setStyleName("roundButton");
		exportImgBtn.addFastClickHandler(source -> {
			app.getDialogManager().showExportImageDialog(null);
			hide();
		});

		cancelBtn = new StandardButton(localize("Cancel"), app);
		cancelBtn.addFastClickHandler(source -> hide());
		if (app.isMebis()) {
			buttonPanel.setStyleName("DialogButtonPanel");
			buttonPanel.add(cancelBtn);
		} else {
			buttonPanel.add(printBtn);
			buttonPanel.add(exportImgBtn);
			buttonPanel.add(embedBtn);
		}
		mainPanel.add(buttonPanel);
		add(mainPanel);
		setLabels();

		copyBtn.addFastClickHandler(source -> {
			app.copyTextToSystemClipboard(linkBox.getText());
			hide();
		});
	}

	private StandardButton roundButton(SVGResource icon, String titleKey) {
		StandardButton btn = new StandardButton(icon,
				localize(titleKey), 24, app);
		btn.setStyleName("roundButton");
		return btn;
	}

	private void addLinkBoxHandlers() {
		// prevent manual deselection
		linkBox.addClickHandler(event -> focusLinkBox());
	}

	/**
	 * focus textBox and select text
	 */
	protected void focusLinkBox() {
		linkBox.setFocus(true);
		linkBox.setSelectionRange(0, 0);
		linkBox.selectAll();
	}

	private void copyEmbedCode() {
		AppW appW = (AppW) this.app;
		Material m = appW.getActiveMaterial();
		if (m != null) {
			String url = appW.getCurrentURL(m.getSharingKeyOrId(), true) + "?embed";
			String code =
					"<iframe src=\"" + url + "\""
					+ " width=\"800\" height=\"600\" allowfullscreen"
					+ " style=\"border: 1px solid #e4e4e4;border-radius: 4px;\""
					+ " frameborder=\"0\"></iframe>";
			this.app.copyTextToSystemClipboard(code);
			ToolTipManagerW.sharedInstance().showBottomMessage(
					localize("CopiedToClipboard"), true, appW);
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
		embedBtn.setText(localize("Embed"));
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
		Scheduler.get().scheduleDeferred(this::focusLinkBox);
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

package org.geogebra.web.shared;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
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
public class ShareLinkDialog extends ComponentDialog {
	/** textbox providing share url */
	protected TextBox linkBox;

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

		FlowPanel linkPanel = new FlowPanel();
		linkPanel.setStyleName("linkPanel");
		Label linkLabel = new Label(localize("Link"));
		linkLabel.setStyleName("linkLabel");
		linkBox = new TextBox();
		linkBox.setReadOnly(true);
		linkBox.setText(shareURL);
		linkBox.setStyleName("linkBox");
		addLinkBoxHandlers();

		StandardButton copyBtn = new StandardButton(localize("Copy"),
				app);
		copyBtn.setStyleName("copyButton");

		copyBtn.addFastClickHandler(source -> {
			app.copyTextToSystemClipboard(linkBox.getText());
			hide();
		});

		FlowPanel contentPanel = new FlowPanel();
		contentPanel.add(linkLabel);
		linkPanel.add(linkBox);
		linkPanel.add(copyBtn);
		contentPanel.add(linkPanel);

		Label shareHelp = new Label(localize(((AppW) app).getVendorSettings()
				.getMenuLocalizationKey("SharedLinkHelpTxt")));
		shareHelp.addStyleName("shareHelpTxt");

		// build button panel (print prev, export img)
		// button panel

		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("buttonPanel");

		StandardButton printBtn = roundButton(
				SharedResources.INSTANCE.print_white(), "Print");
		printBtn.addFastClickHandler(source -> {
			app.getDialogManager().showPrintPreview();
			hide();
		});

		StandardButton exportImgBtn = roundButton(
				SharedResources.INSTANCE.file_download_white(), "exportImage");
		exportImgBtn.addFastClickHandler(source -> {
			app.getDialogManager().showExportImageDialog(null);
			hide();
		});

		StandardButton embedBtn = roundButton(
				SharedResources.INSTANCE.code_white(), "Embed");
		embedBtn.addFastClickHandler(source -> {
			copyEmbedCode();
			hide();
		});

		buttonPanel.add(printBtn);
		buttonPanel.add(exportImgBtn);
		buttonPanel.add(embedBtn);

		contentPanel.add(buttonPanel);
		addDialogContent(contentPanel);
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
		Scheduler.get().scheduleDeferred(this::focusLinkBox);

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
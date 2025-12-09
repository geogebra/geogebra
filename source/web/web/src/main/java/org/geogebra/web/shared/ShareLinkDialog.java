/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.shared;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.Event;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.TextBox;
import org.gwtproject.user.client.ui.Widget;

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
		super(app, data, true, true);
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

		linkBox = new TextBox();
		linkBox.setReadOnly(true);
		linkBox.setText(shareURL);
		linkBox.setStyleName("linkBox");
		addLinkBoxHandlers();

		Label linkLabel = BaseWidgetFactory.INSTANCE.newSecondaryText(
				localize("Link"), "linkLabel");

		StandardButton copyBtn = new StandardButton(localize("Copy"));
		copyBtn.setStyleName("copyButton");

		copyBtn.addFastClickHandler(source -> {
			app.getCopyPaste().copyTextToSystemClipboard(linkBox.getText());
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
				localize(titleKey), 24);
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
			String url = appW.getCurrentURL(m.getSharingKeySafe(), true) + "?embed";
			String code =
					"<iframe src=\"" + url + "\""
					+ " width=\"800\" height=\"600\" allowfullscreen"
					+ " style=\"border: 1px solid #e4e4e4;border-radius: 4px;\""
					+ " frameborder=\"0\"></iframe>";
			this.app.getCopyPaste().copyTextToSystemClipboard(code);
			((AppW) app).getToolTipManager().showBottomMessage(
					localize("CopiedToClipboard"), appW);
		}
		hide();
	}

	@Override
	public void onResize() {
		if (anchor == null) {
			super.onResize();
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
		if (DOM.eventGetType(event) != Event.ONMOUSEMOVE) {
			super.onBrowserEvent(event);
		}
	}
}
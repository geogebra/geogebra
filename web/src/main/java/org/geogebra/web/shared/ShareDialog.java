package org.geogebra.web.shared;

import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Alicia
 *
 *         Share Dialog Material Design
 */
public class ShareDialog extends DialogBoxW implements FastClickHandler {

	private FlowPanel mainPanel;
	private FlowPanel linkPanel;
	private Label linkLabel;
	/** textbox providing share url */
	protected TextBox linkBox;
	private StandardButton copyBtn;

	private FlowPanel buttonPanel;
	private StandardButton printBtn;
	private StandardButton exportImgBtn;

	private String shareURL;

	/**
	 * @param app
	 *            application
	 * @param shareURL
	 *            sharing url of material
	 */
	public ShareDialog(AppW app, String shareURL) {
		super(app.getPanel(), app);
		this.app = app;
		this.shareURL = shareURL;
		initGui();
	}

	private void initGui() {
		addStyleName("shareDialog");
		setAutoHideEnabled(true);
		mainPanel = new FlowPanel();

		linkPanel = new FlowPanel();
		linkPanel.setStyleName("linkPanel");
		linkLabel = new Label();
		linkLabel.setStyleName("linkLabel");
		linkBox = new TextBox();
		linkBox.setReadOnly(true);
		linkBox.setText(this.shareURL);
		linkBox.addFocusHandler(new FocusHandler() {

			public void onFocus(FocusEvent event) {
				linkBox.selectAll();
			}
		});
		linkBox.setStyleName("linkBox");
		copyBtn = new StandardButton(localize("Copy"),
				app);
		copyBtn.setStyleName("copyButton");
		mainPanel.add(linkLabel);
		linkPanel.add(linkBox);
		linkPanel.add(copyBtn);
		mainPanel.add(linkPanel);
		
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("buttonPanel");
		printBtn = new StandardButton(
				SharedResources.INSTANCE.print_white(),
				localize("Print"), 24, app);
		printBtn.setStyleName("roundButton");
		exportImgBtn = new StandardButton(
				SharedResources.INSTANCE.file_download_white(),
				localize("exportImage"), 24, app);
		exportImgBtn.setStyleName("roundButton");
		buttonPanel.add(printBtn);
		buttonPanel.add(exportImgBtn);
		mainPanel.add(buttonPanel);

		add(mainPanel);
		setLabels();
	}

	public void onClick(Widget source) {
		// TODO
	}

	/**
	 * set button labels and dialog title
	 */
	public void setLabels() {
		// dialog title
		getCaption().setText(localize("Share"));
		linkLabel.setText(localize("Link"));
		copyBtn.setText(localize("Copy"));
		printBtn.setText(localize("Print"));
		exportImgBtn.setText(localize("exportImage"));
	}

	private String localize(String id) {
		return app.getLocalization().getMenu(id);
	}
}

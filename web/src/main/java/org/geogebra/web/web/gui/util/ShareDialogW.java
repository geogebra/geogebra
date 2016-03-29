package org.geogebra.web.web.gui.util;

import org.geogebra.web.html5.gui.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.dialog.DialogBoxW;
import org.geogebra.web.web.gui.images.AppResources;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ShareDialogW extends DialogBoxW implements ClickHandler {

	protected AppW app;
	private VerticalPanel contentPanel;
	private TabLayoutPanel tabPanel;
	private VerticalPanel linkPanel;
	private HorizontalPanel iconPanel;
	private FlowPanel copyLinkPanel;
	private VerticalPanel emailPanel;
	private VerticalPanel embedPanel;
	private FlowPanel buttonPanel;
	private Button btOK, btCancel;

	public ShareDialogW(final AppW app) {
		super(app.getPanel());
		this.app = app;
		this.addStyleName("GeoGebraShareDialog");
		this.setGlassEnabled(true);
		this.setVisible(true);
		center();

		this.getCaption().setText(app.getMenu("Share"));
		this.contentPanel = new VerticalPanel();

		this.contentPanel.add(getTabPanel());
		this.contentPanel.add(new HTML("<hr>"));
		this.contentPanel.add(getButtonPanel());
		this.add(this.contentPanel);

	}

	private TabLayoutPanel getTabPanel() {
		tabPanel = new TabLayoutPanel(30, Unit.PX);
		tabPanel.add(getLinkPanel(), app.getPlain("Link"));
		tabPanel.add(getEmailPanel(), app.getPlain("Email"));
		tabPanel.add(getEmbedPanel(), app.getPlain("Embed"));
		tabPanel.selectTab(0);

		return tabPanel;

	}

	private VerticalPanel getLinkPanel() {
		linkPanel = new VerticalPanel();
		linkPanel.setWidth("100%");
		linkPanel.setHeight("100%");

		linkPanel.add(getIconPanel());
		linkPanel.add(getCopyLinkPanel());

		return linkPanel;
	}

	private HorizontalPanel getIconPanel() {
		iconPanel = new HorizontalPanel();

		// Google+

		// Facebook
		Image imgFB = new NoDragImage(AppResources.INSTANCE.social_facebook().getSafeUri().asString());
		iconPanel.add(imgFB);
		// Twitter
		Image imgTW = new Image();
		iconPanel.add(imgTW);
		// Delicious
		// OneNote
		// Edmodo
		// Classroom
		// Email
		// Download

		return iconPanel;
	}

	private FlowPanel getCopyLinkPanel() {
		copyLinkPanel = new FlowPanel();

		Label lblLink = new Label(app.getPlain("Link") + " :");
		Label link = new Label("set link to copy here");
		Image copyToClipboardIcon = new NoDragImage(AppResources.INSTANCE.edit_copy().getSafeUri().asString());

		copyLinkPanel.add(lblLink);
		copyLinkPanel.add(link);
		copyLinkPanel.add(copyToClipboardIcon);

		return copyLinkPanel;
	}

	// TODO implement
	private VerticalPanel getEmailPanel() {
		emailPanel = new VerticalPanel();
		emailPanel.setWidth("100%");
		emailPanel.setHeight("100%");

		return emailPanel;
	}

	// TODO implement
	private VerticalPanel getEmbedPanel() {
		embedPanel = new VerticalPanel();
		embedPanel.setWidth("100%");
		embedPanel.setHeight("100%");

		return embedPanel;
	}

	private FlowPanel getButtonPanel() {

		btOK = new Button(app.getPlain("OK"));
		btOK.getElement().setAttribute("action", "OK");
		btOK.addClickHandler(this);

		btCancel = new Button(app.getPlain("Cancel"));
		btCancel.getElement().setAttribute("action", "Cancel");
		btCancel.addClickHandler(this);
		btCancel.addStyleName("cancelBtn");

		buttonPanel = new FlowPanel();
		buttonPanel.add(btOK);
		buttonPanel.add(btCancel);
		buttonPanel.addStyleName("DialogButtonPanel");

		return buttonPanel;
	}


	@Override
	public void onClick(ClickEvent event) {
		// TODO implement
		Object source = event.getSource();
		if (source == btOK) {
			hide();
		} else if (source == btCancel) {
			hide();
		}

	}
}

package org.geogebra.web.web.gui.util;

import org.geogebra.web.html5.gui.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.dialog.DialogBoxW;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.menubar.FileMenuW;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ShareDialogW extends DialogBoxW implements ClickHandler {

	protected AppW app;
	private VerticalPanel contentPanel;
	private TabLayoutPanel tabPanel;
	private VerticalPanel linkPanel;
	private HorizontalPanel iconPanel;
	private HorizontalPanel copyLinkPanel;
	private VerticalPanel emailPanel;
	// private HorizontalPanel imagePanel; for future use - to share images
	private FlowPanel buttonPanel;
	private Button btOK, btCancel;
	private String TUBEURL = "beta.geogebra.org/m/";
	private String sharingKey = "";

	public ShareDialogW(final AppW app) {
		super(app.getPanel());
		this.app = app;
		this.setGlassEnabled(true);
		if (app.getActiveMaterial() != null
				&& app.getActiveMaterial().getSharingKey() != null) {
			sharingKey = app.getActiveMaterial().getSharingKey();
		}

		this.getCaption().setText(app.getMenu("Share"));
		this.contentPanel = new VerticalPanel();
		this.contentPanel.add(getTabPanel());
		this.contentPanel.add(getButtonPanel());
		this.add(this.contentPanel);

		this.setVisible(true);
		this.center();
	}

	private TabLayoutPanel getTabPanel() {
		tabPanel = new TabLayoutPanel(30, Unit.PX);
		tabPanel.addStyleName("GeoGebraTabLayout");

		tabPanel.add(getLinkPanel(), app.getPlain("Link"));
		tabPanel.add(getEmailPanel(), app.getPlain("Email"));
		// tabPanel.add(getImagePanel(), app.getPlain("Image"));
		tabPanel.selectTab(0);

		return tabPanel;
	}

	private VerticalPanel getLinkPanel() {
		linkPanel = new VerticalPanel();
		linkPanel.addStyleName("GeoGebraLinkPanel");

		linkPanel.add(new Label(""));
		linkPanel.add(getIconPanel());
		linkPanel.add(getCopyLinkPanel());

		return linkPanel;
	}

	private HorizontalPanel getIconPanel() {
		iconPanel = new HorizontalPanel();
		iconPanel.addStyleName("GeoGebraIconPanel");

		// Geogebra
		NoDragImage geogebraimg = new NoDragImage(AppResources.INSTANCE
				.GeoGebraTube().getSafeUri().asString());
		PushButton geogebrabutton = new PushButton(geogebraimg,
				new ClickHandler() {

			public void onClick(ClickEvent event) {
						if (!FileMenuW.nativeShareSupported()) {
							app.uploadToGeoGebraTube();
						} else {
							app.getGgbApi().getBase64(true,
									FileMenuW.getShareStringHandler(app));
						}
			}

		});
		iconPanel.add(geogebrabutton);
		// iconPanel.add(geogebraimg);
		// Facebook
		iconPanel.add(new Anchor(new NoDragImage(AppResources.INSTANCE
				.social_facebook().getSafeUri().asString()).toString(), true,
				"https://www.facebook.com/sharer/sharer.php?u=" + TUBEURL
						+ sharingKey,
				"_blank"));

		// Twitter
		iconPanel.add(new NoDragImage(AppResources.INSTANCE.social_twitter().getSafeUri().asString()));

		// Google+
		Anchor gpluslink = new Anchor(new NoDragImage(AppResources.INSTANCE
				.social_google().getSafeUri().asString()).toString(), true,
				"https://plus.google.com/share?url=" + TUBEURL + sharingKey,
				"_blank");
		iconPanel.add(gpluslink);

		// Pinterest
		// iconPanel.add(new
		// NoDragImage(AppResources.INSTANCE.social_twitter().getSafeUri().asString()));
		// OneNote
		iconPanel.add(new NoDragImage(AppResources.INSTANCE.social_onenote().getSafeUri().asString()));
		// Edmodo
		iconPanel.add(new NoDragImage(AppResources.INSTANCE.social_edmodo().getSafeUri().asString()));
		// Classroom
		iconPanel.add(new NoDragImage(AppResources.INSTANCE.social_google_classroom().getSafeUri().asString()));

		return iconPanel;
	}

	private HorizontalPanel getCopyLinkPanel() {
		copyLinkPanel = new HorizontalPanel();
		copyLinkPanel.addStyleName("GeoGebraCopyLinkPanel");

		// Label lblLink = new Label(app.getPlain("Link") + ": ");
		TextBox link = new TextBox();
		link.setValue(TUBEURL + sharingKey);
		link.setReadOnly(true);
		Image copyToClipboardIcon = new NoDragImage(AppResources.INSTANCE.edit_copy().getSafeUri().asString());

		// copyLinkPanel.add(lblLink);
		copyLinkPanel.add(link);
		copyLinkPanel.add(copyToClipboardIcon);

		return copyLinkPanel;
	}

	private VerticalPanel getEmailPanel() {
		emailPanel = new VerticalPanel();
		emailPanel.addStyleName("GeoGebraEmailPanel");

		Label lblRecipient = new Label(app.getPlain("share_recipient") + ":");
		TextBox recipient = new TextBox();
		recipient.getElement().setPropertyString("placeholder", app.getPlain("share_to"));

		Label lblMessage = new Label(app.getPlain("share_message") + ":");
		TextArea message = new TextArea();
		message.getElement().setPropertyString("placeholder", app.getPlain("share_message_text"));
		message.setVisibleLines(3);

		emailPanel.add(lblRecipient);
		emailPanel.add(recipient);
		emailPanel.add(lblMessage);
		emailPanel.add(message);

		return emailPanel;
	}

	// TODO implement in the future - share images
	/*
	 * private HorizontalPanel getImagePanel() { imagePanel = new
	 * HorizontalPanel(); imagePanel.addStyleName("GeoGebraImagePanel");
	 * imagePanel.add(new Label(""));
	 * 
	 * return imagePanel; }
	 */
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

	// TODO implement
	@Override
	public void onClick(ClickEvent event) {

		Object source = event.getSource();
		if (source == btOK) {
			hide();
		} else if (source == btCancel) {
			hide();
		}

	}
}

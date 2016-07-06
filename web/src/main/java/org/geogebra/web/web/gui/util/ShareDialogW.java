package org.geogebra.web.web.gui.util;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.dialog.DialogBoxW;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.menubar.FileMenuW;
import org.geogebra.web.web.gui.view.spreadsheet.CopyPasteCutW;
import org.geogebra.web.web.move.ggtapi.models.MaterialCallback;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
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
	private Button btSendMail, btCancel;
	String sharingKey = "";
	private TextBox recipient;
	private TextArea message;

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
		this.add(this.contentPanel);
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

		btCancel = new Button(app.getPlain("Cancel"));
		// btCancel.getElement().setAttribute("action", "Cancel");
		btCancel.addClickHandler(this);
		btCancel.addStyleName("cancelBtn");

		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("DialogButtonPanel");
		buttonPanel.add(btCancel);
		linkPanel.add(buttonPanel);

		return linkPanel;
	}

	private HorizontalPanel getIconPanel() {
		iconPanel = new HorizontalPanel();
		iconPanel.addStyleName("GeoGebraIconPanel");

		// ShareDialog will be closed at clicking on icons
		ClickHandler closePopupHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		};

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
						hide();
			}

		});
		iconPanel.add(geogebrabutton);
		// iconPanel.add(geogebraimg);

		// Facebook
		Anchor facebooklink = new Anchor(new NoDragImage(AppResources.INSTANCE
				.social_facebook().getSafeUri().asString()).toString(), true,
				"https://www.facebook.com/sharer/sharer.php?u="
						+ GeoGebraConstants.TUBE_URL_SHORT
						+ sharingKey, "_blank");
		facebooklink.addClickHandler(closePopupHandler);
		iconPanel.add(facebooklink);

		// Twitter
		Anchor twitterlink = new Anchor(new NoDragImage(AppResources.INSTANCE
				.social_twitter().getSafeUri().asString()).toString(), true,
				"https://twitter.com/share?url="
						+ GeoGebraConstants.TUBE_URL_SHORT
						+ sharingKey,
				"_blank");
		twitterlink.addClickHandler(closePopupHandler);
		iconPanel.add(twitterlink);

		// Google+
		Anchor gpluslink = new Anchor(new NoDragImage(AppResources.INSTANCE
				.social_google().getSafeUri().asString()).toString(), true,
				"https://plus.google.com/share?url="
						+ GeoGebraConstants.TUBE_URL_SHORT + sharingKey,
				"_blank");
		gpluslink.addClickHandler(closePopupHandler);
		iconPanel.add(gpluslink);

		// Pinterest
		// iconPanel.add(new
		// NoDragImage(AppResources.INSTANCE.social_twitter().getSafeUri().asString()));

		// OneNote
		Anchor onenote = new Anchor(new NoDragImage(AppResources.INSTANCE
				.social_onenote().getSafeUri().asString()).toString(), true,
				"http://tube.geogebra.org/material/onenote/id/" + sharingKey,
				"_blank");
		onenote.addClickHandler(closePopupHandler);
		iconPanel.add(onenote);

		// Edmodo
		String source_desc = (app.getActiveMaterial() != null) ? "&source="
				+ app.getActiveMaterial().getId() + "&desc="
				+ app.getActiveMaterial().getDescription() : "";
		Anchor edmodolink = new Anchor(new NoDragImage(AppResources.INSTANCE
				.social_edmodo().getSafeUri().asString()).toString(), true,
				"http://www.edmodo.com/home?share=1 " + source_desc + "&url="
						+ GeoGebraConstants.TUBE_URL_SHORT + sharingKey,
				"_blank");
		edmodolink.addClickHandler(closePopupHandler);
		iconPanel.add(edmodolink);

		// Classroom

		Element head = Document.get().getElementsByTagName("head").getItem(0);
		ScriptElement scriptE = Document.get().createScriptElement();
		String scripttext = "window.___gcfg = {parsetags: 'explicit'};";
		scriptE.setInnerText(scripttext);
		head.appendChild(scriptE);

		ScriptElement scriptE2 = Document.get().createScriptElement();
		scriptE2.setSrc("https://apis.google.com/js/platform.js");
		head.appendChild(scriptE2);
		
		SimplePanel classroomcontentPanel = new SimplePanel();
		classroomcontentPanel.getElement().setId("shareggbmaterial_content");
		classroomcontentPanel.addStyleName("GeoGebraShareOnGClassroom");
		
		SimplePanel sharetoclassroomPanel = new SimplePanel();
		sharetoclassroomPanel.addStyleName("g-sharetoclassroom");
		sharetoclassroomPanel.getElement().setAttribute("data-size", "30");
		sharetoclassroomPanel.getElement().setAttribute("data-url",
				GeoGebraConstants.TUBE_URL_SHORT + sharingKey);
		
		classroomcontentPanel.add(sharetoclassroomPanel);
		final FlowPanel classroomPanel = new FlowPanel();
		classroomPanel.add(classroomcontentPanel);

		addCallback(scriptE2, new Callback<Void, Exception>() {

			public void onFailure(Exception reason) {
				Log.debug("onFailure - script injection");

			}

			public void onSuccess(Void result) {
				ScriptElement scriptE3 = Document.get().createScriptElement();
				scriptE3.setInnerText("gapi.sharetoclassroom.go(\"shareggbmaterial_content\");");
				classroomPanel.getElement().appendChild(scriptE3);
			}
		});

		iconPanel.add(classroomPanel);

		return iconPanel;
	}

	private static native void addCallback(JavaScriptObject scriptElement,
			Callback<Void, Exception> callback) /*-{
		scriptElement.onload = $entry(function() {
			if (callback) {
				callback.@com.google.gwt.core.client.Callback::onSuccess(Ljava/lang/Object;)(null);
			}
		});
	}-*/;

	private HorizontalPanel getCopyLinkPanel() {
		copyLinkPanel = new HorizontalPanel();
		copyLinkPanel.addStyleName("GeoGebraCopyLinkPanel");

		// Label lblLink = new Label(app.getPlain("Link") + ": ");

		final TextBox link = new TextBox();
		link.setValue(GeoGebraConstants.TUBE_URL_SHORT + sharingKey);
		link.setReadOnly(true);

		PushButton copyToClipboardIcon = new PushButton(new NoDragImage(
				AppResources.INSTANCE.edit_copy().getSafeUri().asString()),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						CopyToClipboard(GeoGebraConstants.TUBE_URL_SHORT
								+ sharingKey, link);
						link.selectAll();
					}
				});

		// copyLinkPanel.add(lblLink);
		copyLinkPanel.add(link);
		copyLinkPanel.add(copyToClipboardIcon);

		return copyLinkPanel;
	}

	static boolean CopyToClipboard(String value, TextBox link) {
		if (CopyPasteCutW.copyToSystemClipboard(value))
			return true;

		if (Browser.isInternetExplorer()) {
			CopyPasteCutW.copyToSystemClipboardIE(value);
			return true;
		}
		return false;
	}

	private VerticalPanel getEmailPanel() {
		emailPanel = new VerticalPanel();
		emailPanel.addStyleName("GeoGebraEmailPanel");

		Label lblRecipient = new Label(app.getPlain("share_recipient") + ":");
		recipient = new TextBox();
		recipient.getElement().setPropertyString("placeholder", app.getPlain("share_to"));

		Label lblMessage = new Label(app.getPlain("share_message") + ":");
		message = new TextArea();
		message.getElement().setPropertyString("placeholder", app.getPlain("share_message_text"));
		message.setVisibleLines(3);

		emailPanel.add(lblRecipient);
		emailPanel.add(recipient);
		emailPanel.add(lblMessage);
		emailPanel.add(message);

		btSendMail = new Button(app.getPlain("SendMail"));
		// btSendMail.getElement().setAttribute("action", "OK");
		btSendMail.addClickHandler(this);

		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("DialogButtonPanel");
		buttonPanel.add(btSendMail);
		emailPanel.add(buttonPanel);

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

	// TODO implement
	@Override
	public void onClick(ClickEvent event) {

		Object source = event.getSource();
		if (source == btSendMail) {
			Log.debug("send mail to: " + recipient.getText());
			app.getLoginOperation()
					.getGeoGebraTubeAPI()
					.shareMaterial(app.getActiveMaterial(),
							recipient.getText(), message.getText(),
							new MaterialCallback() {
								//
							});
			hide();
		} else if (source == btCancel) {
			hide();
		}

	}
}

package org.geogebra.web.shared;

import org.geogebra.common.main.Localization;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.ggtapi.models.MaterialCallback;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Share dialog for classic
 *
 */
public class ShareDialogW extends DialogBoxW implements ClickHandler {

	/**
	 * Application
	 */
	private VerticalPanel contentPanel;
	private HorizontalPanel iconPanel;
	private VerticalPanel emailPanel;
	// private HorizontalPanel imagePanel; for future use - to share images
	private Button btSendMail;
	private Button btCancel;
	private String shareURL = "";
	private TextBox recipient;
	private TextArea message;
	private Localization loc;
	private TextBox link;
	private Widget anchor;
	private Widget geogebraButton;

	/**
	 * Create a new share dialog.
	 * 
	 * @param app
	 *            application
	 * @param anchor
	 *            relative element
	 * @param geogebraButton
	 *            button for GGB sharing
	 * @param shareURL
	 *            shared URL
	 */
	public ShareDialogW(final AppW app, Widget anchor, Widget geogebraButton,
			String shareURL) {
		super(app.getPanel(), app);
		this.anchor = anchor;
		this.app = app;
		this.loc = app.getLocalization();
		this.shareURL = shareURL;
		this.geogebraButton = geogebraButton;
		this.setGlassEnabled(true);

		this.getCaption().setText(app.getLocalization().getMenu("Share"));
		this.contentPanel = new VerticalPanel();
		this.contentPanel.add(getTabPanel());
		this.add(this.contentPanel);
		addStyleName("shareDialog");
	}

	private TabPanel getTabPanel() {
		TabPanel tabPanel = new TabPanel();
		tabPanel.addStyleName("GeoGebraTabLayout");

		tabPanel.add(getLinkPanel(), loc.getMenu("Link"));
		tabPanel.add(getEmailPanel(), loc.getMenu("Email"));
		// tabPanel.add(getImagePanel(), loc.getMenu("Image"));
		tabPanel.selectTab(0);

		return tabPanel;
	}

	private VerticalPanel getLinkPanel() {
		VerticalPanel linkPanel = new VerticalPanel();
		linkPanel.addStyleName("GeoGebraLinkPanel");

		linkPanel.add(new Label(""));
		linkPanel.add(getIconPanel());
		linkPanel.add(getCopyLinkPanel());

		btCancel = new Button(loc.getMenu("Cancel"));
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
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		};

		// Geogebra
		if (geogebraButton != null) {
			iconPanel.add(geogebraButton);
		}
		// iconPanel.add(geogebraimg);

		// Facebook
		Anchor facebooklink = new Anchor(
				new NoDragImage(SharedResources.INSTANCE
				.social_facebook().getSafeUri().asString()).toString(), true,
				"https://www.facebook.com/sharer/sharer.php?u=" + getURL(),
				"_blank");
		facebooklink.addClickHandler(closePopupHandler);
		iconPanel.add(facebooklink);

		// Twitter
		Anchor twitterlink = new Anchor(new NoDragImage(SharedResources.INSTANCE
				.social_twitter().getSafeUri().asString()).toString(), true,
				"https://twitter.com/share?url="
						+ getURL(),
				"_blank");
		twitterlink.addClickHandler(closePopupHandler);
		iconPanel.add(twitterlink);

		// Google+
		Anchor gpluslink = new Anchor(new NoDragImage(SharedResources.INSTANCE
				.social_google().getSafeUri().asString()).toString(), true,
				"https://plus.google.com/share?url="
						+ getURL(),
				"_blank");
		gpluslink.addClickHandler(closePopupHandler);
		iconPanel.add(gpluslink);

		// Pinterest
		// iconPanel.add(new
		// NoDragImage(AppResources.INSTANCE.social_twitter().getSafeUri().asString()));

		// Edmodo
		Material activeMaterial = ((AppW) app).getActiveMaterial();
		String title = (activeMaterial == null
				|| StringUtil.empty(activeMaterial.getTitle()))
						? app.getKernel().getConstruction().getTitle()
						: activeMaterial.getTitle();
		String sourceDesc = (activeMaterial != null)
				? "&source=" + activeMaterial.getId() + "&desc=" + title : "";
		Anchor edmodolink = new Anchor(
				new NoDragImage(SharedResources.INSTANCE
				.social_edmodo().getSafeUri().asString()).toString(), true,
				"http://www.edmodo.com/home?share=1 " + sourceDesc + "&url="
						+ getURL(),
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
				getURL());
		
		classroomcontentPanel.add(sharetoclassroomPanel);
		final FlowPanel classroomPanel = new FlowPanel();
		classroomPanel.add(classroomcontentPanel);

		addCallback(scriptE2, new Callback<Void, Exception>() {

			@Override
			public void onFailure(Exception reason) {
				Log.debug("onFailure - script injection");

			}

			@Override
			public void onSuccess(Void result) {
				ScriptElement scriptE3 = Document.get().createScriptElement();
				scriptE3.setInnerText("gapi.sharetoclassroom.go(\"shareggbmaterial_content\");");
				classroomPanel.getElement().appendChild(scriptE3);
			}
		});

		iconPanel.add(classroomPanel);

		return iconPanel;
	}

	private String getURL() {
		return shareURL;
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
		HorizontalPanel copyLinkPanel = new HorizontalPanel();
		copyLinkPanel.addStyleName("GeoGebraCopyLinkPanel");

		// Label lblLink = new Label(loc.getMenu("Link") + ": ");

		link = new TextBox();
		link.setValue(getURL());
		link.setReadOnly(true);

		PushButton copyToClipboardIcon = new PushButton(new NoDragImage(
				SharedResources.INSTANCE.edit_copy().getSafeUri().asString()),
				new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						copyLinkToClipboard();
					}
				});

		// copyLinkPanel.add(lblLink);
		copyLinkPanel.add(link);
		copyLinkPanel.add(copyToClipboardIcon);

		return copyLinkPanel;
	}

	/**
	 * Copy sharing link to clipboard
	 */
	protected void copyLinkToClipboard() {
		app.copyTextToSystemClipboard(getURL());
		link.selectAll();
	}

	private VerticalPanel getEmailPanel() {
		emailPanel = new VerticalPanel();
		emailPanel.addStyleName("GeoGebraEmailPanel");

		Label lblRecipient = new Label(loc.getMenu("share_recipient") + ":");
		recipient = new TextBox();
		recipient.getElement().setPropertyString("placeholder",
				loc.getMenu("share_to"));

		Label lblMessage = new Label(loc.getMenu("share_message") + ":");
		message = new TextArea();
		message.getElement().setPropertyString("placeholder",
				loc.getMenu("share_message_text"));
		message.setVisibleLines(3);

		emailPanel.add(lblRecipient);
		emailPanel.add(recipient);
		emailPanel.add(lblMessage);
		emailPanel.add(message);

		btSendMail = new Button(loc.getMenu("Send"));
		// btSendMail.getElement().setAttribute("action", "OK");
		btSendMail.addClickHandler(this);

		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("DialogButtonPanel");
		buttonPanel.add(btSendMail);
		emailPanel.add(buttonPanel);

		return emailPanel;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		GlobalHeader.INSTANCE.selectShareButton(visible);
	}

	@Override
	public void hide() {
		super.hide();
		GlobalHeader.INSTANCE.selectShareButton(false);
	}

	@Override
	public void center() {
		super.center();
		if (anchor != null) {
			showRelativeTo(anchor);
		}
		if (link != null) {
			link.setFocus(true);
			link.selectAll();
		}
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
					.shareMaterial(((AppW) app).getActiveMaterial(),
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

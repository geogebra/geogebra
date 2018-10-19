package org.geogebra.web.shared;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.SaveController.SaveListener;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author csilla
 * 
 *         Joint share dialog for mow (group + link sharing)
 *
 */
public class ShareDialogMow2 extends DialogBoxW
		implements FastClickHandler, SetLabels, SaveListener {
	private AppW appW;
	private FlowPanel dialogContent;
	private Label selGroupLbl;
	private FlowPanel groupPanel;
	private ScrollPanel scrollPanel;
	private FlowPanel noGroupPanel;
	private Label noGroupsLbl;
	private Label noGroupsHelpLbl;
	private FlowPanel shareByLinkPanel;
	private Label linkShareOnOffLbl;
	private Label linkShareHelpLbl;
	private ComponentSwitch shareSwitch;
	private FlowPanel linkPanel;
	private TextBox linkBox;
	/** true if linkBox is focused */
	protected boolean linkBoxFocused = true;
	private StandardButton copyBtn;
	private FlowPanel buttonPanel;
	private StandardButton cancelBtn;
	private StandardButton saveBtn;
	private String shareURL;
	private Material material;
	private MaterialCallbackI callback;
	private java.util.HashMap<String, Boolean> changedGroups = new java.util.HashMap<>();
	private List<String> sharedGroups = new ArrayList<>();

	/**
	 * @param app
	 *            see {@link AppW}
	 * @param shareURL
	 *            share URL
	 * @param mat
	 *            active material
	 */
	public ShareDialogMow2(AppW app, String shareURL, Material mat) {
		super(app.getPanel(), app);
		this.appW = app;
		this.shareURL = shareURL;
		this.material = mat == null ? app.getActiveMaterial() : mat;
		buildGui();
	}

	/**
	 * @return textfield containing share link
	 */
	public TextBox getLinkBox() {
		return linkBox;
	}

	/**
	 * @param sharedGroups
	 *            list of group with which the material was shared
	 */
	public void setSharedGroups(List<String> sharedGroups) {
		Log.debug("SHARED GROUP LIST: " + sharedGroups);
		this.sharedGroups = sharedGroups;
	}

	/**
	 * @return true if share by link is active
	 */
	public boolean isShareLinkOn() {
		return shareSwitch.isSwitchOn();
	}

	private void buildGui() {
		addStyleName("shareDialogMow");
		dialogContent = new FlowPanel();
		getGroupsSharedWith();
		// get list of groups of user
		ArrayList<String> groupNames = app.getLoginOperation().getModel()
				.getUserGroups();
		// ONLY FOR TESTING

		/*
		 * groupNames = new ArrayList<>(Arrays.asList("group 1", "group 2",
		 * "group 3", "group 4", "group 5"));
		 */
		// user has no groups
		if (groupNames.isEmpty()) {
			buildNoGroupPanel();
		}
		// show groups of user
		else {
			buildGroupPanel(groupNames);
		}
		buildShareByLinkPanel();
		buildButtonPanel();
		add(dialogContent);
		setLabels();
	}

	private void buildNoGroupPanel() {
		noGroupPanel = new FlowPanel();
		noGroupPanel.addStyleName("noGroupPanel");
		SimplePanel groupImgHolder = new SimplePanel();
		groupImgHolder.addStyleName("groupImgHolder");
		NoDragImage groupImg = new NoDragImage(
				SharedResources.INSTANCE.groups(), 48);
		groupImgHolder.add(groupImg);
		noGroupPanel.add(groupImgHolder);
		noGroupsLbl = new Label();
		noGroupsLbl.setStyleName("noGroupsLbl");
		noGroupsHelpLbl = new Label();
		noGroupsHelpLbl.setStyleName("noGroupsHelpLbl");
		noGroupPanel.add(noGroupsLbl);
		noGroupPanel.add(noGroupsHelpLbl);
		dialogContent.add(noGroupPanel);
	}

	private void buildGroupPanel(ArrayList<String> groupNames) {
		selGroupLbl = new Label();
		selGroupLbl.setStyleName("selGrLbl");
		dialogContent.add(selGroupLbl);
		groupPanel = new FlowPanel();
		groupPanel.addStyleName("groupPanel");
		scrollPanel = new ScrollPanel();
		groupPanel.add(scrollPanel);
		FlowPanel groups = new FlowPanel();
		// first add button for groups with which material was already shared
		for (String sharedGroup : sharedGroups) {
			groups.add(new GroupButtonMow(appW, sharedGroup, true,
					new AsyncOperation<AbstractMap.SimpleEntry<String, Boolean>>() {

						public void callback(SimpleEntry<String, Boolean> obj) {
							updateChangedGroupList(obj);
						}
					}));
		}
		// then add other existent groups of user
		for (String group : groupNames) {
			if (!containsSharedGroupList(group)) {
				groups.add(new GroupButtonMow(appW, group, false,
						new AsyncOperation<AbstractMap.SimpleEntry<String, Boolean>>() {

							public void callback(
									SimpleEntry<String, Boolean> obj) {
								updateChangedGroupList(obj);
							}
						}));
			}
		}
		scrollPanel.add(groups);
		dialogContent.add(groupPanel);
	}
	
	private boolean containsSharedGroupList(String group) {
		for (String str: sharedGroups) {
			if (str.equals(group)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param obj
	 *            pair containing
	 * 
	 */
	public void updateChangedGroupList(SimpleEntry<String, Boolean> obj) {
		changedGroups.put(obj.getKey(), obj.getValue());
		Log.debug(
				"SET GROUP: " + obj.getKey() + " to value: " + obj.getValue());
	}

	private void buildShareByLinkPanel() {
		shareByLinkPanel = new FlowPanel();
		shareByLinkPanel.addStyleName("shareByLink");
		NoDragImage linkImg = new NoDragImage(
				SharedResources.INSTANCE.mow_link_black(), 24);
		linkImg.addStyleName("linkImg");
		shareByLinkPanel.add(linkImg);
		FlowPanel textPanel = new FlowPanel();
		textPanel.addStyleName("textPanel");
		linkShareOnOffLbl = new Label();
		linkShareOnOffLbl.setStyleName("linkShareOnOff");
		linkShareHelpLbl = new Label();
		linkShareHelpLbl.setStyleName("linkShareHelp");
		textPanel.add(linkShareOnOffLbl);
		textPanel.add(linkShareHelpLbl);
		shareByLinkPanel.add(textPanel);
		shareSwitch = new ComponentSwitch(isMatShared(material),
				new AsyncOperation<Boolean>() {

			public void callback(Boolean obj) {
				onSwitch(obj.booleanValue());
			}
		});
		shareByLinkPanel.add(shareSwitch);
		buildLinkPanel();
		dialogContent.add(shareByLinkPanel);
	}

	private static boolean isMatShared(Material mat) {
		if (mat != null) {
			return "S".equals(mat.getVisibility());
		}
		return false;
	}

	private void buildLinkPanel() {
		linkPanel = new FlowPanel();
		linkPanel.setStyleName("linkPanel");
		linkBox = new TextBox();
		linkBox.setReadOnly(true);
		linkBox.setText(shareURL);
		linkBox.setStyleName("linkBox");
		addLinkBoxHandlers();
		// build and add copy button
		copyBtn = new StandardButton(app.getLocalization().getMenu("Copy"),
				app);
		copyBtn.setStyleName("copyButton");
		copyBtn.addFastClickHandler(this);
		linkPanel.add(linkBox);
		linkPanel.add(copyBtn);
		shareByLinkPanel.add(linkPanel);
		linkPanel.setVisible(isShareLinkOn());
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
					getLinkBox().setFocus(true);
					getLinkBox().setSelectionRange(0, 0);
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

	/**
	 * update switch dependent UI
	 * 
	 * @param isSwitchOn
	 *            true if switch is on
	 */
	public void onSwitch(boolean isSwitchOn) {
		linkShareOnOffLbl
				.setText(isShareLinkOn() ? "linkShareOn" : "linkShareOff");
		linkShareHelpLbl.setText(app.getLocalization().getMenu(isShareLinkOn()
				? "SharedLinkHelpTxt" : "NotSharedLinkHelpTxt"));
		linkPanel.setVisible(isSwitchOn);
		if (isSwitchOn) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					getLinkBox().selectAll();
					getLinkBox().setFocus(true);
				}
			});
		}
	}

	private void buildButtonPanel() {
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		cancelBtn = addButton("Cancel");
		saveBtn = addButton("Save");
		dialogContent.add(buttonPanel);
	}

	private StandardButton addButton(String transKey) {
		StandardButton btn = new StandardButton(
				appW.getLocalization().getMenu(transKey), appW);
		btn.addFastClickHandler(this);
		buttonPanel.add(btn);
		return btn;
	}

	@Override
	public void setLabels() {
		getCaption().setText(app.getLocalization()
				.getMenu("Share"));
		if (selGroupLbl != null) {
			selGroupLbl
				.setText(app.getLocalization().getMenu("shareGroupHelpText"));
		}
		cancelBtn.setText(app.getLocalization().getMenu("Cancel"));
		saveBtn.setText(appW.getLocalization().getMenu("Save"));
		if (noGroupsLbl != null && noGroupsHelpLbl != null) {
			noGroupsLbl.setText(app.getLocalization().getMenu("NoGroups"));
			noGroupsHelpLbl
					.setText(app.getLocalization().getMenu("NoGroupShareTxt"));
		}
		linkShareOnOffLbl
				.setText(isShareLinkOn() ? "linkShareOn" : "linkShareOff");
		linkShareHelpLbl
				.setText(
						app.getLocalization()
								.getMenu(isShareLinkOn() ? "SharedLinkHelpTxt"
										: "NotSharedLinkHelpTxt"));
		copyBtn.setText(app.getLocalization().getMenu("Copy"));
	}

	@Override
	public void onClick(Widget source) {
		if (source == cancelBtn) {
			hide();
		} else if (source == saveBtn) {
			if (shareSwitch.isSwitchOn()) {
				// set from private -> shared
				if (material != null && "P".equals(material.getVisibility())) {
					app.getLoginOperation().getGeoGebraTubeAPI().uploadMaterial(
							material.getSharingKeyOrId(), "S",
							material.getTitle(), null, callback,
							material.getType());
					material.setVisibility("S");
				}
			} else {
				// set from shared -> private
				if (material != null && "S".equals(material.getVisibility())) {
					app.getLoginOperation().getGeoGebraTubeAPI().uploadMaterial(
							material.getSharingKeyOrId(), "P",
							material.getTitle(), null, callback,
							material.getType());
					material.setVisibility("P");
				}
			}
			Log.debug("SHARE WITH GROUP START");
			Log.debug("NR TO SHARE: " + changedGroups.size());
			shareWithGroups(new AsyncOperation<Boolean>() {

				@Override
				public void callback(Boolean obj) {
					showTooltip(obj);
				}
			});
			hide();
		} else if (source == copyBtn) {
			linkBoxFocused = false;
			app.copyTextToSystemClipboard(linkBox.getText());
			focusLinkBox();
			hide();
		}
	}

	@Override
	public void show() {
		super.show();
		super.center();
	}

	/**
	 * @param materialCallbackI
	 *            callback for visibility change
	 */
	public void setCallback(MaterialCallbackI materialCallbackI) {
		this.callback = materialCallbackI;
	}

	/**
	 * @param groupCallback
	 *            callback for share with group
	 */
	protected void shareWithGroups(AsyncOperation<Boolean> groupCallback) {
		for (String group : changedGroups.keySet()) {
			Log.debug("SHARE WITH GROUP: " + group + " is shared: "
					+ changedGroups.get(group));
			appW.getLoginOperation().getGeoGebraTubeAPI().setShared(material,
					group, changedGroups.get(group), groupCallback);
		}
	}

	/**
	 * 
	 */
	protected void getGroupsSharedWith() {
		appW.getLoginOperation().getGeoGebraTubeAPI()
				.getGroups(material.getSharingKeyOrId(),
						new AsyncOperation<List<String>>() {

							@Override
							public void callback(List<String> obj) {
								if (obj != null) {
									setSharedGroups(obj);
								}
							}
						});
	}

	/**
	 * @param success
	 *            shared with group successful or not
	 */
	protected void showTooltip(Boolean success) {
		ToolTipManagerW.sharedInstance().showBottomMessage(
				appW.getLocalization()
						.getPlain(success.booleanValue() ? "GroupShareOk"
								: "GroupShareFail",
								String.valueOf(changedGroups.size()) + appW
										.getLocalization().getMenu(" Group")),
				true, appW);
	}
}

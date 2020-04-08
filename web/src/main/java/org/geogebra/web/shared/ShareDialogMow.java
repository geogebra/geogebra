package org.geogebra.web.shared;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.SaveController.SaveListener;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.laf.VendorSettings;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.ComponentLinkBox;
import org.geogebra.web.shared.components.ComponentSwitch;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author csilla
 * 
 *         Joint share dialog for mow (group + link sharing)
 *
 */
public class ShareDialogMow extends DialogBoxW
		implements FastClickHandler, SetLabels, SaveListener {
	private AppW appW;
	private Localization localization;
	private Label selGroupLbl;
	private ScrollPanel scrollPanel;
	private Label noGroupsLbl;
	private Label noGroupsHelpLbl;
	private Label linkShareOnOffLbl;
	private Label linkShareHelpLbl;
	private ComponentSwitch shareSwitch;
	private FlowPanel linkPanel;
	private ComponentLinkBox linkBox;
	private StandardButton copyBtn;
	private StandardButton cancelBtn;
	private StandardButton saveBtn;
	private Material material;
	private MaterialCallbackI callback;
	private java.util.HashMap<String, Boolean> changedGroups = new java.util.HashMap<>();
	private List<String> sharedGroups = new ArrayList<>();
	private Label sharingAvailableInfo;

	/**
	 * @param app
	 *            see {@link AppW}
	 * @param shareURL
	 *            share URL
	 * @param mat
	 *            active material
	 */
	public ShareDialogMow(AppW app, String shareURL, Material mat) {
		super(app.getPanel(), app);
		this.appW = app;
		this.localization = app.getLocalization();
		this.material = mat == null ? app.getActiveMaterial() : mat;
		buildGui(shareURL);
	}

	/**
	 * @return text field containing share link
	 */
	public ComponentLinkBox getLinkBox() {
		return linkBox;
	}

	/**
	 * @param sharedGroupList
	 *            list of group with which the material was shared
	 */
	public void updateOnSharedGroups(List<String> sharedGroupList) {
		this.sharedGroups = sharedGroupList;
		ArrayList<String> groupNames = app.getLoginOperation().getModel()
				.getUserGroups();
		scrollPanel.clear();
		FlowPanel groups = new FlowPanel();
		// first add button for groups with which material was already shared
		for (String sharedGroup : sharedGroups) {
			addGroup(groups, sharedGroup, true);
		}

		// then add other existent groups of user
		for (String group : groupNames) {
			if (!containsSharedGroupList(group)) {
				addGroup(groups, group, false);
			}
		}
		scrollPanel.add(groups);
		center();
	}

	private void addGroup(FlowPanel groupsPanel, String groupStr,
			boolean selected) {
		groupsPanel.add(new GroupButtonMow(appW, groupStr, selected,
				new AsyncOperation<AbstractMap.SimpleEntry<String, Boolean>>() {

					@Override
					public void callback(SimpleEntry<String, Boolean> obj) {
						updateChangedGroupList(obj);
					}
				}));
	}

	/**
	 * @return true if share by link is active
	 */
	public boolean isShareLinkOn() {
		return shareSwitch.isSwitchOn();
	}

	private void buildGui(String shareURL) {
		addStyleName("shareDialogMow");
		FlowPanel dialogContent = new FlowPanel();
		getGroupsSharedWith();
		// get list of groups of user
		ArrayList<String> groupNames = app.getLoginOperation().getModel()
				.getUserGroups();
		// user has no groups
		if (groupNames.isEmpty()) {
			buildNoGroupPanel(dialogContent);
		}
		// show groups of user
		else {
			buildGroupPanel(dialogContent);
		}
		buildShareByLinkPanel(dialogContent, shareURL);
		buildSharingAvailableInfo(dialogContent);
		buildButtonPanel(dialogContent);
		add(dialogContent);
		setLabels();
	}

	private void buildNoGroupPanel(FlowPanel dialogContent) {
		FlowPanel noGroupPanel = new FlowPanel();
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

	private void buildGroupPanel(FlowPanel dialogContent) {
		selGroupLbl = new Label();
		selGroupLbl.setStyleName("selGrLbl");
		dialogContent.add(selGroupLbl);
		FlowPanel groupPanel = new FlowPanel();
		groupPanel.addStyleName("groupPanel");
		scrollPanel = new ScrollPanel();
		groupPanel.add(scrollPanel);
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
	}

	private void buildShareByLinkPanel(FlowPanel dialogContent,
			String shareURL) {
		FlowPanel shareByLinkPanel = new FlowPanel();
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

					@Override
					public void callback(Boolean obj) {
						onSwitch(obj.booleanValue());
					}
		});
		shareByLinkPanel.add(shareSwitch);
		buildLinkPanel(shareByLinkPanel, shareURL);
		dialogContent.add(shareByLinkPanel);
	}

	private void buildSharingAvailableInfo(FlowPanel dialogContent) {
		sharingAvailableInfo = new Label();
		sharingAvailableInfo.setStyleName("shareLinkAvailableInfo");
		dialogContent.add(sharingAvailableInfo);
	}

	private static boolean isMatShared(Material mat) {
		if (mat != null) {
			return "S".equals(mat.getVisibility());
		}
		return false;
	}

	private void buildLinkPanel(FlowPanel shareByLinkPanel, String shareURL) {
		linkPanel = new FlowPanel();
		linkPanel.setStyleName("linkPanel");
		linkBox = new ComponentLinkBox(true, shareURL, "linkBox");
		// build and add copy button
		copyBtn = new StandardButton(localization.getMenu("Copy"),
				app);
		copyBtn.setStyleName("copyButton");
		copyBtn.addFastClickHandler(this);
		linkPanel.add(linkBox);
		linkPanel.add(copyBtn);
		shareByLinkPanel.add(linkPanel);
		linkPanel.setVisible(isShareLinkOn());
	}

	/**
	 * update switch dependent UI
	 * 
	 * @param isSwitchOn
	 *            true if switch is on
	 */
	public void onSwitch(boolean isSwitchOn) {
		linkShareOnOffLbl
				.setText(localization.getMenu(
						isShareLinkOn() ? "linkShareOn" : "linkShareOff"));
		linkShareHelpLbl.setText(localization.getMenu(getLinkShareHelpLabelTextKey()));
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

	private String getLinkShareHelpLabelTextKey() {
		if (isShareLinkOn()) {
			VendorSettings settings = appW.getVendorSettings();
			return settings.getMenuLocalizationKey("SharedLinkHelpTxt");
		}
		return "NotSharedLinkHelpTxt";
	}

	private void buildButtonPanel(FlowPanel dialogContent) {
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		cancelBtn = addButton(buttonPanel, "Cancel");
		saveBtn = addButton(buttonPanel, "Save");
		dialogContent.add(buttonPanel);
	}

	private StandardButton addButton(FlowPanel buttonPanel, String transKey) {
		StandardButton btn = new StandardButton(
				appW.getLocalization().getMenu(transKey), appW);
		btn.addFastClickHandler(this);
		buttonPanel.add(btn);
		return btn;
	}

	@Override
	public void setLabels() {
		getCaption().setText(localization
				.getMenu("Share"));
		if (selGroupLbl != null) {
			selGroupLbl
				.setText(localization.getMenu("shareGroupHelpText"));
		}
		cancelBtn.setText(localization.getMenu("Cancel"));
		saveBtn.setText(appW.getLocalization().getMenu("Save"));
		if (noGroupsLbl != null && noGroupsHelpLbl != null) {
			noGroupsLbl.setText(localization.getMenu("NoGroups"));
			noGroupsHelpLbl
					.setText(localization.getMenu("NoGroupShareTxt"));
		}
		linkShareOnOffLbl
				.setText(localization.getMenu(
						isShareLinkOn() ? "linkShareOn" : "linkShareOff"));
		linkShareHelpLbl.setText(localization.getMenu(getLinkShareHelpLabelTextKey()));
		sharingAvailableInfo
				.setText(localization.getMenu("SharingAvailableMow"));

		copyBtn.setText(localization.getMenu("Copy"));
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
			shareWithGroups(new AsyncOperation<Boolean>() {

				@Override
				public void callback(Boolean obj) {
					showTooltip(obj);
				}
			});
			hide();
		} else if (source == copyBtn) {
			linkBox.setFocused(false);
			app.copyTextToSystemClipboard(linkBox.getText());
			linkBox.focus();
			ToolTipManagerW.sharedInstance()
			    .showBottomMessage(appW.getLocalization()
			    .getMenu("linkCopyClipboard"), true, appW);
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
		for (Entry<String, Boolean> group : changedGroups.entrySet()) {
			appW.getLoginOperation().getGeoGebraTubeAPI().setShared(material,
					group.getKey(), group.getValue(), groupCallback);
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
									updateOnSharedGroups(obj);
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
						.getMenu(success.booleanValue() ? "GroupShareOk"
								: "GroupShareFail"),
				true, appW);
	}
}

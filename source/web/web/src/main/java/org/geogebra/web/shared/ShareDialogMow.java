package org.geogebra.web.shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MaterialParameters;
import org.geogebra.common.main.SaveController.SaveListener;
import org.geogebra.common.move.ggtapi.GroupIdentifier;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.MaterialRestAPI;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.laf.VendorSettings;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.FastClickHandler;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.components.ComponentLinkBox;
import org.geogebra.web.shared.components.ComponentSwitch;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.ScrollPanel;
import org.gwtproject.user.client.ui.SimplePanel;
import org.gwtproject.user.client.ui.Widget;

/**
 *  Joint share dialog for mow (group + link sharing)
 */
public class ShareDialogMow extends ComponentDialog
		implements FastClickHandler, SaveListener {
	private Localization localization;
	private ScrollPanel scrollPanel;
	private Label linkShareOnOffLbl;
	private Label linkShareHelpLbl;
	private ComponentSwitch shareSwitch;
	private ComponentSwitch multiuserSwitch;
	private FlowPanel multiuserSharePanel;
	private FlowPanel linkPanel;
	private ComponentLinkBox linkBox;
	private StandardButton copyBtn;
	private Material material;
	private MaterialCallbackI callback;
	private HashMap<GroupIdentifier, Boolean> changedGroups = new HashMap<>();
	private List<GroupIdentifier> sharedGroups = new ArrayList<>();
	private int sharedWithGroupCounter = 0;

	/**
	 * @param app
	 *            see {@link AppW}
	 * @param data
	 *            dialog translation keys
	 * @param shareURL
	 *            share URL
	 * @param mat
	 *            active material
	 */
	public ShareDialogMow(AppW app, DialogData data, String shareURL, Material mat) {
		super(app, data, false, true);
		this.localization = app.getLocalization();
		this.material = mat == null ? app.getActiveMaterial() : mat;
		addStyleName("shareDialogMow");
		buildContent(shareURL);
		setAction();
	}

	private void setAction() {
		setOnPositiveAction(() -> {
			if (isShareLinkOn()) {
				// set from private -> shared
				if (material != null && "P".equals(material.getVisibility())) {
					updateMaterial("S");
					return;
				}
			} else {
				// set from shared -> private
				if (material != null && "S".equals(material.getVisibility())) {
					updateMaterial("P");
					return;
				}
			}
			updateMaterial(material.getVisibility());
		});
	}

	private boolean isMultiuserSwitchOn() {
		return multiuserSwitch != null && multiuserSwitch.isSwitchOn()
				&& !multiuserSharePanel.getElement().hasClassName("disabled");
	}

	private void updateMaterial(String visibility) {
		boolean isMultiuser = isMultiuserSwitchOn();
		app.getLoginOperation().getGeoGebraTubeAPI().uploadMaterial(
				material.getSharingKeySafe(), visibility,
				material.getTitle(), null, callback,
				material.getType(), isMultiuser, new MaterialParameters(app));
		Material activeMaterial = app.getActiveMaterial();
		boolean currentlyEditing = activeMaterial != null
				&& material.getSharingKeySafe().equals(activeMaterial.getSharingKeySafe());
		if (material.isMultiuser() && !isMultiuser) {
			app.getShareController().saveAndTerminateMultiuser(material, callback);
		} else if (!material.isMultiuser() && isMultiuser && currentlyEditing) {
			app.getShareController().startMultiuser(material.getSharingKeySafe());
		}
		material.setVisibility(visibility);
		material.setMultiuser(isMultiuser);
		if (currentlyEditing) { // synchronize changes from Open view to active material
			activeMaterial.setVisibility(visibility);
			activeMaterial.setMultiuser(isMultiuser);
		}
		shareWithGroups(this::onGroupShareChanged);
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
	public void updateOnSharedGroups(List<GroupIdentifier> sharedGroupList) {
		if (sharedGroupList == null) {
			return;
		}
		this.sharedGroups = sharedGroupList;
		ArrayList<GroupIdentifier> groupNames = app.getLoginOperation().getModel()
				.getUserGroups();
		scrollPanel.clear();
		FlowPanel groups = new FlowPanel();
		// first add button for groups with which material was already shared
		for (GroupIdentifier sharedGroup : sharedGroups) {
			addGroup(groups, sharedGroup, true);
			sharedWithGroupCounter++;
		}

		// then add other existent groups of user
		for (GroupIdentifier group : groupNames) {
			if (!sharedGroups.contains(group)) {
				addGroup(groups, group, false);
			}
		}
		scrollPanel.add(groups);
		centerAndResize(((AppW) app).getAppletFrame().getKeyboardHeight());

		updateMultiuserSharePanel();
	}

	private void addGroup(FlowPanel groupsPanel, GroupIdentifier groupDesc, boolean shared) {
		groupsPanel.add(new GroupButtonMow(groupDesc, shared,
				this::updateChangedGroupList));
	}

	/**
	 * @return true if share by link is active
	 */
	public boolean isShareLinkOn() {
		return shareSwitch.isSwitchOn();
	}

	private void buildContent(String shareURL) {
		FlowPanel dialogContent = new FlowPanel();
		getGroupsSharedWith();
		// get list of groups of user
		ArrayList<GroupIdentifier> groupNames = app.getLoginOperation().getModel()
				.getUserGroups();
		// user has no groups
		if (groupNames.isEmpty()) {
			buildNoGroupPanel(dialogContent);
		} else { // show groups of user
			buildGroupPanel(dialogContent);
		}
		buildShareByLinkPanel(dialogContent, shareURL);
		if (!StringUtil.empty(((AppW) app).getAppletParameters().getParamMultiplayerUrl())) {
			buildMultiuserPanel(dialogContent);
		}
		buildSharingAvailableInfo(dialogContent);
		addDialogContent(dialogContent);
	}

	private void buildMultiuserPanel(FlowPanel dialogContent) {
		multiuserSwitch = new ComponentSwitch(material.isMultiuser(), null);
		Label multiuserShareLbl = BaseWidgetFactory.INSTANCE.newPrimaryText(
				localization.getMenu("shareDialog.multiUser"), "linkShareOnOff");
		Label multiuserHelpLbl = BaseWidgetFactory.INSTANCE.newSecondaryText(
				localization.getMenu("shareDialog.multiUserHelp"), "linkShareHelp");

		multiuserSharePanel = buildSwitcherPanel(dialogContent, multiuserSwitch,
				SharedResources.INSTANCE.groups(), multiuserShareLbl, multiuserHelpLbl, null);
	}

	private boolean isSharedGroupOrLink() {
		return isShareLinkOn() || sharedWithGroupCounter > 0;
	}

	private void buildShareByLinkPanel(FlowPanel dialogContent, String shareURL) {
		shareSwitch = new ComponentSwitch(isMatShared(material), this::onSwitch);
		linkShareOnOffLbl = BaseWidgetFactory.INSTANCE.newPrimaryText(localization.getMenu(
				isShareLinkOn() ? "linkShareOn" : "linkShareOff"), "linkShareOnOff");
		linkShareHelpLbl = BaseWidgetFactory.INSTANCE.newSecondaryText(
				localization.getMenu(getLinkShareHelpLabelTextKey()), "linkShareHelp");

		buildSwitcherPanel(dialogContent, shareSwitch, SharedResources.INSTANCE.mow_link_black(),
				linkShareOnOffLbl, linkShareHelpLbl, shareURL);
	}

	private FlowPanel buildSwitcherPanel(FlowPanel dialogContent, ComponentSwitch switcher,
			SVGResource icon, Label label, Label helpMsg, String shareURL) {
		FlowPanel shareByLinkPanel = new FlowPanel();
		shareByLinkPanel.addStyleName("shareByLink");

		FlowPanel switcherPanel = new FlowPanel();
		switcherPanel.addStyleName("switcherPanel");

		NoDragImage linkImg = new NoDragImage(icon, 24);
		linkImg.addStyleName("linkImg");
		switcherPanel.add(linkImg);

		FlowPanel textPanel = new FlowPanel();
		textPanel.addStyleName("textPanel");

		textPanel.add(label);
		textPanel.add(helpMsg);
		switcherPanel.add(textPanel);
		switcherPanel.add(switcher);

		shareByLinkPanel.add(switcherPanel);
		if (shareURL != null && !shareURL.isEmpty()) {
			buildLinkPanel(shareByLinkPanel, shareURL);
		}
		dialogContent.add(shareByLinkPanel);

		return shareByLinkPanel;
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
		Label noGroupsLbl = BaseWidgetFactory.INSTANCE.newPrimaryText(
				localization.getMenu("NoGroups"), "noGroupsLbl");
		Label noGroupsHelpLbl = BaseWidgetFactory.INSTANCE.newSecondaryText(
				localization.getMenu("NoGroupShareTxt"), "noGroupsHelpLbl");
		noGroupPanel.add(noGroupsLbl);
		noGroupPanel.add(noGroupsHelpLbl);
		dialogContent.add(noGroupPanel);
	}

	private void buildGroupPanel(FlowPanel dialogContent) {
		Label selGroupLbl = BaseWidgetFactory.INSTANCE.newSecondaryText(
				localization.getMenu("shareGroupHelpText"), "selGrLbl");
		dialogContent.add(selGroupLbl);
		FlowPanel groupPanel = new FlowPanel();
		groupPanel.addStyleName("groupPanel");
		scrollPanel = new ScrollPanel();
		groupPanel.add(scrollPanel);
		dialogContent.add(groupPanel);
	}

	protected void updateChangedGroupList(GroupIdentifier groupID, Boolean shared) {
		if (changedGroups.containsKey(groupID)) {
			changedGroups.remove(groupID);
		} else {
			changedGroups.put(groupID, shared);
		}

		if (shared.booleanValue()) {
			sharedWithGroupCounter++;
		} else {
			sharedWithGroupCounter--;
		}

		updateMultiuserSharePanel();
	}

	private void buildSharingAvailableInfo(FlowPanel dialogContent) {
		Label sharingAvailableInfo = BaseWidgetFactory.INSTANCE.newSecondaryText(
				localization.getMenu("SharingAvailableMow"), "shareLinkAvailableInfo");
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
		copyBtn = new StandardButton(localization.getMenu("Copy"));
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
			Scheduler.get().scheduleDeferred(() -> {
				getLinkBox().selectAll();
				getLinkBox().setFocus(true);
			});
		}
		updateMultiuserSharePanel();
		centerAndResize(((AppW) app).getAppletFrame().getKeyboardHeight());
	}

	private void updateMultiuserSharePanel() {
		if (multiuserSharePanel != null) {
			Dom.toggleClass(multiuserSharePanel, "disabled", !isSharedGroupOrLink());
		}
	}

	private String getLinkShareHelpLabelTextKey() {
		if (isShareLinkOn()) {
			VendorSettings settings = ((AppW) app).getVendorSettings();
			return settings.getMenuLocalizationKey("SharedLinkHelpTxt");
		}
		return "NotSharedLinkHelpTxt";
	}

	@Override
	public void onClick(Widget source) {
		if (source == copyBtn) {
			linkBox.setFocused(false);
			app.getCopyPaste().copyTextToSystemClipboard(linkBox.getText());
			linkBox.focus();
			((AppW) app).getToolTipManager()
				.showBottomMessage(((AppW) app).getLocalization()
				.getMenu("linkCopyClipboard"), (AppW) app);
		}
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
		for (Map.Entry<GroupIdentifier, Boolean> group : changedGroups.entrySet()) {
			app.getLoginOperation().getResourcesAPI().setShared(material,
					group.getKey(), group.getValue(), groupCallback);
		}
	}

	protected void getGroupsSharedWith() {
		final AsyncOperation<List<GroupIdentifier>> partial =
				new AsyncOperation<List<GroupIdentifier>>() {
					private int counter = 2;
					private List<GroupIdentifier> all = new ArrayList<>();

					@Override
					public void callback(List<GroupIdentifier> obj) {
						counter--;
						if (obj == null) {
							all = null;
						} else if (all != null) {
							all.addAll(obj);
						}
						if (counter == 0) {
							updateOnSharedGroups(all);
						}
					}
				};
		MaterialRestAPI api = app.getLoginOperation().getResourcesAPI();
		api.getGroups(material.getSharingKeySafe(), GroupIdentifier.GroupCategory.CLASS, partial);
		api.getGroups(material.getSharingKeySafe(), GroupIdentifier.GroupCategory.COURSE, partial);
	}

	/**
	 * @param success
	 *            shared with group successful or not
	 */
	protected void onGroupShareChanged(boolean success) {
		((AppW) app).getToolTipManager().showBottomMessage(
				app.getLocalization()
						.getMenu(success ? "GroupShareOk"
								: "GroupShareFail"), (AppW) app);
		if (success && callback != null) {
			callback.onLoaded(Collections.singletonList(material), null);
		}
	}
}
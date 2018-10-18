package org.geogebra.web.shared;

import java.util.ArrayList;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.SaveController.SaveListener;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author csilla
 *
 */
public class ShareDialogMow extends DialogBoxW
		implements FastClickHandler, SetLabels, SaveListener {
	private AppW appW;
	private FlowPanel dialog;
	private FlowPanel groupContent;
	private ScrollPanel groupsPanel;
	private Label groupInfoLbl;
	private FlowPanel buttonPanel;
	private StandardButton getLinkBtn;
	private String shareURL;
	private Material material;
	private MaterialCallbackI callback;

	private class Group extends FlowPanel {
		private String groupName;

		public Group(String groupName) {
			this.groupName = groupName;
			this.setStyleName("groupContent");
			NoDragImage img = new NoDragImage(SharedResources.INSTANCE.groups(),
					40);
			FlowPanel groupInfoPanel = new FlowPanel();
			groupInfoPanel.setStyleName("groupInfo");
			Label groupNameLbl = new Label(groupName);
			groupNameLbl.setStyleName("groupName");
			// Label groupMemberLbl = new Label("100 Memeber(s)");
			// groupMemberLbl.setStyleName("groupMember");
			groupInfoPanel.add(groupNameLbl);
			// groupInfoPanel.add(groupMemberLbl);
			this.add(img);
			this.add(groupInfoPanel);
			this.addDomHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					share(getGroupName(), new AsyncOperation<Boolean>() {

						@Override
						public void callback(Boolean obj) {
							showTooltip(obj);
						}

					});
					hide();
				}
			}, ClickEvent.getType());
		}

		public String getGroupName() {
			return groupName;
		}

		protected void showTooltip(Boolean success) {
			ToolTipManagerW.sharedInstance().showBottomMessage(
					getAppW().getLocalization()
							.getPlain(success.booleanValue() ? "GroupShareOk"
									: "GroupShareFail", getGroupName()),
					true, getAppW());
		}
	}

	/**
	 * @param app
	 *            {@link AppW}
	 * @param shareURL
	 *            url of sharing link
	 * @param mat
	 *            material
	 */
	public ShareDialogMow(AppW app, String shareURL, Material mat) {
		super(app.getPanel(), app);
		this.shareURL = shareURL;
		this.appW = app;
		// if mat != null -> share called from material context menu
		// if not -> get active material (share from burger menu)
		this.material = mat == null ? app.getActiveMaterial() : mat;
		setAutoHideEnabled(true);
		setGlassEnabled(false);
		addStyleName("mowShareDialog");
		buildGUI();
		DialogUtil.hideOnLogout(app, this);
	}

	/**
	 * @return application
	 */
	public AppW getAppW() {
		return appW;
	}

	private void buildGUI() {
		dialog = new FlowPanel();
		groupContent = new FlowPanel();
		buildGroupsList();
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("buttonPanel");
		getLinkBtn = new StandardButton(
				SharedResources.INSTANCE.mow_link_white(),
				app.getLocalization().getMenu("getLink"), 32, app);
		getLinkBtn.addFastClickHandler(this);
		getLinkBtn.setStyleName("getLinkBtn");
		buttonPanel.add(getLinkBtn);
		dialog.add(groupContent);
		dialog.add(buttonPanel);
		this.add(dialog);
		setLabels();
	}

	private void buildGroupsList() {
		ArrayList<String> groupNames = app.getLoginOperation().getModel()
				.getUserGroups();
		groupInfoLbl = new Label("");

		// groupNames = new ArrayList<>(Arrays.asList("Group1", "Group2", "Group3",
		// "Group4"));

		if (groupNames.isEmpty()) {
			groupInfoLbl
					.setText(app.getLocalization().getMenu("NoGroupShareTxt"));
			groupInfoLbl.setStyleName("noGrTxt");
			groupContent.add(groupInfoLbl);
			return;
		}
		groupInfoLbl.setText(app.getLocalization().getMenu("GroupShareTxt"));
		groupInfoLbl.setStyleName("chooseGrTxt");
		groupContent.add(groupInfoLbl);
		FlowPanel groupList = new FlowPanel();
		for (String groupName : groupNames) {
			groupList.add(new Group(groupName));
		}
		groupsPanel = new ScrollPanel();
		groupsPanel.setStyleName("groupList");
		groupsPanel.add(groupList);
		groupContent.add(groupsPanel);
	}

	@Override
	public void onClick(Widget source) {
		if (source == getLinkBtn) {
			hide();
			if (material != null && "P".equals(material.getVisibility())) {
				app.getLoginOperation().getGeoGebraTubeAPI().uploadMaterial(
						material.getSharingKeyOrId(), "S", material.getTitle(),
						null, callback, material.getType());
			}
			ShareLinkDialog getLinkSD = new ShareLinkDialog((AppW) app,
					shareURL, null);
			getLinkSD.show();
			getLinkSD.center();
		}
	}

	@Override
	public void setLabels() {
		getCaption().setText(app.getLocalization()
				.getMenu("Share"));
		getLinkBtn.setText(app.getLocalization().getMenu("getLink"));
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
	 * @param groupName
	 *            group name
	 * @param groupCallback
	 *            callback for share with group
	 */
	protected void share(String groupName,
			AsyncOperation<Boolean> groupCallback) {
		getAppW().getLoginOperation().getGeoGebraTubeAPI().setShared(material,
				groupName, true, groupCallback);
	}
}
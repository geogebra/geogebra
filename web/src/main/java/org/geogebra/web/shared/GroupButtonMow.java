package org.geogebra.web.shared;

import java.util.AbstractMap.SimpleEntry;

import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author csilla
 * 
 *         Group button for the joint share dialog (mow)
 *
 */
public class GroupButtonMow extends FlowPanel {
	private AppW appW;
	private FlowPanel contentPanel;
	private boolean selected;
	private AsyncOperation<SimpleEntry<String, Boolean>> callBack;
	private String groupName;

	/**
	 * @param app
	 *            see {@link AppW}
	 * @param groupName
	 *            name of the group
	 * @param selected
	 *            whether it's selected initially
	 * @param callBack
	 *            to add to selected/unselected groups list
	 */
	public GroupButtonMow(AppW app, String groupName, boolean selected,
			AsyncOperation<SimpleEntry<String, Boolean>> callBack) {
		this.appW = app;
		this.groupName = groupName;
		this.selected = selected;
		this.callBack = callBack;
		buildGui();
		addClickHandler();
	}

	/**
	 * @return true if group selected (already shared with group)
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            sets group to selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return callback
	 */
	public AsyncOperation<SimpleEntry<String, Boolean>> getCallBack() {
		return callBack;
	}

	/**
	 * @return group name
	 */
	public String getGroupName() {
		return groupName;
	}

	private void buildGui() {
		this.addStyleName("groupButton");
		if (isSelected()) {
			this.addStyleName("selected");
		}
		contentPanel = new FlowPanel();
		contentPanel.addStyleName("content");
		SimplePanel groupImgHolder = new SimplePanel();
		groupImgHolder.addStyleName("groupImgHolder");
		NoDragImage groupImg = new NoDragImage(
				SharedResources.INSTANCE.groups(), 24);
		groupImg.addStyleName("groupImg");
		groupImgHolder.add(groupImg);
		NoDragImage checkMark = new NoDragImage(
				SharedResources.INSTANCE.check_mark_white(), 14);
		checkMark.addStyleName("checkMark");
		contentPanel.add(groupImgHolder);
		contentPanel.add(checkMark);
		Label groupLbl = new Label(groupName);
		groupLbl.setStyleName("groupName");
		contentPanel.add(groupLbl);
		add(contentPanel);
	}

	private void addClickHandler() {
		addDomHandler(event -> {
			updateToSelected();
			setSelected(!isSelected());
			getCallBack().callback(
					new SimpleEntry<>(
					getGroupName(), isSelected()));
		}, ClickEvent.getType());
	}

	/**
	 * @return see {@link AppW}
	 */
	public AppW getAppW() {
		return appW;
	}

	/**
	 * update design of button on selection/deselection
	 */
	public void updateToSelected() {
		if (isSelected()) {
			removeStyleName("selected");
		} else {
			addStyleName("selected");
		}
	}
}

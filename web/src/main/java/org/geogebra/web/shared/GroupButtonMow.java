package org.geogebra.web.shared;

import java.util.function.BiConsumer;

import org.geogebra.common.move.ggtapi.GroupIdentifier;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.SimplePanel;

/**
 * @author csilla
 * 
 *         Group button for the joint share dialog (mow)
 *
 */
public class GroupButtonMow extends FlowPanel {
	private boolean selected;
	private final BiConsumer<GroupIdentifier, Boolean> callBack;
	private final GroupIdentifier groupDescription;

	/**
	 * @param groupName
	 *            name of the group
	 * @param selected
	 *            whether it's selected initially
	 * @param callBack
	 *            to add to selected/unselected groups list
	 */
	public GroupButtonMow(GroupIdentifier groupName, boolean selected,
			BiConsumer<GroupIdentifier, Boolean> callBack) {
		this.groupDescription = groupName;
		this.selected = selected;
		this.callBack = callBack;
		buildGui();
		addClickHandler();
	}

	/**
	 * @param selected
	 *            sets group to selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return group name
	 */
	public GroupIdentifier getGroupDescription() {
		return groupDescription;
	}

	private void buildGui() {
		this.addStyleName("groupButton");
		if (selected) {
			this.addStyleName("selected");
		}
		FlowPanel contentPanel = new FlowPanel();
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
		Label groupLbl = BaseWidgetFactory.INSTANCE.newSecondaryText(
				groupDescription.name, "groupName");
		contentPanel.add(groupLbl);
		add(contentPanel);
	}

	private void addClickHandler() {
		addDomHandler(event -> {
			setSelected(!selected);
			updateToSelected();
			callBack.accept(groupDescription, selected);
		}, ClickEvent.getType());
	}

	/**
	 * update design of button on selection/deselection
	 */
	public void updateToSelected() {
		Dom.toggleClass(this, "selected", selected);
	}

}

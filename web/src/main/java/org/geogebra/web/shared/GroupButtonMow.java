package org.geogebra.web.shared;

import org.geogebra.web.html5.gui.util.NoDragImage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
	private FlowPanel contentPanel;
	private Label groupLbl;
	private boolean selected;

	/**
	 * @param groupName
	 *            name of the group
	 */
	public GroupButtonMow(String groupName) {
		this.selected = false;
		buildGui(groupName);
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

	private void buildGui(String groupName) {
		this.addStyleName("groupButton");
		contentPanel = new FlowPanel();
		contentPanel.addStyleName("content");
		SimplePanel groupImgHolder = new SimplePanel();
		groupImgHolder.addStyleName("groupImgHolder");
		NoDragImage groupImg = new NoDragImage(SharedResources.INSTANCE.groups(),
				24);
		groupImg.addStyleName("groupImg");
		groupImgHolder.add(groupImg);
		NoDragImage checkMark = new NoDragImage(
				SharedResources.INSTANCE.check_mark_white(), 14);
		checkMark.addStyleName("checkMark");
		contentPanel.add(groupImgHolder);
		contentPanel.add(checkMark);
		groupLbl = new Label(groupName);
		groupLbl.setStyleName("groupName");
		contentPanel.add(groupLbl);
		add(contentPanel);
	}

	private void addClickHandler() {
		addDomHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				updateToSelected();
				setSelected(!isSelected());
				// TODO select group
			}
		}, ClickEvent.getType());
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

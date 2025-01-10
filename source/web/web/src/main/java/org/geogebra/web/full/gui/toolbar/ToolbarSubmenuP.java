package org.geogebra.web.full.gui.toolbar;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.ListItem;
import org.geogebra.web.html5.gui.util.UnorderedList;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.Image;

public class ToolbarSubmenuP extends ToolbarSubmenuW {

	/**
	 * @param app
	 *            application
	 * @param order
	 *            order in parent
	 */
	public ToolbarSubmenuP(AppW app, int order) {
		super(app, order);
		removeStyleName("toolbar_submenu");
	}

	@Override
	protected ListItem createListItem(int mode) {
		ListItem listItem = new ListItem();
		Image image = createImage(mode);
		listItem.add(image);
		listItem.setStyleName("submenu_button");
		return listItem;
	}

	@Override
	protected void initGui() {

		itemList = new UnorderedList();
		itemList.setStyleName("submenuItems");
		add(itemList);

		// catch the events to make sure scrollbar is usable when present
		ClickStartHandler.init(this, new ClickStartHandler(false, true) {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				// nothing to do here
			}
		});
	}

	@Override
	public void setVisible(boolean visible) {
		setStyleName("visible", visible);

	}

	/**
	 * @return number of buttons
	 */
	public int getButtonCount() {
		int count = this.getItemList().getWidgetCount();
		return count;
	}

}

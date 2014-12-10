package geogebra.phone.gui.view.euclidian.toolbar;

import geogebra.html5.gui.util.ListItem;
import geogebra.html5.main.AppW;
import geogebra.web.gui.toolbar.ToolbarSubemuW;

import com.google.gwt.user.client.ui.Image;

public class ToolbarSubmenuP extends ToolbarSubemuW {

	public ToolbarSubmenuP(AppW app, int order) {
		super(app, order);
	}

	@Override
	protected ListItem createListItem(int mode) {
		ListItem listItem = new ListItem();
		Image image = createImage(mode);
		listItem.add(image);
		return listItem;
	}
}

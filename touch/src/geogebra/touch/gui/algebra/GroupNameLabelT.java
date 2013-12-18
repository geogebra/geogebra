package geogebra.touch.gui.algebra;

import geogebra.common.main.SelectionManager;
import geogebra.html5.gui.view.algebra.GroupNameLabel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.TreeItem;

public class GroupNameLabelT extends GroupNameLabel {

	public GroupNameLabelT(SelectionManager selection, TreeItem parent, String strlab) {
		super(selection, parent, strlab);
	}

	@Override
	public void onClick(ClickEvent evt) {
		// do nothing
	}
}

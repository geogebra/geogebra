package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.web.html5.util.Dom;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * General AV item (group header or radio item)
 */
public class AVTreeItem extends TreeItem {

	/**
	 * Empty item
	 */
	public AVTreeItem() {
		super();
	}

	/**
	 * @param w
	 *            item content
	 */
	public AVTreeItem(Widget w) {
		super(w);
	}

	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);

		Element w = Dom.querySelectorForElement(this.getElement(),
				".gwt-TreeItem-selected");
		if (w != null) {
			w.getStyle().setBackgroundColor("#FFFFFF");
		}
	}

}

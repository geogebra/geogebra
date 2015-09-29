package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.web.html5.util.Dom;

import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class AVTreeItem extends TreeItem {
	protected boolean first = false;
	public AVTreeItem() {
		super();
	}

	public AVTreeItem(Widget w) {
		super(w);
	}

	public AVTreeItem(SafeHtml safeHtml) {
		super(safeHtml);
	}

	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		if (!AlgebraViewW.hasAvex()) {
			return;
		}

		Element w = Dom.querySelectorForElement(this.getElement(),
				"gwt-TreeItem-selected");
		if (w != null) {
			w.getStyle().setBackgroundColor("#FFFFFF");
		}

	}

	public void setFirst(boolean b) {
		first = b;

	}
}

package geogebra.touch.gui.algebra;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.SelectionManager;
import geogebra.html5.gui.view.algebra.GroupHeader;
import geogebra.html5.gui.view.algebra.RadioButtonTreeItem;
import geogebra.touch.controller.TouchController;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.TreeItem;

public class GroupHeaderT extends GroupHeader {

	// need to be static to prevent that the second event on Android do not
	// effect another RadioButtonTreeItem (f.e. with the delete-tool)
	boolean touchHandled = false;
	boolean clickHandled = false;

	private TreeItem treeitem;
	private TouchController controller;

	public GroupHeaderT(SelectionManager selection, TreeItem parent, String strlab, SafeUri showUrl, SafeUri hiddenUrl, TouchController controller) {
		super(selection, parent, strlab, showUrl, hiddenUrl);

		this.clear();
		this.il = new GroupNameLabelT(selection,parent,strlab);
		this.add(this.il);
		
		this.treeitem = parent;
		this.controller = controller;

		this.il.addTouchStartHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				if (!GroupHeaderT.this.clickHandled) {
					GroupHeaderT.this.touchHandled = true;
					markChildren();
				}
			}
		});

		this.il.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!GroupHeaderT.this.touchHandled) {
					GroupHeaderT.this.clickHandled = true;
					markChildren();
				}
			}
		});
	}

	void markChildren() {
		ArrayList<GeoElement> list = new ArrayList<GeoElement>();
		for (int i = 0; i < this.treeitem.getChildCount(); i++) {
			list.add(((RadioButtonTreeItem) this.treeitem.getChild(i).getWidget()).getGeo());
		}
		this.controller.handleAlgebraHeaderClicked(list);
	}
}
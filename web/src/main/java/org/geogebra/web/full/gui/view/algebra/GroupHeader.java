package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.main.SelectionManager;
import org.gwtproject.safehtml.shared.SafeUri;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * AV group header
 */
public class GroupHeader extends FlowPanel {
	
	/**
	 * label
	 */
	protected GroupNameLabel il;

	/**
	 * +/- button
	 */
	protected OpenButton open;
	private String label;

	/**
	 * @param selection
	 *            selection manager
	 * @param parent
	 *            parent item
	 * @param strlab
	 *            localized name
	 * @param key
	 *            english name (for sorting)
	 * @param showUrl
	 *            image when open
	 * @param hiddenUrl
	 *            image when collapsed
	 */
	public GroupHeader(SelectionManager selection, TreeItem parent,
			String strlab, String key, SafeUri showUrl, SafeUri hiddenUrl) {
		
		this.setStyleName("elemHeading");
		this.label = key;
		
		add(open = new OpenButton(showUrl, hiddenUrl, parent, "algebraOpenButton"));
		add(il = new GroupNameLabel(selection, parent, strlab));
	}

	/**
	 * @param string
	 *            set group name
	 */
	public void setText(String string) {
		il.setText(string);
	}

	/**
	 * @param value
	 *            whether it's open
	 */
	public void setChecked(boolean value) {
		open.setChecked(value);
	}

	/**
	 * @return sort key
	 */
	public String getLabel() {
		return label;
	}
}

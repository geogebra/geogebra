package org.geogebra.web.full.gui.toolbarpanel.tableview;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * HTML representation of the Empty Table of Values View.
 *
 * @author laszlo
 *
 */
public class TableValuesEmptyPanel extends FlowPanel
		implements SetLabels {
	private Label emptyLabel;
	private Label emptyInfo;
	private Localization loc;

	/**
	 * @param app
	 *            {@link AppW}.
	 */
	public TableValuesEmptyPanel(AppW app) {
		super();
		loc = app.getLocalization();
		createGUI();
	}

	private void createGUI() {
		NoDragImage emptyImage = new NoDragImage(
				MaterialDesignResources.INSTANCE.toolbar_table_view_black(),
				56);
		emptyImage.getElement().setAttribute("role", "decoration");
		emptyImage.addStyleName("emptyTableImage");
		FlowPanel emptyImageWrap = new FlowPanel();
		emptyImageWrap.add(emptyImage);
		emptyLabel = new Label();
		emptyLabel.addStyleName("emptyTableLabel");
		emptyInfo = new Label();
		emptyInfo.addStyleName("emptyTableInfo");
		emptyImageWrap.addStyleName("emptyTableImageWrap");
		add(emptyImageWrap);
		add(emptyLabel);
		add(emptyInfo);
		setParentStyle();
		setLabels();
	}

	private void setParentStyle() {
		Element parent = getElement().getParentElement();
		if (parent == null) {
			return;
		}

		addStyleName("emptyTablePanel");
		removeStyleName("tvTable");
		parent.addClassName("tableViewParent");
	}

	@Override
	public void setLabels() {
		emptyLabel.setText(loc.getMenu("TableValuesEmptyTitle"));
		emptyInfo.setText(loc.getMenu("TableValuesEmptyDescription"));
	}
}

package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class TableTab extends ToolbarPanel.ToolbarTab implements SetLabels {

	private ToolbarPanel toolbarPanel;
	private AppW app;
	private Label emptyLabel;
	private Label emptyInfo;
	private NoDragImage emptyImage;
	private FlowPanel emptyPanel;

	/**
	 * @param toolbarPanel
	 *            toolbar panel
	 */
	public TableTab(ToolbarPanel toolbarPanel) {
		this.toolbarPanel = toolbarPanel;
		this.app = toolbarPanel.app;
		buildGui();
		setLabels();
	}

	private void buildGui() {
		this.emptyPanel = new FlowPanel();
		this.emptyPanel.addStyleName("emptyTablePanel");
		this.emptyImage = new NoDragImage(
				MaterialDesignResources.INSTANCE.toolbar_table_view_white(),
				56);
		this.emptyImage.addStyleName("emptyTableImage");
		this.emptyLabel = new Label();
		this.emptyLabel.addStyleName("emptyTableLabel");
		this.emptyInfo = new Label();
		emptyPanel.add(emptyImage);
		emptyPanel.add(emptyLabel);
		emptyPanel.add(emptyInfo);

		this.setWidget(emptyPanel);
		emptyPanel.getElement().getParentElement()
				.addClassName("tableViewParent");
	}

	@Override
	public void setLabels() {
		emptyLabel.setText(
				app.getLocalization().getMenu("TableValuesEmptyTitle"));
		emptyInfo.setText(
				app.getLocalization().getMenu("TableValuesEmptyDescription"));
	}

	@Override
	public void onResize() {
		super.onResize();
		int w = this.toolbarPanel.getTabWidth();
		if (w < 0) {
			return;
		}
		setWidth(2 * w + "px");
		getElement().getStyle().setLeft(2 * w, Unit.PX);
	}

}

package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * HTML representation of the Table of Values View.
 * 
 * @author laszlo
 *
 */
public class TableValuesViewW extends TableValuesView implements SetLabels {
	private FlexTable table;
	private ScrollPanel main;
	private Label emptyLabel;
	private Label emptyInfo;
	private NoDragImage emptyImage;
	private FlowPanel emptyPanel;
	private AppW app;
	
	/**
	 * @param app1
	 *            {@link AppW}.
	 */
	public TableValuesViewW(AppW app1) {
		super(app1.getKernel());
		this.app = app1;
		createGUI();
	}

	private void createGUI() {
		main = new ScrollPanel();
		table = new FlexTable();
		main.add(table);
	}

	@SuppressWarnings("unused")
	private void buildTable() {
		for (int i = 0; i < 10; i++) {
			table.addCell(i);
			table.addCell(i);
			table.addCell(i);
		}
	}

	/**
	 * 
	 * @return the main widget of the view.
	 */
	public Widget getWidget() {
		return isEmpty() ? getEmptyPanel() : main;
	}

	private Widget getEmptyPanel() {
		if (emptyPanel == null) {
			buildEmptyPanel();
		}
		return emptyPanel;
	}

	private void buildEmptyPanel() {
		this.emptyPanel = new FlowPanel();
		this.emptyPanel.addStyleName("emptyTablePanel");
		this.emptyImage = new NoDragImage(
				MaterialDesignResources.INSTANCE.toolbar_table_view_white(), 72);
		this.emptyImage.addStyleName("emptyTableImage");
		this.emptyLabel = new Label();
		this.emptyInfo = new Label();
		emptyPanel.add(emptyImage);
		emptyPanel.add(emptyLabel);
		emptyPanel.add(emptyInfo);
	}

	@Override
	public void setLabels() {
		if (emptyPanel != null) {
			emptyLabel.setText(app.getLocalization().getMenu("TableValuesEmptyTitle"));
			emptyInfo.setText(app.getLocalization().getMenu("TableValuesEmptyDescription"));
		}
	}

}

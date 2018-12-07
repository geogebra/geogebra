package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.table.TableValuesListener;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbarpanel.StickyValuesTable.TableValuesDataProvider;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * HTML representation of the Table of Values View.
 *
 * @author laszlo
 *
 */
public class TableValuesPanel extends FlowPanel
		implements SetLabels, TableValuesListener, TableValuesDataProvider {

	private static final int TOOLBAR_HEADER_HEIGHT = 48;

	/** view of table values */
	TableValuesView view;
	private StickyValuesTable table;
	private Label emptyLabel;
	private Label emptyInfo;
	private AppW app;

	/**
	 * @param app
	 *            {@link AppW}.
	 */
	public TableValuesPanel(AppW app) {
		super();
		this.app = app;
		view = (TableValuesView) app.getGuiManager().getTableValuesView();
		view.getTableValuesModel().registerListener(this);
		createGUI();
	}

	private void createGUI() {
		table = new StickyValuesTable(app, this);
	}

	@Override
	public void update() {
		if (view.isEmpty()) {
			buildEmptyView();
		} else if (!table.refresh()) {
			buildTable();
		}
		setParentStyle();
	}

	private void buildTable() {
		table.build();
		clear();
		add(table);
	}

	private void buildEmptyView() {
		clear();
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
		if (view.isEmpty()) {
			addStyleName("emptyTablePanel");
			removeStyleName("tvTable");
			parent.addClassName("tableViewParent");
		} else {
			addStyleName("tvTable");
			removeStyleName("emptyTablePanel");
			parent.removeClassName("tableViewParent");
		}
	}

	@Override
	public void setLabels() {
		if (emptyLabel != null) {
			emptyLabel.setText(app.getLocalization().getMenu("TableValuesEmptyTitle"));
			emptyInfo.setText(app.getLocalization().getMenu("TableValuesEmptyDescription"));
		}
	}

	/**
	 * Sets height of the view.
	 *
	 * @param height
	 *            to set.
	 */
	public void setHeight(int height) {
		table.setHeight(height - TOOLBAR_HEADER_HEIGHT);
	}

	/**
	 * Deletes the specified column from the view.
	 *
	 * @param column
	 *            column to delete.
	 * @param cb
	 *            to run on transition end.
	 */
	public void deleteColumn(int column, Runnable cb) {
		table.deleteColumn(column, cb);
	}

	@Override
	public void notifyColumnRemoved(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		deleteColumn(column, null);
	}

	@Override
	public void notifyColumnChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		// not used.
	}

	@Override
	public void notifyColumnAdded(TableValuesModel model, GeoEvaluatable evaluatable, int column) {
		table.onColumnAdded();
		table.scrollTo(evaluatable);
	}

	@Override
	public void notifyColumnHeaderChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		// not used.
	}

	@Override
	public void notifyDatasetChanged(TableValuesModel model) {
		table.refresh();
	}

	@Override
	public void onAttach() {
		super.onAttach();
		this.setParentStyle();
	}

	@Override
	public TableValuesView getView() {
		return view;
	}

	/**
	 * Scroll table view to the corresponding column of the geo.
	 * 
	 * @param geo
	 *            to scroll.
	 */
	public void scrollTo(GeoEvaluatable geo) {
		table.scrollTo(geo);
	}
}

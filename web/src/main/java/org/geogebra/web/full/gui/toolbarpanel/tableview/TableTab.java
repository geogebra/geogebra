package org.geogebra.web.full.gui.toolbarpanel.tableview;

import java.util.function.Supplier;

import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.full.gui.view.probcalculator.ProbabilityCalculatorViewW;
import org.geogebra.web.full.util.CustomScrollbar;
import org.geogebra.web.full.util.StickyTable;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.util.TestHarness;
import org.geogebra.web.shared.components.infoError.ComponentInfoErrorPanel;
import org.geogebra.web.shared.components.infoError.InfoErrorData;

import com.google.gwt.dom.client.Style;

/**
 * Tab of Table Values View.
 * 
 * @author laszlo
 */
public class TableTab extends ToolbarPanel.ToolbarTab {

	private final StickyTable<?> table;
	private final ToolbarPanel toolbarPanel;
	private ComponentInfoErrorPanel emptyPanel;

	/**
	 * @param toolbarPanel
	 *            toolbar panel
	 */
	public TableTab(ToolbarPanel toolbarPanel, StickyTable<?> table) {
		super(toolbarPanel);
		this.toolbarPanel = toolbarPanel;
		this.table = table;
		TestHarness.setAttr(table, "TV_table");
		table.setStyleName("tvTable", true);
		CustomScrollbar.apply(this);
		this.getElement().getFirstChildElement().getStyle().setHeight(100, Style.Unit.PCT);
		buildEmptyTablePanel();
	}

	private void buildEmptyTablePanel() {
		InfoErrorData data = new InfoErrorData("TableValuesEmptyTitle",
				"TableDiscreteDistribution");
		emptyPanel = new ComponentInfoErrorPanel(toolbarPanel.getApp().getLocalization(),
				data, MaterialDesignResources.INSTANCE.toolbar_table_view_black(), null);
	}

	@Override
	protected void onActive() {
		if (toolbarPanel.getApp().getConfig().hasDistributionView()
				&& isEmptyProbabilityTable()) {
			setWidget(emptyPanel);
		} else  {
			setWidget(table);
			table.setHeight(toolbarPanel.getTabHeight());
		}
	}

	private boolean isEmptyProbabilityTable() {
		if (toolbarPanel.getApp().getConfig().hasDistributionView()) {
			ProbabilityCalculatorViewW view = (ProbabilityCalculatorViewW) toolbarPanel.getApp()
					.getGuiManager().getProbabilityCalculator();
			return !view.hasTableView();
		}
		return true;
	}

	@Override
	public void setLabels() {
		if (emptyPanel != null) {
			buildEmptyTablePanel();
		}
	}

	@Override
	public void open() {
		toolbarPanel.openTableView(true);
	}

	@Override
	public void close() {
		toolbarPanel.close(false);
	}

	@Override
	public void onResize() {
		int w = this.toolbarPanel.getTabWidth();
		int h = toolbarPanel.getTabHeight();
		if (w < 0 || h < 0) {
			return;
		}

		setWidth(w + "px");
		setHeight(h + "px");
		table.setHeight(h);
	}

	/**
	 * Scroll table view to the corresponding column of the geo.
	 * 
	 * @param geo
	 *            to scroll.
	 */
	public void scrollTo(GeoEvaluatable geo) {
		if (table instanceof StickyValuesTable) {
			((StickyValuesTable) table).scrollTo(geo);
		}
	}

	/**
	 * @param fallback fallback
	 * @return keyboard listener if editable, fallback otherwise
	 */
	public MathKeyboardListener getKeyboardListener(Supplier<MathKeyboardListener> fallback) {
		if (table instanceof StickyValuesTable) {
			return ((StickyValuesTable) table).getKeyboardListener();
		}
		return fallback.get();
	}
}

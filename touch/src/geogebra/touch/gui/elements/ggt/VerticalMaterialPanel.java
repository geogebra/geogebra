package geogebra.touch.gui.elements.ggt;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.gui.ResizeListener;
import geogebra.html5.main.AppWeb;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.BrowseGUI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;

public class VerticalMaterialPanel extends FlowPanel implements ResizeListener {

	private static int maxHeight() {
		return TouchEntryPoint.getLookAndFeel().getContentWidgetHeight()
				- TouchEntryPoint.getLookAndFeel().getBrowseHeaderHeight()
				- BrowseGUI.CONTROLS_HEIGHT - BrowseGUI.HEADING_HEIGHT;
	}

	private final FlexTable contentPanel;
	private final AppWeb app;
	private int materialHeight = 140;
	private int columns = 2;
	private int start;
	private List<MaterialListElement> materials = new ArrayList<MaterialListElement>();

	public VerticalMaterialPanel(final AppWeb app) {
		this.getElement().getStyle().setFloat(Style.Float.LEFT);
		this.setStyleName("filePanel");
		this.contentPanel = new FlexTable();
		this.app = app;
		this.add(this.contentPanel);
		this.contentPanel.setWidth("100%");
	}

	private int pageCapacity() {
		return this.columns * (maxHeight() / this.materialHeight);
	}

	void nextPage() {
		if (hasNextPage()) {
			this.setMaterials(this.columns, this.materials, this.start
					+ pageCapacity());
		}
	}

	boolean hasNextPage() {
		return (this.start + pageCapacity()) < this.materials.size();
	}

	void prevPage() {
		if (hasPrevPage()) {
			this.setMaterials(this.columns, this.materials,
					Math.max(0, this.start - pageCapacity()));
		}
	}

	boolean hasPrevPage() {
		return this.start > 0;
	}

	public void setLabels() {
		for (final MaterialListElement e : this.materials) {
			e.setLabels();
		}
	}

	public void setMaterials(final int cols, final List<Material> matList) {
		this.materials.clear();
		for (final Material mat : matList) {
			this.materials.add(new MaterialListElement(mat, this.app));
		}
		this.setMaterials(cols, this.materials, 0);
	}

	private void setMaterials(final int cols,
			final List<MaterialListElement> materials, final int offset) {
		final boolean widthChanged = this.columns != 0 && cols != this.columns;
		this.columns = cols;
		this.contentPanel.clear();
		this.start = offset;
		this.materials = materials;

		if (this.columns == 2) {
			this.contentPanel.getCellFormatter().setWidth(0, 0, "50%");
			this.contentPanel.getCellFormatter().setWidth(0, 1, "50%");
		} else {
			this.contentPanel.getCellFormatter().setWidth(0, 0, "100%");
		}

		for (int i = 0; i < materials.size() - this.start && i < pageCapacity(); i++) {
			final MaterialListElement preview = materials.get(i + this.start);
			this.contentPanel.setWidget(i / this.columns, i % this.columns,
					preview);
		}
		if (widthChanged) {
			updateWidth();
		}
	}

	private void updateHeight() {
		final Iterator<MaterialListElement> material = this.materials
				.iterator();

		if (material.hasNext()) {
			final MaterialListElement next = material.next();
			if (next.getOffsetHeight() > 0) {
				this.materialHeight = next.getOffsetHeight();
			}
		}
		if (this.materials != null) {
			this.setMaterials(this.columns, this.materials, this.start);
		}
	}

	@Override
	public void onResize() {
		this.updateWidth();
		this.updateHeight();
	}

	private void updateWidth() {
		this.setWidth(Window.getClientWidth() / 2 * this.columns + "px");
	}
}

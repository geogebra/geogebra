package geogebra.touch.gui.elements.ggt;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.BrowseGUI;
import geogebra.touch.gui.ResizeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	private final Map<String, MaterialListElement> titlesToPreviews = new HashMap<String, MaterialListElement>();
	private int start;
	private List<Material> materials = new ArrayList<Material>();

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
		for (final MaterialListElement e : this.titlesToPreviews.values()) {
			e.setLabels();
		}
	}

	public void setMaterials(final int cols, final List<Material> materials) {
		this.setMaterials(cols, materials, 0);
	}

	private void setMaterials(final int cols, final List<Material> materials,
			final int offset) {
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
			final Material m = materials.get(i + this.start);
			MaterialListElement preview = this.titlesToPreviews.get(m.getURL());
			if (preview == null) {
				preview = new MaterialListElement(m, this.app);
				this.titlesToPreviews.put(m.getURL(), preview);
			}
			this.contentPanel.setWidget(i / this.columns, i % this.columns,
					preview);
		}
		if (widthChanged) {
			updateWidth();
		}
	}

	private void updateHeight() {
		final Iterator<MaterialListElement> material = this.titlesToPreviews
				.values().iterator();
		if (material.hasNext()) {
			final MaterialListElement next = material.next();
			if (next.getOffsetHeight() > 0) {
				this.materialHeight = next.getOffsetHeight();
			}
		}
		// if(this.materialHeight != oldMaterialHeight){
		if (this.materials != null) {
			this.setMaterials(this.columns, this.materials, this.start);
		}
		// }

	}

	@Override
	public void onResize() {
		this.updateWidth();
		this.updateHeight();
	}

	private void updateWidth() {
		this.setWidth(Window.getClientWidth() / 2 * this.columns + "px");
	}

	public void invalidate(final String changedName) {
		if (this.titlesToPreviews.get(changedName) != null) {
			this.titlesToPreviews.remove(changedName);
		}

	}
}

package geogebra.touch.gui.elements.ggt;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.BrowseGUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;

public class VerticalMaterialPanel extends FlowPanel {
	public static final int SPACE = 20;

	private static int maxHeight() {
		return TouchEntryPoint.getLookAndFeel().getContentWidgetHeight()
				- BrowseGUI.CONTROLS_HEIGHT;
	}

	private final FlexTable contentPanel;
	private final AppWeb app;
	private int materialHeight = 140;
	private MaterialListElement lastSelected;
	private int columns = 2;
	private final Map<String, MaterialListElement> titlesToPreviews = new HashMap<String, MaterialListElement>();
	private int start;
	private List<Material> materials = new ArrayList<Material>();

	public VerticalMaterialPanel(final AppWeb app) {
		this.getElement().getStyle().setFloat(Style.Float.LEFT);
		this.contentPanel = new FlexTable();
		this.app = app;
		// this.setWidget(this.contentPanel);
		this.add(this.contentPanel);
		this.contentPanel.setWidth("100%");
	}

	public MaterialListElement getChosenMaterial() {
		return this.lastSelected;
	}


	@Override
	public int getOffsetHeight() {
		return MaterialListElement.PANEL_HEIGHT;
	}

	private int pageCapacity() {
		return this.columns * (maxHeight() / this.materialHeight);
	}

	public void nextPage() {
		if (!hasNextPage()) {
			return;
		}
		this.setMaterials(this.columns, this.materials, this.start
				+ pageCapacity());
	}

	public boolean hasNextPage() {
		if (this.start + pageCapacity() >= this.materials.size()) {
			return false;
		}
		return true;
	}

	public void prevPage() {
		if (!hasPrevPage()) {
			return;
		}
		this.setMaterials(this.columns, this.materials,
				Math.max(0, this.start - pageCapacity()));
	}

	public boolean hasPrevPage() {
		if (this.start <= 0) {
			return false;
		}
		return true;
	}

	public void rememberSelected(final MaterialListElement materialElement) {
		this.lastSelected = materialElement;
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
		this.columns = cols;
		this.updateWidth();
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
			final MaterialListElement preview = new MaterialListElement(m,
					this.app, this);
			preview.initButtons();
			this.titlesToPreviews.put(m.getURL(), preview);
			this.contentPanel.setWidget(i / this.columns, i % this.columns,
					preview);
		}
	}

	public void unselectMaterials() {
		if (this.lastSelected != null) {
			this.lastSelected.markUnSelected();
		}
	}

	public void updateWidth() {
		this.setWidth(Window.getClientWidth() / 2 * this.columns + "px");
	}
}

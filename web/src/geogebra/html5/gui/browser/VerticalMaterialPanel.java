package geogebra.html5.gui.browser;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.gui.ResizeListener;
import geogebra.html5.main.AppWeb;

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
		//TODO random number
		return Window.getClientHeight() - 300;
	}

	private final FlexTable contentPanel;
	private final AppWeb app;
	private int materialHeight = 140;
	private final int columns = 2;
	private final Map<String, MaterialListElement> titlesToPreviews = new HashMap<String, MaterialListElement>();
	private int start;
	private List<Material> materials = new ArrayList<Material>();
	private final BrowseGUI bg;

	public VerticalMaterialPanel(final AppWeb app, BrowseGUI bg) {
		this.getElement().getStyle().setFloat(Style.Float.LEFT);
		this.setStyleName("filePanel");
		this.contentPanel = new FlexTable();
		this.app = app;
		this.add(this.contentPanel);
		this.contentPanel.setWidth("100%");
		this.bg = bg;
	}

	private int pageCapacity() {
		return this.columns * (maxHeight() / this.materialHeight);
	}

	boolean hasNextPage() {
		return (this.start + pageCapacity()) < this.materials.size();
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
		this.setMaterials(materials, 0);
	}

	private void setMaterials(final List<Material> materials,
			final int offset) {
		
		this.contentPanel.clear();
		this.start = offset;
		this.materials = materials;

		for(int i = 0; i < this.columns; i++){
			this.contentPanel.getCellFormatter().setWidth(0, i, (100/this.columns) + "%");
		}
		

		for (int i = 0; i < materials.size() - this.start; i++) {
			final Material m = materials.get(i + this.start);
			MaterialListElement preview = this.titlesToPreviews.get(m.getURL());
			if (preview == null) {
				preview = new MaterialListElement(m, this.app, this.bg);
				this.titlesToPreviews.put(m.getURL(), preview);
			}
			this.contentPanel.setWidget(i / this.columns, i % this.columns,
					preview);
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
			this.setMaterials(this.materials, this.start);
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


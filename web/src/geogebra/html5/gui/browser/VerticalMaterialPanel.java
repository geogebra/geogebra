package geogebra.html5.gui.browser;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.gui.ResizeListener;
import geogebra.html5.gui.browser.SearchPanel.SearchListener;
import geogebra.html5.main.AppWeb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;

public class VerticalMaterialPanel extends FlowPanel implements ResizeListener {

	private final FlowPanel searchContainer;
	private SearchPanel searchPanel;
	private final FlowPanel filePanel;
	private final AppWeb app;
	/*private int materialHeight = 140;
	private final int columns = 2;*/
	private final Map<String, MaterialListElement> titlesToPreviews = new HashMap<String, MaterialListElement>();
	private int start;
	private List<Material> materials = new ArrayList<Material>();
	private final BrowseGUI bg;

	public VerticalMaterialPanel(final AppWeb app, BrowseGUI bg) {
		this.app = app;
		this.bg = bg;
		this.getElement().getStyle().setFloat(Style.Float.LEFT);
		this.setStyleName("contentPanel");
		
		this.searchContainer = new FlowPanel();
		this.searchPanel = new SearchPanel(app.getLocalization(), bg, app.getNetworkOperation());
		
		this.searchPanel.addSearchListener(new SearchListener() {
            @Override
			public void onSearch(final String query) {
				VerticalMaterialPanel.this.bg.displaySearchResults(query);
			}
		});
		this.searchContainer.add(searchPanel);
		this.add(this.searchContainer);
		
		this.filePanel = new FlowPanel();
		this.filePanel.setStyleName("filePanel");
		this.add(this.filePanel);
		
		//this.contentPanel.setWidth("100%");
	}

	/*private int pageCapacity() {
		return this.columns * (maxHeight() / this.materialHeight);
	}*/

	/*boolean hasNextPage() {
		return (this.start + pageCapacity()) < this.materials.size();
	}*/

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
		
		this.filePanel.clear();
		this.start = offset;
		this.materials = materials;

		//Steffi: changed to FlowPanel
		//for(int i = 0; i < this.columns; i++){
			//this.contentPanel.getCellFormatter().setWidth(0, i, (100/this.columns) + "%");
		//}
		

		for (int i = 0; i < materials.size() - this.start; i++) {
			final Material m = materials.get(i + this.start);
			MaterialListElement preview = this.titlesToPreviews.get(m.getURL());
			if (preview == null) {
				preview = new MaterialListElement(m, this.app, this.bg);
				this.titlesToPreviews.put(m.getURL(), preview);
			}
			//Steffi: changed to FlowPanel
			//this.contentPanel.setWidget(i / this.columns, i % this.columns,
			//		preview);
			this.filePanel.add(preview);
		}
		// clearPanel clears flow layout (needed for styling)
		/*final LayoutPanel clearPanel = new LayoutPanel();
		clearPanel.setStyleName("fileClear");
		this.contentPanel.add(clearPanel);*/
	}

	/*private void updateHeight() {
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

	}*/

	public void invalidate(final String changedName) {
		if (this.titlesToPreviews.get(changedName) != null) {
			this.titlesToPreviews.remove(changedName);
		}

	}
	
	@Override
	public void onResize() {
		int searchContainerHeight = this.searchContainer.getOffsetHeight();
		int contentHeight = bg.getOffsetHeight() - BrowseGUI.HEADING_HEIGHT - searchContainerHeight;
		this.filePanel.setHeight(contentHeight + "px");
	}
}


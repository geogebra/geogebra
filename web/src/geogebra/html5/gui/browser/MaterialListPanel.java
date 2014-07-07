package geogebra.html5.gui.browser;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.gui.ResizeListener;
import geogebra.html5.main.AppWeb;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * NOT IN USE YET
 * 
 * The preview panel for the {@link MaterialListElement materials}
 *
 */
public class MaterialListPanel extends FlowPanel implements ResizeListener {
	
	protected List<MaterialListElement> materials = new ArrayList<MaterialListElement>();
	protected AppWeb app;
	
	/**
	 * @param app AppWeb
	 */
	public MaterialListPanel(AppWeb app) {
		this.app = app;
//		this.setStyleName("materialListPanel");
		this.setStyleName("filePanel");
		this.setHeight(Window.getClientHeight() - 61 - 65 +"px");
	}
	
	/**
	 * clears the existing list of materials and adds the new materials (matList)
	 * @param matList List<Material>
	 */
	public void setMaterials(final List<Material> matList) {
		clearMaterials();
		for (final Material mat : matList) {
			addMaterial(mat);
		}
	}
	
	
	private void clearMaterials() {
		this.materials.clear();
		this.clear();
	}
	
	/**
	 * adds the given material to the list of {@link MaterialListElement materials} and the preview-panel
	 * 
	 * @param mat {@link Material}
	 */
	public void addMaterial(Material mat) {
		final MaterialListElement preview = new MaterialListElement(mat, this.app);
		this.materials.add(preview);
		this.add(preview);
	}

	/**
	 * removes the given material from the list of {@link MaterialListElement materials} and the preview-panel
	 * @param mat {@link Material}
	 */
	public void removeMaterial(Material mat) {
		for(MaterialListElement matElem : this.materials) {
			if (matElem.getMaterial().equals(mat)) {
				this.materials.remove(matElem);
				this.remove(matElem);
			}
		}
	}

	/**
	 * clears the list of materials shown in the preview-panel
	 */
	public void clearList() {
		this.materials.clear();
	}
	
	/**
	 * 
	 */
	public void setLabels() {
		for (final MaterialListElement e : this.materials) {
			e.setLabels();
		}
	}

	@Override
	public void onResize() {
		this.setHeight(Window.getClientHeight() - 61 - 65 +"px");
		for (MaterialListElement elem : this.materials) {
			elem.onResize();
		}
	}
}

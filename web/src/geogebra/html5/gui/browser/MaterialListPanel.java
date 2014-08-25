package geogebra.html5.gui.browser;

import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.gui.ResizeListener;
import geogebra.html5.gui.laf.GLookAndFeel;
import geogebra.html5.main.AppWeb;
import geogebra.html5.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.html5.move.ggtapi.models.MaterialCallback;
import geogebra.web.main.AppW;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * 
 * 
 */
public class MaterialListPanel extends FlowPanel implements ResizeListener {
	
	protected AppWeb app;
	/**
	 * last selected {@link MaterialListElement material}
	 */
	protected MaterialListElement lastSelected;
	/**
	 * list of all shown {@link MaterialListElement materials}
	 */
	protected List<MaterialListElement> materials = new ArrayList<MaterialListElement>();
	
	public MaterialListPanel(final AppWeb app) {
		this.app = app;
		this.setPixelSize(Window.getClientWidth() - GLookAndFeel.PROVIDER_PANEL_WIDTH, Window.getClientHeight() - GLookAndFeel.BROWSE_HEADER_HEIGHT);
		this.setStyleName("materialListPanel");
		this.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (lastSelected != null) {
					setDefaultStyle();
				}
			}
		}, ClickEvent.getType());
		
		this.addDomHandler(new ScrollHandler() {
			
			@Override
			public void onScroll(ScrollEvent event) {
				if (lastSelected != null) {
					setDefaultStyle();
				}
			}
		}, ScrollEvent.getType());
		
		this.addDomHandler(new TouchMoveHandler() {
			
			@Override
			public void onTouchMove(TouchMoveEvent event) {
				if (lastSelected != null) {
					setDefaultStyle();
				}
			}
		}, TouchMoveEvent.getType());
	}

	/**
	 * sets all {@link MaterialListElement materials} to the
	 * default style (not selected, not disabled)
	 */
	void setDefaultStyle() {
		for (MaterialListElement mat : this.materials) {
			mat.setDefaultStyle();
		}
	}
	
	/**
	 * clears the panel and adds the local/own files and the
	 * featured materials from ggt to the panel
	 */
	public void loadFeatured() {
		clearMaterials();
		//local files
		if (((AppW) app).getFileManager() != null) {
			((AppW) app).getFileManager().getAllFiles();
		}

		final MaterialCallback rc = new MaterialCallback() {
			@Override
			public void onError(final Throwable exception) {
				// FIXME implement Error Handling!
				exception.printStackTrace();
				App.debug("API error"+exception.getMessage());
			}

			@Override
			public void onLoaded(final List<Material> response) {
				App.debug("API success: " + response.size());
				onSearchResults(response);
			}
		};
		final GeoGebraTubeAPIW api = (GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI();
		
		//load userMaterials first
		if(app.getLoginOperation().isLoggedIn()){
			api.getUsersMaterials(app.getLoginOperation().getModel().getUserId(), rc);
		}
		api.getFeaturedMaterials(rc);
	}
	/**
	 * adds the new materials (matList)
	 * @param matList List<Material>
	 */
	public void onSearchResults(final List<Material> matList) {
		for (final Material mat : matList) {
			addMaterial(mat);
		}
	}

	/**
	 * adds the given material to the list of {@link MaterialListElement materials} and the preview-panel
	 * 
	 * @param mat {@link Material}
	 */
	public void addMaterial(final Material mat) {
		final MaterialListElement preview = ((AppW)app).getLAF().getMaterialElement(mat, this.app);
		this.materials.add(preview);
		this.add(preview);
	}

	/**
	 * clears the list of existing {@link MaterialListElement materials} and the {@link MaterialListPanel preview-panel}
	 */
	public void clearMaterials() {
		this.materials.clear();
		this.clear();
	}
	
	/**
	 * @return {@link MaterialListElement} last selected material
	 */
	public MaterialListElement getChosenMaterial() {
		return this.lastSelected;
	}
	
	/**
	 * sets all materials to disabled
	 */
	public void disableMaterials() {
	    for (MaterialListElement mat : this.materials) {
	    	mat.disableMaterial();
	    }
    }

	/**
	 * @param materialElement 
	 */
	public void rememberSelected(final MaterialListElement materialElement) {
		this.lastSelected = materialElement;
	}
	
	public void displaySearchResults(final String query) {
		clearMaterials();
		
		if (query.equals("")) {
			loadFeatured();
			return;
		}

		//search local
		if (((AppW) this.app).getFileManager() != null) {
			((AppW) this.app).getFileManager().search(query);
		}
		
		//search GeoGebraTube
		((GeoGebraTubeAPIW) this.app.getLoginOperation().getGeoGebraTubeAPI()).search(
				query, new MaterialCallback() {
					@Override
					public void onError(final Throwable exception) {
						// FIXME implement Error Handling!
						exception.printStackTrace();
						App.debug(exception.getMessage());
					}

					@Override
					public void onLoaded(final List<Material> response) {
						onSearchResults(response);
					}
				});
	}

	/**
	 * removes the given material from the list of {@link MaterialListElement materials} and the preview-panel
	 * @param mat {@link Material}
	 */
	public void removeMaterial(final Material mat) {
		for(final MaterialListElement matElem : this.materials) {
			if (matElem.getMaterial().equals(mat)) {
				this.materials.remove(matElem);
				this.remove(matElem);
			}
		}
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
		this.setPixelSize(Window.getClientWidth() - GLookAndFeel.PROVIDER_PANEL_WIDTH, Window.getClientHeight() - GLookAndFeel.BROWSE_HEADER_HEIGHT);
		for (final MaterialListElement elem : this.materials) {
			elem.onResize();
		}
	}
}

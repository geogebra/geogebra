package geogebra.web.gui.browser;

import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.gui.ResizeListener;
import geogebra.html5.main.AppW;
import geogebra.web.gui.laf.GLookAndFeel;
import geogebra.web.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.web.move.ggtapi.models.MaterialCallback;

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
	
	protected AppW app;
	/**
	 * last selected {@link MaterialListElement material}
	 */
	protected MaterialListElement lastSelected;
	/**
	 * list of all shown {@link MaterialListElement materials}
	 */
	protected List<MaterialListElement> materials = new ArrayList<MaterialListElement>();
	
	public MaterialListPanel(final AppW app) {
		this.app = app;
		this.setPixelSize(Window.getClientWidth() - GLookAndFeel.PROVIDER_PANEL_WIDTH, Window.getClientHeight() - GLookAndFeel.BROWSE_HEADER_HEIGHT);
		this.setStyleName("materialListPanel");
		this.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(final ClickEvent event) {
				if (lastSelected != null) {
					setDefaultStyle();
				}
			}
		}, ClickEvent.getType());
		
		this.addDomHandler(new ScrollHandler() {
			
			@Override
			public void onScroll(final ScrollEvent event) {
				if (lastSelected != null) {
					setDefaultStyle();
				}
			}
		}, ScrollEvent.getType());
		
		this.addDomHandler(new TouchMoveHandler() {
			
			@Override
			public void onTouchMove(final TouchMoveEvent event) {
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
	public void setDefaultStyle() {
		this.lastSelected = null;
		for (final MaterialListElement mat : this.materials) {
			mat.setDefaultStyle();
		}
	}
	
	
	/**
	 * loads featured materials and (if user is logged in) users materials
	 */
    public void loadAllMaterials() {
    	clearMaterials();
    	loadLocal();
		final GeoGebraTubeAPIW api = (GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI();
		api.getFeaturedMaterials(new MaterialCallback() {
			
			@Override
			public void onLoaded(final List<Material> response) {
				addGGTMaterials(response);
				if(app.getLoginOperation().isLoggedIn()){
					loadUsersMaterials();
				}
			}
		});
	}
    
    public void loadAllMaterials(final MaterialCallback cb) {
    	clearMaterials();
    	loadLocal();
		final GeoGebraTubeAPIW api = (GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI();
		api.getFeaturedMaterials(new MaterialCallback() {
			
			@Override
			public void onLoaded(final List<Material> response) {
				addGGTMaterials(response);
				if(app.getLoginOperation().isLoggedIn()){
					loadUsersMaterials(cb);
				}
			}
		});
    }
    
	/**
	 * loads users materials from ggt
	 */
	public void loadUsersMaterials() {
		((GeoGebraTubeAPIW)app.getLoginOperation().getGeoGebraTubeAPI()).getUsersMaterials(app.getLoginOperation().getModel().getUserId(), new MaterialCallback() {
			
			@Override
			public void onLoaded(final List<Material> parseResponse) {
				addUsersMaterials(parseResponse);
			}
		});
	}
	
	/**
	 * first load and add users materials to the {@link MaterialListPanel preview-panel} than call cb
	 * @param cb MaterialCallback
	 */
	public void loadUsersMaterials(final MaterialCallback cb) {
		((GeoGebraTubeAPIW)app.getLoginOperation().getGeoGebraTubeAPI()).getUsersMaterials(app.getLoginOperation().getModel().getUserId(), new MaterialCallback() {
			
			@Override
			public void onLoaded(final List<Material> parseResponse) {
				addUsersMaterials(parseResponse);
				cb.onLoaded(parseResponse);
			}
		});
	}

	/**
	 * adds the local saved materials of current loggedIn user
	 */
    private void loadLocal() {
		app.getFileManager().getUsersMaterials();
	}

	/**
	 * adds the new materials (matList) - GeoGebraTube only
	 * @param matList List<Material>
	 */
	public final void addGGTMaterials(final List<Material> matList) {
		for (final Material mat : matList) {
			addMaterial(mat, true, false);
		}
	}
	
	/**
	 * 
	 * @param matList List<Material>
	 */
	public void addUsersMaterials(final List<Material> matList) {
		for (int i = matList.size()-1;i>=0;i--) {
			addMaterial(matList.get(i), false, false);
		}
	}
	
	
	/**
	 * adds the given material to the list of {@link MaterialListElement materials} and the preview-panel
	 * @param mat {@link Material}
	 * @param insertAtEnd boolean
	 * @param isLocal boolean
	 */
	public void addMaterial(final Material mat, final boolean insertAtEnd, final boolean isLocal) {
		final MaterialListElement matElem = getMaterialListElement(mat);
		if (matElem != null) {
			matElem.setMaterial(mat);
		} else {
			addNewMaterial(mat, insertAtEnd, isLocal);
		}
	}

	/**
	 * The actual creation happens in LAF as it needs to be different for phone / tablet / web / widgets
	 * @param mat {@link Material}
	 * @param isLocal boolean
	 */
	private final void addNewMaterial(final Material mat, final boolean insertAtEnd, final boolean isLocal) {
		final MaterialListElement preview = ((GLookAndFeel)app.getLAF()).getMaterialElement(mat, this.app, isLocal);
		this.materials.add(preview);
		if (insertAtEnd) {
			this.add(preview);
		} else {
			this.insert(preview,0);
		}
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
	    for (final MaterialListElement mat : this.materials) {
	    	mat.disableMaterial();
	    }
    }

	/**
	 * @param materialElement {@link MaterialListElement}
	 */
	public void rememberSelected(final MaterialListElement materialElement) {
		this.lastSelected = materialElement;
	}
	
	public void displaySearchResults(final String query) {
		clearMaterials();
		if (query.equals("")) {
			loadAllMaterials();
			return;
		}
		searchLocal(query);
		searchGgt(query);
	}

	private void searchLocal(final String query) {
		this.app.getFileManager().search(query);
	}
	
	/**
	 * search GeoGebraTube
	 * @param query String
	 */
	protected void searchGgt(final String query) {
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
						addGGTMaterials(response);
					}
				});
    }

	/**
	 * removes the given material from the list of {@link MaterialListElement materials} and the preview-panel
	 * @param mat {@link Material}
	 */
	public void removeMaterial(final Material mat) {
		for(final MaterialListElement matElem : this.materials) {
			if (matElem.isLocal && mat.getTitle().equals(matElem.getMaterial().getTitle()) ||
					matElem.isOwnMaterial && matElem.getMaterial().equals(mat)) {
				
				this.materials.remove(matElem);
				this.remove(matElem);
				return;
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

	public void refreshMaterial(final Material material, final boolean isLocal) {
		if (!isLocal) {
			material.setSyncStamp(material.getModified());
		}
		addMaterial(material, false, isLocal);
    }
	
	private MaterialListElement getMaterialListElement(final Material material) {
		for(final MaterialListElement matElem : this.materials) {
			if (!matElem.isLocal && matElem.getMaterial().getId() == material.getId() ||
					matElem.isLocal && matElem.getMaterial().getTitle().equals(material.getTitle())) {
				return matElem;
			}
		}
		return null;
	}

	/**
	 * remove users materials from {@link MaterialListPanel}
	 */
	public void removeUsersMaterials() {
		final List<Material> delete = new ArrayList<Material>();
	    for (final MaterialListElement elem : this.materials) {
	    	if (elem.isOwnMaterial) {
	    		delete.add(elem.getMaterial());
	    	}
	    }
	    for (final Material deleteElem : delete) {
	    	removeMaterial(deleteElem);
	    }
    }
}

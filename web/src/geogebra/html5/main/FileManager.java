package geogebra.html5.main;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;
import geogebra.common.move.ggtapi.models.MaterialFilter;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.util.SaveCallback;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.web.move.ggtapi.models.MaterialCallback;

import java.util.List;

public abstract class FileManager {
	protected AppW app;

	public FileManager(AppW app) {
	    this.app = app;
    }
	
	public abstract void delete(final Material mat);
	public abstract void openMaterial(final Material material);
	public abstract void saveFile(final SaveCallback cb);
	public abstract void removeFile(final Material mat);
	public abstract void addFile(final Material mat);
	public abstract void uploadUsersMaterials();
	protected abstract void getFiles(MaterialFilter materialFilter);
	
	protected Material createMaterial(String base64) {
		final Material mat = new Material(0, MaterialType.ggb);
		
		//TODO check if we need to set timestamp / modified
		mat.setModified(System.currentTimeMillis() / 1000);
		
		if (app.getUniqueId() != null) {
			mat.setId(Integer.parseInt(app.getUniqueId()));
			mat.setSyncStamp(app.getSyncStamp());
		}
		
		mat.setBase64(base64);
		mat.setTitle(app.getKernel().getConstruction().getTitle());
		mat.setDescription(app.getKernel().getConstruction().getWorksheetText(0));
		mat.setThumbnail(app.getEuclidianView1().getCanvasBase64WithTypeString());
		mat.setAuthor(app.getLoginOperation().getUserName());
		return mat;
	}

    public void search(String query) {
		getFiles(MaterialFilter.getSearchFilter(query));
    }
	
    public void getAllFiles() {
		getFiles(MaterialFilter.getUniversalFilter());
    }
	
	public void sync(final Material mat) {
		((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI())
		        .getItem(mat.getId(), new MaterialCallback() {

			        @Override
			        public void onLoaded(final List<Material> parseResponse) {
				        if ((parseResponse.size() == 1 && parseResponse.get(0).getModified() > mat.getSyncStamp())
				                || parseResponse.size() == 0) {
				        	ToolTipManagerW.sharedInstance().showBottomMessage("Note that there are several versions of: " + parseResponse.get(0).getTitle(), true);
					        mat.setId(0);
				        }
				        upload(mat);
			        }

			        @Override
			        public void onError(Throwable exception) {
				        //TODO
			        }
		        });
	}

	/**
	 * uploads the material and removes it from localStorage
	 * @param mat {@link Material}
	 */
	public void upload(final Material mat) {
	    ((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI()).uploadLocalMaterial(app, mat, new MaterialCallback() {

	    	@Override
	    	public void onLoaded(List<Material> parseResponse) {
	    		if (parseResponse.size() == 1) {
		    		delete(mat);
		    		((GuiManagerW) app.getGuiManager()).getBrowseGUI().refreshMaterial(parseResponse.get(0), false);
	    		}
	    	}

	    	@Override
	    	public void onError(Throwable exception) {
	    		//TODO
	    	}
	    });
    }

}

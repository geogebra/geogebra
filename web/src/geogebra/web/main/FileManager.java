package geogebra.web.main;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;
import geogebra.common.move.ggtapi.models.MaterialFilter;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.main.AppW;
import geogebra.html5.main.FileManagerI;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.browser.BrowseGUI;
import geogebra.web.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.web.move.ggtapi.models.MaterialCallback;
import geogebra.web.util.SaveCallback;

import java.util.List;

public abstract class FileManager implements FileManagerI{
	protected AppW app;

	public FileManager(final AppW app) {
	    this.app = app;
    }
	
	public abstract void delete(final Material mat);
	public abstract void openMaterial(final Material material);
	public abstract void saveFile(final SaveCallback cb);
	public abstract void removeFile(final Material mat);
	public abstract void addMaterial(final Material mat);
	public abstract void uploadUsersMaterials();
	protected abstract void getFiles(MaterialFilter materialFilter);
	
	protected Material createMaterial(final String base64) {
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

    /**
     * @param query String
     */
	public void search(final String query) {
		getFiles(MaterialFilter.getSearchFilter(query));
    }
	
    /**
     * adds the files from the current user to the {@link BrowseGUI}
     */
    public void getUsersMaterials() {
    	getFiles(MaterialFilter.getAuthorFilter(app.getLoginOperation().getUserName()));
    }
    
	public void sync(final Material mat) {
		((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI())
		        .getItem(mat.getId(), new MaterialCallback() {

			        @Override
			        public void onLoaded(final List<Material> parseResponse) {
				        if (parseResponse.size() == 1 && parseResponse.get(0).getModified() > mat.getSyncStamp()) {
				        	ToolTipManagerW.sharedInstance().showBottomMessage("Note that there are several versions of: " + parseResponse.get(0).getTitle(), true);
					        mat.setId(0);
				        } else if (parseResponse.size() == 0) {
				        	 mat.setId(0);
				        }
				        upload(mat);
			        }

			        @Override
			        public void onError(final Throwable exception) {
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
	    	public void onLoaded(final List<Material> parseResponse) {
	    		if (parseResponse.size() == 1) {
	    			delete(mat);
	    			final Material newMat = parseResponse.get(0);
	    			newMat.setThumbnail(mat.getThumbnail());		    		
		    		((GuiManagerW) app.getGuiManager()).getBrowseGUI().refreshMaterial(newMat, false);
	    		}
	    	}

	    	@Override
	    	public void onError(final Throwable exception) {
	    		//TODO
	    	}
	    });
    }

}

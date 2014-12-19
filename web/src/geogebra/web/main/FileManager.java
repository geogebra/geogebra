package geogebra.web.main;

import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;
import geogebra.common.move.ggtapi.models.Material.Provider;
import geogebra.common.move.ggtapi.models.MaterialFilter;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.main.AppW;
import geogebra.html5.main.FileManagerI;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.browser.BrowseGUI;
import geogebra.web.gui.browser.SignInButton;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.web.move.ggtapi.models.MaterialCallback;
import geogebra.web.util.SaveCallback;

import java.util.List;

public abstract class FileManager implements FileManagerI {
	private AppW app;
	private Provider provider;
	public static final String FILE_PREFIX = "file_";
	public static final String AUTO_SAVE_KEY = "autosave";

	public FileManager(final AppW app) {
	    this.app = app;
    }
	
	public abstract void delete(final Material mat);
	/**
	 * 
	 * @param base64 only a hint, we can send null and it will be resolved
	 * @param cb
	 */
	public abstract void saveFile(String base64, final SaveCallback cb);
	public abstract void uploadUsersMaterials();
	protected abstract void getFiles(MaterialFilter materialFilter);
	
	/**
	 * Overwritten for phone
	 * @param material {@link Material}
	 */
    public void removeFile(final Material material) {
		((BrowseGUI) app.getGuiManager().getBrowseGUI()).removeMaterial(material);
    }

	/**
	 * Overwritten for phone
	 * @param material {@link Material}
	 */
    public void addMaterial(final Material material) {
		((BrowseGUI) app.getGuiManager().getBrowseGUI()).addMaterial(material);
    }
	
	
	public Material createMaterial(final String base64) {
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
    	getFiles(MaterialFilter.getUniversalFilter());
//    	getFiles(MaterialFilter.getAuthorFilter(app.getLoginOperation().getUserName()));
    }
    
	public void sync(final Material mat) {
		((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI())
		        .getItem(mat.getId()+"", new MaterialCallback() {

			        @Override
			        public void onLoaded(final List<Material> parseResponse) {
			        	if (parseResponse.size() == 1 && parseResponse.get(0).getModified() > mat.getSyncStamp()) {
			        		if(FileManager.this.shouldKeep(mat.getId()) && mat.getModified() < mat.getSyncStamp()){
			        				App.debug("Local copy of "+mat.getId()+" is up to date.");
			        				return;
			        		}
			        		
			        		ToolTipManagerW.sharedInstance().showBottomMessage(app.getLocalization().getPlain("SeveralVersionsOfA", parseResponse.get(0).getTitle()) , true);
			        		mat.setId(0);
			        	} else if (parseResponse.size() == 0) {
			        		mat.setId(0);
			        	} else {
			        		FileManager.this.updateFile(mat.getTitle(),parseResponse.get(0));
			        	}
				        upload(mat);
			        }

			        @Override
			        public void onError(final Throwable exception) {
				        //TODO
			        }
		        });
	}

	protected abstract void updateFile(String title, Material material);

	/**
	 * uploads the material and removes it from localStorage
	 * @param mat {@link Material}
	 */
	public void upload(final Material mat) {
		final String localKey = mat.getTitle();
		mat.setTitle(getTitleFromKey(mat.getTitle()));
	    ((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI()).uploadLocalMaterial(app, mat, new MaterialCallback() {

	    	@Override
	    	public void onLoaded(final List<Material> parseResponse) {
	    		if (parseResponse.size() == 1) {
	    			mat.setTitle(localKey);
	    			App.debug("GGG uploading"+localKey);
	    			if(!FileManager.this.shouldKeep(mat.getId())){
	    				delete(mat);
	    			}else{
	    				FileManager.this.setTubeID(localKey, mat.getId());
	    			}
	    			App.debug("GGG parse"+localKey);
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

	public  abstract void setTubeID(String localKey, int id);

	public boolean shouldKeep(int id) {
	    return true;
    }

	/**
	 * key is of form "file_ID_title"
	 * @param key
	 * @return the title
	 */
	public String getTitleFromKey(String key) {
		return key.substring(key.indexOf("_", key.indexOf("_")+1)+1);
	}
	
	public void setFileProvider(Provider google){
		this.provider = google;
	}

	public Provider getFileProvider(){
		return this.provider;
	}
	
	/**
	 * returns the ID from the given key.
	 * (key is of form "file_ID_fileName")
	 * @param key String
	 * @return int ID
	 */
	public int getIDFromKey(String key) {
		return Integer.parseInt(key.substring(FILE_PREFIX.length(), key.indexOf("_", FILE_PREFIX.length())));
	}

	/**
	 * @param matID local ID of material
	 * @param title of material
	 * @return creates a key (String) for the stockStore
	 */
	public String createKeyString(int matID, String title) {
		return FILE_PREFIX + matID + "_" + title;
	}
	
	@Override
    public void openMaterial(final Material material) {
		try {
			final String base64 = material.getBase64();
			if (base64 == null) {
				return;
			}
			app.getGgbApi().setBase64(base64);
		} catch (final Throwable t) {
			app.showError(app.getLocalization().getError("LoadFileFailed"));
			t.printStackTrace();
		}
    }
	
	/**
	 * only for FileManagerT and FileManagerW
	 * @return {@link AppW}
	 */
	public AppW getApp() {
		return this.app;
	}
	
	public final boolean save(AppW app){
		//not logged in and can't log in
		if (!app.getLoginOperation().isLoggedIn()
				&& (!app.getNetworkOperation().isOnline() || !app.getLoginOperation().mayLogIn())) {
			saveLoggedOut(app);
		//not logged in and possible to log in
		} else if (!app.getLoginOperation().isLoggedIn()) {
			app.getGuiManager().listenToLogin();
			((SignInButton) app.getLAF().getSignInButton(app)).login();
		//logged in
		} else {
			((DialogManagerW) app.getDialogManager()).showSaveDialog();
		}
		return true;
	}

	
}

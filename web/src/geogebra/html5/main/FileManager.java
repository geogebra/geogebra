package geogebra.html5.main;

import geogebra.common.move.ggtapi.models.Material;

import com.google.gwt.core.client.Callback;

public interface FileManager {

	public void delete(final Material mat);
	public void getAllFiles();
	public void openMaterial(final Material material);
	public void saveFile(final Callback<String, Throwable> cb);
	public void search(final String query);
	public void removeFile(final Material mat);
	public void addFile(final Material mat);
}

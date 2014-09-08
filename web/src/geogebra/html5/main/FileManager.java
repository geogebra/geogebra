package geogebra.html5.main;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.util.SaveCallback;

public interface FileManager {

	public void delete(final Material mat);
	public void getAllFiles();
	public void openMaterial(final Material material);
	public void saveFile(final SaveCallback cb);
	public void search(final String query);
	public void removeFile(final Material mat);
	public void addFile(final Material mat);
}

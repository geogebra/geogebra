package geogebra.html5.main;

import geogebra.common.move.ggtapi.models.Material;

public interface FileManagerI {

	void openMaterial(Material material);

	void delete(Material material);

	void uploadUsersMaterials();

	void getUsersMaterials();

	void search(String query);

	void rename(String newTitle, String oldTitle);

}

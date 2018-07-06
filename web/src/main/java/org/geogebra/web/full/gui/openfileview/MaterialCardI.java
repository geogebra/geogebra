package org.geogebra.web.full.gui.openfileview;

public interface MaterialCardI {

	void setVisible(boolean b);

	void remove();

	void onConfirmDelete();

	void rename(String text);

	void setMaterialTitle(String oldTitle);

}

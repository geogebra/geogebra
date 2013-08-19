package geogebra.touch.gui.elements.header;

import geogebra.common.gui.SetLabels;
import geogebra.common.move.ggtapi.models.Material;

public interface WorksheetHeader extends SetLabels {
	public void setMaterial(Material m);
}

package geogebra.common.javax.swing;

import geogebra.common.awt.Font;
import geogebra.common.kernel.geos.GeoElement;

public abstract class AbstractJComboBox {

	public abstract void setVisible(boolean b);

	public abstract Object getItemAt(int i);

	public abstract void setFont(Font font);

	public abstract void setForeground(geogebra.common.awt.Color objectColor);

	public abstract void setBackground(geogebra.common.awt.Color color);

	public abstract void setFocusable(boolean b);

	public abstract void setEditable(boolean b);

	public abstract void addItem(String string);

	public abstract void setSelectedIndex(int selectedIndex);

	public abstract int getSelectedIndex();


}

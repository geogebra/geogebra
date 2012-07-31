package geogebra.common.javax.swing;

import geogebra.common.awt.GFont;

public abstract class AbstractJComboBox {

	public abstract void setVisible(boolean b);

	public abstract Object getItemAt(int i);

	public abstract void setFont(GFont font);

	public abstract void setForeground(geogebra.common.awt.GColor objectColor);

	public abstract void setBackground(geogebra.common.awt.GColor color);

	public abstract void setFocusable(boolean b);

	public abstract void setEditable(boolean b);

	public abstract void addItem(String string);

	public abstract void setSelectedIndex(int selectedIndex);

	public abstract int getSelectedIndex();

	public abstract void addActionListener(
			geogebra.common.euclidian.event.ActionListener newActionListener);

	public abstract void removeAllItems();

	public abstract int getItemCount();

}

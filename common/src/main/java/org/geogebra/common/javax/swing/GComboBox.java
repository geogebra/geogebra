package org.geogebra.common.javax.swing;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.euclidian.event.ActionListener;

public abstract class GComboBox {

	public abstract void setVisible(boolean b);

	public abstract Object getItemAt(int i);

	public abstract void setFont(GFont font);

	public abstract void setForeground(GColor objectColor);

	public abstract void setBackground(GColor color);

	public abstract void setFocusable(boolean b);

	public abstract void setEditable(boolean b);

	public abstract void addItem(String string);

	public abstract void setSelectedIndex(int selectedIndex);

	public abstract int getSelectedIndex();

	public abstract void addActionListener(ActionListener newActionListener);

	public abstract void removeAllItems();

	public abstract int getItemCount();

}

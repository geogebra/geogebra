package org.geogebra.common.javax.swing;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;

public abstract class GLabel {

	public abstract void setVisible(boolean b);

	public abstract void setText(String labelDesc);

	public abstract void setOpaque(boolean b);

	public abstract void setFont(GFont font);

	public abstract void setForeground(GColor objectColor);

	public abstract void setBackground(GColor lightGray);

}

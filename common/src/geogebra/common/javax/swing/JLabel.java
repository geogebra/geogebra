package geogebra.common.javax.swing;

import geogebra.common.awt.Color;
import geogebra.common.awt.Font;

public abstract class JLabel {

	public abstract void setVisible(boolean b);

	public abstract void setText(String labelDesc);

	public abstract void setOpaque(boolean b);

	public abstract void setFont(Font font);

	public abstract void setForeground(Color objectColor);

	public abstract void setBackground(Color lightGray);


}

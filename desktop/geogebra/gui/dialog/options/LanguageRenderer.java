package geogebra.gui.dialog.options;

import geogebra.awt.GFontD;
import geogebra.common.main.App;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * 
 * Make sure eg Malayalam is displayed in the correct font (characters not in default Java font)
 * 
 * @author michael
 *
 */
@SuppressWarnings("serial")
public class LanguageRenderer  extends DefaultListCellRenderer {
	
	private App app;

	@SuppressWarnings("javadoc")
	LanguageRenderer(App app) {
		super();
		this.app = app;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		Component ret = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		if (value instanceof String) {
			String language = (String) value;
			ret.setFont(GFontD.getAwtFont(app.getFontCanDisplay(language)));
		}
		return ret;
	
	}

}

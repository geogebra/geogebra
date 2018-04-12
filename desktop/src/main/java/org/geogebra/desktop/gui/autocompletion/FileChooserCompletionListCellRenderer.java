package org.geogebra.desktop.gui.autocompletion;

import java.awt.Component;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.util.GuiResourcesD;
import org.geogebra.desktop.util.ImageResourceD;

/**
 * A simple list cell renderer derived from {@link DefaultListCellRenderer}.
 * Sets icons on the returned labels depending on the file extension.
 * 
 * @author Julian Lettner
 */
public class FileChooserCompletionListCellRenderer
		extends DefaultListCellRenderer {
	private static final long serialVersionUID = 1L;

	private static final Icon DIRECTORY_ICON = loadIcon(GuiResourcesD.FOLDER);
	private static final Icon UNKNOWN_FILE_ICON = loadIcon(
			GuiResourcesD.TEXT_X_GENERIC);

	// Most of these Icons are from the the tango! icon set:
	// http://tango.freedesktop.org/
	private static final Map<String, Icon> FILE_EXT_ICONS = new HashMap<>();
	static {
		Icon icon;
		// ggb, ggt
		icon = loadIcon(GuiResourcesD.GEOGEBRA);
		FILE_EXT_ICONS.put("ggb", icon);
		FILE_EXT_ICONS.put("ggt", icon);
		// html, htm
		icon = loadIcon(GuiResourcesD.TEXT_HTML);
		FILE_EXT_ICONS.put("html", icon);
		FILE_EXT_ICONS.put("htm", icon);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		// Assumes that the values are of type 'File'
		File file = (File) value;
		// Cast is safe, DefaultListCellRenderer will always return a label
		JLabel label = (JLabel) super.getListCellRendererComponent(list,
				file.getName(), index, isSelected, cellHasFocus);
		label.setIcon(getIcon(file));

		return label;
	}

	private static Icon getIcon(File file) {
		if (file.isDirectory()) {
			return DIRECTORY_ICON;
		}

		// inclusive toLower, default is ""
		String fileExt = StringUtil.getFileExtensionStr(file.getName());
		Icon icon = FILE_EXT_ICONS.get(fileExt);
		if (icon == null) {
			icon = UNKNOWN_FILE_ICON;
		}

		return icon;
	}

	private static Icon loadIcon(ImageResourceD iconImage) {
		URL iconUrl = FileChooserCompletionListCellRenderer.class
				.getResource("/org/geogebra/desktop" + iconImage.getFilename());
		if (iconUrl == null) {
			Log.debug("Could not load icon: " + iconImage);
			return new ImageIcon();
		}
		return new ImageIcon(iconUrl);
	}
}

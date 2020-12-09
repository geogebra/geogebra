package org.geogebra.resources;

import java.net.URL;

import javax.swing.JPanel;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.util.StringUtil;
import org.geogebra.desktop.geogebra3D.gui.GuiResources3D;
import org.geogebra.desktop.util.GuiResourcesD;
import org.geogebra.desktop.util.ImageManagerD;
import org.geogebra.desktop.util.ImageResourceD;
import org.geogebra.desktop.util.StringUtilD;
import org.junit.Assert;
import org.junit.Test;

public class ResourceAvailability {
	@Test
	public void checkGuiResources() {
		for (GuiResourcesD res : GuiResourcesD.values()) {
			String fn = res.getFilename();
			if (!fn.startsWith("/org")) {
				fn = "/org/geogebra/desktop/" + fn;
			}
			URL url = ResourceAvailability.class.getResource(fn);
			Assert.assertNotNull("" + res, url);
		}
	}

	@Test
	public void checkGuiResources3D() {
		for (GuiResources3D res : GuiResources3D.values()) {
			String fn = res.getFilename();
			if (!fn.startsWith("/org")) {
				fn = "/org/geogebra/desktop/geogebra3D" + fn;
			}
			URL url = ResourceAvailability.class.getResource(fn);
			Assert.assertNotNull("" + res, url);
		}
	}

	@Test
	public void checkToolIcons() {
		StringUtil.setPrototypeIfNull(new StringUtilD());
		ImageManagerD man = new ImageManagerD(new JPanel());
		StringBuilder missing = new StringBuilder();

		for (int i = 0; i < 1000; i++) {
			String modeText = EuclidianConstants.getModeTextSimple(i);

			if (modeText.isEmpty()) {
				continue;
			}
			switch (i) {
			case EuclidianConstants.MODE_SELECTION_LISTENER:
			case EuclidianConstants.MODE_PEN_PANEL:
			case EuclidianConstants.MODE_TOOLS_PANEL:
			case EuclidianConstants.MODE_MEDIA_PANEL:
			case EuclidianConstants.MODE_VIDEO:
			case EuclidianConstants.MODE_AUDIO:
			case EuclidianConstants.MODE_GRAPHING:
			case EuclidianConstants.MODE_EXTENSION:
			case EuclidianConstants.MODE_H5P:
			case EuclidianConstants.MODE_TABLE:
			case EuclidianConstants.MODE_EQUATION:
			case EuclidianConstants.MODE_CAMERA:
			case EuclidianConstants.MODE_PDF:
			case EuclidianConstants.MODE_GRASPABLE_MATH:
			case EuclidianConstants.MODE_CAS:
			case EuclidianConstants.MODE_SURFACE_OF_REVOLUTION:
			case EuclidianConstants.MODE_FREEHAND_FUNCTION:
			case EuclidianConstants.MODE_MASK:
				continue;
			default:
				ImageResourceD res = man.getToolImageResource(modeText);
				URL url = ResourceAvailability.class.getResource(res
						.getFilename());
				if (url == null) {
					missing.append(modeText + ",");
				}
			}

		}

		Assert.assertEquals(missing.toString(), missing.toString(), "");
	}
}

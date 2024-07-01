package org.geogebra.desktop.util;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.net.URL;

import javax.swing.JPanel;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.util.StringUtil;
import org.geogebra.desktop.geogebra3D.gui.GuiResources3D;
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
	public void noDupes() {
		assertNoDupesRecursive("src/main/resources", "src/gpl/resources");
		assertNoDupesRecursive("src/main/resources", "src/nonfree/resources");
	}

	private void assertNoDupesRecursive(String f1, String f2) {
		for (String fn: new File(f1).list()) {
			if (new File(f1 + "/" + fn).isDirectory()) {
				assertNoDupesRecursive(f1 + "/" + fn, f2 + "/" + fn);
			} else {
				assertFalse("Duplicate found:" + f2 + "/" + fn,
						new File(f2 + "/" + fn).exists());
			}
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
			case EuclidianConstants.MODE_VIDEO:
			case EuclidianConstants.MODE_AUDIO:
			case EuclidianConstants.MODE_CALCULATOR:
			case EuclidianConstants.MODE_EXTENSION:
			case EuclidianConstants.MODE_H5P:
			case EuclidianConstants.MODE_TABLE:
			case EuclidianConstants.MODE_EQUATION:
			case EuclidianConstants.MODE_CAMERA:
			case EuclidianConstants.MODE_PDF:
			case EuclidianConstants.MODE_GRASPABLE_MATH:
			case EuclidianConstants.MODE_SURFACE_OF_REVOLUTION:
			case EuclidianConstants.MODE_MASK:
			case EuclidianConstants.MODE_MIND_MAP:
			case EuclidianConstants.MODE_RULER:
			case EuclidianConstants.MODE_PROTRACTOR:
			case EuclidianConstants.MODE_TRIANGLE_PROTRACTOR:
			case EuclidianConstants.MODE_PHOTO_LIBRARY:
				continue;
			default:
				ImageResourceD res = man.getToolImageResource(modeText);
				URL url = ResourceAvailability.class.getResource(res
						.getFilename());
				if (url == null) {
					missing.append(res.getFilename()).append(",");
				}
			}

		}

		Assert.assertEquals(missing.toString(), 0, missing.length());
	}
}

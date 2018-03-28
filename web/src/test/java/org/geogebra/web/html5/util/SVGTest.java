package org.geogebra.web.html5.util;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.TreeSet;

import org.geogebra.web.full.css.ToolbarSvgResources;


public class SVGTest {
	/**
	 * 
	 */
	// @Test
	public void rmExtraSVGS() {
		File svgs = new File(
				"src/main/java/org/geogebra/web/full/gui/toolbar/svgimages/");
		TreeSet<String> disk = new TreeSet<>();
		try {
			for (File icon : svgs.listFiles()) {
				String path =icon.getAbsolutePath();
				path = path.substring(path.indexOf("org")).replace('\\', '/');
				disk.add(path);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Method m : ToolbarSvgResources.class.getMethods()) {
			Annotation[] a = m.getAnnotations();
			if (a != null && a.length > 0) {
				String src = a[0].toString();
				src = src.substring(src.indexOf("org/"), src.indexOf("]"));
				disk.remove(src);
			}
		}
		for (String s : disk) {
			System.out.println("svn rm " + s);
		}
	}
}

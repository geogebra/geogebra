package org.geogebra.web.html5.util;

import static com.ibm.icu.impl.Assert.fail;
import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.full.css.ToolbarSvgResourcesSync;
import org.junit.Test;

public class SVGTest {

	@Test
	public void rmExtraSVGS() {
		Path svgs = Paths.get(
				"../common/src/nonfree/resources/org/geogebra/common/icons/svg/web/toolIcons");
		Path root = Paths.get("../common/src/nonfree/resources");
		TreeSet<String> disk = new TreeSet<>();
		try {
			Files.list(svgs).map(root::relativize)
					.map(s -> s.toString().replace('\\', '/'))
					.forEach(disk::add);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		Stream.concat(Arrays.stream(ToolbarSvgResources.class.getMethods()),
				Arrays.stream(ToolbarSvgResourcesSync.class.getMethods())).forEach(m -> {
			Annotation[] a = m.getAnnotations();
			if (a != null && a.length > 0) {
				String src = a[0].toString();
				src = src.substring(src.indexOf("org/"), src.indexOf("]"));
				disk.remove(src);
			}
		});
		assertEquals("", StringUtil.join(",", disk));
	}
}

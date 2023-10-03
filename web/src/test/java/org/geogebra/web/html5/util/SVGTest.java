package org.geogebra.web.html5.util;

import static com.ibm.icu.impl.Assert.fail;
import static org.junit.Assert.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.full.css.ToolbarSvgResourcesSync;
import org.gwtproject.resources.client.ClientBundle;
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
			ClientBundle.Source a = m.getAnnotation(ClientBundle.Source.class);
			if (a != null) {
				String src = a.value()[0];
				disk.remove(src);
			}
		});
		assertEquals("", StringUtil.join(",", disk));
	}
}

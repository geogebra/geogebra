package org.geogebra.web.full.css;

import org.geogebra.web.resources.SVGResource;

public interface ResourceIconProvider {

	SVGResource newFileMenu();

	SVGResource openFileMenu();

	SVGResource fileMenu();

	SVGResource downloadMenu();
}

package org.geogebra.web.html5.main.toolbox;

import org.geogebra.web.html5.gui.view.IconSpec;

public class MebisToolboxIconProvider extends DefaultToolboxIconProvider {

	@Override
	public IconSpec matchIconWithResource(ToolboxIcon icon) {
		switch (icon) {
		case MOUSE_CURSOR:
			return new FaIconSpec("fa-arrow-pointer");
		case PEN:
			return new FaIconSpec("fa-pen");
		case HIGHLIGHTER:
			return new FaIconSpec("fa-highlighter");
		case ERASER:
			return new FaIconSpec("fa-eraser");
		case PLUS:
			return new FaIconSpec("fa-plus");
		case SHAPES:
			return new FaIconSpec("fa-shapes");
		case TEXTS:
			return new FaIconSpec("fa-text-size");
		case TEXT:
			return new FaIconSpec("fa-square-t");
		case EQUATION:
			return new FaIconSpec("fa-calculator-simple");
		case UPLOAD:
			return new FaIconSpec("fa-arrow-up-from-line");
		case IMAGE:
			return new FaIconSpec("fa-image");
		case CAMERA:
			return new FaIconSpec("fa-camera");
		case PDF:
			return new FaIconSpec("fa-file-pdf");
		case LINK:
			return new FaIconSpec("fa-link");
		case WEB:
			return new FaIconSpec("fa-globe");
		case VIDEO:
			return new FaIconSpec("fa-video");
		case AUDIO:
			return new FaIconSpec("fa-microphone");
		case APPS:
			return new FaIconSpec("fa-square-plus");
		case MINDMAP:
			return new FaIconSpec("fa-sitemap");
		case TABLE:
			return new FaIconSpec("fa-table");
		case RULER:
			return new FaIconSpec("fa-ruler-horizontal");
		case TRIANGLE:
			return new FaIconSpec("fa-ruler-triangle");
		case SPOTLIGHT:
			return new FaIconSpec("fa-location-crosshairs");
		}
		return super.matchIconWithResource(icon);
	}
}

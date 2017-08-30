/**********************************************
 * Based on SVG resource generator by Lukas Laag
 * 
 * libgwtsvg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * libgwtsvg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with libgwtsvg.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package org.geogebra.web.generator;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.geogebra.web.resources.SassResource;

import io.bit3.jsass.Options;
import io.bit3.jsass.Output;
import io.bit3.jsass.OutputStyle;

/**
 * Provides implementations of SVGResource.
 */
public class SassResourceGenerator extends AsciiResourceGenerator {

	@Override
	protected String process(String css, URL resource) throws Exception {
		URI inputFile = null;
		try {
			inputFile = resource.toURI();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		URI outputFile = new File("stylesheet.css").toURI();

		Options options = new Options();
		options.setOutputStyle(OutputStyle.COMPRESSED);

		Output output = new io.bit3.jsass.Compiler().compileString(css,
				inputFile, outputFile, options);

		return output.getCss();

	}

	@Override
	protected String getClassName() {
		return SassResource.class.getName();
	}

}

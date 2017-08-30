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

import java.net.URL;

import org.geogebra.web.resources.LessReference;

/**
 * Provides implementations of LessReference.
 */
public class LessReferenceGenerator extends AsciiResourceGenerator {

	@Override
	protected String process(String css, URL inputFile) throws Exception {
		return "";
	}

	@Override
	protected String getClassName() {
		return LessReference.class.getName();
	}


}

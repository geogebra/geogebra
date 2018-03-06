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

package org.geogebra.web.vectomatic;

import java.net.URL;

import org.geogebra.web.generator.AsciiResourceGenerator;
import org.geogebra.web.html5.util.LessResource;

import com.asual.lesscss.LessEngine;

/**
 * Provides implementations of SVGResource.
 */
public class LessResourceGenerator extends AsciiResourceGenerator {

	@Override
	public String process(String css, URL resource) throws Exception {
		LessEngine leg = new LessEngine();
		return leg.compile(resource, true);
	}

	@Override
	protected String getClassName() {
		// TODO Auto-generated method stub
		return LessResource.class.getName();
	}

}

/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.common.gui.view.algebra;

import geogebra.common.kernel.View;
/**
 * Algebra view -- shows algebraic representation of the objects either as value,
 * definition or command
 */
public interface AlgebraView extends View{
	/**
	 * Returns whether this view is currently visible
	 * @return whether this view is currently visible
	 */
	public boolean isVisible();
}

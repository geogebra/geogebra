package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;

/**
 * Sum command helper
 * 
 * @author Zbynek
 *
 */
public interface FoldComputer {

	public GeoElement getTemplate(Construction cons, GeoClass listElement);

	public void add(GeoElement geoElement, Operation op);

	public void setFrom(GeoElement geoElement, Kernel kernel);

	public boolean check(GeoElement geoElement);

	public void finish();

}
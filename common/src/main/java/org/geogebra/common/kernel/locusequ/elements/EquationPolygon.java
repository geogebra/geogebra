/**
 * 
 */
package org.geogebra.common.kernel.locusequ.elements;

import org.geogebra.common.kernel.algos.AlgoPolygon;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.locusequ.EquationScope;

/**
 * @author sergio
 * EquationElement for {@link AlgoPolygon}
 */
public class EquationPolygon extends EquationGenericPolygon {

    /**
     * General constructor.
     * @param element {@link GeoElement}
     * @param scope {@link EquationScope}
     */
    public EquationPolygon(final GeoElement element, final EquationScope scope) {
        super(element, scope);
        
        GeoPolygon polygon = (GeoPolygon) element;
        
        this.setSegments(polygon.getSegments());
    }
}

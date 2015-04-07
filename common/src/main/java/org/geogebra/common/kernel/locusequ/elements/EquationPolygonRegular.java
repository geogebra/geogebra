/**
 * 
 */
package org.geogebra.common.kernel.locusequ.elements;

import org.geogebra.common.kernel.algos.AlgoPolygonRegular;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.locusequ.EquationScope;

/**
 * @author sergio
 * EquationElement for {@link AlgoPolygonRegular}
 */
public class EquationPolygonRegular extends EquationGenericPolygon {

    /**
     * General constructor.
     * @param element {@link GeoElement}
     * @param scope {@link EquationScope} 
     */
    public EquationPolygonRegular(final GeoElement element, final EquationScope scope) {
        super(element, scope);
        
        GeoPolygon polygon = (GeoPolygon) element;
        
        this.setSegments(polygon.getSegments());
    }
}

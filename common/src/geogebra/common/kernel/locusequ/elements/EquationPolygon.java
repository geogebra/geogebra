/**
 * 
 */
package geogebra.common.kernel.locusequ.elements;

import geogebra.common.kernel.algos.AlgoPolygon;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.locusequ.EquationScope;

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

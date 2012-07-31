/**
 * 
 */
package geogebra.common.kernel.locusequ.elements;

import geogebra.common.kernel.algos.AlgoJoinPoints;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationPoint;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.locusequ.SymbolicVector;

/**
 * @author sergio
 * {@link EquationElement}�for {@link AlgoJoinPoints}
 */
public class EquationJoinPoints extends EquationGenericLine {


    private GeoPoint q;
    private EquationPoint qequ;
    
    /**
     * @param line {@link GeoLine} from the construction.
     * @param scope where the {@link EquationPoint}s are.
     */
    public EquationJoinPoints(GeoElement line, EquationScope scope) {
        super(line, scope);
        AlgoJoinPoints algo = (AlgoJoinPoints) line.getParentAlgorithm();

        this.q = algo.getQ();
        this.qequ = this.getScope().getPoint(this.q);
        
        this.setPoint(algo.getP());
        this.setVector(new SymbolicVector(this.getEquationPoint(), this.qequ));
    }
    
    /**
     * Auxiliary constructor when no {@link GeoLine} is involved.
     * @param startPoint first point.
     * @param anotherPoint second point.
     * @param scope scope.
     */
    public EquationJoinPoints(final EquationPoint startPoint, final EquationPoint anotherPoint, final EquationScope scope) {
        super();
        this.setScope(scope);
        
        this.setPoint(startPoint);
        this.setVector(new SymbolicVector(startPoint, anotherPoint));
    }
}

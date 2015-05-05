/**
 * 
 */
package org.geogebra.common.kernel.locusequ.elements;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoJoinPoints;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.locusequ.EquationElement;
import org.geogebra.common.kernel.locusequ.EquationPoint;
import org.geogebra.common.kernel.locusequ.EquationScope;
import org.geogebra.common.kernel.locusequ.SymbolicVector;

/**
 * @author sergio {@link EquationElement} for {@link AlgoJoinPoints}
 */
public class EquationFreeLine extends EquationGenericLine {


    private GeoPoint q;
    private EquationPoint qequ;
    
    /**
     * @param line {@link GeoLine} from the construction.
     * @param scope where the {@link EquationPoint}s are.
     */
    public EquationFreeLine(GeoLine line, EquationScope scope) {
        super(line, scope);
       // AlgoJoinPoints algo = (AlgoJoinPoints) line.getParentAlgorithm();
        GeoPoint start = new GeoPoint(line.getConstruction()), 
        		end = new GeoPoint(line.getConstruction());
        if(Kernel.isZero(line.getX())){
        	// k*y=c
        	start.setCoords(0, -line.getZ()/line.getY(), 1);
        	end.setCoords(1, -line.getZ()/line.getY(), 1);
        }else{
        	// k*x+l*y=c
        	start.setCoords(-line.getZ()/line.getX(),0, 1);
        	end.setCoords((-line.getZ()-line.getY())/line.getX(),1, 1);
        }

        start.update();
        end.update();
        this.q = start;
        this.qequ = this.getScope().getPoint(this.q);        
        this.setPoint(end);
        this.setVector(new SymbolicVector(this.getEquationPoint(), this.qequ));
    }
}

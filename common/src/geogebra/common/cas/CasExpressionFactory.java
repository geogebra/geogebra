/**  class to manipultae ValidExpression trees
 *  @author dominik kreil 
 * */

package geogebra.common.cas;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Equation;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.MyList;
import geogebra.common.kernel.arithmetic.ReplaceableValue;
import geogebra.common.plugin.Operation;


public class CasExpressionFactory {
	private ExpressionValue root;
	private Kernel kernel;
	
	public CasExpressionFactory(ExpressionValue newRoot){
		this.root = newRoot;
		kernel = newRoot.getKernel();
	}

	/* replace roots by rational exponents */
	public ExpressionValue replaceRootsByExp(){
		if(root instanceof ReplaceableValue) 
			((ReplaceableValue)root).replacePowersRoots(false);
		return root;
	}
	
	/* replace rational exponents by roots */
	public ExpressionValue replaceExpByRoots(){
		if(root instanceof ReplaceableValue) 
			((ReplaceableValue)root).replacePowersRoots(true);
		return root;
	}
}

/**  class to manipultae ValidExpression trees
 *  @author dominik kreil 
 * */

package geogebra.cas;

import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.arithmetic.Operation;


public class CasExpressionFactory {
	private ValidExpression root;
	
	public CasExpressionFactory(ValidExpression newRoot){
		this.root = newRoot;
	}

	/* replace rational exponents by roots */
	public ValidExpression replaceExpByRoots(){
		if(!root.isLeaf() && root.isExpressionNode())
			root = replaceExpByRootsRecursive((ExpressionNode)root);
		else
			System.out.println("parameter was not an ExpressionNode");
		return root;
	}
	
	private ExpressionNode replaceExpByRootsRecursive(ExpressionNode branch){	
		if(branch.getOperation()==Operation.POWER && branch.getRight().isExpressionNode()){
			ExpressionNode rightLeaf = (ExpressionNode)branch.getRight();
			boolean hit = false;
			if((rightLeaf.getOperation()==Operation.DIVIDE)) {
					if(rightLeaf.getRight().toString().equals("2")){
						branch.setOperation(Operation.SQRT);
						hit = true;
					}else if(rightLeaf.getRight().toString().equals("3")){
						branch.setOperation(Operation.CBRT);
						hit = true;
					}
					if(hit){
						if(rightLeaf.getLeft().toString().equals("1")){
							branch.setRight(new MyDouble(branch.getKernel(), Double.NaN)); 
						}
						else{   // to parse x^(c/2) to sqrt(x^c)
							branch.setLeft(new ExpressionNode(branch.getKernel(), branch.getLeft(), Operation.POWER, rightLeaf.getLeft()));
						}
					}
										
			}
		}	
		
		if(branch.getLeft().isExpressionNode() && !branch.getLeft().isLeaf())
			branch.setLeft(replaceExpByRootsRecursive((ExpressionNode)branch.getLeft()));
		if(branch.getRight().isExpressionNode() && !branch.getRight().isLeaf())
			branch.setRight(replaceExpByRootsRecursive((ExpressionNode)branch.getRight()));
		return branch;
	}
}

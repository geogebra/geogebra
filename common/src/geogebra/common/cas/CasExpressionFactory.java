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
import geogebra.common.kernel.arithmetic.ValidExpression;
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
		if(root.isExpressionNode()) 
			root = replaceRootsByExpRecursive(root);
		return root;
	}
	
	/* replace rational exponents by roots */
	public ExpressionValue replaceExpByRoots(){
		if(root.isExpressionNode()) 
			root = replaceExpByRootsRecursive((ExpressionNode)root);
		return root;
	}
	
	
	//TODO: remove the wild casting!!!!
	
	private ExpressionValue replaceRootsByExpRecursive(ExpressionValue branch){
		boolean hit = false;
		if(branch == null) return branch;		
		
		ExpressionNode power = null;

		//check right branch
		if(branch.isExpressionNode()){	
			//replaces   SQRT 2 by 1 DIVIDE 2, and same for CBRT
			if((((ExpressionNode)branch).getOperation()==Operation.SQRT)) {
				power = new ExpressionNode(kernel, new MyDouble(kernel, 1), Operation.DIVIDE, new MyDouble(kernel, 2));
				hit = true;
			}
			if((((ExpressionNode)branch).getOperation()==Operation.CBRT)){
				power = new ExpressionNode(kernel, new MyDouble(kernel, 1), Operation.DIVIDE, new MyDouble(kernel, 3));
				hit = true;
			}	
			if(hit){
				((ExpressionNode)branch).setOperation(Operation.POWER);
				((ExpressionNode)branch).setRight(power);
			}
		}		
	
		//if branch is a list, go through all list elements
		if(branch instanceof MyList){
			for(int i=0; i < ((MyList)branch).size(); i++)
				replaceRootsByExpRecursive((ExpressionValue)((MyList)branch).getListElement(i));
					
		}

		//if branch is an equation, go through rhs and lhs
		if(branch instanceof Equation){
			ExpressionNode newRHS = (ExpressionNode) replaceRootsByExpRecursive(((Equation)branch).getRHS());
			ExpressionNode newLHS = (ExpressionNode) replaceRootsByExpRecursive(((Equation)branch).getLHS());
			((Equation)branch).setLHS(newLHS);
			((Equation)branch).setRHS(newRHS);
			//((ExpressionNode)parent).setLeft(new Equation(kernel,newLHS,newRHS));
		}
		
		
		//if branch is a normal ExpressionNode, go through left and right branch
		if(branch instanceof ExpressionNode){
			((ExpressionNode)branch).setRight(replaceRootsByExpRecursive(((ExpressionNode)branch).getRight()));
			((ExpressionNode)branch).setLeft(replaceRootsByExpRecursive(((ExpressionNode)branch).getLeft()));
		}
		
		return branch;
	}
	
	private ExpressionValue replaceExpByRootsRecursive(ExpressionValue branch){	
		if(branch.isExpressionNode()){
			ExpressionNode tempBranch = (ExpressionNode)branch;   //just for better readability
			if(tempBranch.getOperation()==Operation.POWER && tempBranch.getRight().isExpressionNode()){
				ExpressionNode rightLeaf = (ExpressionNode) ((ExpressionNode)branch).getRight();
				boolean hit = false;
				
				//replaces 1 DIVIDE 2 by SQRT 2, and same for CBRT
				if((rightLeaf.getOperation()==Operation.DIVIDE)) {
						if(rightLeaf.getRight().toString().equals("2")){
							tempBranch.setOperation(Operation.SQRT);
							hit = true;
						}else if(rightLeaf.getRight().toString().equals("3")){
							tempBranch.setOperation(Operation.CBRT);
							hit = true;
						}
						if(hit){
							if(rightLeaf.getLeft().toString().equals("1")){
								tempBranch.setRight(new MyDouble(branch.getKernel(), Double.NaN)); 
							}
							else{   // to parse x^(c/2) to sqrt(x^c)
								tempBranch.setLeft(new ExpressionNode(branch.getKernel(), tempBranch.getLeft(), Operation.POWER, rightLeaf.getLeft()));
							}
						}
											
				}
			}
		//if branch is a list, go through all list elements
		if(tempBranch.getRight() instanceof MyList){
			MyList right = (MyList)tempBranch.getRight();
			for(int i=0; i < right.size(); i++)
				replaceExpByRootsRecursive(right.getListElement(i));
					
		}
		if(tempBranch.getLeft() instanceof MyList){
			MyList left = (MyList)tempBranch.getLeft();
			for(int i=0; i < left.size(); i++)
				replaceExpByRootsRecursive(left.getListElement(i));
		}
		
		//if branch has an equation, go through rhs and lhs
		if(tempBranch.getRight() instanceof Equation){
			ExpressionNode newRHS = (ExpressionNode) replaceRootsByExpRecursive(((Equation)tempBranch.getRight()).getRHS());
			ExpressionNode newLHS = (ExpressionNode) replaceRootsByExpRecursive(((Equation)tempBranch.getRight()).getLHS());
			tempBranch.setRight(new Equation(kernel, newLHS, newRHS));
		}
		if(tempBranch.getLeft() instanceof Equation){
			ExpressionNode newRHS = (ExpressionNode) replaceRootsByExpRecursive(((Equation)tempBranch.getLeft()).getRHS());
			ExpressionNode newLHS = (ExpressionNode) replaceRootsByExpRecursive(((Equation)tempBranch.getLeft()).getLHS());
			tempBranch.setLeft(new Equation(kernel, newLHS, newRHS));
		}
		
		if(tempBranch.getLeft() != null)	
			if(tempBranch.getLeft().isExpressionNode() && !tempBranch.getLeft().isLeaf())
				tempBranch.setLeft(replaceExpByRootsRecursive(tempBranch.getLeft()));
		if(tempBranch.getRight() != null)
			if(tempBranch.getRight().isExpressionNode() && !tempBranch.getRight().isLeaf())
				tempBranch.setRight(replaceExpByRootsRecursive(tempBranch.getRight()));
		}
		return branch;
	}
}

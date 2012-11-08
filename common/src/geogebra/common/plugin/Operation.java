package geogebra.common.plugin;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNodeEvaluator;
import geogebra.common.kernel.arithmetic.ExpressionValue;

@SuppressWarnings("javadoc")
public enum Operation { NO_OPERATION, NOT_EQUAL, NOT, OR,AND,IMPLICATION,EQUAL_BOOLEAN,LESS,GREATER,LESS_EQUAL
	,GREATER_EQUAL,PARALLEL,PERPENDICULAR ,IS_ELEMENT_OF,IS_SUBSET_OF
	,IS_SUBSET_OF_STRICT,SET_DIFFERENCE,PLUS{
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,ExpressionValue lt, ExpressionValue rt,
				 ExpressionValue left, ExpressionValue right, StringTemplate tpl,boolean holdsLaTeX) {
			return ev.handlePlus(lt,rt,tpl,holdsLaTeX);
			
		}
	},MINUS{
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,ExpressionValue lt, ExpressionValue rt,
				 ExpressionValue left, ExpressionValue right, StringTemplate tpl,boolean holdsLaTeX) {
			return ev.handleMinus(lt,rt);
			
		}
	},VECTORPRODUCT,

// these next three must be adjacent
// so that brackets work for eg a/(b/c)
// and are removed in (a/b)/c
// see case DIVIDE in ExpressionNode
MULTIPLY{
	@Override
	public ExpressionValue handle(ExpressionNodeEvaluator ev,ExpressionValue lt, ExpressionValue rt,
			 ExpressionValue left, ExpressionValue right, StringTemplate tpl,boolean holdsLaTeX) {
		return ev.handleMult(lt,rt,tpl,holdsLaTeX);
		
	}
},MULTIPLY_OR_FUNCTION{
	@Override
	public ExpressionValue handle(ExpressionNodeEvaluator ev,ExpressionValue lt, ExpressionValue rt,
			 ExpressionValue left, ExpressionValue right, StringTemplate tpl,boolean holdsLaTeX) {
		return ev.handleMult(lt,rt,tpl,holdsLaTeX);
		
	}
},DIVIDE{
	@Override
	public ExpressionValue handle(ExpressionNodeEvaluator ev,ExpressionValue lt, ExpressionValue rt,
			 ExpressionValue left, ExpressionValue right, StringTemplate tpl,boolean holdsLaTeX) {
		return ev.handleDivide(lt,rt,left,right);
		
	}
},POWER{
	@Override
	public ExpressionValue handle(ExpressionNodeEvaluator ev,ExpressionValue lt, ExpressionValue rt,
			 ExpressionValue left, ExpressionValue right, StringTemplate tpl,boolean holdsLaTeX) {
		return ev.handlePower(lt,rt,right);
		
	}
},

FREEHAND,COS{
	@Override
	public ExpressionValue handle(ExpressionNodeEvaluator ev,ExpressionValue lt, ExpressionValue rt,
			 ExpressionValue left, ExpressionValue right, StringTemplate tpl,boolean holdsLaTeX) {
		return ev.handleCos(lt,rt);
		
	}
},SIN{
	@Override
	public ExpressionValue handle(ExpressionNodeEvaluator ev,ExpressionValue lt, ExpressionValue rt,
			 ExpressionValue left, ExpressionValue right, StringTemplate tpl,boolean holdsLaTeX) {
		return ev.handleSin(lt,rt);
		
	}
},TAN,EXP{
	@Override
	public ExpressionValue handle(ExpressionNodeEvaluator ev,ExpressionValue lt, ExpressionValue rt,
			 ExpressionValue left, ExpressionValue right, StringTemplate tpl,boolean holdsLaTeX) {
		return ev.handleExp(lt);
		
	}
},LOG,ARCCOS,ARCSIN,ARCTAN,ARCTAN2,NROOT,SQRT,SQRT_SHORT,ABS
,SGN,
XCOORD{
	@Override
	public ExpressionValue handle(ExpressionNodeEvaluator ev,ExpressionValue lt, ExpressionValue rt,
			 ExpressionValue left, ExpressionValue right, StringTemplate tpl,boolean holdsLaTeX) {
		return ev.handleXcoord(lt,rt,left,right,this,tpl,holdsLaTeX);
		
	}
}
,YCOORD{
	@Override
	public ExpressionValue handle(ExpressionNodeEvaluator ev,ExpressionValue lt, ExpressionValue rt,
			 ExpressionValue left, ExpressionValue right, StringTemplate tpl,boolean holdsLaTeX) {
		return ev.handleYcoord(lt,rt,left,right,this,tpl,holdsLaTeX);
		
	}
},ZCOORD,IMAGINARY{
	@Override
	public ExpressionValue handle(ExpressionNodeEvaluator ev,ExpressionValue lt, ExpressionValue rt,
			 ExpressionValue left, ExpressionValue right, StringTemplate tpl,boolean holdsLaTeX) {
		return ev.handleYcoord(lt,rt,left,right,this,tpl,holdsLaTeX);
		
	}
},REAL{
	@Override
	public ExpressionValue handle(ExpressionNodeEvaluator ev,ExpressionValue lt, ExpressionValue rt,
			 ExpressionValue left, ExpressionValue right, StringTemplate tpl,boolean holdsLaTeX) {
		return ev.handleXcoord(lt,rt,left,right,this,tpl,holdsLaTeX);
		
	}
},FRACTIONAL_PART,COSH,SINH,TANH,ACOSH,ASINH,ATANH,CSC,SEC
,COT,CSCH,SECH,COTH,FLOOR,CEIL,FACTORIAL,ROUND,GAMMA,GAMMA_INCOMPLETE
,GAMMA_INCOMPLETE_REGULARIZED,BETA,BETA_INCOMPLETE,BETA_INCOMPLETE_REGULARIZED
,ERF,PSI,POLYGAMMA,LOG10,LOG2,LOGB,CI,SI,EI,CBRT,RANDOM,CONJUGATE,ARG,FUNCTION{
	@Override
	public ExpressionValue handle(ExpressionNodeEvaluator ev,ExpressionValue lt, ExpressionValue rt,
			 ExpressionValue left, ExpressionValue right, StringTemplate tpl,boolean holdsLaTeX) {
		return ev.handleFunction(lt,rt);
		
	}
},FUNCTION_NVAR,
VEC_FUNCTION,DERIVATIVE,ELEMENT_OF,SUBSTITUTION,INTEGRAL,IF,IF_ELSE,  

// spreadsheet absolute reference using $ signs
$VAR_ROW,$VAR_COL,$VAR_ROW_COL,

ARBCONST,ARBINT,ARBCOMPLEX, SUM, ZETA;

public static boolean isSimpleFunction(Operation op) {
	switch (op) {
	case SIN:
	case COS:
	case TAN:
	case ARCSIN:
	case ARCCOS:
	case ARCTAN:
	case SINH:
	case COSH:
	case TANH:
	case ASINH:
	case ACOSH:
	case ATANH:
	case CSC:
	case SEC:
	case COT:
	case CSCH:
	case SECH:
	case COTH:
		
	case EXP:
	case ZETA:
	case LOG:
	case LOG10:
	case LOG2:
	case SQRT:
	case CBRT:
	case ERF:
	case ABS:
	case CI:
	case SI:
	case EI:
	case PSI:
	case GAMMA:

		return true;
	}
	return false;
}

public ExpressionValue handle(ExpressionNodeEvaluator ev,ExpressionValue lt, ExpressionValue rt,
		 ExpressionValue left, ExpressionValue right, StringTemplate tpl,boolean holdsLaTeX) {
	return ev.handleDefault(lt,rt,left,right,this,tpl,holdsLaTeX);
	
}}
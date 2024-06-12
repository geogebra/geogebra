package org.geogebra.common.kernel.arithmetic;

import java.util.ArrayList;

import org.geogebra.common.cas.giac.CASgiac.CustomFunctions;
import org.geogebra.common.cas.giac.Ggb2giac;
import org.geogebra.common.export.MathmlTemplate;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic3D.Vector3DValue;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoSymbolicI;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Expression -&gt; string converter
 */
public class ExpressionSerializer implements ExpressionNodeConstants {
	/**
	 * Returns a string representation of a node.
	 *
	 * @param left
	 *            left subtree
	 * @param right
	 *            right subtree
	 * @param operation
	 *            operation
	 * @param leftStr
	 *            serialized left subtree
	 * @param rightStr
	 *            serialized right subtree
	 * @param valueForm
	 *            whether to show value or symbols
	 * @param tpl
	 *            string template
	 * @param kernel
	 *            kernel
	 * @return string representation of a node.
	 */
	final public static String operationToString(ExpressionValue left, ExpressionValue right,
			Operation operation, String leftStr, String rightStr, boolean valueForm,
			StringTemplate tpl, Kernel kernel) {
		ExpressionValue leftEval;
		StringBuilder sb = new StringBuilder();

		StringType stringType = tpl.getStringType();
		Localization loc = kernel.getLocalization();
		switch (operation) {
		case NO_OPERATION:
			return leftStr;
		case NOT:
			return tpl.notString(left, leftStr);

		case OR:
			return tpl.orString(left, right, leftStr, rightStr);
		case XOR:
			return tpl.xorString(left, right, leftStr, rightStr);
		case AND_INTERVAL:
			return tpl.andIntervalString(left, right, leftStr, rightStr, valueForm);

		case AND:
			return tpl.andString(left, right, leftStr, rightStr);

		case IMPLICATION:
			if (stringType.equals(StringType.CONTENT_MATHML)) {
				MathmlTemplate.mathml(sb, "<implies/>", leftStr, rightStr);
			} else if (stringType.equals(StringType.GIAC)) {
				// (not A) || B
				sb.append("((!(");
				sb.append(leftStr);
				sb.append("))||(");
				sb.append(rightStr);
				sb.append("))");

			} else {

				tpl.append(sb, leftStr, left, operation);

				sb.append(' ');
				switch (stringType) {
				case LATEX:
					if (tpl.isInsertLineBreaks()) {
						sb.append("\\-");
					}
					sb.append("\\to");
					break;

				case LIBRE_OFFICE:
					sb.append("toward"); // don't know if it is correct TAM
											// 5/28/2012
					break;

				default:
					sb.append(strIMPLIES);
				}
				sb.append(' ');

				tpl.append(sb, rightStr, right, operation);
			}
			break;

		case EQUAL_BOOLEAN:
			if (stringType.equals(StringType.CONTENT_MATHML)) {
				MathmlTemplate.mathml(sb, "<eq/>", leftStr, rightStr);
			} else if (stringType.equals(StringType.OGP)) {
				sb.append("AreEqual[").append(leftStr).append(",").append(rightStr).append("]");
			} else {

				if (tpl.getStringType().isGiac()) {
					// brackets needed round FIRST argument for eg
					// $1==$2 where $1=y=9 and $2=y=9
					// **AND** **ALSO** round whole expressions eg
					// Evaluate(sinh(x)+cosh(x)==exp(x))
					sb.append(CustomFunctions.IS_ZERO).append("((");
					tpl.append(sb, leftStr, left, operation);
					sb.append(")-(");
					tpl.append(sb, rightStr, right, operation);
					sb.append("))");
				} else {

					tpl.infixBinary(sb, left, right, operation, leftStr, rightStr, tpl.equalSign());
				}
			}
			break;

		case NOT_EQUAL:
			if (stringType.equals(StringType.CONTENT_MATHML)) {
				MathmlTemplate.mathml(sb, "<neq/>", leftStr, rightStr);
			} else {
				tpl.infixBinary(sb, left, right, operation, leftStr, rightStr, tpl.notEqualSign());
			}
			break;

		case IS_ELEMENT_OF:
			if (stringType.equals(StringType.CONTENT_MATHML)) {
				MathmlTemplate.mathml(sb, "<in/>", leftStr, rightStr);
			} else if (stringType.isGiac()) {
				sb.append("when(count\\_eq(");
				sb.append(leftStr);
				sb.append(',');
				sb.append(rightStr);
				sb.append(")==0,false,true)");
			} else {
				tpl.append(sb, leftStr, left, operation);
				// sb.append(leftStr);
				sb.append(' ');
				switch (stringType) {
				case LATEX:
					if (tpl.isInsertLineBreaks()) {
						sb.append("\\-");
					}
					sb.append("\\in");
					break;
				case LIBRE_OFFICE:
					sb.append(" in ");
					break;
				default:
					sb.append(strIS_ELEMENT_OF);
				}
				sb.append(' ');
				tpl.append(sb, rightStr, right, operation);
				// sb.append(rightStr);
			}
			break;

		case IS_SUBSET_OF:
			if (stringType.equals(StringType.CONTENT_MATHML)) {
				MathmlTemplate.mathml(sb, "<subset/>", leftStr, rightStr);
			} else if (stringType.isGiac()) {
				sb.append("when((");
				sb.append(leftStr);
				sb.append(") union (");
				sb.append(rightStr);
				sb.append(")==(");
				sb.append(rightStr);
				// {1,2,3,3} union {} = {1,2,3}
				sb.append(") union {},true,false)");
			} else {
				tpl.infixBinary(sb, left, right, operation, leftStr, rightStr, tpl.subsetSign());
			}
			break;

		case IS_SUBSET_OF_STRICT:
			if (stringType.equals(StringType.CONTENT_MATHML)) {
				MathmlTemplate.mathml(sb, "<prsubset/>", leftStr, rightStr);
			} else if (stringType.isGiac()) {
				sb.append("when((");
				sb.append(leftStr);
				sb.append(") union (");
				sb.append(rightStr);
				sb.append(")==(");
				sb.append(rightStr);
				// {1,2,3,3} union {} = {1,2,3}
				sb.append(") union {} && dim(");
				sb.append(leftStr);
				sb.append("union {})<dim(");
				sb.append(rightStr);
				sb.append("union {}),true,false)");
			} else {
				tpl.infixBinary(sb, left, right, operation, leftStr, rightStr,
						tpl.strictSubsetSign());
			}
			break;

		case SET_DIFFERENCE:
			if (stringType.equals(StringType.CONTENT_MATHML)) {
				MathmlTemplate.mathml(sb, "<setdiff/>", leftStr, rightStr);
			} else if (stringType.isGiac()) {
				sb.append("ggbListDifference(");
				sb.append(leftStr);
				sb.append(",");
				sb.append(rightStr);
				sb.append(')');
			} else {
				tpl.append(sb, leftStr, left, operation);
				// sb.append(leftStr);
				sb.append(' ');
				switch (stringType) {
				case LATEX:
					if (tpl.isInsertLineBreaks()) {
						sb.append("\\-");
					}
					sb.append("\\setminus");
					break;
				case LIBRE_OFFICE:
					sb.append(" setminus ");
					break;
				default:
					sb.append(strSET_DIFFERENCE);
				}
				sb.append(' ');
				if (right.isExpressionNode()
						&& right.wrap().getOperation() == Operation.SET_DIFFERENCE) {
					tpl.appendWithBrackets(sb, rightStr);
				} else {
					tpl.append(sb, rightStr, right, operation);
				}
				// sb.append(rightStr);
			}
			break;

		case LESS:
			if (stringType.equals(StringType.CONTENT_MATHML)) {
				MathmlTemplate.mathml(sb, "<lt/>", leftStr, rightStr);
			} else {

				tpl.infixBinary(sb, left, right, operation, leftStr, rightStr, tpl.lessSign());
			}
			break;

		case GREATER:
			if (stringType.equals(StringType.CONTENT_MATHML)) {
				MathmlTemplate.mathml(sb, "<gt/>", leftStr, rightStr);
			} else {
				tpl.infixBinary(sb, left, right, operation, leftStr, rightStr, tpl.greaterSign());
			}
			break;

		case LESS_EQUAL:
			if (stringType.equals(StringType.CONTENT_MATHML)) {
				MathmlTemplate.mathml(sb, "<leq/>", leftStr, rightStr);
			} else {
				tpl.infixBinary(sb, left, right, operation, leftStr, rightStr, tpl.leqSign());
			}
			break;

		case GREATER_EQUAL:
			if (stringType.equals(StringType.CONTENT_MATHML)) {
				MathmlTemplate.mathml(sb, "<qeq/>", leftStr, rightStr);
			} else {
				tpl.infixBinary(sb, left, right, operation, leftStr, rightStr, tpl.geqSign());
			}
			break;

		case PARALLEL:
			if (stringType.equals(StringType.OGP)) {
				sb.append("AreParallel[").append(leftStr).append(",").append(rightStr).append("]");
				break;
			}
			tpl.infixBinary(sb, left, right, operation, leftStr, rightStr, tpl.parallelSign());
			break;

		case PERPENDICULAR:
			if (stringType.equals(StringType.OGP)) {
				sb.append("ArePerpendicular[").append(leftStr).append(",")
						.append(rightStr).append("]");
				break;
			}
			tpl.infixBinary(sb, left, right, operation, leftStr, rightStr, tpl.perpSign());
			break;

		case VECTORPRODUCT:
			if (stringType.equals(StringType.CONTENT_MATHML)) {
				MathmlTemplate.mathml(sb, "<vectorproduct/>", leftStr, rightStr);
			} else if (stringType.isGiac()) {
				ArrayList<ExpressionNode> crossArg = new ArrayList<>();
				crossArg.add(left.wrap());
				crossArg.add(right.wrap());
				sb.append(kernel.getGeoGebraCAS().translateCommandSignature("Cross.2")
						.replace("%0", leftStr).replace("%1", rightStr));
				// from Ggb2Giac Cross.2

			} else {
				tpl.append(sb, leftStr, left, operation);
				// sb.append(leftStr);
				sb.append(' ');
				switch (stringType) {
				case LATEX:
					if (tpl.isInsertLineBreaks()) {
						sb.append("\\-");
					}
					sb.append("\\otimes");
					break;
				case LIBRE_OFFICE:
					sb.append(" cdot ");
					break;
				default:
					sb.append(strVECTORPRODUCT);
				}
				sb.append(' ');
				boolean rightVectorProduct = right.isExpressionNode()
						&& ((ExpressionNode) right).getOperation() == Operation.VECTORPRODUCT;
				if (rightVectorProduct) {
					tpl.appendWithBrackets(sb, rightStr);
				} else {
					tpl.append(sb, rightStr, right, operation);
				}
				// sb.append(rightStr);
			}
			break;
		case PLUS:
			return tpl.plusString(left, right, leftStr, rightStr, valueForm,
					loc);
		case INVISIBLE_PLUS:
			return tpl.invisiblePlusString(leftStr, rightStr);
		case MINUS:
			return tpl.minusString(left, right, leftStr, rightStr, valueForm, loc);

		case MULTIPLY:
			return tpl.multiplyString(left, right, leftStr, rightStr, valueForm, loc);
		case DIVIDE:
			return tpl.divideString(left, right, leftStr, rightStr, valueForm,
					loc);

		case POWER:
			return tpl.powerString(left, right, leftStr, rightStr, valueForm,
					loc);

		case FACTORIAL:
			switch (stringType) {
			case CONTENT_MATHML:
				MathmlTemplate.mathml(sb, "<factorial/>", leftStr, null);
				break;
			case LIBRE_OFFICE:
				sb.append("fact {");
				if ((leftStr.charAt(0) != '-') && left.isLeaf()) {
					sb.append(leftStr);
				} else {
					sb.append('(');
					sb.append(leftStr);
					sb.append(')');
				}
				sb.append(" }");
				break;

			default:
				if (((leftStr.charAt(0) != '-')
						&& tpl.isSinglePowerArg(left) && !StringTemplate.isFraction(left))
						&& !(left instanceof GeoSymbolicI && stringType == StringType.GIAC)
						|| (ExpressionNode.opID(left) > Operation.POWER.ordinal()
								&& ExpressionNode.opID(left) != Operation.FACTORIAL.ordinal())) {
					// not +, -, *, /, ^
					sb.append(leftStr);
				} else {
					tpl.appendWithBrackets(sb, leftStr);
				}
				sb.append('!');
				break;
			}
			break;

		case COS:
			trig(leftStr, sb, "<cos/>", "\\cos", "COS(", "cos", "cos", tpl, loc, true);
			break;

		case SIN:
			trig(leftStr, sb, "<sin/>", "\\sin", "SIN(", "sin", "sin", tpl, loc, true);
			break;

		case TAN:
			trig(leftStr, sb, "<tan/>", "\\tan", "TAN(", "tan", "tan", tpl, loc, true);
			break;

		case CSC:
			trig(leftStr, sb, "<csc/>", "\\csc", "CSC(", "csc", "csc", tpl, loc, true);
			break;

		case SEC:
			trig(leftStr, sb, "<sec/>", "\\sec", "SEC(", "sec", "sec", tpl, loc, true);
			break;

		case COT:
			trig(leftStr, sb, "<cot/>", "\\cot", "COT(", "cot", "cot", tpl, loc, true);
			break;

		case CSCH:
			trig(leftStr, sb, "<csch/>", "\\csch", "CSCH(", "csch", "func csch", tpl, loc, false);
			break;

		case SECH:
			trig(leftStr, sb, "<sech/>", "\\sech", "SECH(", "sech", "func sech", tpl, loc, false);
			break;

		case COTH:
			trig(leftStr, sb, "<coth/>", "\\coth", "COTH(", "coth", "coth", tpl, loc, false);
			break;

		case ARCCOS:
			trig(leftStr, sb, "<arccos/>", "\\arccos", "ACOS(", "acos", "arccos",
					giacDegFix("acos", kernel), tpl, loc, false, true,
					"altText.acos");
			break;

		case ARCCOSD:
			trig(leftStr, sb, "<arccos/>", "\\arccos", "ACOS(", tpl.acosd(kernel), "arccos",
					"acosd", tpl, loc, false, true, "altText.acos");
			break;

		case ARCSIN:
			trig(leftStr, sb, "<arcsin/>", "\\arcsin", "ASIN(", "asin", "arcsin",
					giacDegFix("asin", kernel), tpl, loc, false, true,
					"altText.asin");
			break;

		case ARCSIND:

			trig(leftStr, sb, "<arcsin/>", "\\arcsin", "ASIN(", tpl.asind(kernel), "arcsin",
					"asind", tpl, loc, false, true, "altText.asin");
			break;

		case ARCTAN:
			trig(leftStr, sb, "<arctan/>", "\\arctan", "ATAN(", "atan", "arctan",
					giacDegFix("atan", kernel), tpl, loc, false, true,
					"altText.atan");
			break;

		case ARCTAND:
			trig(leftStr, sb, "<arctan/>", "\\arctan", "ATAN(", tpl.atand(kernel), "arctan",
					"atand", tpl, loc, false, true, "altText.atan");
			break;

		case ARCTAN2:
			twoVar(sb, leftStr, rightStr, "atan2", "<arctan/>", "ATAN2", "atan2", tpl, kernel,
					true);
			break;
		case ARCTAN2D:
			twoVar(sb, leftStr, rightStr, "atan2d", "<arctan/>", "ATAN2", "atan2d", tpl, kernel,
					false);
			break;
		case NPR:
			twoVar(sb, leftStr, rightStr, "nPr", "<npr/>", "NPR", "nPr", tpl, kernel, false);
			break;

		case COSH:
			trig(leftStr, sb, "<cosh/>", "\\cosh", "COSH(", "cosh", "cosh", tpl, loc, false);
			break;

		case SINH:
			trig(leftStr, sb, "<sinh/>", "\\sinh", "SINH(", "sinh", "sinh", tpl, loc, false);
			break;

		case TANH:
			trig(leftStr, sb, "<tanh/>", "\\tanh", "TANH(", "tanh", "tanh", tpl, loc, false);
			break;

		case ACOSH:
			trig(leftStr, sb, "<arccosh/>", "\\operatorname{acosh}", "ACOSH(", "acosh", "arcosh",
					tpl, loc, false);
			break;
		case ASINH:
			trig(leftStr, sb, "<arcsinh/>", "\\operatorname{asinh}", "ASINH(", "asinh", "arsinh",
					tpl, loc, false);
			break;

		case ATANH:
			trig(leftStr, sb, "<arctanh/>", "\\operatorname{atanh}", "ATANH(", "atanh", "artanh",
					tpl, loc, false);
			break;
		case REAL:
			trig(leftStr, sb, "<real/>", "\\operatorname{real}", "", "real", "real", "re", tpl, loc,
					false);
			break;
		case IMAGINARY:
			trig(leftStr, sb, "<imaginary/>", "\\operatorname{imaginary}", "", "imaginary",
					"imaginary", "im", tpl, loc, false);
			break;
		case FRACTIONAL_PART:
			trig(leftStr, sb, "<todo/>", "\\operatorname{fractionalPart}", "", "fractionalPart",
					"fractionalPart", "fPart", tpl, loc, false);
			break;
		case ZETA:
			switch (stringType) {
			case LATEX:
				sb.append("\\zeta\\left( ");
				break;

			case LIBRE_OFFICE:
				sb.append("func zeta left (");
				break;
			case GIAC:
				sb.append("Zeta(");
				break;
			default:
				sb.append("zeta(");
			}
			sb.append(leftStr);
			sb.append(tpl.rightBracket());
			break;
		case CI:
			switch (stringType) {
			case LATEX:

				wrapInBackslashOperatorname(sb, "Ci");

				sb.append(" \\left( ");
				break;

			case LIBRE_OFFICE:
				sb.append("func Ci left (");
				break;

			case GIAC:
				appendFunction(sb, "Ci");
				break;
			default:
				sb.append("cosIntegral(");
			}
			sb.append(leftStr);
			sb.append(tpl.rightBracket());
			break;
		case SI:
			switch (stringType) {
			case LATEX:

				wrapInBackslashOperatorname(sb, "Si");

				sb.append(" \\left( ");
				break;

			case LIBRE_OFFICE:
				sb.append("func Si left (");
				break;

			case GIAC:
				appendFunction(sb, "Si");
				break;

			default:
				sb.append("sinIntegral(");
			}
			sb.append(leftStr);
			sb.append(tpl.rightBracket());
			break;
		case EI:
			switch (stringType) {
			case LATEX:

				wrapInBackslashOperatorname(sb, "Ei");

				sb.append(" \\left( ");
				break;
			case LIBRE_OFFICE:
				sb.append("func Ei left (");
				break;

			case GIAC:
				appendFunction(sb, "Ei");
				break;

			default:
				sb.append("expIntegral(");
			}
			sb.append(leftStr);
			sb.append(tpl.rightBracket());
			break;
		case ARBCONST:
			sb.append("arbconst(");
			sb.append(leftStr);
			sb.append(")");
			break;
		case ARBINT:
			sb.append("arbint(");
			sb.append(leftStr);
			sb.append(")");
			break;
		case ARBCOMPLEX:

			sb.append("arbcomplex(");
			sb.append(leftStr);
			sb.append(")");

			break;
		case EXP:
			switch (stringType) {
			case CONTENT_MATHML:
				MathmlTemplate.mathml(sb, "<exp/>", leftStr, null);
				break;
			case LIBRE_OFFICE:
				sb.append("func ");
			case LATEX:

				// add brackets for eg e^b^c -> e^(b^c)
				boolean addParentheses = left.isOperation(Operation.POWER);

				sb.append("\\mathit{e}^{");
				if (addParentheses) {
					sb.append(tpl.leftBracket());
				}
				sb.append(leftStr);
				if (addParentheses) {
					sb.append(tpl.rightBracket());
				}
				sb.append('}');
				break;

			case GEOGEBRA_XML:
			case GIAC:
				sb.append("exp(");
				sb.append(leftStr);
				sb.append(')');
				break;

			case PSTRICKS:
				sb.append("EXP(");
				sb.append(leftStr);
				sb.append(')');
				break;

			default:
				sb.append(Unicode.EULER_STRING);
				if (left.isLeaf()) {
					sb.append("^");
					sb.append(leftStr);
				} else {
					sb.append("^(");
					sb.append(leftStr);
					sb.append(')');
				}
				break;
			}
			break;

		case LOG:
			if (stringType.equals(StringType.CONTENT_MATHML)) {
				MathmlTemplate.mathml(sb, "<ln/>", leftStr, null);
			} else {
				switch (stringType) {
				case LATEX:
					sb.append("\\ln");
					break;
				case GIAC:
					sb.append("log");
					break;
				default:
					sb.append("ln");
					break;
				}
				tpl.addLogBracketsIfNecessary(sb, leftStr, left);
			}
			break;

		case LOGB:
			switch (stringType) {
			case CONTENT_MATHML:
				MathmlTemplate.mathml(sb, "<log/>", "<logbase>", leftStr, "</logbase>", "",
						rightStr, "");
				break;
			case LATEX:
				sb.append("\\log_{");
				sb.append(leftStr);
				sb.append('}');
				tpl.appendWithBrackets(sb, rightStr);
				break;
			case LIBRE_OFFICE:
				sb.append("log_{");
				sb.append(leftStr);
				sb.append('}');
				tpl.appendWithBrackets(sb, rightStr);
				break;
			case GIAC:
				// make sure eg log_10(100) works
				if ("10".equals(leftStr)) {
					sb.append("log10(");
					sb.append(rightStr);
					sb.append(")");
					break;
				}
				//$FALL-THROUGH$
			case PSTRICKS:
			case PGF:
				// ln(x)/ln(b)
				sb.append("ln(");
				sb.append(rightStr);
				sb.append(")/ln(");
				sb.append(leftStr);
				sb.append(')');
				break;

			default:
				sb.append("log(");
				sb.append(leftStr);
				sb.append(",");
				tpl.appendOptionalSpace(sb);
				sb.append(rightStr);
				sb.append(')');
				break;

			}
			break;

		case LAMBERTW:
			if (stringType.equals(StringType.CONTENT_MATHML)) {
				MathmlTemplate.mathml(sb, "<LambertW/>", leftStr, null);
			} else {
				switch (stringType) {
				case LATEX:
					wrapInBackslashOperatorname(sb, "LambertW");

					sb.append(tpl.leftBracket());
					break;
				case LIBRE_OFFICE:
					sb.append("LambertW left ( ");
					break;
				case GIAC:
				case GEOGEBRA_XML:
					sb.append("LambertW(");
					break;

				case PSTRICKS:
				case PGF:
				default:
					sb.append("LambertW(");
					break;
				}
				sb.append(leftStr);
				appendRightIfDefined(rightStr, sb);
				sb.append(tpl.rightBracket());
			}
			break;

		case POLYGAMMA:
			switch (stringType) {
			case LATEX:
				sb.append("\\psi_{");
				sb.append(leftStr);
				sb.append('}');
				sb.append(tpl.leftBracket());
				sb.append(rightStr);
				sb.append(tpl.rightBracket());
				break;

			case GIAC:
				// *******************
				// arguments swapped
				// swapped back in CommandDispatcherGiac
				// *******************
				appendFunction(sb, "Psi");
				sb.append(rightStr);
				sb.append(',');
				sb.append(leftStr);
				sb.append(')');
				break;
			default:
				sb.append("polygamma(");
				sb.append(leftStr);
				if (stringType.equals(StringType.LIBRE_OFFICE)) {
					sb.append("\",\"");
				} else {
					sb.append(", ");
				}
				sb.append(rightStr);
				sb.append(')');
				break;

			}
			break;

		case ERF:
			switch (stringType) {
			case LATEX:

				wrapInBackslashOperatorname(sb, "erf");

				tpl.appendWithBrackets(sb, leftStr);
				break;
			case LIBRE_OFFICE:
				sb.append("func ");
			case GIAC:
			default:
				sb.append("erf(");
				sb.append(leftStr);
				sb.append(')');
				break;

			}
			break;

		case PSI:
			switch (stringType) {
			case LATEX:
				sb.append("\\psi");
				tpl.appendWithBrackets(sb, leftStr);
				break;

			case GIAC:
				appendFunction(sb, "Psi");
				sb.append(leftStr);
				sb.append(')');
				break;

			case LIBRE_OFFICE:
				sb.append("func ");
			default:
				sb.append("psi(");
				sb.append(leftStr);
				sb.append(')');
				break;

			}
			break;

		case LOG10:
			switch (stringType) {
			case CONTENT_MATHML:
				MathmlTemplate.mathml(sb, "<log/>", leftStr, null);
				break;
			case LATEX:
				sb.append("\\log_{10} \\left(");
				sb.append(leftStr);
				sb.append("\\right)");
				break;
			case LIBRE_OFFICE:
				sb.append("log_10 (");
				sb.append(leftStr);
				sb.append(")");
				break;
			case PSTRICKS:
				sb.append("log(");
				sb.append(leftStr);
				sb.append(')');
				break;

			case GIAC:
			case PGF:
				sb.append("log10("); // user-defined function in Maxima
				sb.append(leftStr);
				sb.append(')');
				break;

			default:
				if (tpl.isForEditorParser()) {
					sb.append("log(10,");
				} else {
					sb.append("lg(");
				}
				sb.append(leftStr);
				sb.append(')');
				break;
			}
			break;

		case LOG2:
			switch (stringType) {
			case LATEX:
				sb.append("\\log_{2} \\left(");
				sb.append(leftStr);
				sb.append("\\right)");
				break;
			case LIBRE_OFFICE:
				sb.append("log_2 (");
				sb.append(leftStr);
				sb.append(")");
				break;
			case GIAC:
				sb.append("log(");
				sb.append(leftStr);
				sb.append(")/log(2)");
				break;

			default:
				if (tpl.isForEditorParser()) {
					sb.append("log(2,");
				} else {
					sb.append("ld(");
				}
				sb.append(leftStr);
				sb.append(')');
				break;
			}
			break;
		case NROOT:
			switch (stringType) {
			case CONTENT_MATHML:
				MathmlTemplate.mathml(sb, "<root/>", leftStr, null);
				break;
			case LATEX:
				sb.append("\\sqrt[");
				sb.append(rightStr);
				sb.append("]{");
				sb.append(leftStr);
				sb.append('}');
				break;
			case LIBRE_OFFICE:
				sb.append("nroot{");
				sb.append(rightStr);
				sb.append("},{");
				sb.append(leftStr);
				sb.append('}');
				break;
			case SCREEN_READER_ASCII:
				sb.append(ScreenReader.nroot(leftStr, rightStr, loc));
				break;
			case GEOGEBRA_XML:
			case GEOGEBRA:
				if (tpl.isPrintLocalizedCommandNames() && !tpl.isForEditorParser()) {
					sb.append(loc.getFunction("nroot"));
				} else {
					sb.append("nroot");
				}

				sb.append("(");
				sb.append(leftStr);
				sb.append(',');
				sb.append(rightStr);
				sb.append(')');
				break;
			case GIAC:
				if (leftStr.equals(Unicode.EULER_STRING)) {
					sb.append("exp(1/(");
					sb.append(rightStr);
					sb.append("))");
				} else {
					// was simplify(surd(, causes problems with output from
					// cubic formula, eg x^3 - 6x^2 - 7x + 9
					sb.append("surd(");
					sb.append(leftStr);
					sb.append(',');
					sb.append(rightStr);
					sb.append(")");
				}
				break;
			default: // MAXIMA, MPREDUCE, PSTRICKS, ...
				sb.append("(");
				sb.append(leftStr);
				sb.append(")^(1/(");
				sb.append(rightStr);
				sb.append("))");
				break;
			}
			break;
		case PLUSMINUS:
			if (right instanceof MyNumberPair) {
				sb.append(Unicode.PLUSMINUS);
				tpl.append(sb, leftStr, left, Operation.PLUSMINUS);
				break;
			}
			tpl.append(sb, leftStr, left, Operation.PLUSMINUS);
			sb.append(Unicode.PLUSMINUS);
			if (right.isLeaf()
					|| (ExpressionNode.opID(right) >= Operation.VECTORPRODUCT.ordinal())) {
				sb.append(rightStr);
			} else {
				tpl.appendWithBrackets(sb, rightStr);
			}
			break;
		case SQRT_SHORT:
		case SQRT:
			switch (stringType) {
			case SCREEN_READER_ASCII:
				sb.append(ScreenReader.getStartSqrt(loc));
				sb.append(leftStr);
				sb.append(ScreenReader.getEndSqrt(loc));

				break;
			case CONTENT_MATHML:
				MathmlTemplate.mathml(sb, "<root/>", leftStr, null);
				break;
			case LATEX:
				sb.append("\\sqrt{");
				sb.append(leftStr);
				sb.append('}');
				break;
			case LIBRE_OFFICE:
				sb.append("sqrt{");
				sb.append(leftStr);
				sb.append('}');
				break;

			default:
				if (tpl.printsUnicodeSqrt()) {
					sb.append("\u221a");
				} else {
					sb.append("sqrt");
				}
				sb.append("(");
				sb.append(leftStr);
				sb.append(')');
			}
			break;

		case CBRT:
			switch (stringType) {
			case SCREEN_READER_ASCII:
				sb.append(ScreenReader.getStartCbrt(loc));
				sb.append(leftStr);
				sb.append(ScreenReader.getEndCbrt(loc));
				break;
			case CONTENT_MATHML:
				MathmlTemplate.mathml(sb, "<root/>", "<degree>", "3", "</degree>", "", leftStr, "");
				break;
			case LATEX:
				sb.append("\\sqrt[3]{");
				sb.append(leftStr);
				sb.append('}');
				break;
			case LIBRE_OFFICE:
				sb.append("nroot{3}{");
				sb.append(leftStr);
				sb.append('}');
				break;

			case GIAC:
				// was simplify(surd(, causes problems with output from cubic
				// formula, eg x^3 - 6x^2 - 7x + 9
				sb.append("surd(");
				sb.append(leftStr);
				sb.append(",3)");
				break;
			default:
				sb.append("cbrt(");
				sb.append(leftStr);
				sb.append(')');
			}
			break;

		case ABS:
			switch (stringType) {
			case SCREEN_READER_ASCII:
				sb.append(ScreenReader.getStartAbs(loc));
				sb.append(leftStr);
				sb.append(ScreenReader.getEndAbs(loc));
				break;
			case CONTENT_MATHML:
				MathmlTemplate.mathml(sb, "<abs/>", leftStr, null);
				break;
			case LATEX:
				sb.append("\\left|");
				sb.append(leftStr);
				sb.append("\\right|");
				break;
			case LIBRE_OFFICE:
				sb.append("abs{");
				sb.append(leftStr);
				sb.append('}');
				break;
			case GIAC:
				// Giac's abs() now works for Vectors
				// so this is OK
				// (used to be custom ggbabs() function)
				sb.append("abs(");
				sb.append(leftStr);
				sb.append(")");
				break;

			default:
				sb.append("abs(");
				sb.append(leftStr);
				sb.append(')');
			}
			break;

		case SGN:
			switch (stringType) {
			case LATEX:
				wrapInBackslashOperatorname(sb, "sgn");

				break;

			case GIAC:
				sb.append("sign");
				break;

			case LIBRE_OFFICE:
				sb.append("func ");
				// fall through
			default:
				sb.append("sgn");
			}
			tpl.appendWithBrackets(sb, leftStr);
			break;

		case CONJUGATE:
			switch (stringType) {
			case CONTENT_MATHML:
				MathmlTemplate.mathml(sb, "<conjugate/>", leftStr, null);
				break;
			case LATEX:
				sb.append("\\overline{");
				sb.append(leftStr);
				sb.append("}");
				break;
			case LIBRE_OFFICE:
				sb.append("overline{");
				sb.append(leftStr);
				sb.append("}");
				break;

			case GIAC:
				sb.append("conj(");
				sb.append(leftStr);
				sb.append(')');
				break;
			default:
				if (tpl.isPrintLocalizedCommandNames()) {
					sb.append(loc.getFunction("conjugate"));
				} else {
					sb.append("conjugate");
				}

				sb.append("(");
				sb.append(leftStr);
				sb.append(')');
			}
			break;

		case ARG:
			switch (stringType) {
			case CONTENT_MATHML:
				MathmlTemplate.mathml(sb, "<arg/>", leftStr, null);
				break;
			case LATEX:
				sb.append("\\arg \\left( ");
				sb.append(leftStr);
				sb.append("\\right)");
				break;
			case GIAC:
				sb.append("arg(");
				sb.append(leftStr);
				sb.append(')');
				break;

			case LIBRE_OFFICE:
				sb.append("func ");
			default:
				sb.append("arg(");
				sb.append(leftStr);
				sb.append(')');
			}
			break;

		case ALT:
			switch (stringType) {
			case CONTENT_MATHML:
				MathmlTemplate.mathml(sb, "<alt/>", leftStr, null);
				break;
			case LATEX:
				sb.append("\\operatorname{alt} \\left( ");
				sb.append(leftStr);
				sb.append("\\right)");
				break;
			case GIAC:
				sb.append("ggbalt(");
				sb.append(leftStr);
				sb.append(')');
				break;

			case LIBRE_OFFICE:
				sb.append("func ");
			default:
				sb.append("alt(");
				sb.append(leftStr);
				sb.append(')');
			}
			break;

		case FLOOR:
			switch (stringType) {
			case CONTENT_MATHML:
				MathmlTemplate.mathml(sb, "<floor/>", leftStr, null);
				break;
			case LATEX:
				sb.append("\\left");
				sb.append("\\lfloor ");
				sb.append(leftStr);
				sb.append("\\right");

				sb.append("\\rfloor ");
				break;
			case LIBRE_OFFICE:
				sb.append(" left lfloor ");
				sb.append(leftStr);
				sb.append(" right rfloor");
				break;

			default:
				sb.append("floor(");
				sb.append(leftStr);
				sb.append(')');
			}
			break;

		case CEIL:
			switch (stringType) {
			case CONTENT_MATHML:
				MathmlTemplate.mathml(sb, "<ceiling/>", leftStr, null);
				break;
			case LATEX:

				sb.append("\\left");

				sb.append("\\lceil ");
				sb.append(leftStr);

				sb.append("\\right");

				sb.append("\\rceil ");
				break;
			case LIBRE_OFFICE:
				sb.append("left lceil ");
				sb.append(leftStr);
				sb.append(" right rceil");
				break;

			case GIAC:
			case PSTRICKS:
				sb.append("ceiling(");
				sb.append(leftStr);
				sb.append(')');
				break;

			default:
				sb.append("ceil(");
				sb.append(leftStr);
				sb.append(')');
			}
			break;

		case ROUND2:
		case ROUND:
			switch (stringType) {
			case LATEX:

				wrapInBackslashOperatorname(sb, "round");

				sb.append(" \\left( ");
				break;

			case LIBRE_OFFICE:
				sb.append("func round left (");

			case GIAC:
				sb.append("ggbround(");
				break;

			default:
				sb.append("round(");
			}
			sb.append(leftStr);
			if (operation == Operation.ROUND2) {
				sb.append(", ");
				sb.append(rightStr);
			}
			sb.append(tpl.rightBracket());
			break;

		case GAMMA:
			switch (stringType) {
			case LATEX:
				sb.append(" \\Gamma \\left( ");
				break;
			case LIBRE_OFFICE:
				sb.append("%GAMMA left (");
				break;
			case GIAC:
				sb.append("Gamma(");
				break;

			default:
				sb.append("gamma(");
			}
			sb.append(leftStr);
			sb.append(tpl.rightBracket());
			break;

		case DIRAC:
			switch (stringType) {
			case LATEX:
				sb.append(" Dirac \\left( ");
				break;
			case LIBRE_OFFICE:
				sb.append("%DIRAC left (");
				break;
			case GIAC:
				sb.append("Dirac(");
				break;

			default:
				sb.append("Dirac(");
			}
			sb.append(leftStr);
			sb.append(tpl.rightBracket());
			break;

		case HEAVISIDE:
			switch (stringType) {
			case LATEX:
				sb.append(" Heaviside \\left( ");
				break;
			case LIBRE_OFFICE:
				sb.append("%HEAVISIDE left (");
				break;
			case GIAC:
				sb.append("Heaviside(");
				break;

			default:
				sb.append("Heaviside(");
			}
			sb.append(leftStr);
			sb.append(tpl.rightBracket());
			break;

		case GAMMA_INCOMPLETE:
			switch (stringType) {
			case LATEX:
				sb.append(" \\gamma \\left( ");
				break;
			case LIBRE_OFFICE:
				sb.append("%GAMMA left (");
				break;
			case GIAC:
				sb.append("igamma(");
				break;

			default:
				sb.append("gamma(");
			}
			sb.append(leftStr);
			if (stringType.equals(StringType.LIBRE_OFFICE)) {
				sb.append("\",\"");
			} else {
				sb.append(", ");
			}
			sb.append(rightStr);
			sb.append(tpl.rightBracket());
			break;

		case GAMMA_INCOMPLETE_REGULARIZED:
			switch (stringType) {
			case LATEX:
				wrapInBackslashOperatorname(sb, "P");

				sb.append(" \\left( ");
				break;

			case LIBRE_OFFICE:
				sb.append("func gammaRegularized left (");

			case GIAC:
				sb.append("igamma(");
				break;

			default:
				sb.append("gammaRegularized(");
			}
			sb.append(leftStr);
			if (stringType.equals(StringType.LIBRE_OFFICE)) {
				sb.append("\",\"");
			} else {
				sb.append(", ");
			}
			sb.append(rightStr);

			if (stringType.isGiac()) {
				sb.append(",1");
			}

			sb.append(tpl.rightBracket());
			break;

		case BETA:
			switch (stringType) {
			case LATEX:
				sb.append("\\Beta \\left( ");
				break;
			case LIBRE_OFFICE:
				sb.append("%BETA left(");
				break;
			case GIAC:
				sb.append("Beta(");
				break;

			default:
				sb.append("beta(");
			}
			sb.append(leftStr);
			if (stringType.equals(StringType.LIBRE_OFFICE)) {
				sb.append("\",\"");
			} else {
				sb.append(", ");
			}
			sb.append(rightStr);
			sb.append(tpl.rightBracket());
			break;

		case BETA_INCOMPLETE:
			switch (stringType) {
			case LATEX:
				sb.append("\\Beta \\left( ");
				break;
			case LIBRE_OFFICE:
				sb.append("%BETA left(");
				break;

			case GIAC:
				sb.append("Beta(");
				break;

			default:
				sb.append("beta(");
			}
			sb.append(leftStr);
			if (stringType.equals(StringType.LIBRE_OFFICE)) {
				sb.append("\",\"");
			} else {
				sb.append(", ");
			}
			sb.append(rightStr);
			sb.append(tpl.rightBracket());
			break;

		case BETA_INCOMPLETE_REGULARIZED:
			switch (stringType) {
			case LATEX:

				wrapInBackslashOperatorname(sb, "I");

				sb.append(" \\left( ");
				break;

			case LIBRE_OFFICE:
				sb.append("func betaRegularized left (");

			case GIAC:
				sb.append("Beta(");
				break;

			default:
				sb.append("betaRegularized(");
			}
			sb.append(leftStr);
			if (stringType.equals(StringType.LIBRE_OFFICE)) {
				sb.append("\",\"");
			} else {
				sb.append(", ");
			}
			sb.append(rightStr);

			if (stringType.isGiac()) {
				sb.append(",1");
			}

			sb.append(tpl.rightBracket());
			break;

		case RANDOM:
			if (valueForm) {
				sb.append(leftStr);
			} else {
				switch (stringType) {

				case GIAC:
					sb.append("rand(0,1)");
					break;
				case LIBRE_OFFICE:
					sb.append("func ");
				default:
					sb.append("random()");
				}
			}
			break;

		case XCOORD:
			if (!stringType.isGiac() && valueForm && !left.wrap().containsFunctionVariable()
					&& (leftEval = left.evaluate(tpl)) instanceof VectorNDValue) {
				sb.append(kernel.format(((VectorNDValue) leftEval).getVector().getX(), tpl));
			} else if (valueForm && ((leftEval = left.evaluate(tpl)) instanceof GeoLine)) {
				sb.append(kernel.format(((GeoLine) leftEval).getX(), tpl));
			} else {
				switch (stringType) {
				case LATEX:
					sb.append(" x \\left( ");
					sb.append(leftStr);
					sb.append(tpl.rightBracket());
					break;
				case LIBRE_OFFICE:
					sb.append("func x left (");
					sb.append(leftStr);
					sb.append(tpl.rightBracket());
				case GIAC:
					sb.append("xcoord(");
					sb.append(leftStr);
					sb.append(")");
					break;

				default:
					sb.append("x(");
					sb.append(leftStr);
					sb.append(')');
				}
			}
			break;

		case YCOORD:
			if (!stringType.isGiac() && valueForm && !left.wrap().containsFunctionVariable()
					&& (leftEval = left.evaluate(tpl)) instanceof VectorNDValue) {
				sb.append(kernel.format(((VectorNDValue) leftEval).getVector().getY(), tpl));
			} else if (valueForm && ((leftEval = left.evaluate(tpl)) instanceof GeoLine)) {
				sb.append(kernel.format(((GeoLine) leftEval).getY(), tpl));
			} else {
				switch (stringType) {
				case LATEX:
					sb.append(" y \\left( ");
					sb.append(leftStr);
					sb.append("\\right)");
					break;
				case LIBRE_OFFICE:
					sb.append("func y left (");
					sb.append(leftStr);
					sb.append(tpl.rightBracket());
				case GIAC:
					sb.append("ycoord(");
					sb.append(leftStr);
					sb.append(")");
					break;

				default:
					sb.append("y(");
					sb.append(leftStr);
					sb.append(')');
				}
			}
			break;

		case ZCOORD:
			if (!stringType.isGiac() && valueForm && !left.wrap().containsFunctionVariable()
					&& (leftEval = left.evaluate(tpl)) instanceof Vector3DValue) {
				sb.append(kernel.format(((Vector3DValue) leftEval).getPointAsDouble()[2], tpl));
			} else if (valueForm && ((leftEval = left.evaluate(tpl)) instanceof GeoLine)) {
				sb.append(kernel.format(((GeoLine) leftEval).getZ(), tpl));
			} else {
				switch (stringType) {
				case LATEX:
					sb.append(" z \\left( ");
					sb.append(leftStr);
					sb.append("\\right)");
					break;
				case LIBRE_OFFICE:
					sb.append("func z left (");
					sb.append(leftStr);
					sb.append(tpl.rightBracket());
				case GIAC:
					sb.append("zcoord(");
					sb.append(leftStr);
					sb.append(")");
					break;

				default:
					sb.append("z(");
					sb.append(leftStr);
					sb.append(')');
				}
			}
			break;

		case MULTIPLY_OR_FUNCTION:
			Log.debug("Operation not resolved");
			//$FALL-THROUGH$
		case FUNCTION:
			if (stringType.isGiac() && right instanceof ListValue && left instanceof Function) {
				ListValue list = (ListValue) right;

				// eg seq(subst(sin(x),x,{4,5,6}[j]),j,0,2)
				// DON'T USE i (sqrt(-1) in Giac)
				sb.append("seq(subst(");
				sb.append(leftStr);
				sb.append(',');
				sb.append(((Function) left).getVarString(tpl));
				sb.append(',');
				sb.append(rightStr);
				sb.append("[j]),j,0,");
				sb.append(list.size() - 1);
				sb.append(')');
				break;
			}

			// GeoFunction and GeoFunctionConditional should not be expanded
			if (left instanceof GeoFunction) {
				GeoFunction geo = (GeoFunction) left;
				if (geo.isLabelSet() || geo.isLocalVariable()) {
					if (stringType.equals(StringType.LIBRE_OFFICE)) {
						sb.append("func ");
					}
					sb.append(geo.getLabel(tpl));
					tpl.appendWithBrackets(sb, rightStr);
				} else {
					// inline function: replace function var by right side
					Function fn = geo.getFunction();
					if (fn != null) {
						FunctionVariable var = geo.getFunction().getFunctionVariable();
						String oldVarStr = var.toString(tpl);

						// without eg "ggbtmpvar" added
						String oldVarStrRaw = var.getSetVarString();

						var.setVarString(rightStr);
						if (stringType.equals(StringType.LIBRE_OFFICE)) {
							sb.append("func ");
						}
						// do not recompute the expression string if we are
						// plugging
						// in the same variable; #3481
						String rhString = oldVarStr.equals(rightStr) ? leftStr : geo.getLabel(tpl);
						sb.append(rhString);
						var.setVarString(oldVarStrRaw);
					}
				}
			} else if (valueForm && left.isExpressionNode()) {
				ExpressionNode en = (ExpressionNode) left;
				// left could contain $ nodes to wrap a GeoElement
				// e.g. A1(x) = x^2 and B1(x) = $A$1(x)
				// value form of B1 is x^2 and NOT x^2(x)
				switch (en.getOperation()) {
				case DOLLAR_VAR_ROW:
				case DOLLAR_VAR_COL:
				case DOLLAR_VAR_ROW_COL:
					tpl.appendWithBrackets(sb, leftStr);
					break;
				case DERIVATIVE:
					if (stringType.isGiac()) {

						String funStr = en.getLeft().toValueString(tpl);

						sb.append("diff(");
						sb.append(funStr);

						// GGB-2356
						if (funStr.startsWith(Kernel.TMP_VARIABLE_PREFIX)) {
							sb.append("(");
							sb.append(rightStr);
							sb.append(")");
						}

						String deg = en.getRight().toValueString(tpl);
						// TODO doesn't work for var!=f, solve this in
						// FunctionExpander
						if (!"1".equals(deg)) {
							sb.append(",x,");
							sb.append(deg);
						}
						sb.append(")");
						break;
					}
					appendUserFunction(sb, leftStr, rightStr, tpl);
					break;
				default:
					appendUserFunction(sb, leftStr, rightStr, tpl);
					break;
				}
			} else {
				// standard case if we get here
				appendUserFunction(sb, leftStr, rightStr, tpl);
			}
			break;

		// TODO: put back into case FUNCTION_NVAR:, see #1115
		case ELEMENT_OF:
			if (tpl.hasCASType() && right instanceof MyList) {

				if (((MyList) right).size() > 1) {
					sb.append(leftStr);
					sb.append("[");
					ListValue list = (ListValue) right;
					for (int i = 0; i < list.size(); i++) {
						if (i != 0) {
							sb.append(',');
						}
						sb.append("(");
						sb.append(list.get(i).toString(tpl));
						sb.append(")-1");
					}
					sb.append("]");
				} else {
					sb.append(Ggb2giac.ELEMENT_2.replace("_", "\\_").replace("%0", leftStr)
							.replace("%1", rightStr));
				}

				break;
			}

			appendFunctionNVar(sb, left, leftStr, rightStr, tpl);
			break;
		case FUNCTION_NVAR:
			if (valueForm) {
				// TODO: avoid replacing of expressions in operationToString
				if ((left instanceof FunctionalNVar) && (right instanceof MyList)) {
					FunctionNVar func = ((FunctionalNVar) left).getFunction();
					ExpressionNode en = func.getExpression().getCopy(kernel);
					for (int i = 0; (i < func.getVarNumber())
							&& (i < ((MyList) right).size()); i++) {
						en.replace(func.getFunctionVariables()[i],
								((MyList) right).get(i));
					}
					// add brackets, see TRAC-1287
					if (!stringType.equals(StringType.LATEX)) {
						sb.append(tpl.leftBracket());
					}
					sb.append(en.toValueString(tpl));
					if (!stringType.equals(StringType.LATEX)) {
						sb.append(tpl.rightBracket());
					}
				} else if (left instanceof GeoDummyVariable) {
					sb.append(tpl.leftBracket());
					sb.append(leftStr);
					tpl.appendWithBrackets(sb, rightStr);
					sb.append(tpl.rightBracket());
				} else {
					tpl.appendWithBrackets(sb, leftStr);
				}
			} else {
				appendFunctionNVar(sb, left, leftStr, rightStr, tpl);
			}
			break;

		case VEC_FUNCTION:
			// GeoCurveCartesian and GeoSurfaceCartesian should not be expanded
			if (left.isGeoElement() && (((GeoElement) left).isGeoCurveCartesian()
					|| ((GeoElement) left).isGeoSurfaceCartesian())) {
				sb.append(((GeoElement) left).getLabel(tpl));
			} else {
				sb.append(leftStr);
			}
			tpl.appendWithBrackets(sb, rightStr);
			break;
		case DIFF:
			// we only serialize this temporarily during GIAC parsing, so only
			// default template needed
			// GIAC template added for safety
			if (tpl.hasCASType()) {
				sb.append("diff(");
			} else {
				sb.append("ggbdiff(");
			}
			sb.append(leftStr);
			sb.append(',');
			sb.append(rightStr);
			sb.append(")");
		case DERIVATIVE: // e.g. f''
			if (tpl.hasCASType()) {
				Log.error("Serialization not handled in Operation.Function");
			}
			// labeled GeoElements should not be expanded
			if (left.isGeoElement() && ((GeoElement) left).isLabelSet()) {
				sb.append(((GeoElement) left).getLabel(tpl));
			} else {
				sb.append(leftStr);
			}

			if (right.unwrap() instanceof NumberValue) {
				int order = (int) Math.round(right.evaluateDouble());
				for (; order > 0; order--) {
					sb.append('\'');
				}
			} else {
				sb.append(right);
			}
			break;

		case DOLLAR_VAR_ROW: // e.g. A$1
			if (valueForm || tpl.hasCASType()) {
				// GeoElement value
				sb.append(leftStr);
			} else {
				// $ for row
				GeoElement geo = (GeoElement) left;
				if (geo.getSpreadsheetCoords() != null) {
					sb.append(geo.getSpreadsheetLabelWithDollars(false, true));
				} else {
					sb.append(leftStr);
				}
			}
			break;

		case DOLLAR_VAR_COL: // e.g. $A1
			if (valueForm || tpl.hasCASType()) {
				// GeoElement value
				sb.append(leftStr);
			} else {
				// maybe wrongly parsed dynamic reference in CAS -- TODO decide
				// whether we need this
				if (!left.isGeoElement()) {
					sb.append('$');
					sb.append(leftStr);
					break;
				}
				// $ for row
				GeoElement geo = (GeoElement) left;
				if (geo.getSpreadsheetCoords() != null) {
					sb.append(geo.getSpreadsheetLabelWithDollars(true, false));
				} else {
					sb.append(leftStr);
				}
			}
			break;

		case DOLLAR_VAR_ROW_COL: // e.g. $A$1
			if (valueForm || tpl.hasCASType()) {
				// GeoElement value
				sb.append(leftStr);
			} else {
				// $ for row
				GeoElement geo = (GeoElement) left;
				if (geo.getSpreadsheetCoords() != null) {
					sb.append(geo.getSpreadsheetLabelWithDollars(true, true));
				} else {
					sb.append(leftStr);
				}
			}
			break;

		case FREEHAND:
			// need to output eg freehand(ggbtmpvarx) so that Derivative fails
			// rather than giving zero
			sb.append(loc.getFunction("freehand"));
			sb.append('(');
			sb.append(leftStr);
			sb.append(')');
			break;
		case DATA:
			// need to output eg freehand(ggbtmpvarx) so that Derivative fails
			// rather than giving zero
			if (tpl.isPrintLocalizedCommandNames()) {
				sb.append(loc.getCommand("DataFunction"));
			} else {
				sb.append("DataFunction");
			}
			sb.append(tpl.leftSquareBracket());
			if (tpl.hasType(StringType.GEOGEBRA_XML)) {
				sb.append(rightStr);
				sb.append(',');
			}
			sb.append(leftStr);
			sb.append(tpl.rightSquareBracket());
			break;
		case INTEGRAL:
			if (stringType == StringType.LATEX) {
				sb.append("\\int ");
				sb.append(leftStr);
				sb.append("d");
				sb.append(rightStr);
			} else if (stringType == StringType.LIBRE_OFFICE) {
				sb.append("int ");
				sb.append(leftStr);
				sb.append(" d");
				sb.append(rightStr);
			} else {
				if (stringType.isGiac()) {
					sb.append("int(");
				} else {
					sb.append("gGbInTeGrAl(");
				}
				sb.append(leftStr);
				sb.append(',');
				sb.append(rightStr);
				sb.append(")");
			}
			break;
		case INVERSE_NORMAL:
			// Giac only
			if (stringType == StringType.GIAC) {
				sb.append("normal\\_icdf(");
			} else {
				sb.append("InverseNormal(0,1,");

			}
			sb.append(leftStr);
			sb.append(")");
			break;
		case SUM:
			if (stringType == StringType.LATEX) {
				sb.append("\\sum_{");
				sb.append(((MyNumberPair) left).y.toString(tpl));
				sb.append("=");
				sb.append(((MyNumberPair) right).x.toString(tpl));
				sb.append("}^{");
				sb.append(((MyNumberPair) right).y.toString(tpl));
				sb.append("}");
				sb.append(((MyNumberPair) left).x.toString(tpl));
			} else if (stringType == StringType.LIBRE_OFFICE) {
				sb.append("sum from{");
				sb.append(((MyNumberPair) left).y.toString(tpl));
				sb.append("=");
				sb.append(((MyNumberPair) right).x.toString(tpl));
				sb.append("} to{");
				sb.append(((MyNumberPair) right).y.toString(tpl));
				sb.append("}");
				sb.append(((MyNumberPair) left).x.toString(tpl));
			} else {
				if (stringType.isGiac()) {
					sb.append("sum(");
				} else {
					sb.append("gGbSuM(");
				}
				sb.append(leftStr);
				sb.append(',');
				sb.append(rightStr);
				sb.append(")");
			}
			break;
		case PRODUCT:
			if (stringType == StringType.LATEX) {
				sb.append("\\prod_{");
				sb.append(((MyNumberPair) left).y.toString(tpl));
				sb.append("=");
				sb.append(((MyNumberPair) right).x.toString(tpl));
				sb.append("}^{");
				sb.append(((MyNumberPair) right).y.toString(tpl));
				sb.append("}");
				sb.append(((MyNumberPair) left).x.toString(tpl));
			} else if (stringType == StringType.LIBRE_OFFICE) {
				sb.append("product from{");
				sb.append(((MyNumberPair) left).y.toString(tpl));
				sb.append("=");
				sb.append(((MyNumberPair) right).x.toString(tpl));
				sb.append("} to{");
				sb.append(((MyNumberPair) right).y.toString(tpl));
				sb.append("}");
				sb.append(((MyNumberPair) left).x.toString(tpl));
			} else {
				if (stringType.isGiac()) {
					sb.append("product(");
				} else {
					sb.append("gGbPrOdUcT(");
				}
				sb.append(leftStr);
				sb.append(',');
				sb.append(rightStr);
				sb.append(")");
			}
			break;
		case SUBSTITUTION:
			if (stringType == StringType.LATEX) {
				sb.append("\\left.");
				sb.append(rightStr);
				sb.append("\\right\\mid_{");
				sb.append(leftStr);
				sb.append("}");
			} else if (stringType == StringType.LIBRE_OFFICE) {
				sb.append("left none");
				sb.append(rightStr);
				sb.append("right rline_{");
				sb.append(leftStr);
				sb.append("}");
			} else {
				if (stringType.isGiac()) {
					sb.append("subst(");
				} else {
					sb.append("gGbSuBsTiTuTiOn(");
				}
				sb.append(leftStr);
				sb.append(',');
				sb.append(rightStr);
				sb.append(")");
			}
			break;
		case IF_SHORT:
		case IF:
			if (stringType.isGiac()) {
				sb.append("when(");
				sb.append(leftStr);
				sb.append(',');
				sb.append(rightStr);
				sb.append(",undef)");
			} else {
				appendIfCommand(sb, tpl, loc);
				sb.append(leftStr);
				sb.append(", ");
				sb.append(rightStr);
				sb.append(tpl.rightCommandBracket());
			}
			break;
		case IF_ELSE:
			if (stringType.isGiac()) {
				sb.append("when(");
				sb.append(leftStr);
				sb.append(",");
				sb.append(rightStr);
				sb.append(")");
			} else {
				appendIfCommand(sb, tpl, loc);
				sb.append(leftStr);
				sb.append(", ");
				sb.append(rightStr);
				sb.append(tpl.rightCommandBracket());
			}
			break;

		case IF_LIST:
			if (stringType.isGiac()) {
				sb.append("piecewise(");
			} else {
				appendIfCommand(sb, tpl, loc);
			}

			MyList cond = (MyList) left;
			MyList fn = (MyList) right;
			for (int i = 0; i < cond.size(); i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(valueForm ? cond.get(i).toValueString(tpl)
						: cond.get(i).toString(tpl));
				sb.append(", ");
				sb.append(valueForm ? fn.get(i).toValueString(tpl)
						: fn.get(i).toString(tpl));
			}
			if (fn.size() > cond.size()) {
				sb.append(", ");
				sb.append(valueForm ? fn.get(fn.size() - 1).toValueString(tpl)
						: fn.get(fn.size() - 1).toString(tpl));
			}

			sb.append(stringType.isGiac() ? ")" : tpl.rightCommandBracket());

			break;
		case SEQUENCE:
			if (tpl.hasCASType()) {
				sb.append("seq(round(");
				sb.append(leftStr);
				sb.append("),round(");
				sb.append(rightStr);
				sb.append("),1)");

			} else {
				if (left.isLeaf()) {
					sb.append(leftStr);
				} else {
					tpl.appendWithBrackets(sb, leftStr);
				}

				sb.append(Unicode.ELLIPSIS);

				if (right.isLeaf()) {
					sb.append(rightStr);
				} else {
					tpl.appendWithBrackets(sb, rightStr);
				}
			}

			break;
		default:
			sb.append("unhandled operation ");
			sb.append(operation);
		}
		return sb.toString();
	}

	private static void appendRightIfDefined(String rightStr, StringBuilder sb) {
		if (!rightStr.contains("?")) {
			sb.append(',');
			sb.append(rightStr);
		}
	}

	private static void trig(String leftStr, StringBuilder sb, String mathml, String latex,
			String psTricks, String key, String libreOffice, StringTemplate tpl, Localization loc,
			boolean needDegrees) {

		// send "key" for Giac
		trig(leftStr, sb, mathml, latex, psTricks, key, libreOffice, key, tpl, loc, needDegrees);
	}

	private static void trig(String leftStr, StringBuilder sb, String mathml, String latex,
			String psTricks2, String key, String libreOffice, String giac, StringTemplate tpl,
			Localization loc, boolean needDegrees) {

		trig(leftStr, sb, mathml, latex, psTricks2, key, libreOffice, giac, tpl, loc, needDegrees,
				false, null);
	}

	/**
	 * @param leftStr
	 *            argument
	 * @param sb output string builder
	 */
	private static void trig(String leftStr, StringBuilder sb, String mathml, String latex,
			String psTricks, String key, String libreOffice, String giac, StringTemplate tpl,
			Localization loc, boolean needDegrees, boolean inverseNeedsDegrees,
			String altText) {

		if (tpl.hasType(StringType.CONTENT_MATHML)) {
			MathmlTemplate.mathml(sb, mathml, leftStr, null);
		} else {
			switch (tpl.getStringType()) {
			case SCREEN_READER_ASCII:

				if (altText == null) {
					sb.append(loc.getFunction(key));
				} else if (altText.startsWith("altText.")) {
					sb.append(loc.getMenuDefault(altText,
							altText.replace("altText.", "")));
				}

				sb.append(tpl.leftBracket());
				break;
			case LATEX:
				if (tpl.isPrintLocalizedCommandNames()) {
					// eg \\operatorname{sen} when sin translated
					sb.append("\\operatorname{");
					sb.append(loc.getFunction(key));
					sb.append("}");
				} else {
					sb.append(latex);
				}
				sb.append(" \\left( ");
				break;
			case LIBRE_OFFICE:
				if (!libreOffice.equals(loc.getFunction(key))) {
					sb.append("func ");

				}
				sb.append(loc.getFunction(key));
				sb.append(" left( ");
				break;
			case GIAC:
				sb.append(giac);
				sb.append('(');
				break;
			case PGF:
				// http://tex.stackexchange.com/questions/12951/incorrect-plot-using-pgfplots
				if (inverseNeedsDegrees) {
					sb.append("rad(");
				}
				sb.append(key);
				sb.append('(');
				break;
			case PSTRICKS:
				sb.append(psTricks);
				break;
			default:
				if (tpl.isPrintLocalizedCommandNames() || loc.areEnglishCommandsForced()) {
					sb.append(loc.getFunction(key));
				} else {
					sb.append(key);
				}
				sb.append("(");
			}
			if (needDegrees && tpl.hasType(StringType.PGF)) {
				sb.append("(").append(leftStr).append(") 180/pi");
			} else {
				sb.append(leftStr);
			}

			sb.append(tpl.rightBracket());

			// extra closing bracket for rad(atan(...))
			if (inverseNeedsDegrees && tpl.hasType(StringType.PGF)) {
				sb.append(")");
			}
		}
	}

	private static void appendFunction(StringBuilder sb, String string) {
		sb.append(string);
		sb.append('(');
	}

	private static void twoVar(StringBuilder sb, String leftStr, String rightStr, String op,
			String mathml, String pstricks, String giac, StringTemplate tpl, Kernel kernel,
			boolean trig) {
		StringType stringType = tpl.getStringType();
		if (stringType.equals(StringType.CONTENT_MATHML)) {
			MathmlTemplate.mathml(sb, mathml, leftStr, rightStr);
		} else {
			switch (stringType) {
			case LATEX:

				wrapInBackslashOperatorname(sb, op);

				sb.append(" \\left( ");
				break;
			case LIBRE_OFFICE:
				sb.append("func ");
				sb.append(op);
				sb.append("left( ");
				break;
			case PSTRICKS:
				sb.append(pstricks);
				sb.append("(");
				break;

			case GIAC:
				sb.append(trig ? giacDegFix(giac, kernel) : op);
				sb.append("(");
				break;

			default:
				sb.append(op);
				sb.append(tpl.leftBracket());
			}
			sb.append(leftStr);
			sb.append(", ");
			sb.append(rightStr);
			sb.append(tpl.rightBracket());
		}
	}

	private static void appendIfCommand(StringBuilder sb, StringTemplate tpl, Localization loc) {
		if (tpl.isPrintLocalizedCommandNames()) {
			sb.append(loc.getCommand("If"));
			sb.append(tpl.leftBracket());
		} else {
			sb.append("If");
			sb.append(tpl.leftSquareBracket());
		}
	}

	private static void appendUserFunction(StringBuilder sb, String leftStr, String rightStr,
			StringTemplate tpl) {
		sb.append(leftStr);
		tpl.appendWithBrackets(sb, rightStr);
	}

	private static void wrapInBackslashOperatorname(StringBuilder sb, String cmd) {

		sb.append("\\operatorname{");
		sb.append(cmd);
		sb.append("}");
	}

	private static void appendFunctionNVar(StringBuilder sb, ExpressionValue left, String leftStr,
			String rightStr, StringTemplate tpl) {
		// multivariate functions
		if (left.isGeoElement()) {
			sb.append(((GeoElement) left).getLabel(tpl));
		} else {
			sb.append(leftStr);
		}
		// no parameters for LeftSide[a], Derivative[sin(x+y),y], etc
		// parameters for unknown cas functions
		if (!left.isGeoElement() || ((GeoElement) left).isLabelSet()
				|| ((GeoElement) left).isLocalVariable() || left instanceof GeoDummyVariable
				|| left instanceof GeoCasCell) {
			sb.append(tpl.leftBracket());

			// rightStr is a list of arguments, e.g. {2, 3}
			// drop the curly braces { and }
			// or list( and ) in case of mpreduce

			sb.append(rightStr);

			sb.append(tpl.rightBracket());
		}
	}

	private static String giacDegFix(String string, Kernel kernel) {
		if (kernel.getInverseTrigReturnsAngle()) {
			return "deg" + string;
		}
		return string;
	}
}

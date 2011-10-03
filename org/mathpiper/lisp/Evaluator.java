/* {{{ License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */ //}}}

// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:folding=explicit:collapseFolds=0:
package org.mathpiper.lisp;

// class EvalFuncBase defines the interface to 'something that can
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mathpiper.io.MathPiperOutputStream;
import org.mathpiper.io.StringOutputStream;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.localvariables.LocalVariableFrame;
import org.mathpiper.lisp.printers.MathPiperPrinter;
import org.mathpiper.lisp.stacks.UserStackInformation;

// evaluate'
public abstract class Evaluator {

	public static boolean DEBUG = false;
	public static boolean TRACE_TO_STANDARD_OUT = false;
	public static boolean VERBOSE_DEBUG = false;
	private static int evalDepth = 0;
	public static boolean iTraced = false;
	private static List traceFunctionList = null;
	private static List traceExceptFunctionList = null;
        public static boolean iStackTraced = false;
	UserStackInformation iBasicInfo = new UserStackInformation();

	public static void showExpression(StringBuffer outString, Environment aEnvironment, ConsPointer aExpression) throws Exception {
		MathPiperPrinter infixprinter = new MathPiperPrinter(aEnvironment.iPrefixOperators, aEnvironment.iInfixOperators, aEnvironment.iPostfixOperators, aEnvironment.iBodiedOperators);
		// Print out the current expression
		//StringOutput stream(outString);
		MathPiperOutputStream stream = new StringOutputStream(outString);
		infixprinter.print(-1, aExpression, stream, aEnvironment);
		// Escape quotes.
		for (int i = outString.length() - 1; i >= 0; --i) {
			char c = outString.charAt(i);
			if (c == '\"') {
				//outString.insert(i, '\\');
				outString.deleteCharAt(i);
			}
		}// end for.

	}//end method.

	public static void traceShowEnter(Environment aEnvironment, ConsPointer aExpression, String extraInfo) throws Exception {
		for (int i = 0; i < evalDepth; i++) {
			// aEnvironment.iEvalDepth; i++) {
			if (TRACE_TO_STANDARD_OUT) {
				System.out.print("    ");
			} else {
				aEnvironment.write("    ");
			}
		}//end for.

		if (TRACE_TO_STANDARD_OUT) {
			System.out.print("Enter<" + extraInfo + ">{(");
		} else {
			aEnvironment.write("Enter<" + extraInfo + ">{(");
		}

		String function = "";
		if (aExpression.car() instanceof ConsPointer) {
			ConsPointer sub = (ConsPointer) aExpression.car();
			if (sub.car() instanceof String) {
				function = (String) sub.car();
			}
		}
		if (TRACE_TO_STANDARD_OUT) {
			System.out.print(function);
		} else {
			aEnvironment.write(function);
		}//end else.

		if (TRACE_TO_STANDARD_OUT) {
			System.out.print(", ");
		} else {
			aEnvironment.write(", ");
		}
		traceShowExpression(aEnvironment, aExpression);

		if (TRACE_TO_STANDARD_OUT) {
			System.out.print(");\n");
		} else {
			aEnvironment.write(");\n");
		}

		if (traceFunctionList != null) {
			for (int i = 0; i < (evalDepth + 1); i++) {
				if (TRACE_TO_STANDARD_OUT) {
					System.out.print("    ");
				} else {
					aEnvironment.write("    ");
				}
			}//end for.

			//Trace stack frames.
			List<String> functionsOnStack = new ArrayList();
			LocalVariableFrame localVariableFrame = aEnvironment.iLocalVariablesFrame;
			while (localVariableFrame != null) {
				functionsOnStack.add(localVariableFrame.getFunctionName());
				localVariableFrame = localVariableFrame.iNext;
			}//end while
			Collections.reverse(functionsOnStack);
			StringBuilder functionsDump = new StringBuilder();
			functionsDump.append("(User Function Call Stack: ");
			for(String functionName: functionsOnStack)
			{
				functionsDump.append(functionName + ", ");
			}//end for.
			functionsDump.append(")\n");
			if (TRACE_TO_STANDARD_OUT) {
				System.out.print(functionsDump.toString());
			} else {
				aEnvironment.write(functionsDump.toString());
			}//end else.
		}//end if.


		evalDepth++;
	}//end method.

	public static void traceShowArg(Environment aEnvironment, ConsPointer aParam, ConsPointer aValue) throws Exception {
		for (int i = 0; i < evalDepth; i++) {
			//aEnvironment.iEvalDepth; i++) {
			if (TRACE_TO_STANDARD_OUT) {
				System.out.print("    ");
			} else {
				aEnvironment.write("    ");
			}
		}
		if (TRACE_TO_STANDARD_OUT) {
			System.out.print("Arg(");
		} else {
			aEnvironment.write("Arg(");
		}
		traceShowExpression(aEnvironment, aParam);
		if (TRACE_TO_STANDARD_OUT) {
			System.out.print(" -> ");
		} else {
			aEnvironment.write(" -> ");
		}
		traceShowExpression(aEnvironment, aValue);
		if (TRACE_TO_STANDARD_OUT) {
			System.out.print(");\n");
		} else {
			aEnvironment.write(");\n");
		}
	}//end method.

	public static void traceShowExpression(Environment aEnvironment, ConsPointer aExpression) throws Exception {
		StringBuffer outString = new StringBuffer();

		showExpression(outString, aEnvironment, aExpression);

		String expression = outString.toString();
		expression = expression.replace("\n", "");

		if (TRACE_TO_STANDARD_OUT) {
			System.out.print(expression);
		} else {
			aEnvironment.write(expression);
		}
	}//end method.

	public static void traceShowRule(Environment aEnvironment, ConsPointer aExpression, String ruleDump) throws Exception {

		for (int i = 0; i < evalDepth; i++) {
			// aEnvironment.iEvalDepth; i++) {
			if (TRACE_TO_STANDARD_OUT) {
				System.out.print("    ");
			} else {
				aEnvironment.write("    ");
			}
		}

		String function = "";
		if (aExpression.car() instanceof ConsPointer) {
			ConsPointer sub = (ConsPointer) aExpression.car();
			if (sub.car() instanceof String) {
				function = (String) sub.car();
			}
		}//end function.

		ruleDump = ruleDump.replace("\n", "");

		if (TRACE_TO_STANDARD_OUT) {
			System.out.print("**** Rule in function (" + function + ") matched: ");
			System.out.print(ruleDump);
			System.out.print("\n");
		} else {
			aEnvironment.write("**** Rule in function (" + function + ") matched: ");
			aEnvironment.write(ruleDump);
			aEnvironment.write("\n");
		}
	}//end method.

	public static void traceShowLeave(Environment aEnvironment, ConsPointer aResult, ConsPointer aExpression, String extraInfo, String localVariables) throws Exception {
		if (evalDepth != 0) {
			evalDepth--;
		}
		for (int i = 0; i < evalDepth; i++) {
			// aEnvironment.iEvalDepth; i++) {
			if (TRACE_TO_STANDARD_OUT) {
				System.out.print("    ");
			} else {
				aEnvironment.write("    ");
			}
		}
		if (TRACE_TO_STANDARD_OUT) {
			System.out.print("Leave<" + extraInfo + ">}(");
		} else {
			aEnvironment.write("Leave<" + extraInfo + ">}(");
		}
		traceShowExpression(aEnvironment, aExpression);
		if (TRACE_TO_STANDARD_OUT) {
			System.out.print(" -> ");
		} else {
			aEnvironment.write(" -> ");
		}

		traceShowExpression(aEnvironment, aResult);

		if (localVariables != null) {
			if (TRACE_TO_STANDARD_OUT) {
				System.out.print(",    " + localVariables);
			} else {
				aEnvironment.write(",    " + localVariables);
			}//end else.

		}//end if.

		if (TRACE_TO_STANDARD_OUT) {
			System.out.print(");\n");
		} else {
			aEnvironment.write(");\n");
		}//end else.
	}//end method.

	public static boolean isTraced() {
		return iTraced;
	}

	public static void traceOff() {
		iTraced = false;
	}

	public static void traceOn() {
		iTraced = true;
	}

	public static boolean isStackTraced() {
		return iStackTraced;
	}

	public static void stackTraceOff() {
		iStackTraced = false;
	}

	public static void stackTraceOn() {
		iStackTraced = true;
	}

	public abstract void evaluate(Environment aEnvironment, int aStackTop, ConsPointer aResult, ConsPointer aArgumentsOrExpression) throws Exception;


	public UserStackInformation stackInformation() {
		return iBasicInfo;
	}

	public void showStack(Environment aEnvironment, MathPiperOutputStream aOutput) {
	}//end method.

	public static void setTraceFunctionList(List traceFunctionList) {
		Evaluator.traceFunctionList = traceFunctionList;
	}

	public static void setTraceExceptFunctionList(List traceExceptFunctionList) {
		Evaluator.traceExceptFunctionList = traceExceptFunctionList;
	}

	public static boolean isTraceFunction(String functionName) {
		if (!(traceFunctionList == null)) {
			return traceFunctionList.contains(functionName);
		} else if (!(traceExceptFunctionList == null)) {
			return !traceExceptFunctionList.contains(functionName);
		} else {
			return true;
		}//end else.

	}//end method.
}//end class.

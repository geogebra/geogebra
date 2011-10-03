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
package org.mathpiper.lisp.stacks;

import org.mathpiper.lisp.*;
import org.mathpiper.lisp.cons.ConsPointerArray;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.cons.Cons;

/** 
 * Implements a stack of pointers to CONS that can be used to pass
 * arguments to functions, and receive results back.
 */
public class ArgumentStack {

    ConsPointerArray iArgumentStack;
    int iStackTopIndex;

    //TODO appropriate constructor?
    public ArgumentStack(Environment aEnvironment, int aStackSize) {
        iArgumentStack = new ConsPointerArray(aEnvironment, aStackSize, null);
        iStackTopIndex = 0;
        //printf("STACKSIZE %d\n",aStackSize);
    }

    public int getStackTopIndex() {
        return iStackTopIndex;
    }

    public void raiseStackOverflowError(int aStackTop, Environment aEnvironment) throws Exception {
        LispError.raiseError("Argument stack reached maximum. Please extend argument stack with --stack argument on the command line.", "[INTERNAL]", aStackTop, aEnvironment);
    }

    public void pushArgumentOnStack(Cons aCons, int aStackTop, Environment aEnvironment) throws Exception {
        if (iStackTopIndex >= iArgumentStack.size()) {
            raiseStackOverflowError(aStackTop, aEnvironment);
        }
        iArgumentStack.setElement(iStackTopIndex, aCons);
        iStackTopIndex++;
    }

    public void pushNulls(int aNr, int aStackTop, Environment aEnvironment) throws Exception {
        if (iStackTopIndex + aNr > iArgumentStack.size()) {
            raiseStackOverflowError(aStackTop, aEnvironment);
        }
        iStackTopIndex += aNr;
    }

    public ConsPointer getElement(int aPos, int aStackTop, Environment aEnvironment) throws Exception {
        LispError.lispAssert(aPos >= 0 && aPos < iStackTopIndex, aEnvironment, aStackTop);
        return iArgumentStack.getElement(aPos);
    }

    public void popTo(int aTop, int aStackTop, Environment aEnvironment) throws Exception {
        LispError.lispAssert(aTop <= iStackTopIndex, aEnvironment, aStackTop);
        while (iStackTopIndex > aTop) {
            iStackTopIndex--;
            iArgumentStack.setElement(iStackTopIndex, null);
        }
    }

    public void reset(int aStackTop, Environment aEnvironment) throws Exception {
        this.popTo(0, aStackTop, aEnvironment);
    }//end method.

    public String dump(int aStackTop, Environment aEnvironment) throws Exception {

        StringBuilder stringBuilder = new StringBuilder();

        int functionBaseIndex = 0;

        int functionPositionIndex = 0;


        while (functionBaseIndex <= aStackTop) {

            if(functionBaseIndex == 0)
            {
                stringBuilder.append("\n\n========================================= Start Of Built In Function Stack Trace\n");
            }
            else
            {
                stringBuilder.append("-----------------------------------------\n");
            }

            ConsPointer consPointer = getElement(functionBaseIndex, aStackTop, aEnvironment);

            int argumentCount = Utility.listLength(aEnvironment, aStackTop, consPointer);

            ConsPointer argumentPointer = new ConsPointer();

            Object car = consPointer.getCons().car();

            ConsPointer consTraverser = new ConsPointer( consPointer.getCons());

            stringBuilder.append(functionPositionIndex++ + ": ");
            stringBuilder.append(Utility.printMathPiperExpression(aStackTop, consTraverser, aEnvironment, -1));
            stringBuilder.append("\n");

            consTraverser.goNext(aStackTop, aEnvironment);

            while(consTraverser.getCons() != null)
            {
                stringBuilder.append("   " + functionPositionIndex++ + ": ");
                stringBuilder.append("-> " + Utility.printMathPiperExpression(aStackTop, consTraverser, aEnvironment, -1));
                stringBuilder.append("\n");
                
                consTraverser.goNext(aStackTop, aEnvironment);
            }


            functionBaseIndex = functionBaseIndex + argumentCount;

        }//end while.

        stringBuilder.append("========================================= End Of Built In Function Stack Trace\n\n");

        return stringBuilder.toString();
        
    }//end method.

    public ConsPointer[] getElements(int quantity) throws IndexOutOfBoundsException {
        int last = iStackTopIndex;
        int first = last - quantity;
        return iArgumentStack.getElements(first, last);
    }//end method.
}//end class.


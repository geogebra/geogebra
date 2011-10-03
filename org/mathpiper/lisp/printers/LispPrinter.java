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
package org.mathpiper.lisp.printers;

import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.*;
import org.mathpiper.io.MathPiperOutputStream;

public class LispPrinter {

    //private List<Cons> visitedLists = new ArrayList<Cons>();
    public void print(int aStackTop, ConsPointer aExpression, MathPiperOutputStream aOutput, Environment aEnvironment) throws Exception {
        printExpression(aExpression, aOutput, aEnvironment, 0);

        //visitedLists.clear();
    }


    public void rememberLastChar(char aChar) {
    }


    void printExpression(ConsPointer aExpression, MathPiperOutputStream aOutput, Environment aEnvironment, int aDepth /* =0 */) throws Exception {
        ConsPointer consWalker = new ConsPointer();
        consWalker.setCons(aExpression.getCons());
        int item = 0;

        while (consWalker.getCons() != null) {

            if (consWalker.car() instanceof String) {
                String string = (String) consWalker.car();
                aOutput.write(string);
                aOutput.putChar(' ');
            } // else print "(", print sublist, and print ")"
            else if (consWalker.car() instanceof ConsPointer) {
                if (item != 0) {
                    indent(aOutput, aDepth + 1);
                }

                /*
                Cons atomCons = (Cons) consWalker.getCons();
                if (visitedLists.contains(atomCons)) {
                aOutput.write("(CYCLE_LIST)");

                } else {
                visitedLists.add(atomCons);*/

                if (item != 0) {
                    indent(aOutput, aDepth + 1);
                }
                aOutput.write("(");
                printExpression(((ConsPointer) consWalker.car()), aOutput, aEnvironment, aDepth + 1);
                aOutput.write(")");
                item = 0;
                //}


            } else {
                aOutput.write("[BuiltinObject]");
            }
            consWalker = (consWalker.cdr()); // print rest element
            item++;
        }//end while.

    }//end method.


    void indent(MathPiperOutputStream aOutput, int aDepth) throws Exception {
        aOutput.write("\n");
        int i;
        for (i = aDepth; i > 0; i--) {
            aOutput.write("  ");
        }
    }

};

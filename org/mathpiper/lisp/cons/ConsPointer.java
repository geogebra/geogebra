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
package org.mathpiper.lisp.cons;

import org.mathpiper.io.StringOutput;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.printers.LispPrinter;

/** 
 * Provides a smart pointer type to CONS
 *  that can be inserted into linked lists. They do the actual
 *  reference counting, and consequent destruction of the object if
 *  nothing points to it. ConsPointer is used in Cons as a pointer
 *  to the next object, and in diverse parts of the built-in internal
 *  functions to hold temporary values.
 */
public class ConsPointer {

    Cons iCons;


    public Object car() throws Exception {
        return iCons.car();
    }

    public ConsPointer cdr() {
        return iCons.cdr();
    }

    public ConsPointer()
    {
        super();
    }


    public ConsPointer( Cons aCons) {
        super();
        iCons = aCons;
    }

    //public ConsPointer(ConsPointer aConsPointer) {
    //    iCons = aConsPointer.getCons();
    //    }//todo:tk:I am removing this until a mechanism is developed to traverse
    //    conses which does not destroy the original ConsPointer's pointer. See Utility.flatCopy.

    public void setCons(Cons aNext) {
        iCons = aNext;
    }

    public Cons getCons() {
        return iCons;
    }


    //iPointer = (iPointer.cdr());
    public void goNext(int aStackTop , Environment aEnvironment) throws Exception {
        LispError.check(aEnvironment, aStackTop, iCons != null, LispError.NOT_LONG_ENOUGH, "INTERNAL");
        iCons = iCons.cdr().iCons;
    }

    public void goSub(int aStackTop , Environment aEnvironment) throws Exception {
        LispError.check(aEnvironment, aStackTop, iCons != null, LispError.INVALID_ARGUMENT, "INTERNAL");
        LispError.check(aEnvironment, aStackTop, iCons.car() instanceof ConsPointer, LispError.NOT_A_LIST, "INTERNAL");
        iCons = ((ConsPointer)iCons.car()).getCons();
    }

    @Override
    public String toString() {
        StringOutput out = new StringOutput();
        LispPrinter printer = new LispPrinter();
        try {
            printer.print(-1, this, out, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toString();
    }//end method.

    public int type()
    {
        return iCons.type();
    }//end method.

}//end class.

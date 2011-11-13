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
package org.mathpiper.builtin.functions.core;


import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.lisp.cons.AtomCons;
import org.mathpiper.lisp.cons.Cons;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Utility;


/**
 *
 *  
 */
public class BuiltinAssoc extends BuiltinFunction {

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception {
        // key to find
        ConsPointer key = new ConsPointer();
        key.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());

        // assoc-list to find it in
        ConsPointer list = new ConsPointer();
        list.setCons(getArgumentPointer(aEnvironment, aStackTop, 2).getCons());

        Cons listCons;

        //check that it is a compound object
        LispError.checkArgument(aEnvironment, aStackTop, list.car() instanceof ConsPointer, 2, "BuiltinAssoc");
        listCons = ((ConsPointer) list.car()).getCons();
        LispError.checkArgument(aEnvironment, aStackTop, listCons != null, 2, "BuiltinAssoc");
        listCons = listCons.cdr().getCons();

        Cons result = Utility.associativeListGet(aEnvironment, aStackTop, key, listCons);

        if (result != null) {
            getTopOfStackPointer(aEnvironment, aStackTop).setCons(result);

        } else {
            getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, aStackTop, "Empty"));
        }


    }//end method.


}//end class.


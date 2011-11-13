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
package org.mathpiper.lisp.behaviours;

import org.mathpiper.lisp.cons.Cons;
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.cons.SublistCons;

/** Substitute behaviour for backquote mechanism as in LISP.
 * When typing `(...) all occurrences of @a will be
 * replaced with:
 * 1) a evaluated if a is an atom
 * 2) function call with function name replaced by evaluated
 *    head of function if a is a function. For instance, if
 *    a is f(x) and f is g, then f(x) gets replaced by g(x)
 */
public class BackQuoteSubstitute implements Substitute {

    Environment iEnvironment;


    public BackQuoteSubstitute(Environment aEnvironment) {
        iEnvironment = aEnvironment;
    }


    public boolean matches(Environment aEnvironment, int aStackTop, ConsPointer aResult, ConsPointer aElement) throws Exception {
        if (!(aElement.car() instanceof ConsPointer)) {
            return false;
        }

        Cons ptr = ((ConsPointer) aElement.car()).getCons();
        if (ptr == null) {
            return false;
        }

        if (!(ptr.car() instanceof String)) {
            return false;
        }

        if (ptr.car().equals("`")) {
            aResult.setCons(aElement.getCons());
            return true;
        }

        if (!ptr.car().equals("@")) {
            return false;
        }

        ptr = ptr.cdr().getCons();

        if (ptr == null) {
            return false;
        }

        if (ptr.car() instanceof String) {
            ConsPointer cur = new ConsPointer();
            cur.setCons(ptr);
            iEnvironment.iLispExpressionEvaluator.evaluate(iEnvironment, aStackTop, aResult, cur);
            return true;
        } else {
            ptr = ((ConsPointer) ptr.car()).getCons();
            ConsPointer cur = new ConsPointer();
            cur.setCons(ptr);
            ConsPointer args = new ConsPointer();
            args.setCons(ptr.cdr().getCons());
            ConsPointer result = new ConsPointer();
            iEnvironment.iLispExpressionEvaluator.evaluate(iEnvironment, aStackTop, result, cur);
            result.cdr().setCons(args.getCons());
            ConsPointer result2 = new ConsPointer();
            result2.setCons(SublistCons.getInstance(aEnvironment, result.getCons()));
            Utility.substitute(aEnvironment, aStackTop, aResult, result2, this);
            return true;
        }
        //      return false;
    }

};

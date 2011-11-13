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
package org.mathpiper.lisp.rulebases;

import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsTraverser;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.cons.SublistCons;

public class ListedMacroRulebase extends MacroRulebase {

    public ListedMacroRulebase(Environment aEnvironment, int aStackTop, ConsPointer aParameters, String functionName) throws Exception {
        super(aEnvironment, aStackTop, aParameters, functionName);
    }


    @Override
    public boolean isArity(int aArity) {
        return (arity() <= aArity);
    }


    @Override
    public void evaluate(Environment aEnvironment, int aStackTop, ConsPointer aResult, ConsPointer aArguments) throws Exception {
        ConsPointer newArgs = new ConsPointer();
        ConsTraverser consTraverser = new ConsTraverser(aEnvironment, aArguments);
        ConsPointer ptr = newArgs;
        int arity = arity();
        int i = 0;
        while (i < arity && consTraverser.getCons() != null) {
            ptr.setCons(consTraverser.getCons().copy(aEnvironment, false));
            ptr = (ptr.cdr());
            i++;
            consTraverser.goNext(aStackTop);
        }
        if (consTraverser.cdr().getCons() == null) {
            ptr.setCons(consTraverser.getCons().copy(aEnvironment, false));
            ptr = (ptr.cdr());
            i++;
            consTraverser.goNext(aStackTop);
            LispError.lispAssert(consTraverser.getCons() == null, aEnvironment, aStackTop);
        } else {
            ConsPointer head = new ConsPointer();
            head.setCons(aEnvironment.iListAtom.copy(aEnvironment, false));
            head.cdr().setCons(consTraverser.getCons());
            ptr.setCons(SublistCons.getInstance(aEnvironment, head.getCons()));
        }
        super.evaluate(aEnvironment, aStackTop, aResult, newArgs);
    }

}

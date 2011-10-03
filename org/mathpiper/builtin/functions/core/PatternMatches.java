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

import org.mathpiper.builtin.BuiltinContainer;
import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.builtin.PatternContainer;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.cons.ConsTraverser;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.Utility;

/**
 *
 *  
 */
public class PatternMatches extends BuiltinFunction
{

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception
    {
        ConsPointer pattern = new ConsPointer();
        pattern.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());
        BuiltinContainer gen = (BuiltinContainer) pattern.car();
        LispError.checkArgument(aEnvironment, aStackTop, gen != null, 1, "PatternMatches");
        LispError.checkArgument(aEnvironment, aStackTop, gen.typeName().equals("\"Pattern\""), 1, "PatternMatches");

        ConsPointer list = new ConsPointer();
        list.setCons(getArgumentPointer(aEnvironment, aStackTop, 2).getCons());

        PatternContainer patclass = (PatternContainer) gen;

        ConsTraverser consTraverser = new ConsTraverser(aEnvironment, list);
        LispError.checkArgument(aEnvironment, aStackTop, consTraverser.getCons() != null, 2, "PatternMatches");
        LispError.checkArgument(aEnvironment, aStackTop, consTraverser.car() instanceof ConsPointer, 2, "PatternMatches");
        consTraverser.goSub(aStackTop);
        LispError.checkArgument(aEnvironment, aStackTop, consTraverser.getCons() != null, 2, "PatternMatches");
        consTraverser.goNext(aStackTop);

        ConsPointer ptr = consTraverser.getPointer();
        LispError.checkArgument(aEnvironment, aStackTop, ptr != null, 2, "PatternMatches");
        boolean matches = patclass.matches(aEnvironment, aStackTop, ptr);
        Utility.putBooleanInPointer(aEnvironment, getTopOfStackPointer(aEnvironment, aStackTop), matches);
    }
}

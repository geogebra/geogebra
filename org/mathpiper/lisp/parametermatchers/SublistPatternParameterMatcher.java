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
package org.mathpiper.lisp.parametermatchers;

import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.cons.ConsTraverser;
import org.mathpiper.lisp.Environment;

/// Class for matching against a list of PatternParameterMatcher objects.
public class SublistPatternParameterMatcher extends PatternParameterMatcher {

    protected PatternParameterMatcher[] iMatchers;

    protected int iNumberOfMatchers;


    public SublistPatternParameterMatcher(PatternParameterMatcher[] aMatchers, int aNrMatchers) {
        iMatchers = aMatchers;
        iNumberOfMatchers = aNrMatchers;
    }


    public boolean argumentMatches(Environment aEnvironment, int aStackTop, ConsPointer aExpression, ConsPointer[] arguments) throws Exception {

        if (!(aExpression.car() instanceof ConsPointer)) {
            return false;
        }

        ConsTraverser consTraverser = new ConsTraverser(aEnvironment, aExpression);

        consTraverser.goSub(aStackTop);

        for (int i = 0; i < iNumberOfMatchers; i++) {

            ConsPointer consPointer = consTraverser.getPointer();

            if (consPointer == null) {
                return false;
            }

            if (consTraverser.getCons() == null) {
                return false;
            }

            if (!iMatchers[i].argumentMatches(aEnvironment, aStackTop, consPointer, arguments)) {
                return false;
            }

            consTraverser.goNext(aStackTop);
        }

        if (consTraverser.getCons() != null) {
            return false;
        }
        
        return true;
    }


    public String getType()
    {
        return "Sublist";
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for(int x = 0; x < iMatchers.length; x++)
        {
            PatternParameterMatcher matcher = iMatchers[x];

            stringBuilder.append(matcher.getType());
            
            stringBuilder.append(": ");
            
            stringBuilder.append(matcher.toString());

            stringBuilder.append(", ");
            
        }

        return stringBuilder.toString();
    }

}

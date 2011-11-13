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
import org.mathpiper.lisp.Environment;

//Abstract class for matching one argument to a pattern.
public abstract class PatternParameterMatcher {

    /**
     *Check whether some expression matches to the pattern.
     *@aEnvironment the underlying Lisp environment.
     *@aExpression the expression to test.
     *@arguments (input/output) actual values of the pattern variables for aExpression.
     */
    public abstract boolean argumentMatches(Environment aEnvironment, int aStackTop, ConsPointer aExpression, ConsPointer[] arguments) throws Exception;


    public abstract String getType();

}

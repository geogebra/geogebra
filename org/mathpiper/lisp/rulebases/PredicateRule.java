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
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.Utility;

/**
 * A rule with a predicate (the rule matches if the predicate evaluates to True.)
 */
class PredicateRule extends Rule {

    protected int iPrecedence;
    protected ConsPointer iBody;
    protected ConsPointer iPredicate;


    public PredicateRule(Environment aEnvironment, int aPrecedence, ConsPointer aPredicate, ConsPointer aBody) {
        iBody = new ConsPointer();
        iBody.setCons(aBody.getCons());
        iPredicate = new ConsPointer();
        iPrecedence = aPrecedence;
        iPredicate.setCons(aPredicate.getCons());

    }


    protected PredicateRule(Environment aEnvironment) {
        iBody = new ConsPointer();
        iPredicate = new ConsPointer();
    }


    private PredicateRule() {
    }


    /**
     *  Return true if the rule matches.
     * 
     * @param aEnvironment
     * @param aArguments
     * @return
     * @throws java.lang.Exception
     */
    // iPredicate is evaluated in \a Environment. If the result
    /// IsTrue(), this function returns true
    public boolean matches(Environment aEnvironment, int aStackTop, ConsPointer[] aArguments) throws Exception {
        ConsPointer pred = new ConsPointer();
        aEnvironment.iLispExpressionEvaluator.evaluate(aEnvironment, aStackTop, pred, iPredicate);
        return Utility.isTrue(aEnvironment, pred, aStackTop);
    }

    /// Access #iPrecedence.

    public int getPrecedence() {
        return iPrecedence;
    }

    /// Access #iBody.

    public ConsPointer getBodyPointer() {
        return iBody;
    }


    public ConsPointer getPredicatePointer() {
        return this.iPredicate;
    }

}

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
 */

//}}}
// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:folding=explicit:collapseFolds=0:

package org.mathpiper.interpreters;

/**
 * This interface must be implemented by all client code that uses the
 * asynchronous {@link Interpreter}.
 */
public interface ResponseListener
{
    /**
     * Called by the asynchronous interperter to provide ResponseListeners with
     * an {@link EvaluationResponse} object which contains the results of the latest
     * evaluation.
     *
     * @param response
     */
    public void response(EvaluationResponse response);

    
    /**
     * Tells the asynchronous interpreter whether this ResponseListener would like to
     * be automatically removed from its listener list after the current evaluation
     * is complete.
     *
     * @return {@code true} if automatic removal is desired and {@code false} otherwise
     */
    public boolean remove();

}// end interface.

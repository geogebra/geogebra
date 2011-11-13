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

import org.mathpiper.builtin.BigNumber;
import org.mathpiper.lisp.collections.TokenMap;

/**
 *
 *  
 */
public class LexLessThan extends LexCompare2
{

    boolean lexFunction(String f1, String f2, TokenMap aHashTable, int aPrecision)
    {
        return f1.compareTo(f2) < 0;
    }

    boolean numFunction(BigNumber n1, BigNumber n2)
    {
        return n1.lessThan(n2) && !n1.equals(n2);
    }
}

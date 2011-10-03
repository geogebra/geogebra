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
package org.mathpiper.io;

public class StringInputStream
        extends MathPiperInputStream {

    int iCurrent;
    StringBuffer iString;


    public StringInputStream(StringBuffer aString, InputStatus aStatus) {
        super(aStatus);
        iString = aString;
        iCurrent = 0;
    }


    public char next()
            throws Exception {

        if (iCurrent == iString.length()) {
            return '\0';
        }

        iCurrent++;

        char c = iString.charAt(iCurrent - 1);

        if (c == '\n') {
            iStatus.nextLine();
        }

        return c;
    }


    public char peek()
            throws Exception {

        if (iCurrent == iString.length()) {
            return '\0';
        }

        return iString.charAt(iCurrent);
    }


    public boolean endOfStream() {

        return (iCurrent == iString.length());
    }


    public StringBuffer startPtr() {

        return iString;
    }


    public int position() {

        return iCurrent;
    }


    public void setPosition(int aPosition) {
        iCurrent = aPosition;
    }

}

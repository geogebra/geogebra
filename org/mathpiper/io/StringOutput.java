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

public class StringOutput implements MathPiperOutputStream {

    StringBuffer stringBuffer;


    public StringOutput() {
        this.stringBuffer = new java.lang.StringBuffer();
    }


    public void putChar(char aChar) {
        this.stringBuffer.append(aChar);
    }


    /*public void setStringBuffer(StringBuffer stringBuffer)
    {
    this.stringBuffer = stringBuffer;
    }//end method.*/
    public String toString() {
        if (this.stringBuffer.length() != 0) {
            String outputMessage = this.stringBuffer.toString();
            this.clear();
            return outputMessage;
        } else {
            return null;
        }//end else.


    }//end method.


    public void clear() {
        this.stringBuffer.delete(0, this.stringBuffer.length());
    }


    public void write(String aString) throws Exception {
        int i;
        for (i = 0; i < aString.length(); i++) {
            putChar(aString.charAt(i));
        }
    }

}//end class.

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

import java.io.InputStreamReader;

public class StandardFileInputStream
        extends StringInputStream {
    // private static String path;
    //static void setPath(String aPath)
    //{
    //    path = aPath;
    //}

    public StandardFileInputStream(String aFileName, InputStatus aStatus)
            throws Exception {
        super(new StringBuffer(), aStatus);

        //System.out.println("YYYYYY " + aFileName);//Note:tk: remove.
        InputStreamReader stream = new InputStreamReader(new java.io.FileInputStream(aFileName));
        int c;

        while (true) {
            c = stream.read();

            if (c == -1) {
                break;
            }

            iString.append((char) c);
        }
    }


    public StandardFileInputStream(java.io.InputStreamReader aStream, InputStatus aStatus)
            throws Exception {
        super(new StringBuffer(), aStatus);

        int c;

        while (true) {
            c = aStream.read();

            if (c == -1) {
                break;
            }

            iString.append((char) c);
        }
    }

}

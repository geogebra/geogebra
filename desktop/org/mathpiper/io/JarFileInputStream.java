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
package org.mathpiper.io;

import java.io.InputStreamReader;

import java.net.*;

public class JarFileInputStream extends StringInputStream {

    public JarFileInputStream(String aFileName, InputStatus aStatus) throws Exception {
        super(new StringBuffer(), aStatus);
        URL url = new URL(aFileName);
        JarURLConnection con = (JarURLConnection) url.openConnection();
        InputStreamReader stream = new InputStreamReader(con.getInputStream());
        int c;
        while (true) {
            c = stream.read();
            if (c == -1) {
                break;
            }
            iString.append((char) c);
        }
    }

}

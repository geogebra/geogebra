/*
 * SVG Salamander
 * Copyright (c) 2004, Mark McKay
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 *   - Redistributions of source code must retain the above 
 *     copyright notice, this list of conditions and the following
 *     disclaimer.
 *   - Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials 
 *     provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE. 
 * 
 * Mark McKay can be contacted at mark@kitfox.com.  Salamander and other
 * projects can be found at http://www.kitfox.com
 *
 * Created on February 12, 2004, 2:45 PM
 */

package com.kitfox.svg.xml.cpx;

import com.kitfox.svg.SVGConst;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class CPXTest {

    /** Creates a new instance of CPXTest */
    public CPXTest() {

//        FileInputStream fin = new FileInputStream();
        writeTest();
        readTest();
    }

    public void writeTest()
    {
        try {

            InputStream is = CPXTest.class.getResourceAsStream("/data/readme.txt");
//System.err.println("Is " + is);

            FileOutputStream fout = new FileOutputStream("C:\\tmp\\cpxFile.cpx");
            CPXOutputStream cout = new CPXOutputStream(fout);

            byte[] buffer = new byte[1024];
            int numBytes;
            while ((numBytes = is.read(buffer)) != -1)
            {
                cout.write(buffer, 0, numBytes);
            }
            cout.close();
        }
        catch (Exception e)
        {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING, null, e);
        }
    }

    public void readTest()
    {
        try {

//            InputStream is = CPXTest.class.getResourceAsStream("/rawdata/test/cpx/text.txt");
//            InputStream is = CPXTest.class.getResourceAsStream("/rawdata/test/cpx/cpxFile.cpx");
            FileInputStream is = new FileInputStream("C:\\tmp\\cpxFile.cpx");
            CPXInputStream cin = new CPXInputStream(is);

            BufferedReader br = new BufferedReader(new InputStreamReader(cin));
            String line;
            while ((line = br.readLine()) != null)
            {
                System.err.println(line);
            }
        }
        catch (Exception e)
        {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING, null, e);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        new CPXTest();
    }

}

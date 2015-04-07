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
 */

package com.kitfox.svg.xml;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author kitfox
 */
public class Base64OutputStream extends FilterOutputStream
{
    int buf;
    int numBytes;
    int numChunks;
    
    public Base64OutputStream(OutputStream out)
    {
        super(out);
    }
    
    public void flush() throws IOException
    {
        out.flush();
    }
    
    public void close() throws IOException
    {
        switch (numBytes)
        {
            case 1:
                buf <<= 4;
                out.write(getBase64Byte(1));
                out.write(getBase64Byte(0));
                out.write('=');
                out.write('=');
                break;
            case 2:
                buf <<= 2;
                out.write(getBase64Byte(2));
                out.write(getBase64Byte(1));
                out.write(getBase64Byte(0));
                out.write('=');
                break;
            case 3:
                out.write(getBase64Byte(3));
                out.write(getBase64Byte(2));
                out.write(getBase64Byte(1));
                out.write(getBase64Byte(0));
                break;
            default:
                assert false;
        }
        
        out.close();
    }
    
    public void write(int b) throws IOException
    {
        buf = (buf << 8) | (0xff & b);
        numBytes++;
        
        if (numBytes == 3)
        {
            out.write(getBase64Byte(3));
            out.write(getBase64Byte(2));
            out.write(getBase64Byte(1));
            out.write(getBase64Byte(0));
            
            numBytes = 0;
            numChunks++;
            if (numChunks == 16)
            {
//                out.write('\r');
//                out.write('\n');
                numChunks = 0;
            }
        }
    }
    
    public byte getBase64Byte(int index)
    {
        return Base64Util.encodeByte((buf >> (index * 6)) & 0x3f);
    }
}

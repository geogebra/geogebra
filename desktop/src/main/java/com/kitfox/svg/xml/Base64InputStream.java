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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author kitfox
 */
public class Base64InputStream extends FilterInputStream
{
    int buf;  //Cached bytes to read
    int bufSize;  //Number of bytes waiting to be read from buffer
    boolean drain = false;  //After set, read no more chunks
    
    public Base64InputStream(InputStream in)
    {
        super(in);
    }

    public int read() throws IOException
    {
        if (drain && bufSize == 0)
        {
            return -1;
        }
        
        if (bufSize == 0)
        {
            //Read next chunk into 4 byte buffer
            int chunk = in.read();
            if (chunk == -1)
            {
                drain = true;
                return -1;
            }
            
            //get remaining 3 bytes
            for (int i = 0; i < 3; ++i)
            {
                int value = in.read();
                if (value == -1)
                {
                    throw new IOException("Early termination of base64 stream");
                }
                chunk = (chunk << 8) | (value & 0xff);
            }

            //Check for special termination characters
            if ((chunk & 0xffff) == (((byte)'=' << 8) | (byte)'='))
            {
                bufSize = 1;
                drain = true;
            }
            else if ((chunk & 0xff) == (byte)'=')
            {
                bufSize = 2;
                drain = true;
            }
            else
            {
                bufSize = 3;
            }
            
            //Fill buffer with decoded characters
            for (int i = 0; i < bufSize + 1; ++i)
            {
                buf = (buf << 6) | Base64Util.decodeByte((chunk >> 24) & 0xff);
                chunk <<= 8;
            }
        }
        
        //Return nth remaing bte & decrement counter
        return (buf >> (--bufSize * 8)) & 0xff;
    } 
}

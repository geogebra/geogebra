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
 * Created on February 12, 2004, 10:34 AM
 */

package com.kitfox.svg.xml.cpx;

import com.kitfox.svg.SVGConst;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.security.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.*;

/**
 * This class reads/decodes the CPX file format.  This format is a simple
 * compression/encryption transformer for XML data.  This stream takes in
 * encrypted XML and outputs decrypted.  It does this by checking for a magic
 * number at the start of the stream.  If absent, it treats the stream as
 * raw XML data and passes it through unaltered.  This is to aid development
 * in debugging versions, where the XML files will not be in CPX format.
 *
 * See http://java.sun.com/developer/technicalArticles/Security/Crypto/
 *
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class CPXInputStream extends FilterInputStream implements CPXConsts {


    SecureRandom sec = new SecureRandom();

    Inflater inflater = new Inflater();

    int xlateMode;

    //Keep header bytes in case this stream turns out to be plain text
    byte[] head = new byte[4];
    int headSize = 0;
    int headPtr = 0;

    boolean reachedEOF = false;
    byte[] inBuffer = new byte[2048];
    byte[] decryptBuffer = new byte[2048];

    /** Creates a new instance of CPXInputStream */
    public CPXInputStream(InputStream in) throws IOException {
        super(in);

        //Determine processing type
        for (int i = 0; i < 4; i++)
        {
            int val = in.read();
            head[i] = (byte)val;
            if (val == -1 || head[i] != MAGIC_NUMBER[i])
            {
                headSize = i + 1;
                xlateMode = XL_PLAIN;
                return;
            }
        }

        xlateMode = XL_ZIP_CRYPT;
    }

    /**
     * We do not allow marking
     */
    public boolean markSupported() { return false; }

    /**
     * Closes this input stream and releases any system resources
     * associated with the stream.
     * This
     * method simply performs <code>in.close()</code>.
     *
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public void close() throws IOException {
        reachedEOF = true;
        in.close();
    }

    /**
     * Reads the next byte of data from this input stream. The value
     * byte is returned as an <code>int</code> in the range
     * <code>0</code> to <code>255</code>. If no byte is available
     * because the end of the stream has been reached, the value
     * <code>-1</code> is returned. This method blocks until input data
     * is available, the end of the stream is detected, or an exception
     * is thrown.
     * <p>
     * This method
     * simply performs <code>in.read()</code> and returns the result.
     *
     * @return     the next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public int read() throws IOException
    {
        final byte[] b = new byte[1];
        int retVal = read(b, 0, 1);
        if (retVal == -1) return -1;
        return b[0];
    }

    /**
     * Reads up to <code>byte.length</code> bytes of data from this
     * input stream into an array of bytes. This method blocks until some
     * input is available.
     * <p>
     * This method simply performs the call
     * <code>read(b, 0, b.length)</code> and returns
     * the  result. It is important that it does
     * <i>not</i> do <code>in.read(b)</code> instead;
     * certain subclasses of  <code>FilterInputStream</code>
     * depend on the implementation strategy actually
     * used.
     *
     * @param      b   the buffer into which the data is read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the stream has been reached.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#read(byte[], int, int)
     */
    public int read(byte[] b) throws IOException
    {
        return read(b, 0, b.length);
    }

    /**
     * Reads up to <code>len</code> bytes of data from this input stream
     * into an array of bytes. This method blocks until some input is
     * available.
     * <p>
     * This method simply performs <code>in.read(b, off, len)</code>
     * and returns the result.
     *
     * @param      b     the buffer into which the data is read.
     * @param      off   the start offset of the data.
     * @param      len   the maximum number of bytes read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the stream has been reached.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public int read(byte[] b, int off, int len) throws IOException
    {
        if (reachedEOF) return -1;

        if (xlateMode == XL_PLAIN)
        {
            int count = 0;
            //Write header if appropriate
            while (headPtr < headSize && len > 0)
            {
                b[off++] = head[headPtr++];
                count++;
                len--;
            }

            return (len == 0) ? count : count + in.read(b, off, len);
        }

        //Decrypt and inflate
        if (inflater.needsInput() && !decryptChunk())
        {
            reachedEOF = true;

            //Read remaining bytes
            int numRead;
            try {
                numRead = inflater.inflate(b, off, len);
            }
            catch (Exception e)
            {
                Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING, null, e);
                return -1;
            }

            if (!inflater.finished())
            {
                Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
                    "Inflation imncomplete");
            }

            return numRead == 0 ? -1 : numRead;
        }

        try
        {
            return inflater.inflate(b, off, len);
        }
        catch (DataFormatException e)
        {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING, null, e);
            return -1;
        }
    }


    /**
     * Call when inflater indicates that it needs more bytes.
     * @return - true if we decrypted more bytes to deflate, false if we
     * encountered the end of stream
     */
    protected boolean decryptChunk() throws IOException
    {
        while (inflater.needsInput())
        {
            int numInBytes = in.read(inBuffer);
            if (numInBytes == -1) return false;
//            int numDecryptBytes = cipher.update(inBuffer, 0, numInBytes, decryptBuffer);
//            inflater.setInput(decryptBuffer, 0, numDecryptBytes);
inflater.setInput(inBuffer, 0, numInBytes);
        }

        return true;
    }

    /**
     * This method returns 1 if we've not reached EOF, 0 if we have.  Programs
     * should not rely on this to determine the number of bytes that can be
     * read without blocking.
     */
    public int available() { return reachedEOF ? 0 : 1; }

    /**
     * Skips bytes by reading them into a cached buffer
     */
    public long skip(long n) throws IOException
    {
        int skipSize = (int)n;
        if (skipSize > inBuffer.length) skipSize = inBuffer.length;
        return read(inBuffer, 0, skipSize);
    }

}

/*
 import java.security.KeyPairGenerator;
  import java.security.KeyPair;
  import java.security.KeyPairGenerator;
  import java.security.PrivateKey;
  import java.security.PublicKey;
  import java.security.SecureRandom;
  import java.security.Cipher;

  ....

  java.security.Security.addProvider(new cryptix.provider.Cryptix());

  SecureRandom random = new SecureRandom(SecureRandom.getSeed(30));
  KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
  keygen.initialize(1024, random);
  keypair = keygen.generateKeyPair();

  PublicKey  pubkey  = keypair.getPublic();
  PrivateKey privkey = keypair.getPrivate();
 */

/*
 *
 *Generate key pairs
KeyPairGenerator keyGen =
             KeyPairGenerator.getInstance("DSA");
KeyGen.initialize(1024, new SecureRandom(userSeed));
KeyPair pair = KeyGen.generateKeyPair();
 */
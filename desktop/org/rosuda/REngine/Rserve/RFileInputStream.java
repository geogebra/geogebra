package org.rosuda.REngine.Rserve;

// JRclient library - client interface to Rserve, see http://www.rosuda.org/Rserve/
// Copyright (C) 2004 Simon Urbanek
// --- for licensing information see LICENSE file in the original JRclient distribution ---

import java.io.*;
import org.rosuda.REngine.*;
import org.rosuda.REngine.Rserve.protocol.*;

/** <b>RFileInputStream</b> is an {@link InputStream} to transfer files
    from <b>Rserve</b> server to the client. It is used very much like
    a {@link FileInputStream}. Currently mark and seek is not supported.
    The current implementation is also "one-shot" only, that means the file
    can be read only once.
    @version $Id: RFileInputStream.java 2743 2007-05-04 16:42:17Z urbanek $
*/
public class RFileInputStream extends InputStream {
    /** RTalk class to use for communication with the Rserve */
    RTalk rt;
    /** set to <code>true</code> when {@link #close} was called.
	Any subsequent read requests on closed stream  result in an 
	{@link IOException} or error result */
    boolean closed;
    /** set to <code>true</code> once EOF is reached - or more specifically
	the first time remore fread returns OK and 0 bytes */
    boolean eof;

    /** tries to open file on the R server, using specified {@link RTalk} object
	and filename. Be aware that the filename has to be specified in host
	format (which is usually unix). In general you should not use directories
	since Rserve provides an own directory for every connection. Future Rserve
	servers may even strip all directory navigation characters for security
	purposes. Therefore only filenames without path specification are considered
	valid, the behavior in respect to absolute paths in filenames is undefined. */
    RFileInputStream(RTalk rti, String fn) throws IOException {
	rt=rti;
	RPacket rp=rt.request(RTalk.CMD_openFile,fn);
	if (rp==null || !rp.isOk())
	    throw new IOException((rp==null)?"Connection to Rserve failed":("Request return code: "+rp.getStat()));
	closed=false; eof=false;
    }

    /** reads one byte from the file. This function should be avoided, since
	{@link RFileInputStream} provides no buffering. This means that each
	call to this function leads to a complete packet exchange between
	the server and the client. Use {@link #read(byte[],int,int)} instead
	whenever possible. In fact this function calls <code>#read(b,0,1)</code>.
	@return -1 on any failure, or the acquired byte (0..255) on success */
    public int read() throws IOException {
	byte[] b=new byte[1];
	if (read(b,0,1)<1) return -1;
	return b[0];
    }

    /** Reads specified number of bytes (or less) from the remote file.
	@param b buffer to store the read bytes
	@param off offset where to strat filling the buffer
	@param len maximal number of bytes to read
	@return number of bytes read or -1 if EOF reached
    */
    public int read(byte[] b, int off, int len) throws IOException {
	if (closed) throw new IOException("File is not open");
	if (eof) return -1;
	RPacket rp=rt.request(RTalk.CMD_readFile,len);
	if (rp==null || !rp.isOk())
	    throw new IOException((rp==null)?"Connection to Rserve failed":("Request return code: "+rp.getStat()));
	byte[] rd=rp.getCont();
	if (rd==null) {
	    eof=true;
	    return -1;
	};
	int i=0;
	while(i<rd.length) { b[off+i]=rd[i]; i++; };
	return rd.length;
    }

    /** close stream - is not related to the actual RConnection, calling
	close does not close the RConnection
    */
    public void close() throws IOException {
	RPacket rp=rt.request(RTalk.CMD_closeFile,(byte[])null);
	if (rp==null || !rp.isOk())
	    throw new IOException((rp==null)?"Connection to Rserve failed":("Request return code: "+rp.getStat()));
	closed=true;
    }
}

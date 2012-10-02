package org.rosuda.REngine.Rserve;

// JRclient library - client interface to Rserve, see http://www.rosuda.org/Rserve/
// Copyright (C) 2004-08 Simon Urbanek
// --- for licensing information see LICENSE file in the original JRclient distribution ---

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPNull;
import org.rosuda.REngine.REngine;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.protocol.REXPFactory;
import org.rosuda.REngine.Rserve.protocol.RPacket;
import org.rosuda.REngine.Rserve.protocol.RTalk;
import org.rosuda.REngine.Rserve.protocol.jcrypt;

/**  class providing TCP/IP connection to an Rserve
     @version $Id: RConnection.java 3404 2011-07-28 14:57:31Z urbanek $
*/
public class RConnection extends REngine {
    /** last error string */
    String lastError=null;
    Socket s;
    boolean connected=false;
    InputStream is;
    OutputStream os;
    boolean authReq=false;
    int authType=AT_plain;
    String Key=null;
    RTalk rt=null;

    String host;
    int port;

    /** This static variable specifies the character set used to encode string for transfer. Under normal circumstances there should be no reason for changing this variable. The default is UTF-8, which makes sure that 7-bit ASCII characters are sent in a backward-compatible fashion. Currently (Rserve 0.1-7) there is no further conversion on Rserve's side, i.e. the strings are passed to R without re-coding. If necessary the setting should be changed <u>before</u> connecting to the Rserve in case later Rserves will provide a possibility of setting the encoding during the handshake. */
    public static String transferCharset="UTF-8";
    
    /** authorization type: plain text */
    public static final int AT_plain = 0;
    /** authorization type: unix crypt */
    public static final int AT_crypt = 1;

    /** version of the server (as reported in IDstring just after Rsrv) */
    protected int rsrvVersion;
    
    /** make a new local connection on default port (6311) */
    public RConnection() throws RserveException {
		this("127.0.0.1",6311);
    }

    /** make a new connection to specified host on default port (6311)
	@param host host name/IP
    */
    public RConnection(String host) throws RserveException {
		this(host,6311);
    }

    /** make a new connection to specified host and given port.
	 * Make sure you check {@link #isConnected} to ensure the connection was successfully created.
	 * @param host host name/IP
	 * @param port TCP port
	 */
    public RConnection(String host, int port) throws RserveException {
		this(host, port, null);
    }

	/** restore a connection based on a previously detached session
	 * @param session detached session object */
    RConnection(RSession session) throws RserveException {
		this(null, 0, session);
    }

    RConnection(String host, int port, RSession session) throws RserveException {
        try {
            if (connected) s.close();
            s=null;
        } catch (Exception e) {
            throw new RserveException(this,"Cannot connect: "+e.getMessage());
        }
		if (session!=null) {
			host=session.host;
			port=session.port;
		}
        connected=false;
		this.host=host;
		this.port=port;
        try {
            s=new Socket(host,port);
			// disable Nagle's algorithm since we really want immediate replies
			s.setTcpNoDelay(true);
        } catch (Exception sce) {
            throw new RserveException(this,"Cannot connect: "+sce.getMessage());
        }
        try {
            is=s.getInputStream();
            os=s.getOutputStream();
        } catch (Exception gse) {
            throw new RserveException(this,"Cannot get io stream: "+gse.getMessage());
        }
        rt=new RTalk(is,os);
		if (session==null) {
			byte[] IDs=new byte[32];
			int n=-1;
			try {
				n=is.read(IDs);
			} catch (Exception sre) {
				throw new RserveException(this,"Error while receiving data: "+sre.getMessage());
			}
			try {
				if (n!=32) {
					throw new RserveException(this,"Handshake failed: expected 32 bytes header, got "+n);
				}
				String ids=new String(IDs);
				if (ids.substring(0,4).compareTo("Rsrv")!=0)
					throw new RserveException(this,"Handshake failed: Rsrv signature expected, but received \""+ids+"\" instead.");
				try {
					rsrvVersion=Integer.parseInt(ids.substring(4,8));
				} catch (Exception px) {}
				// we support (knowingly) up to 103
				if (rsrvVersion>103)
					throw new RserveException(this,"Handshake failed: The server uses more recent protocol than this client.");
				if (ids.substring(8,12).compareTo("QAP1")!=0)
					throw new RserveException(this,"Handshake failed: unupported transfer protocol ("+ids.substring(8,12)+"), I talk only QAP1.");
				for (int i=12;i<32;i+=4) {
					String attr=ids.substring(i,i+4);
					if (attr.compareTo("ARpt")==0) {
						if (!authReq) { // this method is only fallback when no other was specified
							authReq=true;
							authType=AT_plain;
						}
					}
					if (attr.compareTo("ARuc")==0) {
						authReq=true;
						authType=AT_crypt;
					}
					if (attr.charAt(0)=='K') {
						Key=attr.substring(1,3);
					}
				}
			} catch (RserveException innerX) {
				try { s.close(); } catch (Exception ex01) {}; is=null; os=null; s=null;
				throw innerX;
			}
		} else { // we have a session to take care of
			try {
				os.write(session.key,0,32);
			} catch (Exception sre) {
				throw new RserveException(this,"Error while sending session key: "+sre.getMessage());
			}
			rsrvVersion = session.rsrvVersion;
		}
		connected=true;
		lastError="OK";
    }    
	
    public void finalize() {
        close();
        is=null;
	os=null;
    }

    /** get server version as reported during the handshake.
        @return server version as integer (Rsrv0100 will return 100) */
    public int getServerVersion() {
        return rsrvVersion;
    }
    
    /** closes current connection */
    public boolean close() {
        try {
            if (s != null) s.close();
            connected = false;
			return true;
        } catch(Exception e) { };
		return false;
    }
    
    /** evaluates the given command, but does not fetch the result (useful for assignment
	operations)
	@param cmd command/expression string */
    public void voidEval(String cmd) throws RserveException {
		if (!connected || rt==null)
			throw new RserveException(this,"Not connected");
		RPacket rp=rt.request(RTalk.CMD_voidEval,cmd+"\n");
		if (rp!=null && rp.isOk()) return;
        throw new RserveException(this,"voidEval failed",rp);
    }

	/** evaluates the given command, detaches the session (see @link{detach()}) and closes connection while the command is being evaluted (requires Rserve 0.4+).
		Note that a session cannot be attached again until the commad was successfully processed. Techincally the session is put into listening mode while the command is being evaluated but accept is called only after the command was evaluated. One commonly used techique to monitor detached working sessions is to use second connection to poll the status (e.g. create a temporary file and return the full path before detaching thus allowing new connections to read it).
		@param cmd command/expression string
		@return session object that can be use to attach back to the session once the command completed */
    public RSession voidEvalDetach(String cmd) throws RserveException {
		if (!connected || rt==null)
			throw new RserveException(this,"Not connected");
		RPacket rp=rt.request(RTalk.CMD_detachedVoidEval,cmd+"\n");
		if (rp==null || !rp.isOk())
			throw new RserveException(this,"detached void eval failed",rp);
		RSession s = new RSession(this, rp);
		close();
		return s;
    }
	
    REXP parseEvalResponse(RPacket rp) throws RserveException {
		int rxo=0;
		byte[] pc=rp.getCont();
		if (rsrvVersion>100) { /* since 0101 eval responds correctly by using DT_SEXP type/len header which is 4 bytes long */
			rxo=4;
			/* we should check parameter type (should be DT_SEXP) and fail if it's not */
			if (pc[0]!=RTalk.DT_SEXP && pc[0]!=(RTalk.DT_SEXP|RTalk.DT_LARGE))
				throw new RserveException(this,"Error while processing eval output: SEXP (type "+RTalk.DT_SEXP+") expected but found result type "+pc[0]+".");
			if (pc[0]==(RTalk.DT_SEXP|RTalk.DT_LARGE))
				rxo=8; // large data need skip of 8 bytes
			/* warning: we are not checking or using the length - we assume that only the one SEXP is returned. This is true for the current CMD_eval implementation, but may not be in the future. */
		}
		if (pc.length>rxo) {
			try {
				REXPFactory rx=new REXPFactory();
				rx.parseREXP(pc, rxo);
				return rx.getREXP();
			} catch (REXPMismatchException me) {
				me.printStackTrace();
				throw new RserveException(this, "Error when parsing response: "+me.getMessage());
			}
		}
		return null;
    }

    /** evaluates the given command and retrieves the result
	@param cmd command/expression string
	@return R-xpression or <code>null</code> if an error occured */
    public REXP eval(String cmd) throws RserveException {
		if (!connected || rt==null)
            throw new RserveException(this,"Not connected");
		RPacket rp=rt.request(RTalk.CMD_eval,cmd+"\n");
		if (rp!=null && rp.isOk())
			return parseEvalResponse(rp);
        throw new RserveException(this,"eval failed",rp);
    }

    /** assign a string value to a symbol in R. The symbol is created if it doesn't exist already.
        @param sym symbol name. Currently assign uses CMD_setSEXP command of Rserve, i.e. the symbol value is NOT parsed. It is the responsibility of the user to make sure that the symbol name is valid in R (recall the difference between a symbol and an expression!). In fact R will always create the symbol, but it may not be accessible (examples: "bar\nfoo" or "bar$foo").
        @param ct contents
        */
    public void assign(String sym, String ct) throws RserveException {
		if (!connected || rt==null)
            throw new RserveException(this,"Not connected");
        byte[] symn=sym.getBytes();
        byte[] ctn=ct.getBytes();
        int sl=symn.length+1;
        int cl=ctn.length+1;
        if ((sl&3)>0) sl=(sl&0xfffffc)+4; // make sure the symbol length is divisible by 4
        if ((cl&3)>0) cl=(cl&0xfffffc)+4; // make sure the content length is divisible by 4
        byte[] rq=new byte[sl+4+cl+4];
        int ic;
        for(ic=0;ic<symn.length;ic++) rq[ic+4]=symn[ic];
        while (ic<sl) { rq[ic+4]=0; ic++; }
        for(ic=0;ic<ctn.length;ic++) rq[ic+sl+8]=ctn[ic];
        while (ic<cl) { rq[ic+sl+8]=0; ic++; }
		RTalk.setHdr(RTalk.DT_STRING,sl,rq,0);
		RTalk.setHdr(RTalk.DT_STRING,cl,rq,sl+4);
		RPacket rp=rt.request(RTalk.CMD_setSEXP,rq);
        if (rp!=null && rp.isOk()) return;
        throw new RserveException(this,"assign failed",rp);
    }

    /** assign a content of a REXP to a symbol in R. The symbol is created if it doesn't exist already.
     * @param sym symbol name. Currently assign uses CMD_setSEXP command of Rserve, i.e. the symbol value is NOT parsed. It is the responsibility of the user to make sure that the symbol name is valid in R (recall the difference between a symbol and an expression!). In fact R will always create the symbol, but it may not be accessible (examples: "bar\nfoo" or "bar$foo").
	 * @param rexp contents
	 */
public void assign(String sym, REXP rexp) throws RserveException {
	if (!connected || rt==null)
	    throw new RserveException(this,"Not connected");
	try {
		REXPFactory r = new REXPFactory(rexp);
		int rl=r.getBinaryLength();
		byte[] symn=sym.getBytes();
		int sl=symn.length+1;
		if ((sl&3)>0) sl=(sl&0xfffffc)+4; // make sure the symbol length is divisible by 4
		byte[] rq=new byte[sl+rl+((rl>0xfffff0)?12:8)];
		int ic;
		for(ic=0;ic<symn.length;ic++) rq[ic+4]=symn[ic];
		while(ic<sl) { rq[ic+4]=0; ic++; }; // pad with 0
		RTalk.setHdr(RTalk.DT_STRING,sl,rq,0);
		RTalk.setHdr(RTalk.DT_SEXP,rl,rq,sl+4);
		r.getBinaryRepresentation(rq,sl+((rl>0xfffff0)?12:8));
		RPacket rp=rt.request(RTalk.CMD_setSEXP,rq);
		if (rp!=null && rp.isOk()) return;
		throw new RserveException(this,"assign failed",rp);
	} catch (REXPMismatchException me) {
		throw new RserveException(this, "Error creating binary representation: "+me.getMessage());
	}
}

    /** open a file on the Rserve for reading
        @param fn file name. should not contain any path delimiters, since Rserve may restrict the access to local working directory.
        @return input stream to be used for reading. Note that the stream is read-once only, there is no support for seek or rewind. */
    public RFileInputStream openFile(String fn) throws IOException {
		return new RFileInputStream(rt,fn);
    }

    /** create a file on the Rserve for writing
        @param fn file name. should not contain any path delimiters, since Rserve may restrict the access to local working directory.
        @return output stream to be used for writinging. Note that the stream is write-once only, there is no support for seek or rewind. */
    public RFileOutputStream createFile(String fn) throws IOException {
        return new RFileOutputStream(rt,fn);
    }

    /** remove a file on the Rserve
        @param fn file name. should not contain any path delimiters, since Rserve may restrict the access to local working directory. */
    public void removeFile(String fn) throws RserveException {
		if (!connected || rt==null)
			throw new RserveException(this,"Not connected");
		RPacket rp=rt.request(RTalk.CMD_removeFile,fn);
		if (rp!=null && rp.isOk()) return;
        throw new RserveException(this,"removeFile failed",rp);
    }

    /** shutdown remote Rserve. Note that some Rserves cannot be shut down from the client side. */
    public void shutdown() throws RserveException {
		if (!connected || rt==null)
			throw new RserveException(this,"Not connected");

		RPacket rp=rt.request(RTalk.CMD_shutdown);
		if (rp!=null && rp.isOk()) return;
        throw new RserveException(this,"shutdown failed",rp);
    }

    /** Sets send buffer size of the Rserve (in bytes) for the current connection. All responses send by Rserve are stored in the send buffer before transmitting. This means that any objects you want to get from the Rserve need to fit into that buffer. By default the size of the send buffer is 2MB. If you need to receive larger objects from Rserve, you will need to use this function to enlarge the buffer. In order to save memory, you can also reduce the buffer size once it's not used anymore. Currently the buffer size is only limited by the memory available and/or 1GB (whichever is smaller). Current Rserve implementations won't go below buffer sizes of 32kb though. If the specified buffer size results in 'out of memory' on the server, the corresponding error is sent and the connection is terminated.<br>
        <i>Note:</i> This command may go away in future versions of Rserve which will use dynamic send buffer allocation.
        @param sbs send buffer size (in bytes) min=32k, max=1GB
     */
    public void setSendBufferSize(long sbs) throws RserveException {
        if (!connected || rt==null)
			throw new RserveException(this,"Not connected");

        RPacket rp=rt.request(RTalk.CMD_setBufferSize,(int)sbs);
        if (rp!=null && rp.isOk()) return;
        throw new RserveException(this,"setSendBufferSize failed",rp);        
    }

    /** set string encoding for this session. It is strongly
     * recommended to make sure the encoding is always set to UTF-8
     * because that is the only encoding supported by this Java
     * client. It can be done either by uisng the
     * <code>encoding</code> option in the server or by calling
     * setStringEncoding("utf8") at the beginning of a session (but
     * after login).
     @param enc name of the encoding as defined by Rserve - as of
     Rserve version 0.5-3 valid values are "utf8", "latin1" and
     "native" (case-sensitive)
     @since Rserve 0.5-3
    */
    public void setStringEncoding(String enc) throws RserveException {
        if (!connected || rt==null)
			throw new RserveException(this,"Not connected");
	RPacket rp = rt.request(RTalk.CMD_setEncoding, enc);
	if (rp != null && rp.isOk()) return;
	throw new RserveException(this,"setStringEncoding failed", rp);
    }

    /** login using supplied user/pwd. Note that login must be the first
	command if used
	@param user username
	@param pwd password */
    public void login(String user, String pwd) throws RserveException {
		if (!authReq) return;
		if (!connected || rt==null)
			throw new RserveException(this,"Not connected");
		if (authType==AT_crypt) {
			if (Key==null) Key="rs";
			RPacket rp=rt.request(RTalk.CMD_login,user+"\n"+jcrypt.crypt(Key,pwd));
			if (rp!=null && rp.isOk()) return;
			try { s.close(); } catch(Exception e) {};
			is=null; os=null; s=null; connected=false;
            throw new RserveException(this,"login failed",rp);
		}
		RPacket rp=rt.request(RTalk.CMD_login,user+"\n"+pwd);
		if (rp!=null && rp.isOk()) return;
		try {s.close();} catch (Exception e) {};
		is=null; os=null; s=null; connected=false;
        throw new RserveException(this,"login failed",rp);
    }

    
    /** detaches the session and closes the connection (requires Rserve 0.4+). The session can be only resumed by calling @link{RSession.attach} */
	public RSession detach() throws RserveException {
		if (!connected || rt==null)
            throw new RserveException(this,"Not connected");
		RPacket rp=rt.request(RTalk.CMD_detachSession);
		if (rp==null || !rp.isOk())
			throw new RserveException(this,"Cannot detach",rp);
		RSession s = new RSession(this, rp);
		close();
		return s;
    }

    /** check connection state. Note that currently this state is not checked on-the-spot,
	that is if connection went down by an outside event this is not reflected by
	the flag
	@return <code>true</code> if this connection is alive */
    public boolean isConnected() { return connected; }
    
    /** check authentication requirement sent by server
	@return <code>true</code> is server requires authentication. In such case first
	command after connecting must be {@link #login}. */
    public boolean needLogin() { return authReq; }
    
    /** get last error string
	@return last error string */
    public String getLastError() { return lastError; }
	
    /** evaluates the given command in the master server process asynchronously (control command). Note that control commands are always asynchronous, i.e., the expression is enqueued for evaluation in the master process and the method returns before the expression is evaluated (in non-parallel builds the client has to close the connection before the expression can be evaluated). There is no way to check for errors and control commands should be sent with utmost care as they can abort the server process. The evaluation has no immediate effect on the client session.
     *  @param cmd command/expression string 
     *  @since Rserve 0.6-0 */
    public void serverEval(String cmd) throws RserveException {
	if (!connected || rt == null)
	    throw new RserveException(this, "Not connected");
	RPacket rp = rt.request(RTalk.CMD_ctrlEval, cmd+"\n");
	if (rp != null && rp.isOk()) return;
	throw new RserveException(this,"serverEval failed",rp);
    }
    
    /** sources the given file (the path must be local to the server!) in the master server process asynchronously (control command). See {@link #serverEval()} for details on control commands.
     *  @param serverFile path to a file on the server (it is recommended to always use full paths, because the server process has a different working directory than the client child process!).
     *  @since Rserve 0.6-0 */
    public void serverSource(String serverFile) throws RserveException {
	if (!connected || rt == null)
	    throw new RserveException(this, "Not connected");
	RPacket rp = rt.request(RTalk.CMD_ctrlSource, serverFile);
	if (rp != null && rp.isOk()) return;
	throw new RserveException(this,"serverSource failed",rp);
    }
    
    /** attempt to shut down the server process cleanly. Note that there is a fundamental difference between the {@link shutdown()} method and this method: <code>serverShutdown()</code> is a proper control command and thus fully authentication controllable, whereas {@link shutdown()} is a client-side command sent to the client child process and thus relying on the ability of the client to signal the server process which may be disabled. Therefore <code>serverShutdown()</code> is preferred and more reliable for Rserve 0.6-0 and higher.
     *  @since Rserve 0.6-0 */
    public void serverShutdown() throws RserveException {
	if (!connected || rt == null)
	    throw new RserveException(this, "Not connected");
	RPacket rp = rt.request(RTalk.CMD_ctrlShutdown);
	if (rp != null && rp.isOk()) return;
	throw new RserveException(this,"serverShutdown failed",rp);
    }
    
//========= REngine interface API

public REXP parse(String text, boolean resolve) throws REngineException {
	throw new REngineException(this, "Rserve doesn't support separate parsing step.");
}
public REXP eval(REXP what, REXP where, boolean resolve) throws REngineException {
	return new REXPNull();
}
public REXP parseAndEval(String text, REXP where, boolean resolve) throws REngineException {
	if (where!=null) throw new REngineException(this, "Rserve doesn't support environments other than .GlobalEnv");
	try {
		return eval(text);
	} catch (RserveException re) {
		throw new REngineException(this, re.getMessage());
	}
}

/** assign into an environment
@param symbol symbol name
@param value value to assign
@param env environment to assign to */
public void assign(String symbol, REXP value, REXP env) throws REngineException {
	if (env!=null) throw new REngineException(this, "Rserve doesn't support environments other than .GlobalEnv");
	try {
		assign(symbol, value);
	} catch (RserveException re) {
		throw new REngineException(this, re.getMessage());
	}
}

/** get a value from an environment
@param symbol symbol name
@param env environment
@param resolve resolve the resulting REXP or just return a reference		
@return value */
public REXP get(String symbol, REXP env, boolean resolve) throws REngineException {
	if (!resolve) throw new REngineException(this, "Rserve doesn't support references");
	try {
		return eval("get(\""+symbol+"\")");
	} catch (RserveException re) {
		throw new REngineException(this, re.getMessage());
	}
}

/** fetch the contents of the given reference. The resulting REXP may never be REXPReference.
@param ref reference to resolve
@return resolved reference */
public REXP resolveReference(REXP ref) throws REngineException {
	throw new REngineException(this, "Rserve doesn't support references");
}
	
	public REXP createReference(REXP ref) throws REngineException {
		throw new REngineException(this, "Rserve doesn't support references");
	}
	public void finalizeReference(REXP ref) throws REngineException {
		throw new REngineException(this, "Rserve doesn't support references");
	}
	
public REXP getParentEnvironment(REXP env, boolean resolve) throws REngineException {
	throw new REngineException(this, "Rserve doesn't support environments other than .GlobalEnv");
}

public REXP newEnvironment(REXP parent, boolean resolve) throws REngineException {
	throw new REngineException(this, "Rserve doesn't support environments other than .GlobalEnv");
}

}


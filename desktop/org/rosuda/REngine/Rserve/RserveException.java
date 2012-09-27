// JRclient library - client interface to Rserve, see http://www.rosuda.org/Rserve/
// Copyright (C) 2004 Simon Urbanek
// --- for licensing information see LICENSE file in the original JRclient distribution ---
//
//  RserveException.java
//
//  Created by Simon Urbanek on Mon Aug 18 2003.
//
//  $Id: RserveException.java 3156 2009-08-12 15:35:30Z urbanek $
//

package org.rosuda.REngine.Rserve;

import org.rosuda.REngine.Rserve.protocol.RPacket;
import org.rosuda.REngine.Rserve.protocol.RTalk;
import org.rosuda.REngine.REngineException;

public class RserveException extends REngineException {
    protected String err;
    protected int reqReturnCode;

    public String getRequestErrorDescription() {
		return getRequestErrorDescription(reqReturnCode);
	}
	
    public String getRequestErrorDescription(int code) {
        switch(code) {
            case 0: return "no error";
            case 2: return "R parser: input incomplete";
            case 3: return "R parser: syntax error";
            case RTalk.ERR_auth_failed: return "authorization failed";
            case RTalk.ERR_conn_broken: return "connection broken";
            case RTalk.ERR_inv_cmd: return "invalid command";
            case RTalk.ERR_inv_par: return "invalid parameter";
            case RTalk.ERR_IOerror: return "I/O error on the server";
            case RTalk.ERR_not_open: return "connection is not open";
            case RTalk.ERR_access_denied: return "access denied (local to the server)";
            case RTalk.ERR_unsupported_cmd: return "unsupported command";
            case RTalk.ERR_unknown_cmd: return "unknown command";
            case RTalk.ERR_data_overflow: return "data overflow, incoming data too big";
            case RTalk.ERR_object_too_big: return "evaluation successful, but returned object is too big to transport";
            case RTalk.ERR_out_of_mem: return "FATAL: Rserve ran out of memory, closing connection";
			case RTalk.ERR_session_busy: return "session is busy";
			case RTalk.ERR_detach_failed: return "session detach failed";
		case RTalk.ERR_ctrl_closed: return "control pipe to master process is closed/broken";
        }
        return "error code: "+code;
    }

    public String getMessage() {
        return super.getMessage()+((reqReturnCode!=-1)?", request status: "+getRequestErrorDescription():"");
    }
    
    public RserveException(RConnection c, String msg) {
        this(c,msg,-1);
    }

    public RserveException(RConnection c, String msg, int requestReturnCode) {
        super(c, msg);
        reqReturnCode=requestReturnCode;
		if (c!=null) c.lastError=getMessage();
    }

	public RserveException(RConnection c, String msg, RPacket p) {
		this(c, msg, (p==null)?-1:p.getStat());
	}
	
    public int getRequestReturnCode() {
        return reqReturnCode;
    }
}

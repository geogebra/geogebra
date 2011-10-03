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
package org.mathpiper.exceptions;


public class EvaluationException extends Exception //Note:tk: made this class public so that clients can use this exception.
{

    private int lineNumber = -1;
    private String fileName = null;
    private String functionName = null;
    private String type = null;


    public EvaluationException(String message, String fileName, int lineNumber, String functionName) {
        this("Unspecified", message, fileName, lineNumber, functionName);
    }



    public EvaluationException(String type, String message, String fileName, int lineNumber, String functionName) {
        super(message);
        this.type = type;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.functionName = functionName;
    }

    public EvaluationException(String message, String fileName, int lineNumber) {
        this( message,  fileName,  lineNumber, null);
    }


    public int getLineNumber() {
        return lineNumber;
    }


    public String getFileName() {
        return fileName;
    }


    public String getFunctionName() {
        return functionName;
    }

    public String getType() {
        return type;
    }


}

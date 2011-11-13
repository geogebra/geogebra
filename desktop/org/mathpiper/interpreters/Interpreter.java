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
 *///}}}
// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:folding=explicit:collapseFolds=0:
package org.mathpiper.interpreters;

import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.cons.ConsPointer;

/**
 * Interpreter is implemented by all MathPiper interpreters and it allows client code to evaluate
 * MathPiper scripts and to also control the evaluation process.
 */
public interface Interpreter
{

    /**
     * Evaluates a MathPiper expression.  The results of the evaluation are returned
     * in a {@link EvaluationResponse} object.
     *
     * @param expression the MathPiper expression to be evaluated
     * @return an EvaluationResponse object
     */
    public EvaluationResponse evaluate(String expression);
    
    /**
     * Evaluates a MathPiper expression and optinally notifies evaluation listeners.  The results of the evaluation are returned
     * in a {@link EvaluationResponse} object.
     *
     * @param expression the MathPiper expression to be evaluated
     * @param notifyListeners if true, evaluation listeners will be notified
     * @return an EvaluationResponse object
     */
    public EvaluationResponse evaluate(String expression, boolean notifyListeners);

    /**
     * Evaluates a MathPiper expression.  The results of the evaluation are returned
     * in a {@link EvaluationResponse} object.
     *
     * @param expressionPointer the list form of a MathPiper expression to be evaluated
     * @return an EvaluationResponse object
     */
    public EvaluationResponse evaluate(ConsPointer expressionPointer);

    /**
     * Halts the current evaluation.
     */
    public void haltEvaluation();

    //java.util.zip.ZipFile getScriptsZip();

    /**
     * Adds a path to the paths which are searched when locating MathPiper scripts.
     *
     * @param directoryPath the path to a directory which contains MathPiper scripts
     */
    public void addScriptsDirectory(String directoryPath);
   
    /**
     * Allows asynchrnous interpreter clients to add themselves to the list of listeners which
     * get notified when the response from an asynchronous evaluation is ready.  These
     * clients must all implement the {@link ResponseListener} interface.
     *
     * @param responseListener a response listener
     */
    public void addResponseListener(ResponseListener responseListener);

     /**
     * Allows asynchrnous interpreter clients to remove themselves from the list of listeners which
     * get notified when the response from an asynchronous evaluation is ready.
     * 
     * @param responseListener a response listener
     */
    public void removeResponseListener(ResponseListener listener);
     
    /**
     * Returns the interpreter's execution {@link Environment}.  Warning: the Environment class
     * is currently not well protected and it will change in the future.
     * 
     * @return the Environment.
     */
    public Environment getEnvironment();

}

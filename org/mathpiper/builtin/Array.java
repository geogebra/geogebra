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

package org.mathpiper.builtin;

import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.cons.Cons;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsPointerArray;


public class Array extends BuiltinContainer
{
	ConsPointerArray iArray;

	public Array(Environment aEnvironment, int aSize,Cons aInitialItem)
	{
		iArray = new ConsPointerArray(aEnvironment, aSize,aInitialItem);
	}

	public String typeName()
	{
		return "\"Array\"";
	}

	public int size()
	{
		return iArray.size();
	}
	public Cons getElement(int aItem, int aStackTop, Environment aEnvironment) throws Exception
	{
		LispError.lispAssert(aItem>0 && aItem<=iArray.size(), aEnvironment, aStackTop);
		return iArray.getElement(aItem-1).getCons();
	}
	public void setElement(int aItem,Cons aObject, int aStackTop, Environment aEnvironment) throws Exception
	{
		LispError.lispAssert(aItem>0 && aItem<=iArray.size(), aEnvironment, aStackTop);
		iArray.setElement(aItem-1,aObject);
	}

    public Object getObject()
    {
        return this;
    }

}

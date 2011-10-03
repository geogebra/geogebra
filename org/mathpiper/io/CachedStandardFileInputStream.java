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

package org.mathpiper.io;

/** CachedStandardFileInputStream : input from stdin */
public class CachedStandardFileInputStream extends MathPiperInputStream
{
	StringBuffer iBuffer;
	int iCurrentPos;

	public CachedStandardFileInputStream(InputStatus aStatus)
	{
		super(aStatus);
		rewind();
	}
        
	public char next() throws Exception
	{
		int c = peek();
		iCurrentPos++;
		if (c == '\n')
			iStatus.nextLine();
		return (char)c;
	}
        
	public char peek() throws Exception
	{
		if (iCurrentPos == iBuffer.length())
		{
			int newc;
			newc = System.in.read();
			iBuffer.append((char)newc);
			while (newc != '\n')
			{
				newc = System.in.read();
				iBuffer.append((char)newc);
			}
		}
		return iBuffer.charAt(iCurrentPos);
	}
        
	public boolean endOfStream()
	{
		return false;
	}
        
	public void rewind()
	{
		iBuffer = new StringBuffer();
		iCurrentPos = 0;
	}
        
	public StringBuffer startPtr()
	{
		return iBuffer;
	}
        
	public int position()
	{
		return iCurrentPos;
	}
        
	public void setPosition(int aPosition)
	{
		iCurrentPos = aPosition;
	}


}

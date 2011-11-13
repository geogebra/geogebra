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


/** \class MathPiperInputStream : pure abstract class declaring the interface
 *  that needs to be implemented by a file (something that expressions
 *  can be read from).
 */
public abstract class MathPiperInputStream
{
	public InputStatus iStatus;

	/** Constructor with InputStatus. InputStatus retains the information
	 * needed when an error occurred, and the file has already been
	 * closed.
	 */
	public MathPiperInputStream(InputStatus aStatus)
	{
		iStatus = aStatus;
	}

	/// Return the next character in the file
	public abstract char next() throws Exception;

	/** peek at the next character in the file, without advancing the file
	 *  pointer.
	 */
	public abstract char peek() throws Exception;

	public InputStatus status()
	{
		return iStatus;
	}

	/// Check if the file position is past the end of the file.
	public abstract boolean endOfStream();
	/** startPtr returns the start of a buffer, if there is one.
	 * Implementations of this class can keep the file in memory
	 * as a whole, and return the start pointer and current position.
	 * Especially the parsing code requires this, because it can then
	 * efficiently look up a symbol in the hash table without having to
	 * first create a buffer to hold the symbol in. If startPtr is supported,
	 * the whole file should be in memory for the whole period the file
	 * is being read.
	 */
	public abstract StringBuffer startPtr();
	public abstract int position();
	public abstract void setPosition(int aPosition);

};

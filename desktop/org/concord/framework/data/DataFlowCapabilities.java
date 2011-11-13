/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01742
 *
 *  Web Site: http://www.concord.org
 *  Email: info@concord.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * END LICENSE */

/*
 * Created on Jan 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.framework.data;

/**
 * @author scytacki
 *
 */
public interface DataFlowCapabilities 
{
	public final static class Capabilities 
	{
		protected boolean start;
		protected boolean stop;
		protected boolean reset;
		
		public Capabilities(boolean start, boolean stop, boolean reset)
		{
			this.start = start;
			this.stop = stop;
			this.reset = reset;
		}
		
		public boolean canStart()
		{
			return start;
		}
		public boolean canStop()
		{
			return stop;			
		}
		public boolean canReset()
		{
			return reset;
		}		
	}
	
	/**
	 * An object that wants to report to others what methods
	 * actually do things can use this interface to do so.
	 * 
	 * @return
	 */
	public Capabilities getDataFlowCapabilities();
}

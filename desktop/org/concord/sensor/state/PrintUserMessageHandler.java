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
 * Created on Jan 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.sensor.state;

import org.concord.framework.text.UserMessageHandler;

/**
 * @author Informaiton Services
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PrintUserMessageHandler 
	implements UserMessageHandler
{

		/**
		 * @see org.concord.framework.text.UserMessageHandler#showOptionMessage(java.lang.String, java.lang.String, java.lang.String[], java.lang.String)
		 */
		public int showOptionMessage(String message, String title, String[] options, String defaultOption) {
			System.out.println(title + ": " + message);
			String optionStr = "(";
			for(int i=0; i<options.length; i++) {
				optionStr += " " + options[i];
				if(options[i].equals(defaultOption)){
					optionStr += "+";
				}
			}
			System.out.println(optionStr + " )");
			return 0;
		}

		/**
		 * @see org.concord.framework.text.UserMessageHandler#showMessage(java.lang.String, java.lang.String)
		 */
		public void showMessage(String message, String title) {
			System.out.println(title + ": " + message);
		}
}

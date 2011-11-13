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
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2006-05-05 15:52:16 $
 * $Author: maven $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.framework.util;

import java.awt.Color;
import java.util.Vector;

public interface CheckedColorTreeModel
{
    /**
     * This is used when an item is created or renamed in the 
     * dialogs.  It should be localized.
     * 
     * @return
     */
    public String getItemTypeName();
    
    public Vector getItems(Object parent);
    
    public Object addItem(Object parent, String name, Color color);

    public Object removeItem(Object parent, Object item);
    
    public void setSelectedItem(Object item, boolean checked);
    
    public void updateItems();
    
    public Color getItemColor(Object item);
    
    public String getItemLabel(Object item);
    
    public void setItemLabel(Object item, String label);
    
    public void setItemChecked(Object item, boolean checked);
    
    public Color getNewColor();
}

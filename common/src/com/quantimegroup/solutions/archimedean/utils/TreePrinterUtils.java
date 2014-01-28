/*
Archimedean 1.1, a 3D applet/application for visualizing, building, 
transforming and analyzing Archimedean solids and their derivatives.
Copyright 1998, 2011 Raffi J. Kasparian, www.raffikasparian.com.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.quantimegroup.solutions.archimedean.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JComponent;

public class TreePrinterUtils {
	public static void printLayout(Component comp) {
		TreePrinter printer = new TreePrinter(new ComponentDelegate());
		System.out.println(printer.getTree(comp));
	}

	public static void printThreads(ThreadGroup group) {
		TreePrinter printer = new TreePrinter(new ThreadGroupDelegate());
		System.out.println(printer.getTree(group));
	}

	public static void nameComponent(JComponent jcomp, String name) {
		jcomp.putClientProperty(TreePrinterUtils.class.getName(), name);
	}

	public static String getComponentName(JComponent jcomp) {
		return (String) jcomp.getClientProperty(TreePrinterUtils.class.getName());
	}

	public static void printThreads() {
		ThreadGroup group = Thread.currentThread().getThreadGroup();
		while (group.getParent() != null) {
			group = group.getParent();
		}
		printThreads(group);
	}

	public static class ThreadGroupDelegate implements TreePrinter.TreeDelegate {
		public String childToString(Object o) {
			Thread g = (Thread) o;
			String s = "Thread \"" + g.getName() + "\" (Priority: " + g.getPriority() + ")";
			if (g.isDaemon()) {
				s += " - Daemon";
			}
			return s;
		}

		public String parentToString(Object o) {
			ThreadGroup g = (ThreadGroup) o;
			String s = "ThreadGroup \"" + g.getName() + "\" (Maximum Priority: " + g.getMaxPriority() + ")";
			return s;
		}

		public boolean shouldTreatLikeParent(Object o) {
			return o instanceof ThreadGroup;
		}

		public Object[] getChildren(Object parent) {
			ThreadGroup cont = (ThreadGroup) parent;
			Thread[] threads = new Thread[cont.activeCount()];
			int numThreads = cont.enumerate(threads, false);
			ThreadGroup[] groups = new ThreadGroup[cont.activeGroupCount()];
			int numGroups = cont.enumerate(groups, false);
			Object[] children = new Object[numThreads + numGroups];
			System.arraycopy(threads, 0, children, 0, numThreads);
			System.arraycopy(groups, 0, children, numThreads, numGroups);
			return children;
		}
	}

	public static class ComponentDelegate implements TreePrinter.TreeDelegate {
		private String getShortClassName(Object o) {
			String s = o.getClass().getName();
			int i = s.lastIndexOf(".");
			if (i >= 0) {
				s = s.substring(i + 1);
			}
			return s;
		}

		public String childToString(Object o) {
			Component comp = (Component) o;
			Rectangle bounds = comp.getBounds();
			int x = (int) bounds.getX();
			int y = (int) bounds.getY();
			int width = (int) bounds.getWidth();
			int height = (int) bounds.getHeight();
			String boundsString = " [" + x + ", " + y + ", " + width + ", " + height + "]";
			String nameString = "";
			if (comp instanceof JComponent) {
				JComponent jcomp = (JComponent) comp;
				String name = getComponentName(jcomp);
				if (name != null) {
					nameString = " name: " + name;
				}
			}
			return getShortClassName(comp) + boundsString + nameString;
		}

		public String parentToString(Object o) {
			Container cont = (Container) o;
			LayoutManager layout = cont.getLayout();
			String layoutString = layout == null ? "null" : getShortClassName(layout);

			int numChildren = cont.getComponentCount();
			String childrenString = "" + numChildren + (numChildren == 1 ? " child" : " children");

			return childToString(cont) + " (" + layoutString + ": " + childrenString + ")";
		}

		public boolean shouldTreatLikeParent(Object o) {
			if (o instanceof Container) {
				Container cont = (Container) o;
				if (cont.getComponentCount() > 0) {
					return true;
				}
			}
			return false;
		}

		public Object[] getChildren(Object parent) {
			Container cont = (Container) parent;
			return cont.getComponents();
		}
	}
}
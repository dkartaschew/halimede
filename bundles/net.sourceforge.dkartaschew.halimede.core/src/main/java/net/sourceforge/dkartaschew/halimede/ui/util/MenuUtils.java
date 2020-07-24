/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2020 Darran Kartaschew 
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 */
package net.sourceforge.dkartaschew.halimede.ui.util;

import java.util.List;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TypedListener;

/**
 * Utility class to assist in context injection.
 */
public class MenuUtils {

	/**
	 * Inject all menu items.
	 * 
	 * @param menuItems The collection of menu items to inject.
	 * @param context   The application context
	 */
	public static void injectMenuItems(List<MenuItem> menuItems, IEclipseContext context) {
		for (MenuItem menu : menuItems) {
			Listener[] listeners = menu.getListeners(SWT.Selection);
			if (listeners != null && listeners.length > 0) {
				for (Listener l : listeners) {
					if (l instanceof TypedListener) {
						TypedListener tl = (TypedListener) l;
						ContextInjectionFactory.inject(tl.getEventListener(), context);
					}
					ContextInjectionFactory.inject(l, context);
				}
			}
		}
	}

}

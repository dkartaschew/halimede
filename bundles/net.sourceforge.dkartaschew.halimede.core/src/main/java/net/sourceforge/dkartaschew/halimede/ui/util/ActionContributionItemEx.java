/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2019 Darran Kartaschew 
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

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

/**
 * A contribution item which delegates to an action.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * We are extending the underlying class to correct missing tooltips for menus.
 */
public class ActionContributionItemEx extends ActionContributionItem {

	/**
	 * Create a new ActionContributionItem
	 * 
	 * @param action The action.
	 */
	public ActionContributionItemEx(IAction action) {
		super(action);
	}

	@Override
	public void update(String propertyName) {
		super.update(propertyName);
		Widget widget = getWidget();
		if (widget != null) {
			if (widget instanceof MenuItem) {
				MenuItem mi = (MenuItem) widget;
				IAction action = getAction();
				if (action.getToolTipText() != null) {
					mi.setToolTipText(action.getToolTipText());
				}
			}
		}
	}

}

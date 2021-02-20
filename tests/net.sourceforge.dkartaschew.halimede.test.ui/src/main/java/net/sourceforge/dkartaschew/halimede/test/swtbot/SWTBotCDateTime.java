/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2021 Darran Kartaschew 
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

package net.sourceforge.dkartaschew.halimede.test.swtbot;

import java.util.Date;

import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swtbot.swt.finder.ReferenceBy;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.SWTBotWidget;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.matchers.WidgetOfType;
import org.eclipse.swtbot.swt.finder.utils.MessageFormat;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBotControl;
import org.hamcrest.SelfDescribing;

@SWTBotWidget(clasz = CDateTime.class, preferredName = "cDateTime", referenceBy = { ReferenceBy.LABEL })
public class SWTBotCDateTime extends AbstractSWTBotControl<CDateTime> {

	/**
	 * Create a new CDateTime SWTBot instance
	 * 
	 * @param bot The parent bot
	 * @param index The index of the widget on the current shell
	 * @return The CDateTime SWTBot instance.
	 * @throws WidgetNotFoundException if the widget is <code>null</code> or widget has been disposed.
	 */
	public static SWTBotCDateTime get(SWTBot bot, int index) throws WidgetNotFoundException {
		return new SWTBotCDateTime(bot.widget(WidgetOfType.widgetOfType(CDateTime.class), index));
	}

	/**
	 * Constructs an instance of this object with the given widget.
	 *
	 * @param w the widget.
	 * @throws WidgetNotFoundException if the widget is <code>null</code> or widget has been disposed.
	 */
	public SWTBotCDateTime(CDateTime w) throws WidgetNotFoundException {
		this(w, null);
	}

	/**
	 * Constructs an instance of this object with the given widget.
	 *
	 * @param w the widget.
	 * @param description the description of the widget, this will be reported by {@link #toString()}
	 * @throws WidgetNotFoundException if the widget is <code>null</code> or widget has been disposed.
	 */
	public SWTBotCDateTime(CDateTime w, SelfDescribing description) throws WidgetNotFoundException {
		super(w, description);
	}

	/**
	 * Gets the date of this widget.
	 *
	 * @return the date/time set into the widget.
	 */
	public Date getDate() {
		return syncExec(() -> widget.getSelection());
	}

	/**
	 * Sets the date.
	 *
	 * @param toSet the date to set into the control.
	 */
	public void setDate(final Date toSet) {
		log.debug(MessageFormat.format("Setting date on control: {0} to {1}", this, toSet)); //$NON-NLS-1$
		waitForEnabled();
		syncExec(() -> widget.setSelection(toSet));
		notify(SWT.Selection);
	}

}

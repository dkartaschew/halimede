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

package net.sourceforge.dkartaschew.halimede.data.render;

import java.io.PrintStream;
import java.time.ZonedDateTime;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;
import net.sourceforge.dkartaschew.halimede.util.WordUtils;

/**
 * Output renderer to a simple PrintWriter stream.
 */
public class TextOutputRenderer implements ICertificateOutputRenderer {

	private final static String HR = "---------------------------------------------------------";
	private final static String TAB = "    ";

	/**
	 * The output stream to write to.
	 */
	private final PrintStream out;
	/**
	 * Flag to indicate if the last entry was a HR.
	 */
	private boolean lastWasHR = false;

	/**
	 * Create a new Text output render
	 * 
	 * @param outputStream The output stream to write to.
	 */
	public TextOutputRenderer(PrintStream outputStream) {
		this.out = outputStream;
	}

	@Override
	public void addHeaderLine(String value) {
		if (!lastWasHR) {
			addEmptyLine();
		}
		out.println(WordUtils.wrap(value, 80));
		lastWasHR = false;
	}

	@Override
	public void addEmptyLine() {
		out.println();
		lastWasHR = false;
	}

	@Override
	public void addContentLine(String value) {
		if (value == null) {
			out.println();
			return;
		}
		if (!value.contains(System.lineSeparator())) {
			out.print(TAB);
			out.println(value);
		} else {
			String[] lines = value.split(System.lineSeparator());
			for (String line : lines) {
				out.print(TAB);
				out.println(line);
			}
		}
		lastWasHR = false;
	}
	
	@Override
	public void addContentLine(String key, String value) {
		addContentLine(key, value, false);
	}

	@Override
	public void addContentLine(String key, String value, boolean monospace) {
		StringBuilder sb = new StringBuilder(TAB);
		sb.append(key != null ? key : "");
		sb.append(" ");
		int sz = sb.length();
		if (value == null) {
			out.println(sb.toString());
			return;
		}
		out.print(sb.toString());
		if (!value.contains(System.lineSeparator())) {
			out.println(value);
		} else {
			String[] lines = value.split(System.lineSeparator());
			StringBuilder spacerBuilder = new StringBuilder();
			for (int i = 0; i < sz; i++) {
				spacerBuilder.append(' ');
			}
			String spacer = spacerBuilder.toString();
			out.println(lines[0]);
			for (int i = 1; i < lines.length; i++) {
				out.print(spacer);
				out.println(lines[i]);
			}
		}
		lastWasHR = false;
	}

	@Override
	public void addHorizontalLine() {
		addEmptyLine();
		out.println(HR);
		addEmptyLine();
		lastWasHR = true;
	}

	@Override
	public void finaliseRender() {
		addHorizontalLine();
		out.print("Generated: ");
		out.println(DateTimeUtil.toString(ZonedDateTime.now()));
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		if (bundle != null) {
			Version version = bundle.getVersion();
			out.print(bundle.getHeaders().get("Bundle-Name"));
			out.print(" {");
			out.print(bundle.getSymbolicName());
			out.print("} ");
			out.print(version.toString());
		}
		addEmptyLine();
		out.flush();
	}

}

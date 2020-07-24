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
import net.sourceforge.dkartaschew.halimede.util.HTMLEncode;

/**
 * HTML Output renderer to a simple PrintWriter stream.
 */
public class HTMLOutputRenderer implements ICertificateOutputRenderer {

	private final static String SPACE = "&nbsp;";

	/**
	 * The output stream to write to.
	 */
	protected final PrintStream out;

	/**
	 * Create a new Text output render
	 * 
	 * @param outputStream The output stream to write to.
	 * @param title The title head tag contents
	 */
	public HTMLOutputRenderer(PrintStream outputStream, String title) {
		this.out = outputStream;
		head(title);
	}

	/**
	 * Create the header section
	 * 
	 * @param title The title heag tag contents
	 */
	protected void head(String title) {
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<meta content=\"text/html; charset=UTF-8\" http-equiv=\"content-type\" />");
		out.println("<title>" + HTMLEncode.escape(title) + "</title>");
		out.println("<style>");
		out.println("table, th, td {");
		out.println("    border: 0px;");
		out.println("    padding: 5px;");
		out.println("    font-family: sans-serif;");
		out.println("    font-size: 10pt;");
		out.println("}");
		out.println("td.key {");
		out.println("    white-space:nowrap;");
		out.println("    vertical-align: top;");
		out.println("    padding-left: 2em;");
		out.println("}");
		out.println("td.mono {");
		out.println("    font-family: \"Lucida Console\", Monaco, \"Courier New\", Courier, monospace");
		out.println("}");
		out.println("p.small {");
		out.println("    font-family: sans-serif;");
		out.println("    font-size: 8pt;");
		out.println("}");
		out.println("hr {");
		out.println("    border-style: inset;");
		out.println("    border-width: 1px;");
		out.println("    border-bottom: 0px;");
		out.println("    opacity: 0.25;");
		out.println("}");
		out.println("</style>");
		out.println("</head>");
		out.println("<body>");
		out.println("<table>");
	}

	@Override
	public void addHeaderLine(String value) {
		out.println("<tr>");
		out.print("<td colspan=2><b>");
		out.print(HTMLEncode.escape(value));
		out.println("</b></td>");
		out.println("</tr>");
	}

	@Override
	public void addEmptyLine() {
		out.println("<tr>");
		out.print("<td colspan=2></td>");
		out.println("</tr>");
	}

	@Override
	public void addContentLine(String value) {
		out.println("<tr>");
		out.println("<td colspan=2>");
		value = HTMLEncode.escape(value);
		if (!value.contains(System.lineSeparator())) {
			out.println(value);
		} else {
			String[] lines = value.split(System.lineSeparator());
			for (String line : lines) {
				out.println(line + "<br>");
			}
		}
		out.println("</td>");
		out.println("</tr>");
	}
	
	@Override
	public void addContentLine(String key, String value) {
		addContentLine(key, value, false);
	}

	@Override
	public void addContentLine(String key, String value, boolean monospace) {
		out.println("<tr>");
		out.println("<td class=\"key\">" + (key != null ? HTMLEncode.escape(key) : "") + "</td>");
		if (value == null) {
			out.println("<td></td>");
			out.println("</tr>");
			return;
		}
		out.print("<td");
		if (monospace) {
			out.print(" class=\"mono\"");
		}
		out.print(">");
		// Escape the value for HTML
		value = HTMLEncode.escape(value);
		if (!monospace || !value.contains(System.lineSeparator())) {
			if (!value.contains(System.lineSeparator())) {
				out.println(value);
			} else {
				String[] lines = value.split(System.lineSeparator());
				for (String line : lines) {
					out.println(line + "<br>");
				}
			}
		} else {
			String[] lines = value.split(System.lineSeparator());
			for (String line : lines) {
				out.println(line.replace(" ", SPACE) + "<br>");
			}
		}
		out.println("</td>");
		out.println("</tr>");
	}

	@Override
	public void addHorizontalLine() {
		out.println("<tr>");
		out.println("<td colspan=2>");
		out.println("<hr>");
		out.println("</td>");
		out.println("</tr>");
	}

	@Override
	public void finaliseRender() {
		out.println("</table>");
		out.println("<hr>");
		out.println("<p class=\"small\">");
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
		out.println("</p>");

		out.println("</body>");
		out.println("</html>");
		out.flush();
	}

}

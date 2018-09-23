package net.sourceforge.dkartaschew.halimede.ui.composite;

import java.io.PrintStream;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;

import net.sourceforge.dkartaschew.halimede.data.render.HTMLOutputRenderer;
import net.sourceforge.dkartaschew.halimede.util.HTMLEncode;

/**
 * HTML Output renderer to a simple PrintWriter stream, with fonts to match those of the current composite.
 */
public class CompositeHTMLRenderer extends HTMLOutputRenderer {

	/**
	 * The parent composite to derive style information from.
	 */
	private final Composite parent;

	/**
	 * Create a new Text output render
	 * 
	 * @param outputStream The output stream to write to.
	 * @param title The title head tag contents
	 * @param parent The parent composite to derive font and style information from.
	 */
	public CompositeHTMLRenderer(PrintStream outputStream, String title, Composite parent) {
		super(outputStream, title);
		this.parent = parent;
		init(title);
	}

	@Override
	protected void head(String title) {
		// NOP
	}

	/**
	 * Initialise our head
	 * 
	 * @param title The title to use.
	 */
	protected void init(String title) {
		/*
		 * Determine the font and style information from the parent.
		 */
		String fontName;
		int fontSize;
		try {
			Font font = parent.getFont();
			FontData[] data = font.getFontData();
			fontName = data[0].getName();
			fontSize = data[0].getHeight();
		} catch (Throwable e) {
			// ignore and set some default values;
			fontName = "sans-serif";
			fontSize = 10;
		}

		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<meta content=\"text/html; charset=UTF-8\" http-equiv=\"content-type\" />");
		out.println("<title>" + HTMLEncode.escape(title) + "</title>");
		out.println("<style>");
		out.println("table, th, td {");
		out.println("    border: 0px;");
		out.println("    padding: 2px;");
		out.println("    font-family: " + fontName + ";");
		out.println("    font-size: " + fontSize + "pt;");
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
		out.println("    font-family: " + fontName + ";");
		out.println("    font-size: " + (fontSize - 2) + "pt;");
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
	public void finaliseRender() {
		out.println("</table>");
		out.println("</body>");
		out.println("</html>");
		out.flush();
	}

}

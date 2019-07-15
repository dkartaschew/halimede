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

package net.sourceforge.dkartaschew.halimede.ui.composite;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.render.ICertificateOutputRenderer;
import net.sourceforge.dkartaschew.halimede.ui.util.SWTFontUtils;
import net.sourceforge.dkartaschew.halimede.util.WordUtils;

public class CompositeOutputRenderer extends Composite implements ICertificateOutputRenderer {

	/**
	 * Annotation to use for drawing the line.
	 */
	class HRAnnotation extends Annotation {

		public static final String TYPE = PluginDefaults.ID + ".annotation.hr";

		/**
		 * Create annotation marker
		 * 
		 * @param position The position of the marker
		 */
		public HRAnnotation(int position) {
			super(TYPE, false, Integer.toString(position));
		}

	}

	/**
	 * Drawing strategy for the line.
	 */
	class HRDrawingStrategy implements AnnotationPainter.IDrawingStrategy {

		@Override
		public void draw(Annotation annotation, GC gc, StyledText textWidget, int offset, int length, Color color) {
			if (gc != null) {
				final Color foreground = gc.getForeground();

				Point left = textWidget.getLocationAtOffset(offset);
				Point right = textWidget.getLocationAtOffset(offset + length);
				if (left.x > right.x) {
					/*
					 * hack: sometimes linewrapping text widget gives us the wrong x/y for the first character of a line
					 * that has been wrapped.
					 */
					left.x = 0;
					left.y = right.y;
				}
				right.x = textWidget.getClientArea().width;

				int baseline = textWidget.getBaseline(offset);
				int vcenter = left.y + (baseline / 2) + (baseline / 4);

				gc.setLineWidth(0); // NOTE: 0 means width is 1 but with optimized performance
				gc.setLineStyle(SWT.LINE_SOLID);

				left.x += 3;
				right.x -= 5;
				vcenter -= 2;

				if (right.x > left.x) {

					// draw the horizontal rule
					gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
					gc.drawLine(left.x, vcenter, right.x, vcenter);
				}

				gc.setForeground(foreground);
			} else {
				textWidget.redrawRange(offset, length, true);
			}
		}

	}

	/**
	 * The widget to display the contents.
	 */
	private SourceViewer viewer;
	/**
	 * The underlying widget of the viewer.
	 */
	private StyledText textArea;

	/**
	 * Header font.
	 */
	private Font headerFont;
	/**
	 * System monospace font.
	 */
	private Font monospace;
	/**
	 * The current length.
	 */
	private int currentLength;
	/**
	 * Current EOL
	 */
	private final static String EOL = System.lineSeparator();
	/**
	 * EOL Length.
	 */
	private final static int EOLLength = EOL.length();
	/**
	 * Size of the header font.
	 */
	private final static int HEADER_FONT_HEIGHT_ADJUSTMENT = 2;
	/**
	 * Default margin
	 */
	private final static int DEFAULT_INDENT = 8;
	/**
	 * Default wrap for long strings.
	 */
	private final static int DEFAULT_WRAP = 120;

	/**
	 * Create the composite based renderer.
	 * 
	 * @param parent The parent composite.
	 * @param style The applied style.
	 * @param title The title.
	 */
	public CompositeOutputRenderer(Composite parent, int style, String title) {
		super(parent, SWT.NONE);
		init(parent, title);
	}

	/**
	 * Initialise the composite
	 * 
	 * @param parent The parent instance.
	 * @param title The title.
	 */
	private void init(Composite parent, String title) {

		/*
		 * Get our font references.
		 */
		this.monospace = SWTFontUtils.getMonospacedFont(getDisplay());
		FontData[] font = parent.getFont().getFontData();
		font[0].setHeight(font[0].getHeight() + HEADER_FONT_HEIGHT_ADJUSTMENT);
		font[0].setStyle(SWT.BOLD);
		this.headerFont = new Font(getDisplay(), font[0]);
		/*
		 * Start layout.
		 */
		setLayout(new GridLayout(1, false));

		/*
		 * Create the source text viewer (We need this to be able to draw the horizontal rule line).
		 */
		viewer = new SourceViewer(this, null,
				SWT.DOUBLE_BUFFERED | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.setDocument(new Document(), new AnnotationModel());

		/*
		 * Get access to the underlying text widget.
		 */
		textArea = viewer.getTextWidget();
		textArea.setAlwaysShowScrollBars(false);
		textArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		/*
		 * Start creation of annotation services.
		 */
		IAnnotationAccess annotationAccess = new IAnnotationAccess() {
			public Object getType(Annotation annotation) {
				return annotation.getType();
			}

			public boolean isMultiLine(Annotation annotation) {
				return true;
			}

			public boolean isTemporary(Annotation annotation) {
				return true;
			}
		};

		/**
		 * Create a custom painter to draw horizontal line.
		 */
		AnnotationPainter painter = new AnnotationPainter(viewer, annotationAccess);
		painter.addDrawingStrategy(HRAnnotation.TYPE, new HRDrawingStrategy());
		painter.addAnnotationType(HRAnnotation.TYPE, HRAnnotation.TYPE);
		painter.setAnnotationTypeColor(HRAnnotation.TYPE, textArea.getForeground());
		viewer.addPainter(painter);

		/*
		 * Add basic menu
		 */
		Menu menu = new Menu(textArea);
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("Copy");
		item.addListener(SWT.Selection, e -> textArea.copy());
		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Select All");
		item.addListener(SWT.Selection, e -> textArea.selectAll());
		item.setAccelerator(SWT.CTRL + 'a');
		textArea.setMenu(menu);
		textArea.addKeyListener(new KeyAdapter() {
		    @Override
		    public void keyPressed(KeyEvent e)
		    {
		        if (e.stateMask == SWT.CTRL && e.keyCode == 'a') {
		            textArea.selectAll();
		            e.doit = false;
		        }
		    }
		});
	}

	@Override
	public void finaliseRender() {
		// NOP
	}

	@Override
	protected void checkSubclass() {
		/* Do nothing - Subclassing is allowed */
	}

	@Override
	public void dispose() {
		super.dispose();
		if (headerFont != null && !headerFont.isDisposed()) {
			headerFont.dispose();
		}
	}

	@Override
	public void addHeaderLine(String value) {
		StyleRange range = new StyleRange();
		range.start = currentLength;
		range.length = value.length();
		range.font = headerFont;
		textArea.append(value);
		textArea.append(EOL);
		textArea.setStyleRange(range);
		textArea.setLineVerticalIndent(textArea.getLineCount() - 2, DEFAULT_INDENT);
		currentLength += (range.length + EOLLength);
	}

	@Override
	public void addEmptyLine() {
		textArea.append(EOL);
		currentLength += EOLLength;
	}

	@Override
	public void addContentLine(String key, String value) {
		addContentLine(key, value, false);
	}

	@Override
	public void addContentLine(String key, String value, boolean monospace) {
		key = notNull(key);
		value = notNull(value);

		StyleRange range = new StyleRange();
		range.start = currentLength;
		range.length = key.length();
		range.fontStyle = SWT.BOLD;
		textArea.append(key);
		textArea.append(EOL);
		textArea.setStyleRange(range);
		textArea.setLineIndent(textArea.getLineCount() - 2, 1, DEFAULT_INDENT);
		textArea.setLineVerticalIndent(textArea.getLineCount() - 2, DEFAULT_INDENT / 2);
		currentLength += (range.length + EOLLength);

		if (!monospace) {
			// We treat monospace as preformatted.
			if (value.length() > DEFAULT_WRAP) {
				if (value.contains(EOL)) {
					// As WordUtils.wrap() is for single lines, lets split/rejoin...
					StringBuilder sb = new StringBuilder();
					for (String l : value.split(EOL)) {
						sb.append(WordUtils.wrap(l, DEFAULT_WRAP));
						sb.append(EOL);
					}
					value = sb.toString();
				} else {
					value = WordUtils.wrap(value, DEFAULT_WRAP);
				}
			}
		}

		range = new StyleRange();
		range.start = currentLength;
		range.length = value.length();
		range.font = this.monospace;
		textArea.append(value);
		textArea.append(EOL);
		if (monospace) {
			range = new StyleRange();
			range.start = currentLength;
			range.length = value.length();
			range.font = this.monospace;
			textArea.setStyleRange(range);
		}
		int c = countEOL(value);
		textArea.setLineIndent(textArea.getLineCount() - (1 + c), c, DEFAULT_INDENT * 2);

		currentLength += (value.length() + EOLLength);
	}

	@Override
	public void addHorizontalLine() {
		textArea.append(EOL);
		textArea.setLineVerticalIndent(textArea.getLineCount() - 2, DEFAULT_INDENT / 2);
		IAnnotationModel model = viewer.getAnnotationModel();
		model.addAnnotation(new HRAnnotation(currentLength), new Position(currentLength));
		currentLength += EOLLength;
	}

	/**
	 * Ensure the given string is not null
	 * 
	 * @param value The value to check
	 * @return The original value if not null, or ""
	 */
	private String notNull(String value) {
		return (value != null) ? value : "";
	}

	/**
	 * Get the number of EOL in the string
	 * 
	 * @param value The Sting to check EOL for
	 * @return The number of EOL in the string + 1
	 */
	private int countEOL(String value) {
		if (value.contains(EOL)) {
			int lastIndex = 0;
			int count = 0;
			while (lastIndex != -1) {
				lastIndex = value.indexOf(EOL, lastIndex);
				if (lastIndex != -1) {
					count++;
					lastIndex += EOLLength;
				}
			}
			return count + 1;
		}
		return 1;
	}

}

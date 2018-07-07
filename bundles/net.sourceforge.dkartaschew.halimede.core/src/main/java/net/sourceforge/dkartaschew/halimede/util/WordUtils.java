/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.dkartaschew.halimede.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Operations on Strings that contain words.
 * </p>
 *
 * <p>
 * This class tries to handle <code>null</code> input gracefully. An exception will not be thrown for a
 * <code>null</code> input. Each method documents its behavior in more detail.
 * </p>
 *
 * @since 1.1
 */
public class WordUtils {

	/**
	 * <p>
	 * <code>WordUtils</code> instances should NOT be constructed in standard programming. Instead, the class should be
	 * used as <code>WordUtils.wrap("foo bar", 20);</code>.
	 * </p>
	 *
	 * <p>
	 * This constructor is public to permit tools that require a JavaBean instance to operate.
	 * </p>
	 */
	public WordUtils() {
		super();
	}

	// Wrapping
	// --------------------------------------------------------------------------
	/**
	 * <p>
	 * Wraps a single line of text, identifying words by <code>' '</code>.
	 * </p>
	 *
	 * <p>
	 * New lines will be separated by the system property line separator. Very long words, such as URLs will <i>not</i>
	 * be wrapped.
	 * </p>
	 *
	 * <p>
	 * Leading spaces on a new line are stripped. Trailing spaces are not stripped.
	 * </p>
	 *
	 * <table border="1" summary="Wrap Results">
	 * <tr>
	 * <th>input</th>
	 * <th>wrapLength</th>
	 * <th>result</th>
	 * </tr>
	 * <tr>
	 * <td>null</td>
	 * <td>*</td>
	 * <td>null</td>
	 * </tr>
	 * <tr>
	 * <td>""</td>
	 * <td>*</td>
	 * <td>""</td>
	 * </tr>
	 * <tr>
	 * <td>"Here is one line of text that is going to be wrapped after 20 columns."</td>
	 * <td>20</td>
	 * <td>"Here is one line of\ntext that is going\nto be wrapped after\n20 columns."</td>
	 * </tr>
	 * <tr>
	 * <td>"Click here to jump to the commons website - http://commons.apache.org"</td>
	 * <td>20</td>
	 * <td>"Click here to jump\nto the commons\nwebsite -\nhttp://commons.apache.org"</td>
	 * </tr>
	 * <tr>
	 * <td>"Click here, http://commons.apache.org, to jump to the commons website"</td>
	 * <td>20</td>
	 * <td>"Click here,\nhttp://commons.apache.org,\nto jump to the\ncommons website"</td>
	 * </tr>
	 * </table>
	 *
	 * (assuming that '\n' is the systems line separator)
	 *
	 * @param str the String to be word wrapped, may be null
	 * @param wrapLength the column to wrap the words at, less than 1 is treated as 1
	 * @return a line with newlines inserted, <code>null</code> if null input
	 */
	public static String wrap(final String str, final int wrapLength) {
		return wrap(str, wrapLength, null, false);
	}

	/**
	 * <p>
	 * Wraps a single line of text, identifying words by <code>' '</code>.
	 * </p>
	 *
	 * <p>
	 * Leading spaces on a new line are stripped. Trailing spaces are not stripped.
	 * </p>
	 *
	 * <table border="1" summary="Wrap Results">
	 * <tr>
	 * <th>input</th>
	 * <th>wrapLength</th>
	 * <th>newLineString</th>
	 * <th>wrapLongWords</th>
	 * <th>result</th>
	 * </tr>
	 * <tr>
	 * <td>null</td>
	 * <td>*</td>
	 * <td>*</td>
	 * <td>true/false</td>
	 * <td>null</td>
	 * </tr>
	 * <tr>
	 * <td>""</td>
	 * <td>*</td>
	 * <td>*</td>
	 * <td>true/false</td>
	 * <td>""</td>
	 * </tr>
	 * <tr>
	 * <td>"Here is one line of text that is going to be wrapped after 20 columns."</td>
	 * <td>20</td>
	 * <td>"\n"</td>
	 * <td>true/false</td>
	 * <td>"Here is one line of\ntext that is going\nto be wrapped after\n20 columns."</td>
	 * </tr>
	 * <tr>
	 * <td>"Here is one line of text that is going to be wrapped after 20 columns."</td>
	 * <td>20</td>
	 * <td>"&lt;br /&gt;"</td>
	 * <td>true/false</td>
	 * <td>"Here is one line of&lt;br /&gt;text that is going&lt; br /&gt;to be wrapped after&lt;br /&gt;20
	 * columns."</td>
	 * </tr>
	 * <tr>
	 * <td>"Here is one line of text that is going to be wrapped after 20 columns."</td>
	 * <td>20</td>
	 * <td>null</td>
	 * <td>true/false</td>
	 * <td>"Here is one line of" + systemNewLine + "text that is going" + systemNewLine + "to be wrapped after" +
	 * systemNewLine + "20 columns."</td>
	 * </tr>
	 * <tr>
	 * <td>"Click here to jump to the commons website - http://commons.apache.org"</td>
	 * <td>20</td>
	 * <td>"\n"</td>
	 * <td>false</td>
	 * <td>"Click here to jump\nto the commons\nwebsite -\nhttp://commons.apache.org"</td>
	 * </tr>
	 * <tr>
	 * <td>"Click here to jump to the commons website - http://commons.apache.org"</td>
	 * <td>20</td>
	 * <td>"\n"</td>
	 * <td>true</td>
	 * <td>"Click here to jump\nto the commons\nwebsite -\nhttp://commons.apach\ne.org"</td>
	 * </tr>
	 * </table>
	 *
	 * @param str the String to be word wrapped, may be null
	 * @param wrapLength the column to wrap the words at, less than 1 is treated as 1
	 * @param newLineStr the string to insert for a new line, <code>null</code> uses the system property line separator
	 * @param wrapLongWords true if long words (such as URLs) should be wrapped
	 * @return a line with newlines inserted, <code>null</code> if null input
	 */
	public static String wrap(final String str, final int wrapLength, final String newLineStr,
			final boolean wrapLongWords) {
		return wrap(str, wrapLength, newLineStr, wrapLongWords, " ");
	}

	/**
	 * <p>
	 * Wraps a single line of text, identifying words by <code>wrapOn</code>.
	 * </p>
	 *
	 * <p>
	 * Leading spaces on a new line are stripped. Trailing spaces are not stripped.
	 * </p>
	 *
	 * <table border="1" summary="Wrap Results">
	 * <tr>
	 * <th>input</th>
	 * <th>wrapLength</th>
	 * <th>newLineString</th>
	 * <th>wrapLongWords</th>
	 * <th>wrapOn</th>
	 * <th>result</th>
	 * </tr>
	 * <tr>
	 * <td>null</td>
	 * <td>*</td>
	 * <td>*</td>
	 * <td>true/false</td>
	 * <td>*</td>
	 * <td>null</td>
	 * </tr>
	 * <tr>
	 * <td>""</td>
	 * <td>*</td>
	 * <td>*</td>
	 * <td>true/false</td>
	 * <td>*</td>
	 * <td>""</td>
	 * </tr>
	 * <tr>
	 * <td>"Here is one line of text that is going to be wrapped after 20 columns."</td>
	 * <td>20</td>
	 * <td>"\n"</td>
	 * <td>true/false</td>
	 * <td>" "</td>
	 * <td>"Here is one line of\ntext that is going\nto be wrapped after\n20 columns."</td>
	 * </tr>
	 * <tr>
	 * <td>"Here is one line of text that is going to be wrapped after 20 columns."</td>
	 * <td>20</td>
	 * <td>"&lt;br /&gt;"</td>
	 * <td>true/false</td>
	 * <td>" "</td>
	 * <td>"Here is one line of&lt;br /&gt;text that is going&lt;br /&gt; to be wrapped after&lt;br /&gt;20
	 * columns."</td>
	 * </tr>
	 * <tr>
	 * <td>"Here is one line of text that is going to be wrapped after 20 columns."</td>
	 * <td>20</td>
	 * <td>null</td>
	 * <td>true/false</td>
	 * <td>" "</td>
	 * <td>"Here is one line of" + systemNewLine + "text that is going" + systemNewLine + "to be wrapped after" +
	 * systemNewLine + "20 columns."</td>
	 * </tr>
	 * <tr>
	 * <td>"Click here to jump to the commons website - http://commons.apache.org"</td>
	 * <td>20</td>
	 * <td>"\n"</td>
	 * <td>false</td>
	 * <td>" "</td>
	 * <td>"Click here to jump\nto the commons\nwebsite -\nhttp://commons.apache.org"</td>
	 * </tr>
	 * <tr>
	 * <td>"Click here to jump to the commons website - http://commons.apache.org"</td>
	 * <td>20</td>
	 * <td>"\n"</td>
	 * <td>true</td>
	 * <td>" "</td>
	 * <td>"Click here to jump\nto the commons\nwebsite -\nhttp://commons.apach\ne.org"</td>
	 * </tr>
	 * <tr>
	 * <td>"flammable/inflammable"</td>
	 * <td>20</td>
	 * <td>"\n"</td>
	 * <td>true</td>
	 * <td>"/"</td>
	 * <td>"flammable\ninflammable"</td>
	 * </tr>
	 * </table>
	 * 
	 * @param str the String to be word wrapped, may be null
	 * @param wrapLength the column to wrap the words at, less than 1 is treated as 1
	 * @param newLineStr the string to insert for a new line, <code>null</code> uses the system property line separator
	 * @param wrapLongWords true if long words (such as URLs) should be wrapped
	 * @param wrapOn regex expression to be used as a breakable characters, if blank string is provided a space
	 *            character will be used
	 * @return a line with newlines inserted, <code>null</code> if null input
	 */
	public static String wrap(final String str, int wrapLength, String newLineStr, final boolean wrapLongWords,
			String wrapOn) {
		if (str == null) {
			return null;
		}
		if (newLineStr == null) {
			newLineStr = System.lineSeparator();
		}
		if (wrapLength < 1) {
			wrapLength = 1;
		}
		final Pattern patternToWrapOn = Pattern.compile(wrapOn);
		final int inputLineLength = str.length();
		int offset = 0;
		final StringBuilder wrappedLine = new StringBuilder(inputLineLength + 32);

		while (offset < inputLineLength) {
			int spaceToWrapAt = -1;
			Matcher matcher = patternToWrapOn
					.matcher(str.substring(offset, Math.min(offset + wrapLength + 1, inputLineLength)));
			if (matcher.find()) {
				if (matcher.start() == 0) {
					offset += matcher.end();
					continue;
				}
				spaceToWrapAt = matcher.start() + offset;
			}

			// only last line without leading spaces is left
			if (inputLineLength - offset <= wrapLength) {
				break;
			}

			while (matcher.find()) {
				spaceToWrapAt = matcher.start() + offset;
			}

			if (spaceToWrapAt >= offset) {
				// normal case
				wrappedLine.append(str, offset, spaceToWrapAt);
				wrappedLine.append(newLineStr);
				offset = spaceToWrapAt + 1;

			} else {
				// really long word or URL
				if (wrapLongWords) {
					// wrap really long word one line at a time
					wrappedLine.append(str, offset, wrapLength + offset);
					wrappedLine.append(newLineStr);
					offset += wrapLength;
				} else {
					// do not wrap really long word, just extend beyond limit
					matcher = patternToWrapOn.matcher(str.substring(offset + wrapLength));
					if (matcher.find()) {
						spaceToWrapAt = matcher.start() + offset + wrapLength;
					}

					if (spaceToWrapAt >= 0) {
						wrappedLine.append(str, offset, spaceToWrapAt);
						wrappedLine.append(newLineStr);
						offset = spaceToWrapAt + 1;
					} else {
						wrappedLine.append(str, offset, str.length());
						offset = inputLineLength;
					}
				}
			}
		}

		// Whatever is left in line is short enough to just pass through
		wrappedLine.append(str, offset, str.length());

		return wrappedLine.toString();
	}

}
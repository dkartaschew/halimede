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

package net.sourceforge.dkartaschew.halimede.util;

/**
 * HTML encoding class...
 * <p>
 * This is pretty basic only handling the basics.
 * <p>
 * Inspired by: http://www.htmlescape.net/htmlescape_for_java.html <br>
 * Ulrich Jensen, http://www.htmlescape.net
 *
 */
public class HTMLEncode {

	/**
	 * HTML Escape
	 * <p>
	 * Does NOT convert line endings to &lt;br&gt;
	 * 
	 * @param value The String to escape
	 * @return The escaped String
	 */
	public static String escape(String value) {

		if (value == null || value.isEmpty()) {
			return "";
		}
		StringBuffer out = new StringBuffer(value.length());
		char[] chars = value.toCharArray();
		
		for (int i = 0; i < chars.length; i++) {
			switch (chars[i]) {
			case 60: out.append("&lt;"); break; // <
			case 62: out.append("&gt;"); break; // >
			case 34: out.append("&quot;"); break; // "
			case 38: out.append("&amp;"); break; //& 
			case 39: out.append("&apos;"); break; // '
			case 162: out.append("&cent;"); break; //¢ 
			case 163: out.append("&pound;"); break; //£
			case 165: out.append("&yen;"); break; //¥ 
			case 8364: out.append("&euro;"); break; //€ 
			case 169: out.append("&copy;"); break; //© 
			case 174: out.append("&reg;"); break; //® 
			case 8482: out.append("&trade;"); break; //™ 
			
	        case 198: out.append("&AElig;"); break; //Æ 
	        case 193: out.append("&Aacute;"); break; //Á 
	        case 194: out.append("&Acirc;"); break; //Â 
	        case 192: out.append("&Agrave;"); break; //À 
	        case 197: out.append("&Aring;"); break; //Å 
	        case 195: out.append("&Atilde;"); break; //Ã 
	        case 196: out.append("&Auml;"); break; //Ä 
	        case 199: out.append("&Ccedil;"); break; //Ç 
	        case 208: out.append("&ETH;"); break; //Ð 
	        case 201: out.append("&Eacute;"); break; //É 
	        case 202: out.append("&Ecirc;"); break; //Ê 
	        case 200: out.append("&Egrave;"); break; //È 
	        case 203: out.append("&Euml;"); break; //Ë 
	        case 205: out.append("&Iacute;"); break; //Í 
	        case 206: out.append("&Icirc;"); break; //Î 
	        case 204: out.append("&Igrave;"); break; //Ì 
	        case 207: out.append("&Iuml;"); break; //Ï 
	        case 209: out.append("&Ntilde;"); break; //Ñ 
	        case 211: out.append("&Oacute;"); break; //Ó 
	        case 212: out.append("&Ocirc;"); break; //Ô 
	        case 210: out.append("&Ograve;"); break; //Ò 
	        case 216: out.append("&Oslash;"); break; //Ø 
	        case 213: out.append("&Otilde;"); break; //Õ 
	        case 214: out.append("&Ouml;"); break; //Ö 
	        case 222: out.append("&THORN;"); break; //Þ 
	        case 218: out.append("&Uacute;"); break; //Ú 
	        case 219: out.append("&Ucirc;"); break; //Û 
	        case 217: out.append("&Ugrave;"); break; //Ù 
	        case 220: out.append("&Uuml;"); break; //Ü 
	        case 221: out.append("&Yacute;"); break; //Ý 
	        case 225: out.append("&aacute;"); break; //á 
	        case 226: out.append("&acirc;"); break; //â 
	        case 230: out.append("&aelig;"); break; //æ 
	        case 224: out.append("&agrave;"); break; //à 
	        case 229: out.append("&aring;"); break; //å 
	        case 227: out.append("&atilde;"); break; //ã 
	        case 228: out.append("&auml;"); break; //ä 
	        case 231: out.append("&ccedil;"); break; //ç 
	        case 233: out.append("&eacute;"); break; //é 
	        case 234: out.append("&ecirc;"); break; //ê 
	        case 232: out.append("&egrave;"); break; //è 
	        case 240: out.append("&eth;"); break; //ð 
	        case 235: out.append("&euml;"); break; //ë 
	        case 237: out.append("&iacute;"); break; //í 
	        case 238: out.append("&icirc;"); break; //î 
	        case 236: out.append("&igrave;"); break; //ì 
	        case 239: out.append("&iuml;"); break; //ï 
	        case 241: out.append("&ntilde;"); break; //ñ 
	        case 243: out.append("&oacute;"); break; //ó 
	        case 244: out.append("&ocirc;"); break; //ô 
	        case 242: out.append("&ograve;"); break; //ò 
	        case 248: out.append("&oslash;"); break; //ø 
	        case 245: out.append("&otilde;"); break; //õ 
	        case 246: out.append("&ouml;"); break; //ö 
	        case 223: out.append("&szlig;"); break; //ß 
	        case 254: out.append("&thorn;"); break; //þ 
	        case 250: out.append("&uacute;"); break; //ú 
	        case 251: out.append("&ucirc;"); break; //û 
	        case 249: out.append("&ugrave;"); break; //ù 
	        case 252: out.append("&uuml;"); break; //ü 
	        case 253: out.append("&yacute;"); break; //ý 
	        case 255: out.append("&yuml;"); break; //ÿ 
	        
			default:
				if (chars[i] > 127) {
					out.append(String.format("&#x%x;", (int)chars[i]));
				} else {
					out.append(chars[i]);
				}
				break;
			}
		}
		return out.toString();
	}
}

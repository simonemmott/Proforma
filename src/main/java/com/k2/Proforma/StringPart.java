package com.k2.Proforma;

import java.io.IOException;
import java.io.Writer;

/**
 * This class represents a static string on a proforma line
 * 
 * @author simon
 *
 */
public class StringPart implements Part {
	
	/**
	 * The static string value of this part of the proforma line
	 */
	private final String value;
	/**
	 * Create the string part for the given string
	 * @param value	The string to use for the static string part of a proforma line
	 */
	StringPart(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() { return value; }

	@Override
	public Writer write(int indent, Writer out) throws IOException {
		out.write(value);
		return out;
	}

}

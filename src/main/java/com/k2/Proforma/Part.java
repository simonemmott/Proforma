package com.k2.Proforma;

import java.io.IOException;
import java.io.Writer;

/**
 * This interface defines parts of a proforma line
 * 
 * @author simon
 *
 */
public interface Part {
	/**
	 * Output the part as a String converting any parameters to their parameter value
	 * @return	The part as a string
	 */
	public String toString();
	
	/**
	 * Write the part to the given writer
	 * @param indent		The amount of indent to prepend to proforma parts
	 * @param out		The writer on which to write the part
	 * @return		The output writer for method chaining
	 * @throws IOException	If there is an error writing to the writer
	 */
	public Writer write(int indent, Writer out) throws IOException;
}

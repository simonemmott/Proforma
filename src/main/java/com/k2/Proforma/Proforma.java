package com.k2.Proforma;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A proforma is a simple string template comprising lines of parts
 * 
 * Each line in the proforma is output to the given writer with a implementation system specific carriage return or the carriage
 * return specified by the call to setCarriageReturn(...)
 * 
 * Each line comprises parts. Parts can be plain strings, parameters or other proformas.
 * 
 * A proforma can optionally automatically increase the indent given to it for output. This is useful when embedding
 * proformas in other proformas to automatically increase the indent for embedded proformas;
 * 
 * @author simon
 *
 */
public class Proforma implements Part
{
	/**
	 * A convenience enumeration to specify the MS DOS carriage return or the UNIX carriage return
	 * @author simon
	 *
	 */
	public enum CarriageReturn {
		DOS("\r\n"),
		UNIX("\n");
		
		String cr;
		CarriageReturn(String cr) {
			this.cr = cr;
		}
		
		String cr() { return cr; }
	}
	
	private Map<String, Parameter> parameters = new HashMap<String, Parameter>();
	private List<Line> lines = new ArrayList<Line>();
	private String indent = "  ";
	private boolean embedded = false;
	private boolean autoIncrementIndent = true;
	private String cr = String.format("%n");
	
	/**
	 * This static method generates a parameter with the given alias
	 * @param alias		The alias for the parameter
	 * @return			A parameter with the given alias
	 */
	public static Parameter param(String alias) {
		return new Parameter(alias);
	}

	/**
	 * Add a line to the proforma comprising the given parts in order
	 * @param parts	An array of the parts on the line
	 * @return	This proforma for method chaining
	 */
	public Proforma add(Object ...parts) {
		Line line = new Line();
		for (Object part : parts) {
			if (part instanceof Part) {
				line.add((Part)part);
			} else if (part instanceof Parameter) {
				Parameter param = (Parameter)part;
				if (parameters.containsKey(param.getAlias())) {
					line.add(parameters.get(param.getAlias()));
				} else {
					parameters.put(param.getAlias(), param);
					line.add(param);
				}
			} else {
				line.add(part.toString());
			}
		}
		lines.add(line);
		return this;
	}

	/**
	 * Set the value of the parameter with the given alias to the given value
	 * @param alias	The alias of the parameter to set
	 * @param value	The string value for the parameter
	 * @return	This proforma for method chaining
	 */
	public Proforma set(String alias, String value) {
		Parameter param = parameters.get(alias);
		if (param != null) {
			param.setValue(value);
		}
		return this;
	}

	/**
	 * Set the value for the parameter identified by alias to the give call back.
	 * @param alias	The alias of the parameter to set
	 * @param callBack	The call back to get the parameter value
	 * @return	This parameter for method chaining
	 */
	public Proforma set(String alias, CallBack<String> callBack) {
		return set(alias, callBack, false);
	}

	/**
	 * Set the value for the parameter identified by alias to the given call back specifying whether or not to continuously execute the call back
	 * @param alias		The alias of the parameter to set
	 * @param callBack	The call back to use to get the parameter value
	 * @param continuous		True if the call back should be called each time the parameter value is requested
	 * @return	This parameter for method chaining
	 */
	public Proforma set(String alias, CallBack<String> callBack, boolean continuous) {
		Parameter param = parameters.get(alias);
		if (param != null) {
			if (continuous) {
				param.setContinuousCallback(callBack);
			} else {
				param.setCallback(callBack);
			}
		}
		return this;
	}

	/**
	 * Set the string to be repeated for the indent.
	 * 
	 * The default is '  ' (two spaces)
	 * @param indent		The string to be repeated for the indent
	 * @return	This proforma for method chaining
	 */
	public Proforma setIndent(String indent) {
		this.indent = indent;
		return this;
	}
	
	/**
	 * Identifies that this proforma is embedded
	 * 
	 * Embedded proformas omit the indent on the first line and the carriage return on the last line
	 * 
	 * @param embedded	True if the proforma is embedded
	 * @return	This proforma for method chaining
	 */
	public Proforma setEmbedded(boolean embedded) {
		this.embedded = embedded;
		return this;
	}

	/**
	 * Identifies that this proforma should automatically increase the given indent
	 * 
	 * @param autoIncrementIndent	True if proformas embedded in this proforma should have their indent automatically incremented by one
	 * @return	This proforma for method chaining
	 */
	public Proforma setAutoIncrementIndent(boolean autoIncrementIndent) {
		this.autoIncrementIndent = autoIncrementIndent;
		return this;
	}

	/**
	 * Set the carriage return to the given string
	 * @param cr		The string to use as the carriage return
	 * @return		This proforma for method chaining
	 */
	public Proforma setCarriageReturn(String cr) {
		this.cr = cr;
		return this;
	}

	/**
	 * Set the carriage return to the given carriage return format
	 * @param cr		The carriage return to use
	 * @return		This proforma for method chaining
	 */
	public Proforma setCarriageReturn(CarriageReturn cr) {
		this.cr = cr.cr();
		return this;
	}

	/**
	 * This method writes the proforma out to the output writer starting with a 0 indent
	 * @param out	The output writer
	 * @return		The output writer for method chaining
	 */
	public Writer write(Writer out) {
		return write(0, out);
	}
	/**
	 * Write the proforma out onto the given writer with the given indent
	 * 
	 * IO Exceptions thrown by the writer are converted into the unchecked ProformaError
	 */
	public Writer write(int i, Writer out) {
		for (int l=0; l<lines.size(); l++) {
			Line line = lines.get(l);
			try {
				if (i > 0) {
					if (l>0 || !embedded) {
						for (int j=0; j<i; j++) {
							out.write(indent);
						}
					}
				}

				line.write((autoIncrementIndent) ? i+1: i, out);

				if (i >= 0) {
					if (l<lines.size()-1 || !embedded) {
						out.write(cr);
					}
				}
			} catch (IOException e) {
				throw new ProformaError(e);
			}
		}
		return out;
	}

}

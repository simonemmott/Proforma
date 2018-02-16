package com.k2.Proforma;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
	
	public Proforma() {}
	
	private Proforma(Proforma cloneFrom) {
		this.autoIncrementIndent = cloneFrom.autoIncrementIndent;
		this.embedded = cloneFrom.autoIncrementIndent;
		this.lines = cloneFrom.lines;
		this.valueSourceParameter = cloneFrom.valueSourceParameter;
	}
	
	private List<Line> lines = new ArrayList<Line>();
	private boolean embedded = false;
	private boolean autoIncrementIndent = true;
	
	/**
	 * This static method generates a parameter with the given alias
	 * @param alias		The alias for the parameter
	 * @return			A parameter with the given alias
	 */
	public static <T> Parameter<T> param(Class<T> cls, String alias) {
		return new Parameter<T>(cls,alias);
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
				line.add((Parameter<?>)part);
			} else {
				line.add(part.toString());
			}
		}
		lines.add(line);
		return this;
	}
	
	List<Line> getLines() { return lines; }
	 
	boolean embedded() { return embedded; }
	 
	boolean autoIncrementIndent() { return autoIncrementIndent; }
	
	private Parameter<?> valueSourceParameter;
	public Proforma with(Parameter<?> valueSourceParameter) {
		Proforma p = new Proforma(this);
		p.valueSourceParameter = valueSourceParameter;
		return p;
	}

	public <E> ProformaOutput<E> with(E valueSource) {
		return new ProformaOutput<E>(this, valueSource);
	}
	
	@SuppressWarnings("rawtypes")
	public ProformaOutput<?> setIndent(String indentString) {
		ProformaOutput<?> po = new ProformaOutput(this);
		return po.setIndent(indentString);
	}
	
	@SuppressWarnings("rawtypes")
	public ProformaOutput<?> setCarriageReturn(String cr) {
		ProformaOutput<?> po = new ProformaOutput(this);
		return po.setCarriageReturn(cr);
	}

	@SuppressWarnings("rawtypes")
	public ProformaOutput<?> setCarriageReturn(CarriageReturn cr) {
		ProformaOutput<?> po = new ProformaOutput(this);
		return po.setCarriageReturn(cr);
	}

	/**
	 * Set the value of the parameter with the given alias to the given value
	 * @param alias	The alias of the parameter to set
	 * @param value	The string value for the parameter
	 * @return	This proforma for method chaining
	 */
	@SuppressWarnings({ "rawtypes" })
	public <E> ProformaOutput<?> set(Class<E> valueClass, String alias, E value) {
		ProformaOutput<?> po = new ProformaOutput(this);
		return (ProformaOutput<?>) po.set(valueClass, alias, value);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Writer write(int indent, Writer out, ProformaOutput poIn) throws IOException {
		ProformaOutput po = new ProformaOutput(this)
				.setIndent(poIn.getIndent())
				.setCarriageReturn(poIn.getCarriageReturn());
		if (valueSourceParameter != null) {
			Object value = poIn.valueOf(valueSourceParameter);
			
			if (value instanceof Collection) {
				Collection<?> c = (Collection<?>)value;
				Iterator<?> i = c.iterator();
				while (i.hasNext()) {
					po.with(i.next()).write(indent, out);
				}
				return out;
			} else {
				return ((ProformaOutput<?>) po.with(value)).write(indent, out);
			}
		}
		return po.write(indent, out, poIn);
	}

}

package com.k2.Proforma;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.k2.Expressions.expression.Expression;
import com.k2.Util.StringUtil;

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
public class Proforma extends AbstractPart implements Part
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

	private List<Line> lines = new ArrayList<Line>();
	private boolean embedded = false;
	private boolean autoIncrementIndent = true;
	private Parameter<?> valueSourceParameter;
	
	/**
	 * Create a new default (empty) proforma
	 */
	public Proforma() {}
	
	/**
	 * Create a new proforma as a clone of the given proforma
	 * 
	 * @param clone		The proforma to clone into the created proforma
	 */
	private Proforma(Proforma clone) {
		this.autoIncrementIndent = clone.autoIncrementIndent;
		this.embedded = clone.autoIncrementIndent;
		this.lines = clone.lines;
		this.valueSourceParameter = clone.valueSourceParameter;
		this.conditionalExpression = clone.conditionalExpression;
	}
	
	/**
	 * This static method generates a parameter with the given alias
	 * @param cls		The class of the value supplied by the parameter
	 * @param alias		The alias for the parameter
	 * @return			A parameter with the given alias
	 * @param <T> 	The datatype of the to be supplied by the required parameter
	 */
	public static <T> Parameter<T> param(Class<T> cls, String alias) {
		return new Parameter<T>(cls,alias);
	}

	/**
	 * This static method creates a string part for the given object that is included if the given boolean expression 
	 * valuates to true
	 * @param includeIf		A boolean expression that is evaluated to identify whether the resultant string part is to be included in the output
	 * @param value			The object this is to be converted into a String part using StringUtil.toString()
	 * @return				The resultant conditional string part
	 */
	public static StringPart includeIf(Expression<Boolean> includeIf, Object value) {
		return new StringPart(StringUtil.toString(value)).includeIf(includeIf);
	}
	
	/**
	 * This static method creates a string part for the given object that is included if a boolean parameter with the given
	 * alias evaluates to true
	 * @param alias		The alias of the boolean parameter that controls whether the returned string part is included in the output
	 * @param value		The value used to create the string part
	 * @return			A string part whose value is drived from the given value using the StringUtil.toString() methods
	 */
	public static StringPart includeIf(String alias, Object value) {
		return new StringPart(StringUtil.toString(value)).includeIf(param(Boolean.class, alias));
	}
	
	/**
	 * Add a line to the proforma comprising the given parts in order
	 * @param parts	An array of the parts on the line
	 * @return	This proforma for method chaining
	 */
	public Proforma add(Object ...parts) {
		lines.add(new Line(parts));
		return this;
	}
	
	/**
	 * This method add a conditional line to the proforma
	 * 
	 * A conditional line is only included in the output if the given boolean expression evaluated to true
	 * @param conditionalExpression		The boolean expression that must evaluate to true for this line to be included in the output
	 * @param parts						The parts comprising this line
	 * @return							This proforma for method chaining
	 */
	public Proforma addIf(Expression<Boolean> conditionalExpression, Object ...parts) {
		lines.add(new Line(parts).includeIf(conditionalExpression));
		return this;
	}
	
	/**
	 * Get the list of lines in this proforma
	 * @return	The list of lines in this proforma
	 */
	List<Line> getLines() { return lines; }
	 
	/**
	 * Identify whether this proforma is an embedded proforma
	 * @return	True if this proforma is embedded
	 */
	boolean embedded() { return embedded; }
	 
	/**
	 * Identify whether this profoma automatically increments it's indent when output. Defaults to true
	 * @return	True if this proforma automatically increments its' indent when output
	 */
	boolean autoIncrementIndent() { return autoIncrementIndent; }
	
	@Override
	public Proforma includeIf(Expression<Boolean> conditionalExpression) {
		Proforma p = new Proforma(this);
		p.conditionalExpression = conditionalExpression;
		return p;
	}
	
	/**
	 * Create a clone of this proforma setting the parameter that will provide the object from which this proforma will derive its values
	 * @param valueSourceParameter	The parameter that when evaluated will provide values for this proforma
	 * @return		A clone of this proforma with its source parameter set
	 */
	public Proforma with(Parameter<?> valueSourceParameter) {
		Proforma p = new Proforma(this);
		p.valueSourceParameter = valueSourceParameter;
		return p;
	}

	/**
	 * Create a new proforma output evaluator for the given object to evaluate the parameters in this proforma
	 * @param valueSource	The object that provids the values for this proforma
	 * @return		A proforma output to evaluate the parameters in this proforma
	 * @param <E> The type of the object providing values for this proforma
	 */
	public <E> ProformaOutput<E> with(E valueSource) {
		return new ProformaOutput<E>(this, valueSource);
	}
	
	/**
	 * Create a proforma output to evaluate the parameters in this proforma setting the indent string to the given value
	 * @param indentString	The string to use as the indent when outputting this proforma
	 * @return		A proforma output to evaluate this proforma with the indent string set to the given value
	 */
	@SuppressWarnings("rawtypes")
	public ProformaOutput<?> setIndent(String indentString) {
		ProformaOutput<?> po = new ProformaOutput(this);
		return po.setIndent(indentString);
	}
	
	/**
	 * Create a proforma output to evaluate the parameters in this proforma setting the carriage to the given value
	 * @param cr		The string to use as the carriage return when outputting this proforma
	 * @return		A proforma output to evaluate this proforma with the carriage return set to the given value
	 */
	@SuppressWarnings("rawtypes")
	public ProformaOutput<?> setCarriageReturn(String cr) {
		ProformaOutput<?> po = new ProformaOutput(this);
		return po.setCarriageReturn(cr);
	}

	/**
	 * Create a proforma output to evaluate the parameters in this proforma setting the carriage to the given value
	 * @param cr		The Carriage return enumeration to use as the carriage return when outputting this proforma
	 * @return		A proforma output to evaluate this proforma with the carriage return set to the given value
	 */
	@SuppressWarnings("rawtypes")
	public ProformaOutput<?> setCarriageReturn(CarriageReturn cr) {
		ProformaOutput<?> po = new ProformaOutput(this);
		return po.setCarriageReturn(cr);
	}

	/**
	 * Set the value of the parameter with the given alias to the given value
	 * @param valueClass	The type of the parameter value to set
	 * @param alias	The alias of the parameter to set
	 * @param value	The string value for the parameter
	 * @return	This proforma for method chaining
	 * @param <E> The data type of the parameter to be set
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
		ProformaOutput po = poIn.clone(this);
		if (valueSourceParameter != null) {
			Object value = poIn.valueOf(valueSourceParameter);
			
			if (value instanceof Collection) {
				Iterator<?> i = ((Collection<?>)value).iterator();
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

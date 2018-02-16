# Proforma - v0.1.0
The Proforma project provides a powerful string templating solution.
Proformas define the stringified format to be output containing lines of parts.
Parts can be either an object, a parameter or another proforma.

A proforma is output on an instance of the Writer interface using a proforma output instance. The proforma output instance is automatically
created by the first call to any of the proforma methods that specify an output attribute. e.g. setIndent(...) or set(...) etc.

The methods of the proforma output class typically all return the proforma output for method chaining or a clone of it accordingly.

The exception to this is the write method which returns the writer instance that it was called with, again for method chaining.

Javadoc documentation of this project can be found [here](https://simonemmott.github.io/Proforma/index.html)


### License

[GNU GENERAL PUBLIC LICENSE v3](http://fsf.org/)

## Basic Example

The Proforma is used to generate parameterised output on a writer instance.

This code:

```java
Proforma proforma = new Proforma()
		.add("Line 1 has this ", Proforma.param(String.class, "p1"))
		.add("Line 2 the parameter ", Proforma.param("p2"), " and ", Proforma.param(String.class, "p3"), " only.")
		.add("This proforma has the following parameters: ", 
				Proforma.param(String.class, "p1"), ", ", 
				Proforma.param(String.class, "p2"), " and ", 
				Proforma.param(String.class, "p3"));
		
proforma.set("p1", "PARAMETER")	
		.set("p2", "OTHER")
		.set("p3", "PARAM");
		.write(new PrintWriter(System.out)).flush();
```
Produces the following output.

```text
Line 1 has this PARAMETER
Line 2 the parameter OTHER and PARAM only.
This proforma has the following parameters: PARAMETER, OTHER and PARAM
```

## Getting Started

Download a jar file containing the latest version or fork this project and install in your IDE

Maven users can add this project using the following additions to the pom.xml file.
```maven
<dependencies>
    ...
    <dependency>
        <groupId>com.k2</groupId>
        <artifactId>Proforma</artifactId>
        <version>0.1.0</version>
    </dependency>
    ...
</dependencies>
```

## Working With Proforma

The Profoma provides an API for generating templated and parameterized string output to an implementation of the Writer interface

### Setup up the Proforma template
Proformas define a template of literal strings and parameters. The template is defined in lines and parts.

A line contains 0 or more parts and parts are either literal object, parameters or Proforma instances.

Lines are added to the proforma template using the `add(...)` method.

e.g.

```java
Proforma proforma = new Proforma()
		.add("This is ", Proforma.param(String.class, "name"), " template")
		.add("This is line 2")
		.add("This line has another ", Proforma.param(String.class, "parameter"))
		.add("This line had two parameters ", Proforma.param(String.class, "name"), " and ", Proforma.param(String.class, "parameter"));
```
The above code creates a proforma template with 4 lines and two parameters. **NOTE** Repeated inclusions of parameters with the same alias
result in the original parameter with the alias being reused at the defined location in the template.

Parameters can be defined separately from the proforma template

e.g.

```java
Parameter p1 = Proforma.param(String.class, "p1");

Proforma proforma = new Proforma().add("This is ", p1, " template");
```

Lines can be conditionally included in the proforma using the `addIf(...)` method. The following code adds a line that is conditional on a boolean 
parameter.  Predicates from the K2 Expressions project can also be used.

```java
Proforma proforme = new Proforma().addIf(Proforma.param(Boolean.class, "if"), "This line is conditional");
```

There is a convenience method that simplified this slightly. The code below creates an identical proforma to the code above:

```java
Proforma proforme = new Proforma().addIf("if", "This line is conditional");
```
**NOTE** The first parameter of the `addIf(...)` method is either a boolean expression or the string alias of a boolean parameter

Proforma parts can also be optional.

The following code creates an proforma with a single line containing optional parts:

```java
Proforma proforma = new Proforma()
		.add(	Proforma.includeIf(Proforma.param(Boolean.class, "if"), "Optional "), 
				Proforma.param(String.class, "optional").includeIf(Proforma.param(Boolean.class, "if")));
```
**NOTE** The line will be output even if all of its parts are excluded.

Proformas can be included as parts of a line.

The following code includes a proforma as part of a line. The values of the child proforma will be sources from the same values as the
main proforma

```java
Proforma tooProforma = new Proforma().setEmbedded(true)
		.add("This is a Too")
		.add("ID: ", Proforma.param(Integer.class, "id"))
		.add("Name: ", Proforma.param(String.class, "name"));

Proforma barProforma = new Proforma().setEmbedded(true)
		.add("This is a Bar")
		.add("ID: ", Proforma.param(Integer.class, "id"))
		.add("Name: ", Proforma.param(String.class, "name"))
		.add();
		
Proforma fooProformaNoDrillDown = new Proforma()
		.add("ID: ", Proforma.param(Integer.class, "id"))
		.add("Name: ", Proforma.param(String.class, "name"))
		.add("Description: ", Proforma.param(String.class, "description"))
		.add("Too: ", tooProforma)
		.add("Bars: ", barProforma);
```

Embedded proformas can draw their values from the value of a parameter available to the parent proforma. The code bellow shows an example of this.

```java
Proforma tooProforma = new Proforma().setEmbedded(true)
		.add("This is a Too")
		.add("ID: ", Proforma.param(Integer.class, "id"))
		.add("Name: ", Proforma.param(String.class, "name"));

Proforma barProforma = new Proforma().setEmbedded(true)
		.add("This is a Bar")
		.add("ID: ", Proforma.param(Integer.class, "id"))
		.add("Name: ", Proforma.param(String.class, "name"))
		.add();

Proforma fooProformaWithDrillDown = new Proforma()
		.add("ID: ", Proforma.param(Integer.class, "id"))
		.add("Name: ", Proforma.param(String.class, "name"))
		.add("Description: ", Proforma.param(String.class, "description"))
		.add("Too: ", tooProforma.with(Proforma.param(Too.class, "too")))
		.add("Bars: ", barProforma.with(Proforma.param(List.class, "bars")));
```
**NOTE** The `barProforma` is included with a `List` with the alias 'bars'. Using a collection as the value source for a proforma will cause the profoma to be output repeatedly for each value in the list. Since the `ProformaOutput` class extends the `com.k2.Expressionz.evaluators.ParamterOrObjectEvalutator` the values provided to the proforma come first from the paramter values that have been set and then from the object that the `ProformaOutput` was created with using the `with(...)` method.

Poformas can be optionally included in a line through the `includeIf(...)` method of the proforma which returns a clone of the proforma on which the `includeIf(...)` method was called.


### Parameters
Parameters are defined using the static `param(Class, String)` method of Proforma.
e.g.

```java
Parameter p1 = Proforma.param(Date.class, "p1");
```
Defines a parameter Date identified by the alias "p1"


Parameters can have their values set indirectly through the Proforma instance and identified by their alias.

```java
Proforma proforma = new Proforma().add("Line 1 has a paramter: ", Proforma.param("p1"));

proforma.set(Date.class, "p1", StringUtil.toDate("2018/01/01", "yyyy/MM/dd"));
```


### Generating Output
The output from a proforma is generated by calling the `write(Writer)` method of the proforma or the `write(int, Writer)` method.

The `writer(...)` methods return the given writer for method chaining.

e.g.

```java
proforma.write(new PrintWriter(System.out)).flush();
```
Causes the proforma template to be written to the output stream replacing all parameters with their defined values.

The proforma template can be indented if required. The default indent string is '  ' (two blank spaces). The indent is repeated a given number of times.

e.g.

```java
proforma.setIndent("\t").write(2, new PrintWriter(System.out)).flush();
```

Causes the proforma template to be written to the output stream indented by 2 tab characters.

The carriage return gnerated by the proforma is the system default carriage return.  The carriage return can be set using the `setCarriageReturn(...)` methods

e.g.

```java
proforma.setCarriageReturn("\n");
```
forces the profoma to use "\n" as the carriage return;

And

```java
proforma.setCarriageReturn(CarriageReturn.DOS);
```
forces the proforma to use the DOS carriage return.

Proformas can be defined as embedded. Embedded proformas omit the indent characters from the first line and the carriage return from the last line.

The code below shows identifying a proforma as embedded:

```java
proformma.setEmbedded(true);
```

A Proforma can automatically increase the indent of proformas embedded in their proforma template.

The code below set the proforma to no automatically increase the indent of proformas embedded in its templete.

```java
proforma.setAutoIncrement(false);
```
By default a proforma does automatically increase the indent of proformas embedded in its template.








































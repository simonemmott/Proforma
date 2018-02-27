package com.k2.Proforma;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k2.Expressions.expression.ParameterExpression;
import com.k2.Util.CallBack;

public class ProformaTest {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Test
	public void reallyBasicTest() {
		Parameter<String> p = Proforma.param(String.class, "p1");
		Proforma proforma = new Proforma();
		ProformaOutput<Object> po = new ProformaOutput<Object>(proforma);
		po.set(p, "Hello");
		assertEquals("Hello", p.evaluate(po));
	}

	@Test
	public void reallyBasicTest2() {
		Parameter<String> p = Proforma.param(String.class, "p1");
		Proforma proforma = new Proforma();
		ProformaOutput<Object> po = new ProformaOutput<Object>(proforma);
		po.set(String.class, "p1", "Hello");
		assertEquals("Hello", p.evaluate(po));
	}

	@Test
	public void basicTest()
    {
		
		Proforma proforma = new Proforma()
							.add("This is ", Proforma.param(String.class, "name"), " template")
							.add("This is line 2")
							.add("This line has another ", Proforma.param(String.class, "parameter"))
							.add("This line had two parameters ", Proforma.param(String.class, "name"), " and ", Proforma.param(String.class, "parameter"));
		
		String exprected = 	"\t\t\tThis is MY template\n" + 
				"\t\t\tThis is line 2\n" + 
				"\t\t\tThis line has another PARAMETER\n" + 
				"\t\t\tThis line had two parameters MY and PARAMETER\n";

		StringWriter sw = new StringWriter();
		
		ProformaOutput<?> po = new ProformaOutput<Object>(proforma);
		po	.set(String.class, "name", "MY")
			.set(String.class, "parameter", "PARAMETER")
			.setIndent("\t")
			.write(3, sw);

		System.out.println(sw.toString());

		assertEquals(exprected, sw.toString());
		
	}
	
	
	@Test
	public void proformaTest()
    {
		
		Proforma embedded = new Proforma().setEmbedded(true)
				.add("This is an embedded proforma called ", Proforma.param(String.class, "name"))
				.add("With twp parameters ", Proforma.param(String.class, "name"), " and ", Proforma.param(String.class, "parameter"));

		Proforma proforma = new Proforma()
							.add("This proforma '", embedded, "' is embedded")
							.add("With another line ", Proforma.param(String.class, "name"), " ", Proforma.param(String.class, "parameter"));
				
		String exprected = 	"\t\t\tThis proforma 'This is an embedded proforma called MY\n" + 
							"\t\t\t\tWith twp parameters MY and PARAMETER' is embedded\n" + 
							"\t\t\tWith another line MY PARAMETER\n";
		
		StringWriter sw = new StringWriter();
		
		proforma.set(String.class, "name", "MY")
				.set(String.class, "parameter", "PARAMETER")
				.setIndent("\t")
				.write(3, sw);

		System.out.println(sw.toString());
		
		assertEquals(exprected, sw.toString());
		
	}

	class Bar {
		Integer id;
		String name;
		Bar(Integer id, String name) {
			this.id = id;
			this.name = name;
		}
	}
	
	class Too {
		Integer id;
		String name;
		Too(Integer id, String name) {
			this.id = id;
			this.name = name;
		}
	}
	
	class Foo{
		Integer id;
		String name;
		String description;
		Too too;
		List<Bar> bars = new ArrayList<Bar>();
		Foo(Integer id, String name, String description) {
			this.id = id;
			this.name = name;
			this.description = description;
		}
		Foo(Integer id, String name, String description, Too too) {
			this.id = id;
			this.name = name;
			this.description = description;
			this.too = too;
		}
		Foo add(Bar bar) { bars.add(bar); return this;}
	}
	Proforma barOnALine = new Proforma()
			.add(Proforma.param("name"));

	Proforma listTestProforma = new Proforma()
			.add("Start")
			.add(barOnALine.with(Proforma.param(List.class, "bars")))
			.add("End");
	
	@Test
	public void listTest() throws IOException {
		Foo foo = new Foo(1, "hello", "world!").add(new Bar(1, "Hello")).add(new Bar(2, "World!"));

		StringWriter sw = new StringWriter();

		String expected = 	
				"Start\n" + 
				"  Hello\n" + 
				"  World!\n" + 
				"\n" + 
				"End\n";

		listTestProforma.with(foo).setAutoFlush(true).write(sw).flush();
		
		assertEquals(expected, sw.toString());

	}

		
	Proforma simpleFooProforma = new Proforma()
			.add("ID: ", Proforma.param(Integer.class, "id"))
			.add("Name: ", Proforma.param(String.class, "name"))
			.add("Description: ", Proforma.param(String.class, "description"));
	
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
	
	Proforma fooProformaNoDrillDown = new Proforma()
			.add("ID: ", Proforma.param(Integer.class, "id"))
			.add("Name: ", Proforma.param(String.class, "name"))
			.add("Description: ", Proforma.param(String.class, "description"))
			.add("Too: ", tooProforma)
			.add("Bars: ", barProforma);
	
	@Test
	public void withTest() throws IOException {
		
		Foo foo = new Foo(1, "hello", "world!");

		String exprected = 	"ID: 1\n" + 
				"Name: hello\n" + 
				"Description: world!\n";

		StringWriter sw = new StringWriter();

		simpleFooProforma.with(foo)
				.setIndent("\t")
				.write(sw).flush();
		
		assertEquals(exprected, sw.toString());
		
	}

	@Test
	public void with2Test() throws IOException {
		
		Foo foo = new Foo(1, "hello", "world!");

		String exprected = 	"ID: 10\n" + 
				"Name: NAME\n" + 
				"Description: world!\n";

		StringWriter sw = new StringWriter();

		simpleFooProforma.set(String.class, "name", "NAME")
				.with(foo)
				.set(Integer.class, "id", 10)
				.setIndent("\t")
				.write(sw).flush();
		
		assertEquals(exprected, sw.toString());
		
	}

	@Test
	public void withCollectionTest() throws IOException {
		
		Foo foo1 = new Foo(1, "hello", "world!");
		Foo foo2 = new Foo(2, "how", "are you!");
		Foo foo3 = new Foo(3, "goodbye", "cruel world!");
		
		List<Foo> fooList = new ArrayList<Foo>();
		fooList.add(foo1);
		fooList.add(foo2);
		fooList.add(foo3);

		String exprected = 	"ID: 1\n" + 
				"Name: hello\n" + 
				"Description: world!\n" + 
				"ID: 2\n" + 
				"Name: how\n" + 
				"Description: are you!\n" + 
				"ID: 3\n" + 
				"Name: goodbye\n" + 
				"Description: cruel world!\n";

		StringWriter sw = new StringWriter();

		simpleFooProforma.with(fooList)
				.setIndent("\t")
				.write(sw).flush();
		
//		System.out.println(sw.toString());
		
		assertEquals(exprected, sw.toString());
		
	}

	@Test
	public void withCollectionATest() throws IOException {
		
		Foo foo1 = new Foo(1, "hello", "world!", new Too(1, "Too 1"))
				.add(new Bar(1, "Foo 1 Bar 1"))
				.add(new Bar(2, "Foo 1 Bar 2"))
				.add(new Bar(3, "Foo 1 Bar 3"));

		
		String exprected = 	"ID: 1\n" + 
				"Name: hello\n" + 
				"Description: world!\n" + 
				"Too: This is a Too\n" + 
				"	ID: 1\n" + 
				"	Name: Too 1\n" + 
				"Bars: This is a Bar\n" + 
				"	ID: 1\n" + 
				"	Name: Foo 1 Bar 1\n" + 
				"	This is a Bar\n" + 
				"	ID: 2\n" + 
				"	Name: Foo 1 Bar 2\n" + 
				"	This is a Bar\n" + 
				"	ID: 3\n" + 
				"	Name: Foo 1 Bar 3\n" + 
				"	\n";

		StringWriter sw = new StringWriter();

		fooProformaWithDrillDown.with(foo1)
				.setIndent("\t")
				.write(sw).flush();
		
		System.out.println(sw.toString());
		
		assertEquals(exprected, sw.toString());
		
	}

	@Test
	public void withCollection2Test() throws IOException {
		
		Foo foo1 = new Foo(1, "hello", "world!", new Too(1, "Too 1"))
				.add(new Bar(1, "Foo 1 Bar 1"))
				.add(new Bar(2, "Foo 1 Bar 2"))
				.add(new Bar(3, "Foo 1 Bar 3"));
		Foo foo2 = new Foo(2, "how", "are you!", new Too(2, "Too 2"))
				.add(new Bar(1, "Foo 2 Bar 1"))
				.add(new Bar(2, "Foo 3 Bar 2"))
				.add(new Bar(3, "Foo 4 Bar 3"));
		Foo foo3 = new Foo(3, "goodbye", "cruel world!", new Too(3, "Too 3"))
				.add(new Bar(1, "Foo 3 Bar 1"))
				.add(new Bar(2, "Foo 3 Bar 2"))
				.add(new Bar(3, "Foo 3 Bar 3"));

		
		List<Foo> fooList = new ArrayList<Foo>();
		fooList.add(foo1);
		fooList.add(foo2);
		fooList.add(foo3);

		String exprected = 	"ID: 1\n" + 
				"Name: hello\n" + 
				"Description: world!\n" + 
				"Too: This is a Too\n" + 
				"	ID: 1\n" + 
				"	Name: Too 1\n" + 
				"Bars: This is a Bar\n" + 
				"	ID: 1\n" + 
				"	Name: Foo 1 Bar 1\n" + 
				"	This is a Bar\n" + 
				"	ID: 2\n" + 
				"	Name: Foo 1 Bar 2\n" + 
				"	This is a Bar\n" + 
				"	ID: 3\n" + 
				"	Name: Foo 1 Bar 3\n" + 
				"	\n" + 
				"ID: 2\n" + 
				"Name: how\n" + 
				"Description: are you!\n" + 
				"Too: This is a Too\n" + 
				"	ID: 2\n" + 
				"	Name: Too 2\n" + 
				"Bars: This is a Bar\n" + 
				"	ID: 1\n" + 
				"	Name: Foo 2 Bar 1\n" + 
				"	This is a Bar\n" + 
				"	ID: 2\n" + 
				"	Name: Foo 3 Bar 2\n" + 
				"	This is a Bar\n" + 
				"	ID: 3\n" + 
				"	Name: Foo 4 Bar 3\n" + 
				"	\n" + 
				"ID: 3\n" + 
				"Name: goodbye\n" + 
				"Description: cruel world!\n" + 
				"Too: This is a Too\n" + 
				"	ID: 3\n" + 
				"	Name: Too 3\n" + 
				"Bars: This is a Bar\n" + 
				"	ID: 1\n" + 
				"	Name: Foo 3 Bar 1\n" + 
				"	This is a Bar\n" + 
				"	ID: 2\n" + 
				"	Name: Foo 3 Bar 2\n" + 
				"	This is a Bar\n" + 
				"	ID: 3\n" + 
				"	Name: Foo 3 Bar 3\n" +
				"	\n";

		StringWriter sw = new StringWriter();

		fooProformaWithDrillDown.with(fooList)
				.setIndent("\t")
				.write(sw).flush();
		
		System.out.println(sw.toString());
		
		assertEquals(exprected, sw.toString());
		
	}

	@Test
	public void withCollection3Test() throws IOException {

		Foo foo1 = new Foo(1, "hello", "world!", new Too(1, "Too 1"))
				.add(new Bar(1, "Foo 1 Bar 1"))
				.add(new Bar(2, "Foo 1 Bar 2"))
				.add(new Bar(3, "Foo 1 Bar 3"));
		Foo foo2 = new Foo(2, "how", "are you!", new Too(2, "Too 2"))
				.add(new Bar(1, "Foo 2 Bar 1"))
				.add(new Bar(2, "Foo 3 Bar 2"))
				.add(new Bar(3, "Foo 4 Bar 3"));
		Foo foo3 = new Foo(3, "goodbye", "cruel world!", new Too(3, "Too 3"))
				.add(new Bar(1, "Foo 3 Bar 1"))
				.add(new Bar(2, "Foo 3 Bar 2"))
				.add(new Bar(3, "Foo 3 Bar 3"));

		
		List<Foo> fooList = new ArrayList<Foo>();
		fooList.add(foo1);
		fooList.add(foo2);
		fooList.add(foo3);

		String exprected = 	"ID: 1\n" + 
				"Name: hello\n" + 
				"Description: world!\n" + 
				"Too: This is a Too\n" + 
				"	ID: 1\n" + 
				"	Name: hello\n" + 
				"Bars: This is a Bar\n" + 
				"	ID: 1\n" + 
				"	Name: hello\n" + 
				"	\n" + 
				"ID: 2\n" + 
				"Name: how\n" + 
				"Description: are you!\n" + 
				"Too: This is a Too\n" + 
				"	ID: 2\n" + 
				"	Name: how\n" + 
				"Bars: This is a Bar\n" + 
				"	ID: 2\n" + 
				"	Name: how\n" + 
				"	\n" + 
				"ID: 3\n" + 
				"Name: goodbye\n" + 
				"Description: cruel world!\n" + 
				"Too: This is a Too\n" + 
				"	ID: 3\n" + 
				"	Name: goodbye\n" + 
				"Bars: This is a Bar\n" + 
				"	ID: 3\n" + 
				"	Name: goodbye\n" + 
				"	\n";

		StringWriter sw = new StringWriter();

		fooProformaNoDrillDown.with(fooList)
				.setIndent("\t")
				.write(sw).flush();
		
		System.out.println(sw.toString());
		
		assertEquals(exprected, sw.toString());
		
	}

	@Test
	public void ifTest() throws IOException {
		
		Proforma proforma = new Proforma()
				.add("This proforma may contain")
				.add(simpleFooProforma.includeIf(Proforma.param(Boolean.class, "if")))
				.add("another proforma");
		
		Foo foo = new Foo(1, "hello", "world!");

		String included = 	"This proforma may contain\n" + 
				"\tID: 1\n" + 
				"\tName: hello\n" + 
				"\tDescription: world!\n" +
				"\n" +
				"another proforma\n";

		String excluded = 	"This proforma may contain\n" + 
				"\n" +
				"another proforma\n";

		StringWriter sw = new StringWriter();

		proforma.with(foo)
				.set(Boolean.class, "if", true)
				.setIndent("\t")
				.write(sw).flush();

		System.out.println(sw.toString());
		
		assertEquals(included, sw.toString());
		
		sw = new StringWriter();
		
		proforma.with(foo)
				.set(Boolean.class, "if", false)
				.setIndent("\t")
				.write(sw).flush();
		
		System.out.println(sw.toString());
		
		assertEquals(excluded, sw.toString());

	}
	
	@Test
	public void ifTest2() throws IOException {
		
		Proforma proforma = new Proforma()
				.add(	Proforma.includeIf(Proforma.param(Boolean.class, "if"), "Optional "), 
						Proforma.param(String.class, "optional").includeIf(Proforma.param(Boolean.class, "if")));
		
		String included = "Optional part\n";
		
		StringWriter sw = new StringWriter();

		proforma.set(Boolean.class, "if", true)
				.set(String.class, "optional", "part")
				.write(sw).flush();
		
		assertEquals(included, sw.toString());

		String excluded = "\n";
		
		sw = new StringWriter();

		proforma.set(Boolean.class, "if", false)
				.set(String.class, "optional", "part")
				.write(sw).flush();
		
		assertEquals(excluded, sw.toString());

	}

	@Test
	public void ifLineTest() throws IOException {
		
		Proforma proforma = new Proforma()
				.add("This line is always included")
				.addIf(Proforma.param(Boolean.class, "if"), "This line may be included")
				.add("This line is also always included");
		
		String included = "This line is always included\n" +
				"This line may be included\n" +
				"This line is also always included\n";
		
		StringWriter sw = new StringWriter();

		proforma.set(Boolean.class, "if", true)
				.write(sw).flush();
		
		assertEquals(included, sw.toString());

		String excluded = "This line is always included\n" +
				"This line is also always included\n";
		
		sw = new StringWriter();

		proforma.set(Boolean.class, "if", false)
				.write(sw).flush();
		
		assertEquals(excluded, sw.toString());

	}


}

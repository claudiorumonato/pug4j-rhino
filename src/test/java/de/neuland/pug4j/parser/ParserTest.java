package de.neuland.pug4j.parser;

import java.io.IOException;
import java.net.URISyntaxException;

import org.cld.pug4j.RhinoExpressionHandler;
import org.junit.Assert;

import de.neuland.pug4j.TestFileHelper;
import de.neuland.pug4j.parser.node.Node;
import de.neuland.pug4j.template.FileTemplateLoader;

public class ParserTest {

	protected Parser parser;
	protected Node root;

	protected void loadInParser(String fileName) throws URISyntaxException {
		loadInParser(fileName,"jade");
	}
	protected void loadInParser(String fileName,String extension) throws URISyntaxException {

		try {
			FileTemplateLoader loader = new FileTemplateLoader(
					TestFileHelper.getParserResourcePath(""), extension);
			parser = new Parser(fileName, loader, new RhinoExpressionHandler());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("template " + fileName + " was not found");
		}
		root = parser.parse();
	}
}

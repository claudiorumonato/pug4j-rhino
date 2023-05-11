package de.neuland.pug4j.compiler;

import java.io.StringWriter;
import java.io.Writer;

import org.cld.pug4j.RhinoPugModel;

import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.parser.node.Node;
import de.neuland.pug4j.template.PugTemplate;

public class Compiler {

	private final Node rootNode;
	private boolean prettyPrint;
	private PugTemplate template = new PugTemplate();

	public Compiler(Node rootNode) {
		this.rootNode = rootNode;
	}

	public String compileToString(RhinoPugModel model) throws PugCompilerException {
		StringWriter writer = new StringWriter();
		compile(model, writer);
		return writer.toString();
	}

	public void compile(RhinoPugModel model, Writer w) throws PugCompilerException {
		IndentWriter writer = new IndentWriter(w);
		writer.setUseIndent(prettyPrint);
		rootNode.execute(writer, model, template);
	}

	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}

	public void setTemplate(PugTemplate pugTemplate) {
		this.template = pugTemplate;
	}
}
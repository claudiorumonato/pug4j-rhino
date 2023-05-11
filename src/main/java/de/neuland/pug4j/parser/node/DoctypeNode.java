package de.neuland.pug4j.parser.node;

import org.cld.pug4j.RhinoPugModel;

import de.neuland.pug4j.compiler.IndentWriter;
import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.template.PugTemplate;

public class DoctypeNode extends Node {
	@Override
	public void execute(IndentWriter writer, RhinoPugModel model, PugTemplate template) throws PugCompilerException {
		String name = getValue();
		template.setDoctype(name);
		writer.append(template.getDoctypeLine());
		writer.setCompiledDoctype(true);
	}

}

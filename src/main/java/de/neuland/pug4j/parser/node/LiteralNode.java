package de.neuland.pug4j.parser.node;

import org.cld.pug4j.RhinoPugModel;

import de.neuland.pug4j.compiler.IndentWriter;
import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.template.PugTemplate;

public class LiteralNode extends Node {

	@Override
	public void execute(IndentWriter writer, RhinoPugModel model, PugTemplate template) throws PugCompilerException {
		writer.append(value);
	}

}

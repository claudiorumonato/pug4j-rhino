package de.neuland.pug4j.parser.node;

import org.cld.pug4j.RhinoPugModel;

import de.neuland.pug4j.compiler.IndentWriter;
import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.template.PugTemplate;

public class MixinNode extends CallNode {
	private String rest;

	@Override
	public void execute(IndentWriter writer, RhinoPugModel model, PugTemplate template) throws PugCompilerException {
		if (isCall()) {
			super.execute(writer, model, template);
		} else {
			model.setMixin(getName(), this);
		}
	}

	public void setRest(String rest) {
		this.rest = rest;
	}

	public String getRest() {
		return rest;
	}
}

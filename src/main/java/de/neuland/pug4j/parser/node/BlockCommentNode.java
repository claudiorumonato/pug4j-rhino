package de.neuland.pug4j.parser.node;

import org.cld.pug4j.RhinoPugModel;

import de.neuland.pug4j.compiler.IndentWriter;
import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.template.PugTemplate;

public class BlockCommentNode extends Node {
    private boolean buffered;
    @Override
    public void execute(IndentWriter writer, RhinoPugModel model, PugTemplate template) throws PugCompilerException {
        if (!isBuffered()) {
      			return;
      		}
      	if(writer.isPp()) {
            writer.prettyIndent(1, true);
        }
        writer.append("<!--" + value);
        block.execute(writer, model, template);
        if(writer.isPp()) {
            writer.prettyIndent(1, true);
        }
        writer.append("-->");
    }

    public boolean isBuffered() {
        return buffered;
    }

    public void setBuffered(boolean buffered) {
        this.buffered = buffered;
    }
}

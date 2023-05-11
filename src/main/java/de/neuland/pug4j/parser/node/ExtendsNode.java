package de.neuland.pug4j.parser.node;

import org.cld.pug4j.RhinoPugModel;

import de.neuland.pug4j.compiler.IndentWriter;
import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.template.PugTemplate;

public class ExtendsNode extends Node {
    FileReference file;
    @Override
    public void execute(IndentWriter writer, RhinoPugModel model, PugTemplate template) throws PugCompilerException {
        writer.append(value);
        //Todo implement Extends
    }

    public FileReference getFile() {
        return file;
    }

    public void setFile(FileReference file) {
        this.file = file;
    }
}

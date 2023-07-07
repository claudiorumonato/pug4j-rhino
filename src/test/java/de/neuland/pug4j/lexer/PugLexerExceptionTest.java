package de.neuland.pug4j.lexer;

import de.neuland.pug4j.TestFileHelper;
import de.neuland.pug4j.exceptions.PugLexerException;
import de.neuland.pug4j.expression.RhinoExpressionHandler;
import de.neuland.pug4j.template.FileTemplateLoader;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PugLexerExceptionTest {

    @Test
    public void shouldThrowExceptionOnInvalidId() throws IOException {
        assertExceptionMessage("invalid_id", "\"#\" is not a valid ID.");
    }
    @Test
    public void shouldThrowExceptionOnInvalidClassName() throws IOException {
        assertExceptionMessage("invalid_classname", "\"ö\" is not a valid class name.  Class names can only contain \"_\", \"-\", a-z and 0-9, and must contain at least one of \"_\", or a-z");
    }
    @Test
    public void shouldThrowExceptionOnInvalidClassName2() throws IOException {
        assertExceptionMessage("invalid_classname2", "Class names must contain at least one letter or underscore.");
    }
    @Test
    public void shouldThrowExceptionOnMalformedInclude() throws IOException {
        assertExceptionMessage("malformed_include", "malformed include");
    }
    @Test
    public void shouldThrowExceptionOnMalformedExtends() throws IOException {
        assertExceptionMessage("malformed_extends", "malformed extends");
    }

    private void assertExceptionMessage(String template, String expectedMessage) throws IOException {
        try {
            lex(template);
            fail("Should fail with PugLexerException and message:"+ expectedMessage);
        }catch(PugLexerException | URISyntaxException ex){
            assertEquals(expectedMessage,ex.getMessage());
        }
    }

    private void lex(String template) throws IOException, URISyntaxException {
        FileTemplateLoader templateLoader = new FileTemplateLoader(
                TestFileHelper.getLexerResourcePath("error-checks"), "pug");

        Lexer lexer = new Lexer(template, templateLoader, new RhinoExpressionHandler());
        lexer.getTokens();
    }
}

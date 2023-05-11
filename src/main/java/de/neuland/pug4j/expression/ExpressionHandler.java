package de.neuland.pug4j.expression;

import org.cld.pug4j.RhinoPugModel;

import de.neuland.pug4j.exceptions.ExpressionException;

/**
 * Created by christoph on 27.10.15.
 */
public interface ExpressionHandler {
    Boolean evaluateBooleanExpression(String expression, RhinoPugModel model) throws ExpressionException;

    Object evaluateExpression(String expression, RhinoPugModel model) throws ExpressionException;

    String evaluateStringExpression(String expression, RhinoPugModel model) throws ExpressionException;

    void assertExpression(String expression) throws ExpressionException;

    void setCache(boolean cache);

    void clearCache();
}


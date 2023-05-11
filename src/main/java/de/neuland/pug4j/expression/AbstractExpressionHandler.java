package de.neuland.pug4j.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cld.pug4j.RhinoPugModel;

public abstract class AbstractExpressionHandler implements ExpressionHandler {

    public static Pattern isLocalAssignment = Pattern.compile("^(var|let|const)[\\s]+([a-zA-Z0-9-_]+)[\\s]?={1}[\\s]?[^=]+$");

    protected void saveLocalVariableName(String expression, RhinoPugModel model) {
        Matcher matcher = isLocalAssignment.matcher(expression);
        if (matcher.matches()) {
            String var = matcher.group(2);
            model.putLocalVariableName(var);
        }
    }
    @Override
    public void setCache(boolean cache) {

    }

    @Override
    public void clearCache() {

    }
}

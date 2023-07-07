package de.neuland.pug4j.expression;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

import de.neuland.pug4j.exceptions.ExpressionException;
import de.neuland.pug4j.model.PugModel;

public class RhinoExpressionHandler extends AbstractExpressionHandler {

	@Override
	public Boolean evaluateBooleanExpression(String expr, PugModel model) throws ExpressionException {
		return BooleanUtil.convert(evaluateExpression(expr, model));
	}
	
	@Override
	public Object evaluateExpression(String expr, PugModel model) throws ExpressionException {
		Object res = null;
		try {
			saveLocalVariableName(expr, model);
			if (expr.startsWith("{"))
				expr = "("+expr+")";
			res = model.parse(expr, "<inline>");
		} catch (EcmaError jserr) {
			if (!jserr.getName().equals("ReferenceError")) {
				System.out.println(jserr.getMessage());
				if (!jserr.getName().equals("SyntaxError"))
					jserr.printStackTrace(System.err);
			}
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		return res;
	}

	@Override
	public String evaluateStringExpression(String expr, PugModel model) throws ExpressionException {
		Object result = evaluateExpression(expr, model);
		return result == null ? "" : result.toString();
	}

	public void assertExpression(String expr) throws ExpressionException {
		try {
			
		} catch (Exception ex) {
			throw new ExpressionException(expr, ex);
		}
	}
	
	public static Object toJava(Object value) {
		if (value instanceof NativeObject) {
			NativeObject obj = (NativeObject) value;
			Map<String,Object> res = new LinkedHashMap<String,Object>();
			for (Object id: obj.getAllIds())
				res.put(id.toString(), toJava(obj.get(id)));
			value = res;
		} else if (value instanceof NativeArray) {
			NativeArray arr = (NativeArray) value;
			List<Object> res = new ArrayList<Object>();
			for (long i = 0; i < arr.getLength(); i ++)
				res.add(toJava(arr.get(i)));
			value = res;
		}
		return value;
	}
}

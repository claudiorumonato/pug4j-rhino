package org.cld.html;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import de.neuland.pug4j.exceptions.ExpressionException;
import de.neuland.pug4j.expression.AbstractExpressionHandler;
import de.neuland.pug4j.expression.BooleanUtil;
import de.neuland.pug4j.model.PugModel;

public class RhinoExpressionHandler extends AbstractExpressionHandler {

	private final Context m_ctx;
	private final Scriptable m_scope;
	
	public RhinoExpressionHandler(Context ctx, Scriptable scope) {
		m_ctx = (ctx != null) ? ctx : Context.enter();
		m_scope = (scope != null) ? scope : m_ctx.initStandardObjects();
	}
	
	public RhinoExpressionHandler(Context ctx) { this(ctx, null); }
	
	public RhinoExpressionHandler() { this(null, null); }
	
	public Context getContext() { return m_ctx; }
	
	public Scriptable getScope() { return m_scope; }
	
	@SuppressWarnings("unchecked")
	private static Object toJS(Object val, Scriptable scp, Context ctx) {
		
		if (val instanceof List) {
			List<Object> arr = (List<Object>)val;
			Scriptable scr = ctx.newArray(scp, arr.size());
			for (int i = 0; i < arr.size(); i ++)
				scr.put(i, scr, arr.get(i));
			return scr;
		}
		
		if (val instanceof Map) {
			Map<String,Object> obj = (Map<String,Object>)val;
			Scriptable scr = ctx.newObject(scp);
			for (String str: obj.keySet())
				scr.put(str, scr, obj.get(str));
			return scr;
		}
		
		return val;
	}
	
	// Copia il model nell'oggetto Scriptable affinché sia "usabile" in Javascript.
	private static void putModel(PugModel model, Scriptable data, Context ctx) {
		Scriptable scope = ctx.initStandardObjects();
		for (String key: model.keySet())
			ScriptableObject.putProperty(data, key, toJS(model.get(key), scope, ctx));
	}
	
	// Aggiorno il model con le modifiche apportate da script.
	// @TODO Serve?
	private static void readModel(Scriptable data, PugModel model) {
		for (Object id: data.getIds())
			if (id instanceof String)
				model.put((String) id, ScriptableObject.getProperty(data, (String) id));
	}
	
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
			final Scriptable scope = m_scope;
			putModel(model, scope, m_ctx);
			res = m_ctx.evaluateString(scope, expr, "<inline>", 0, null);
			readModel(scope, model);
		} catch (Exception ex) {
			String m = ex.getMessage();
			if (m.startsWith("ReferenceError:")) {
				// Ignore undefined variables
			} else if (m.startsWith("SyntaxError:")) {
				System.out.println(ex.getMessage());
			} else {
				System.err.println(ex.getMessage());
				ex.printStackTrace(System.err);
			}
			res = null;
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

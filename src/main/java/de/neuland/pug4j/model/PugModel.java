package de.neuland.pug4j.model;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class PugModel extends AbstractPugModel<Scriptable> {

	private Context m_ctx;
	private Scriptable m_model;
	private Scriptable m_scope;
	private final boolean m_ownContext;
	
	public PugModel(Map<String, Object> defaults) {
		this(defaults, null);
	}
	
	public PugModel(Map<String, Object> defaults, Context ctx) {
		m_ctx = (m_ownContext = (ctx == null))
				? Context.enter()
				: ctx;
		m_model = m_ctx.initStandardObjects();
		m_scope = null;
		initialize(defaults);
	}

	public Scriptable scope() {
		return scopes.getLast();
	}
	
	public Object parse(String expr, String name) {
		Object res = null;
		if (name == null)
			name = "<anon>";
		try {
			res = m_ctx.evaluateString(m_scope, expr, name, 0, null);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		return res;
	}
	
	protected Scriptable newScope() {
		Scriptable scope = m_ctx.newObject(m_model);
		scope.put(LOCAL_VARS, scope, m_ctx.newObject(m_model));
		scope.setParentScope(m_scope);
		return m_scope = scope;
	}
	
	public void clear(boolean dispose) {
		scopes.clear();
		m_scope = null;
		if (m_ownContext) {
			Context.exit();
			m_ctx = (!dispose) ? Context.enter() : null;
		}
		if (!dispose) {
			m_scope = m_ctx.initStandardObjects();
			scopes.add(m_ctx.newObject(m_scope));
		}
	}
	
	@Override
	public void clear() {
		clear(false);
	}
	
	protected void scopePut(Scriptable scope, String key, Object val) {
		ScriptableObject.putProperty(scope, key, val);
	}
	
	protected Object scopeGet(Scriptable scope, String key) {
		return ScriptableObject.getProperty(scope, key);
	}
	
	protected boolean scopeHas(Scriptable scope, String key) {
		return ScriptableObject.hasProperty(scope, key);
	}

	protected void scopeDel(Scriptable scope, String key) {
		ScriptableObject.deleteProperty(scope, key);
	}

	protected Scriptable fetchLocalVars(Scriptable scope) {
		return (Scriptable) scope.get(LOCAL_VARS, scope);
	}
	
	protected Set<String> getKeys(Scriptable scope) {
		Set<String> keys = new LinkedHashSet<String>();
		for (Object id: scope.getIds())
			if (id instanceof String)
				keys.add(id.toString());
		return keys;
	}
}

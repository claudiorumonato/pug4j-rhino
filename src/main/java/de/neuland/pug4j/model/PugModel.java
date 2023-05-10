package de.neuland.pug4j.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import de.neuland.pug4j.filter.Filter;
import de.neuland.pug4j.parser.node.MixinNode;

public class PugModel implements Map<String, Object> {

	private static final String LOCALS = "locals";
	public static final String LOCAL_VARS = "pug4j__localVars";
	public static final String PUG4J_MODEL_PREFIX = "pug4j__";
	private Deque<Scriptable> scopes = new LinkedList<Scriptable>();
	private Map<String, MixinNode> mixins = new HashMap<String, MixinNode>();
	private Map<String, Filter> filter = new HashMap<String, Filter>();

	private Context m_ctx;
	private Scriptable m_scope;
	private final boolean m_ownContext;
	
	public PugModel(Map<String, Object> defaults) {
		this(defaults, null);
	}
	
	public PugModel(Map<String, Object> defaults, Context ctx) {
		
		m_ctx = (m_ownContext = (ctx == null))
				? Context.enter()
				: ctx;
		
		m_scope = m_ctx.initStandardObjects();
		
		pushScope();
		if (defaults != null) {
			putAll(defaults);
		}

		putLocal(LOCALS, this);
	}

	public Scriptable scope() {
		return scopes.getLast();
	}

	public Context context() {
		return m_ctx;
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

	public void pushScope() {
		Scriptable scope = m_ctx.newObject(m_scope);
		scope.put(LOCAL_VARS, scope, m_ctx.newObject(m_scope));
		scopes.add(scope);
	}

	public void popScope() {
		scopes.removeLast();
	}

	public void setMixin(String name, MixinNode node) {
		mixins.put(name, node);
	}

	public MixinNode getMixin(String name) {
		return mixins.get(name);
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
	
	@Override
	public boolean containsKey(Object key) {
		for (Iterator<Scriptable> i = scopes.descendingIterator(); i.hasNext();) {
			Scriptable scope = i.next();
			if (scope.has(key.toString(), scope)) {
				return true;
			}
		}
		return false;
	}

	public boolean knowsKey(Object key) {
		for (Iterator<Scriptable> i = scopes.descendingIterator(); i.hasNext();) {
			Scriptable scope = i.next();
			Set<String> localVars = getKeys((Scriptable)scope.get(LOCAL_VARS, scope));
			if (scope.has(key.toString(), scope) || localVars.contains(key)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		for (Iterator<Scriptable> i = scopes.descendingIterator(); i.hasNext();) {
			Scriptable scope = i.next();
			Set<String> keys = getKeys(scope);
			for (String key: keys) {
				if (scope.get(key, scope).equals(value))
					return true;
			}
		}
		return false;
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (String key: keySet()) {
			map.put(key, get(key));
		}
		return map.entrySet();
	}

	@Override
	// adds the object to the highest scope
	public Object get(Object key) {
		for (Iterator<Scriptable> i = scopes.descendingIterator(); i.hasNext();) {
			Scriptable scope = i.next();
			if (scope.has(key.toString(), scope)) {
				return scope.get(key.toString(), scope);
			}
		}
		return null;
	}
	private Scriptable getScopeWithKey(Object key) {
		for (Iterator<Scriptable> i = scopes.descendingIterator(); i.hasNext();) {
			Scriptable scope = i.next();
			Set<String> localVars = getKeys((Scriptable)scope.get(LOCAL_VARS, scope));
			if (scope.has(key.toString(), scope) || localVars.contains(key)) {
				return scope;
			}
		}
		return null;
	}
	@Override
	public boolean isEmpty() {
		return keySet().isEmpty();
	}

	private Set<String> getKeys(Scriptable scope) {
		Set<String> keys = new LinkedHashSet<String>();
		for (Object id: scope.getIds())
			if (id instanceof String)
				keys.add(id.toString());
		return keys;
	}
	
	@Override
	// returns a set of unique keys
	public Set<String> keySet() {
		Set<String> keys = new HashSet<String>();
		for (Iterator<Scriptable> i = scopes.descendingIterator(); i.hasNext();) {
			keys.addAll(getKeys(i.next()));
		}
		return keys;
	}

	@Override
	// adds the object to the correct scope
	public Object put(String key, Object value) {
		Set<String> localVars= getLocalVars();
		if(localVars.contains(key)) {
			return putLocal(key, value);
		}else{
			return putGlobal(key, value);
		}
	}

	private Set<String> getLocalVars() {
		Scriptable scope = scopes.getLast();
		return getKeys((Scriptable) scope.get(LOCAL_VARS, scope));
	}

	// adds the object to the current scope
	public Object putLocal(String key, Object value) {
		Object currentValue = get(key);
		Scriptable	scope = scopes.getLast();
		scope.put(key, scope, value);
		return currentValue;
	}

	// adds the object to the scope where the variable was last defined
	public Object putGlobal(String key, Object value) {
		Object currentValue = get(key);
		Scriptable scope = getScopeWithKey(key);
		if (scope == null)
			scope = scopes.getLast();
		scope.put(key, scope, value);
		return currentValue;
	}

	@Override
	// addes all map entries to the current scope map
	public void putAll(Map<? extends String, ? extends Object> m) {
		Scriptable scope = scopes.getLast();
		for (String key: m.keySet())
			scope.put(key, scope, m.get(key));
	}

	@Override
	// removes the scopes first object with the given key
	public Object remove(Object key) {
		for (Iterator<Scriptable> i = scopes.descendingIterator(); i.hasNext();) {
			Scriptable scope = i.next();
			if (scope.has(key.toString(), scope)) {
				Object object = scope.get(key.toString(), scope);
				scope.delete(key.toString());
				return object;
			}
		}
		return null;
	}

	@Override
	// returns the size of all unique keys
	public int size() {
		return keySet().size();
	}

	@Override
	// returns the size of all unique keys
	public Collection<Object> values() {
		List<Object> values = new ArrayList<Object>();
		for (String key : keySet()) {
			values.add(get(key));
		}
		return values;
	}

	public Filter getFilter(String name) {
		return filter.get(name);
	}

	public void addFilter(String name, Filter filter) {
		this.filter.put(name, filter);
	}

	public void putLocalVariableName(String name) {
		getLocalVars().add(name);
	}
}

package de.neuland.pug4j.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.neuland.pug4j.filter.Filter;
import de.neuland.pug4j.parser.node.MixinNode;

public abstract class AbstractPugModel<T> implements Map<String, Object> {

	public static final String LOCAL_VARS = "pug4j__localVars";
	public static final String PUG4J_MODEL_PREFIX = "pug4j__";

	protected static final String LOCALS = "locals";
	
	protected Map<String, MixinNode> mixins = new HashMap<String, MixinNode>();
	protected Map<String, Filter> filter = new HashMap<String, Filter>();
	protected Deque<T> scopes = new LinkedList<T>();

	abstract protected T newScope();
	
	abstract protected Set<String> getKeys(T scope);
	
	abstract protected T fetchLocalVars(T scope);
	
	abstract protected void scopePut(T scope, String key, Object val);
	
	abstract protected Object scopeGet(T scope, String key);
	
	abstract protected boolean scopeHas(T scope, String key);
	
	abstract protected void scopeDel(T scope, String key);
	
	abstract public T scope();
	
	protected void initialize(Map<String, Object> defaults) {
		pushScope();
		if (defaults != null)
			putAll(defaults);
		putLocal(LOCALS, this);
	}
	
	protected Object scopeGet(T scope, String key, Object val) {
		return !scopeHas(scope, key) ? val : scopeGet(scope, key);
	}
	
	public boolean knowsKey(Object key) {
		for (Iterator<T> i = scopes.descendingIterator(); i.hasNext();) {
			T scope = i.next();
			Set<String> localVars = getKeys(fetchLocalVars(scope));
			if (scopeHas(scope, key.toString()) || localVars.contains(key))
				return true;
		}
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		for (Iterator<T> i = scopes.descendingIterator(); i.hasNext();) {
			T scope = i.next();
			if (scopeHas(scope, key.toString()))
				return true;
		}
		return false;
	}
	
	@Override
	public Object get(Object key) {
		for (Iterator<T> i = scopes.descendingIterator(); i.hasNext();) {
			T scope = i.next();
			if (scopeHas(scope, key.toString()))
				return scopeGet(scope, key.toString());
		}
		return null;
	}
	
	@Override
	public boolean containsValue(Object value) {
		for (Iterator<T> i = scopes.descendingIterator(); i.hasNext();) {
			T scope = i.next();
			Set<String> keys = getKeys(scope);
			for (String key: keys) {
				if (scopeGet(scope, key).equals(value))
					return true;
			}
		}
		return false;
	}

	// removes the scopes first object with the given key
	@Override
	public Object remove(Object key) {
		for (Iterator<T> i = scopes.descendingIterator(); i.hasNext();) {
			T scope = i.next();
			if (scopeHas(scope, key.toString())) {
				Object object = scopeGet(scope, key.toString());
				scopeDel(scope, key.toString());
				return object;
			}
		}
		return null;
	}
	
	// adds the object to the current scope
	public Object putLocal(String key, Object value) {
		Object currentValue = get(key);
		T scope = scopes.getLast();
		scopePut(scope, key, value);
		return currentValue;
	}
	
	// addes all map entries to the current scope map
	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		T scope = scopes.getLast();
		for (String key: m.keySet())
			scopePut(scope, key, m.get(key));
	}
	
	// adds the object to the scope where the variable was last defined
	public Object putGlobal(String key, Object value) {
		Object currentValue = get(key);
		T scope = getScopeWithKey(key);
		if (scope == null)
			scope = scopes.getLast();
		scopePut(scope, key, value);
		return currentValue;
	}

	// adds the object to the correct scope
	@Override
	public Object put(String key, Object value) {
		return (getLocalVars().contains(key))
				? putLocal(key, value)
				: putGlobal(key, value);
	}
	
	protected T getScopeWithKey(Object key) {
		for (Iterator<T> i = scopes.descendingIterator(); i.hasNext();) {
			T scope = i.next();
			Set<String> localVars = getKeys(fetchLocalVars(scope));
			if (scopeHas(scope, key.toString()) || localVars.contains(key))
				return scope;
		}
		return null;
	}


	protected Set<String> getLocalVars() {
		return getKeys(fetchLocalVars(scopes.getLast()));
	}
	
	public void putLocalVariableName(String name) {
		getLocalVars().add(name);
	}

	public void pushScope() {
		scopes.add(newScope());
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

	// returns a set of unique keys
	@Override
	public Set<String> keySet() {
		Set<String> keys = new HashSet<String>();
		for (Iterator<T> i = scopes.descendingIterator(); i.hasNext();) {
			keys.addAll(getKeys(i.next()));
		}
		return keys;
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
	public boolean isEmpty() {
		return keySet().isEmpty();
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
}

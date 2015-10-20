package de.neuland.jade4j.lexer.token;

import java.util.LinkedHashMap;
import java.util.Map;

import de.neuland.jade4j.parser.node.ExpressionString;

public class Attribute extends Token {
	private Map<String, Object> attributes = new LinkedHashMap<String, Object>();

	public Attribute(String value, int lineNumber) {
		super(value, lineNumber);
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void addAttribute(String name, String value) {
		attributes.put(name, value);
	}

	public void addExpressionAttribute(String name, String expression, boolean escapedAttr) {
		ExpressionString value = new ExpressionString(expression);
		value.setEscape(escapedAttr);
		attributes.put(name, value);
	}

	public void addBooleanAttribute(String name, Boolean value) {
		attributes.put(name, value);
	}

}

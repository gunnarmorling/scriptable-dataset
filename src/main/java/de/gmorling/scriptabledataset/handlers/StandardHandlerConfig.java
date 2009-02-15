package de.gmorling.scriptabledataset.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages handlers, that shall always executed for scripts in a given language.
 * These standard handlers will be called <b>before<b> any custom handlers in
 * the pre invocation and <b>after<b> any custom handlers in the post
 * invocation.
 * 
 * @author Gunnar Morling
 */
public class StandardHandlerConfig {

	private static Map<String, List<Class<? extends ScriptInvocationHandler>>> standardHandlers;

	static {

		standardHandlers = new HashMap<String, List<Class<? extends ScriptInvocationHandler>>>();

		List<Class<? extends ScriptInvocationHandler>> jRubyStandardHandlers = new ArrayList<Class<? extends ScriptInvocationHandler>>();
		jRubyStandardHandlers.add(JRubyScriptInvocationHandler.class);

		standardHandlers.put("jruby", jRubyStandardHandlers);
	}

	public static List<Class<? extends ScriptInvocationHandler>> getHandlerClassesByLanguage(String language) {

		if (standardHandlers.containsKey(language)) {
			List<Class<? extends ScriptInvocationHandler>> theValue = new ArrayList<Class<? extends ScriptInvocationHandler>>();
			theValue.addAll(standardHandlers.get(language));

			return theValue;
		}
		else {
			return Collections.emptyList();
		}
	}
}

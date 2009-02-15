package de.gmorling.scriptabledataset.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

package de.gmorling.scriptabledataset.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Manages handlers, that shall always executed for scripts in a given language.
 * These standard handlers will be called <b>before</b> any custom handlers in
 * the pre invocation and <b>after</b> any custom handlers in the post
 * invocation.
 * 
 * @author Gunnar Morling
 */
public class StandardHandlerConfig {

	private static Map<String, List<ScriptInvocationHandler>> standardHandlers;

	static {

		standardHandlers = new HashMap<String, List<ScriptInvocationHandler>>();

		ServiceLoader<ScriptInvocationHandler> serviceLoader = ServiceLoader.load(ScriptInvocationHandler.class);
		Iterator<ScriptInvocationHandler> iterator = serviceLoader.iterator();
		
		while (iterator.hasNext()) {
			
			ScriptInvocationHandler scriptInvocationHandler = iterator.next();
			
			List<ScriptInvocationHandler> handlersForLanguage = standardHandlers.get(scriptInvocationHandler.getLanguageName());
			
			if(handlersForLanguage == null) {
				handlersForLanguage = new ArrayList<ScriptInvocationHandler>();
				standardHandlers.put(scriptInvocationHandler.getLanguageName(), handlersForLanguage);
			}
			
			handlersForLanguage.add(scriptInvocationHandler);
		}
	}

	public static List<ScriptInvocationHandler> getStandardHandlersByLanguage(String language) {
		
		if (standardHandlers.containsKey(language)) {
			return new ArrayList<ScriptInvocationHandler>(standardHandlers.get(language));
		}
		else {
			return Collections.emptyList();
		}
	}
}
/*
 * Copyright 2008-2009, Gunnar Morling
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.gmorling.scriptabledataset.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		
		try {
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
		catch(ServiceConfigurationError error) {
			
			Logger logger = LoggerFactory.getLogger(StandardHandlerConfig.class);
			logger.error("Loading of standard script invocation handlers failed, most likely due to an unknown handler implementation given in META-INF/services" + ScriptInvocationHandler.class.getName());
			standardHandlers = Collections.emptyMap();
		}
	}

	public static List<ScriptInvocationHandler> getStandardHandlersByLanguage(String language) {
		
		if (standardHandlers.containsKey(language)) {
			return Collections.unmodifiableList(standardHandlers.get(language));
		}
		else {
			return Collections.emptyList();
		}
	}
}
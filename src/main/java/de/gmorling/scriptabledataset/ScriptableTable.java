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
package de.gmorling.scriptabledataset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import de.gmorling.scriptabledataset.handlers.ScriptInvocationHandler;
import de.gmorling.scriptabledataset.handlers.StandardHandlerConfig;

/**
 * ITable implementation, that allows the usage of script statements as field
 * values.
 * 
 * @author Gunnar Morling
 */
public class ScriptableTable implements ITable {

	private final Logger logger = LoggerFactory.getLogger(ScriptableTable.class);

	private ITable wrapped;

	private Map<String, ScriptEngine> enginesByPrefix = new HashMap<String, ScriptEngine>();

	private Map<String, List<ScriptInvocationHandler>> handlersByPrefix = new HashMap<String, List<ScriptInvocationHandler>>();

	/**
	 * Creates a new ScriptableTable.
	 * 
	 * @param wrapped
	 *            The ITable to be wrapped by this scriptable table. May not be
	 *            null.
	 * @param configurations
	 *            An list with configurations
	 */
	public ScriptableTable(ITable wrapped, List<ScriptableDataSetConfig> configurations) {

		this.wrapped = wrapped;

		ScriptEngineManager manager = new ScriptEngineManager();

		// load the engines
		for (ScriptableDataSetConfig oneConfig : configurations) {

			ScriptEngine engine = manager.getEngineByName(oneConfig.getLanguageName());

			if (engine != null) {
				enginesByPrefix.put(oneConfig.getPrefix(), engine);
				
				List<ScriptInvocationHandler> handlers = getAllHandlers(oneConfig);

				for (ScriptInvocationHandler oneHandler : handlers) {
					oneHandler.setScriptEngine(engine);
				}
				
				handlersByPrefix.put(oneConfig.getPrefix(), handlers);
				
				logger.info("Registered scripting engine {} for language {}.", engine, oneConfig.getLanguageName());
			}
			else {
				throw new RuntimeException("No scripting engine found for language \"" + oneConfig.getLanguageName() + "\".");
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int getRowCount() {

		return wrapped.getRowCount();
	}

	/**
	 * {@inheritDoc}
	 */
	public ITableMetaData getTableMetaData() {

		return wrapped.getTableMetaData();
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getValue(int row, String column) throws DataSetException {

		Object theValue = wrapped.getValue(row, column);

		// only strings can be processed
		if (theValue instanceof String) {
			String script = (String) theValue;

			for (Entry<String, ScriptEngine> oneEntry : enginesByPrefix.entrySet()) {

				String prefix = oneEntry.getKey();

				// found engine for prefix
				if (script.startsWith(prefix)) {

					ScriptEngine engine = oneEntry.getValue();
					script = script.substring(prefix.length());

					List<ScriptInvocationHandler> handlers = handlersByPrefix.get(oneEntry.getKey());

					try {

						//preInvoke
						for (ScriptInvocationHandler handler : handlers) {
							script = handler.preInvoke(script);
						}

						logger.debug("Executing script: {}", script);

						// the actual script evaluation
						theValue = engine.eval(script);

						// call postInvoke in reversed order
						Collections.reverse(handlers);
						for (ScriptInvocationHandler handler : handlers) {
							theValue = handler.postInvoke(theValue);
						}

					}
					catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}

		return theValue;
	}

	/**
	 * Returns a list with all standard handlers registered for the language of
	 * the config and all handlers declared in the config itself.
	 * 
	 * @param config
	 *            A config object.
	 * @return A list with handlers. Never null.
	 */
	private List<ScriptInvocationHandler> getAllHandlers(ScriptableDataSetConfig config) {

		List<ScriptInvocationHandler> theValue = new ArrayList<ScriptInvocationHandler>();

		// standard handlers for the language
		theValue.addAll(StandardHandlerConfig.getStandardHandlersByLanguage(config.getLanguageName()));

		// custom handlers
		theValue.addAll(config.getHandlers());

		return theValue;
	}
}
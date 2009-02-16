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

	private Map<String, ScriptEngine> engines = new HashMap<String, ScriptEngine>();

	private Map<String, ScriptableDataSetConfig> handlers = new HashMap<String, ScriptableDataSetConfig>();

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

			String engineName = oneConfig.getLanguageName();
			ScriptEngine engine = manager.getEngineByName(engineName);

			if (engine != null) {
				engines.put(oneConfig.getPrefix(), engine);
				handlers.put(oneConfig.getPrefix(), oneConfig);

				logger.info("Registered scripting engine {} for language {}.", engine, oneConfig.getLanguageName());
			}
			else {
				throw new RuntimeException("No scripting engine found for language \"" + engineName + "\".");
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

			for (Entry<String, ScriptableDataSetConfig> oneEntry : handlers.entrySet()) {

				String prefix = oneEntry.getKey();

				// found engine for prefix
				if (script.startsWith(prefix)) {

					ScriptEngine engine = engines.get(oneEntry.getKey());
					script = script.substring(prefix.length());

					List<Class<? extends ScriptInvocationHandler>> handlerClasses = getHandlerClasses(oneEntry
							.getValue());

					List<ScriptInvocationHandler> handlers = new ArrayList<ScriptInvocationHandler>(handlerClasses
							.size());
					try {

						// instantiate the handlers and call preInvoke
						for (Class<? extends ScriptInvocationHandler> handlerClass : handlerClasses) {

							ScriptInvocationHandler handler = handlerClass.newInstance();
							handler.setScriptEngine(engine);
							handlers.add(handler);

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
						e.printStackTrace();
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
	private List<Class<? extends ScriptInvocationHandler>> getHandlerClasses(ScriptableDataSetConfig config) {

		List<Class<? extends ScriptInvocationHandler>> theValue = new ArrayList<Class<? extends ScriptInvocationHandler>>();

		// standard handlers for the language
		theValue.addAll(StandardHandlerConfig.getHandlerClassesByLanguage(config.getLanguageName()));

		// custom handlers
		theValue.addAll(config.getHandlerClasses());

		return theValue;
	}
}

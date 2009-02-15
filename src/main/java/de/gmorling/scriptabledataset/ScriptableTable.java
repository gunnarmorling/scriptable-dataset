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

public class ScriptableTable implements ITable {

	private final Logger logger = LoggerFactory.getLogger(ScriptableTable.class);

	private ITable wrapped;

	private Map<String, ScriptEngine> engines = new HashMap<String, ScriptEngine>();

	private Map<String, ScriptableDataSetConfig> handlers = new HashMap<String, ScriptableDataSetConfig>();

	public ScriptableTable(ITable wrapped, ScriptableDataSetConfig[] configurations) {

		this.wrapped = wrapped;

		ScriptEngineManager manager = new ScriptEngineManager();

		for (ScriptableDataSetConfig oneConfig : configurations) {

			String engineName = oneConfig.getLanguageName();
			ScriptEngine engine = manager.getEngineByName(engineName);

			if (engine != null) {
				engines.put(oneConfig.getPrefix(), engine);
				handlers.put(oneConfig.getPrefix(), oneConfig);
			}
			else {
				logger.warn("No scripting engine found for name \"{}\"", engineName);
			}
		}
	}

	public int getRowCount() {

		return wrapped.getRowCount();
	}

	public ITableMetaData getTableMetaData() {

		return wrapped.getTableMetaData();
	}

	public Object getValue(int row, String column) throws DataSetException {

		Object value = wrapped.getValue(row, column);

		if (value instanceof String) {
			String script = (String) value;

			for (Entry<String, ScriptableDataSetConfig> oneEntry : handlers.entrySet()) {

				String prefix = oneEntry.getKey();
				if (script.startsWith(prefix)) {

					ScriptEngine engine = engines.get(oneEntry.getKey());

					script = script.substring(prefix.length());

					try {

						// standard handlers for the language
						List<Class<? extends ScriptInvocationHandler>> handlerClasses = StandardHandlerConfig
								.getHandlerClassesByLanguage(oneEntry.getValue().getLanguageName());

						handlerClasses.addAll(oneEntry.getValue().getHandlerClasses());

						List<ScriptInvocationHandler> handlers = new ArrayList<ScriptInvocationHandler>(handlerClasses
								.size());

						for (Class<? extends ScriptInvocationHandler> handlerClass : handlerClasses) {

							ScriptInvocationHandler handler = handlerClass.newInstance();
							handler.setScriptEngine(engine);
							handlers.add(handler);

							script = handler.preInvoke(script);
						}

						Object theValue = engine.eval(script);

						Collections.reverse(handlers);
						for (ScriptInvocationHandler handler : handlers) {
							theValue = handler.postInvoke(theValue);
						}

						return theValue;

					}
					catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}

		}

		return value;
	}

}

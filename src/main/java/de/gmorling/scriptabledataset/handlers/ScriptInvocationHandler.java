package de.gmorling.scriptabledataset.handlers;

import javax.script.ScriptEngine;

/**
 * Implementations can be registered with a ScriptableDataSet to be called
 * before and after script contained in a data set field is executed. This can
 * be used to add commonly used import statements for all scripts of a given
 * language or to post-process the result of a script execution.
 * 
 * @author Gunnar Morling
 */
public interface ScriptInvocationHandler {

	/**
	 * Will be called before a script contained in a field of a data set is
	 * executed.
	 * 
	 * @param script
	 *            The script to be executed.
	 * @return The script to be executed, enriched with common imports for
	 *         example.
	 */
	String preInvoke(String script);

	/**
	 * Will be called after a script contained in a field of a data set is
	 * executed.
	 * 
	 * @param object
	 *            The result of the script execution.
	 * @return The result of the script execution, possibly modified by this
	 *         handler.
	 */
	Object postInvoke(Object object);

	/**
	 * Makes the scripting engine available to handler implementations.
	 * 
	 * @param engine
	 *            The scripting engine used to execute the current script.
	 */
	void setScriptEngine(ScriptEngine engine);
}

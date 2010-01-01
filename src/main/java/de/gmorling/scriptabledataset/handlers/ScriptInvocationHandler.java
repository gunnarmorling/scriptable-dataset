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

import javax.script.ScriptEngine;

/**
 * <p>Implementations can be registered with a ScriptableDataSet to be called
 * before and after script contained in a data set field is executed. This can
 * be used to add commonly used import statements for all scripts of a given
 * language or to post-process the result of a script execution.</p>
 * <p>
 * Implementations must define a default constructor, if they shall be used as
 * standard handler for a language.
 * </p>
 * 
 * @author Gunnar Morling
 */
public interface ScriptInvocationHandler {

	/**
	 * Must return the name of the scripting language for which this handler
	 * can be registered, as expected by the JSR 223 scripting engine manager,
	 * e.g. "jruby".
	 * 
	 * @return The name of the scripting language.
	 */
	String getLanguageName();
	
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

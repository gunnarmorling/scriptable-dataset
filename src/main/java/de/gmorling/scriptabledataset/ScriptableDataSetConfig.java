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
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import de.gmorling.scriptabledataset.handlers.ScriptInvocationHandler;

/**
 * Configures the usage of one scripting language with a prefix and a list of
 * invocation handlers.
 * 
 * @author Gunnar Morling
 */
public class ScriptableDataSetConfig {

	private final String prefix;

	private final String languageName;

	private final List<ScriptInvocationHandler> handlers = new ArrayList<ScriptInvocationHandler>();

	/**
	 * Creates a new ScriptableDataSetConfig.
	 * 
	 * @param languageName
	 *            The name of the language as expected by the JSR 223 scripting
	 *            engine manager, e.g. "jruby". May not be null.
	 * @param prefix
	 *            A prefix, which shall precede fields in a ScriptableDataSet in
	 *            that language, e.g. "jruby:". May not be null.
	 */
	public ScriptableDataSetConfig(String languageName, String prefix) {

		this(languageName, prefix, null);
	}
	
	/**
	 * Creates a new ScriptableDataSetConfig.
	 * 
	 * @param languageName
	 *            The name of the language as expected by the JSR 223 scripting
	 *            engine manager, e.g. "jruby". May not be null.
	 * @param prefix
	 *            A prefix, which shall precede fields in a ScriptableDataSet in
	 *            that language, e.g. "jruby:". May not be null.
	 * @param handlers
	 *            An optional list with handlers to be applied for fields with
	 *            the given prefix.
	 */
	public ScriptableDataSetConfig(String languageName, String prefix,
			List<ScriptInvocationHandler> handlers) {

		super();

		Validate.notNull(languageName);
		Validate.notNull(prefix);

		this.prefix = prefix;
		this.languageName = languageName;

		if (handlers != null) {
			this.handlers.addAll(handlers);
		}
	}

	public String getPrefix() {

		return prefix;
	}

	public String getLanguageName() {

		return languageName;
	}

	public List<ScriptInvocationHandler> getHandlers() {

		return handlers;
	}

	@Override
	public String toString() {

		return ReflectionToStringBuilder.toString(this);
	}

}

package de.gmorling.scriptabledataset;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import de.gmorling.scriptabledataset.handlers.ScriptInvocationHandler;

public class ScriptableDataSetConfig {

	private final String prefix;

	private final String languageName;

	private final List<Class<? extends ScriptInvocationHandler>> handlerClasses = new ArrayList<Class<? extends ScriptInvocationHandler>>();

	/**
	 * Creates a new ScriptableDataSetConfig.
	 * 
	 * @param languageName
	 *            The name of the language as expected by the JSR 223 scripting
	 *            engine manager, e.g. "jruby". May not be null.
	 * @param prefix
	 *            A prefix, which shall precede fields in a ScriptableDataSet in
	 *            that language, e.g. "jruby:". May not be null.
	 * @param handlerClasses
	 *            An optional list with handler classes to be applied for fields
	 *            with the given prefix.
	 */
	public ScriptableDataSetConfig(String languageName, String prefix,
			List<Class<? extends ScriptInvocationHandler>> handlerClasses) {

		super();

		Validate.notNull(languageName);
		Validate.notNull(prefix);

		this.prefix = prefix;
		this.languageName = languageName;

		if (handlerClasses != null) {
			this.handlerClasses.addAll(handlerClasses);
		}
	}

	public String getPrefix() {

		return prefix;
	}

	public String getLanguageName() {

		return languageName;
	}

	public List<Class<? extends ScriptInvocationHandler>> getHandlerClasses() {

		return handlerClasses;
	}

	@Override
	public String toString() {

		return ReflectionToStringBuilder.toString(this);
	}

}

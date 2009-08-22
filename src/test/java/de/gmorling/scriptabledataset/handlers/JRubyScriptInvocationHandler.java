package de.gmorling.scriptabledataset.handlers;

import javax.script.Invocable;
import javax.script.ScriptEngine;

import org.jruby.RubyObject;

import de.gmorling.scriptabledataset.handlers.ScriptInvocationHandler;

/**
 * A <code>ScriptInvocationHandler</code> for JRuby scripts. It adds some
 * commonly used imports to the begin of each script and converts Objects
 * returned by the scripting engine into types processable by DBUnit.
 * 
 * @author Gunnar Morling
 * 
 */
public class JRubyScriptInvocationHandler implements ScriptInvocationHandler {

	private ScriptEngine engine;

	public String getLanguageName() {
		return "jruby";
	}
	
	public String preInvoke(String script) {
		return "require 'date';" + script;
	}

	public Object postInvoke(Object object) {

		if (object instanceof RubyObject) {

			Invocable i = (Invocable) engine;

			RubyObject rubyObject = (RubyObject) object;
			if (rubyObject.getMetaClass().getName().equals("DateTime")) {

				try {
					object = i.invokeMethod(object, "strftime", "%Y-%m-%d");
//					object = i.invokeMethod(object, "to_i");
//					object = new Date(Long.parseLong(object.toString()));

				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}

		return object;
	}

	public void setScriptEngine(ScriptEngine engine) {

		this.engine = engine;
	}

}
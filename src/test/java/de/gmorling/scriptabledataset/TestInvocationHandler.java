package de.gmorling.scriptabledataset;

import javax.script.ScriptEngine;

import de.gmorling.scriptabledataset.handlers.ScriptInvocationHandler;

public class TestInvocationHandler implements ScriptInvocationHandler {

	public String preInvoke(String script) {

		return "require 'bigdecimal'; require 'bigdecimal/math'; include BigMath; " + script;
	}

	public Object postInvoke(Object object) {

		return object;
	}

	public void setScriptEngine(ScriptEngine engine) {

	}

}
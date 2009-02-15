package de.gmorling.scriptabledataset.handlers;

import javax.script.ScriptEngine;

public interface ScriptInvocationHandler {

	String preInvoke(String script);

	Object postInvoke(Object object);

	void setScriptEngine(ScriptEngine engine);
}

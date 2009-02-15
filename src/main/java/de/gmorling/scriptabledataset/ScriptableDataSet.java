package de.gmorling.scriptabledataset;

import org.apache.commons.lang.Validate;
import org.dbunit.dataset.AbstractDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITableIterator;

/**
 * An implementation of an DBUnit <code>IDataSet</code>, that allows the use of
 * script expressions in its fields, as in the following example:
 * 
 * <pre>
 * &lt;dataset&gt;
 *     &lt;location num=&quot;jruby:12&quot; addr=&quot;jruby:'Webster Street'&quot; date=&quot;jruby:DateTime::now() - 14&quot;/&gt;
 * &lt;/dataset&gt;
 * </pre>
 * 
 * In order to use a certain scripting language, an JSR 223 compatible engine
 * has to exist for that language.
 * 
 * @author Gunnar Morling
 * 
 */
public class ScriptableDataSet extends AbstractDataSet {

	private IDataSet wrapped;

	private ScriptableDataSetConfig[] configurations;

	public ScriptableDataSet(IDataSet wrapped, ScriptableDataSetConfig... configurations) {

		Validate.notNull(wrapped);
		Validate.notNull(configurations);
		Validate.noNullElements(configurations);

		this.wrapped = wrapped;
		this.configurations = configurations;
	}

	@Override
	protected ITableIterator createIterator(boolean reversed) throws DataSetException {

		return new ScriptableIterator(reversed ? wrapped.reverseIterator() : wrapped.iterator(), configurations);
	}

}

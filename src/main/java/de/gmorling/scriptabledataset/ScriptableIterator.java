package de.gmorling.scriptabledataset;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;

public class ScriptableIterator implements ITableIterator {

	private ITableIterator wrapped;

	private ScriptableDataSetConfig[] configurations;

	public ScriptableIterator(ITableIterator wrapped, ScriptableDataSetConfig[] configurations) {

		this.wrapped = wrapped;
		this.configurations = configurations;
	}

	public ITable getTable() throws DataSetException {

		return new ScriptableTable(wrapped.getTable(), configurations);
	}

	public ITableMetaData getTableMetaData() throws DataSetException {

		return wrapped.getTableMetaData();
	}

	public boolean next() throws DataSetException {

		return wrapped.next();
	}

}

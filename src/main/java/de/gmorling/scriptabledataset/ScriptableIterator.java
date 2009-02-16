package de.gmorling.scriptabledataset;

import java.util.List;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;

public class ScriptableIterator implements ITableIterator {

	private ITableIterator wrapped;

	private List<ScriptableDataSetConfig> configurations;

	public ScriptableIterator(ITableIterator wrapped, List<ScriptableDataSetConfig> configurations) {

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

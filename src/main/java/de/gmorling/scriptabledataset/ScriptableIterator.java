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

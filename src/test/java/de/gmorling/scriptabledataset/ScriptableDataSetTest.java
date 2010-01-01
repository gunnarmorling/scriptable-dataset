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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.gmorling.scriptabledataset.handlers.JRubyImportAddingInvocationHandler;
import de.gmorling.scriptabledataset.handlers.ScriptInvocationHandler;

/**
 * Test for ScriptableDataSet.
 * 
 * @author Gunnar Morling
 * 
 */
public class ScriptableDataSetTest {

	private static Connection connection;

	private static IDatabaseConnection dbUnitConnection;

	private ResultSet resultSet;

	@BeforeClass
	public static void initializeConnection() throws Exception {
		
		connection = DriverManager.getConnection("jdbc:derby:derbyTest;create=true");
		connection.setAutoCommit(false);
		
		dbUnitConnection = new DatabaseConnection(connection);
	}
	
	@Before
	public void createTable() throws Exception {

		connection.createStatement().execute("create table location(num int, addr varchar(40), date timestamp)");
	}

	@After
	public void rollbackTransaction() throws Exception {

		if(resultSet != null) {
			resultSet.close();
		}
		
		connection.rollback();
	}
	
	@AfterClass
	public static void closeConnection() throws Exception {
		
		dbUnitConnection.close();
	}

	/**
	 * Test for using JRuby as scripting language.
	 * 
	 * @throws Exception
	 *             In case of any error.
	 */
	@Test
	public void jRubyScript() throws Exception {

		IDataSet dataSet = new ScriptableDataSet(
			new FlatXmlDataSet(ScriptableDataSetTest.class.getResourceAsStream("jruby.xml")),
			new ScriptableDataSetConfig("jruby", "jruby:"));
		
		insertDataSetAndCreateResultSet(dataSet);
		
		assertNextRow(resultSet, 6, "teertS retsbeW", addDaysToToday(-14));
	}
	
	/**
	 * Test for using Groovy as scripting language.
	 * 
	 * @throws Exception
	 *             In case of any error.
	 */
	@Test
	public void groovyScript() throws Exception {

		IDataSet dataSet = new ScriptableDataSet(
			new FlatXmlDataSet(ScriptableDataSetTest.class.getResourceAsStream("groovy.xml")), 
			new ScriptableDataSetConfig("groovy", "groovy:"));
		
		insertDataSetAndCreateResultSet(dataSet);
		
		assertNextRow(resultSet, 6, "teertS retsbeW", addDaysToToday(-14));
	}
	
	/**
	 * Test for using JRuby and Groovy within one data set file.
	 * 
	 * @throws Exception
	 *             In case of any error.
	 */
	@Test
	public void dataSetWithMultipleLanguages() throws Exception {
		
		IDataSet dataSet = new ScriptableDataSet(
			new FlatXmlDataSet(ScriptableDataSetTest.class.getResourceAsStream("multiple_languages.xml")),
			new ScriptableDataSetConfig("jruby", "jruby:"),
			new ScriptableDataSetConfig("groovy", "groovy:"));

		insertDataSetAndCreateResultSet(dataSet);

		assertNextRow(resultSet, 6, "teertS retsbeW", addDaysToToday(-14));
		assertNextRow(resultSet, 6, "teertS retsbeW", addDaysToToday(-14));
	}

	/**
	 * Test for using JRuby as scripting language in conjunction with a special
	 * invocation handler.
	 * 
	 * @throws Exception
	 *             In case of any error.
	 */
	@Test
	public void customHandler() throws Exception {

		List<ScriptInvocationHandler> handlers = new ArrayList<ScriptInvocationHandler>();
		handlers.add(new JRubyImportAddingInvocationHandler());
		
		IDataSet dataSet = new ScriptableDataSet(
			new FlatXmlDataSet(ScriptableDataSetTest.class.getResourceAsStream("customhandler.xml")),
			new ScriptableDataSetConfig("jruby", "jruby:", handlers));

		insertDataSetAndCreateResultSet(dataSet);

		assertNextRow(resultSet, 1, "Webster Street", addDaysToToday(-14));
	}

	/**
	 * Test for usage of an unknown scripting engine.
	 * 
	 * @throws Exception
	 *             In case of any error.
	 */
	@Test(expected = RuntimeException.class)
	public void unknownScriptingEngine() throws Exception {

		IDataSet dataSet = new ScriptableDataSet(
			new FlatXmlDataSet(ScriptableDataSetTest.class.getResourceAsStream("unknownscriptingengine.xml")),
			new ScriptableDataSetConfig("unknown", "unknown:"));

		DatabaseOperation.INSERT.execute(dbUnitConnection, dataSet);
	}
	
	private void insertDataSetAndCreateResultSet(IDataSet dataSet)
			throws DatabaseUnitException, SQLException {
		
		DatabaseOperation.INSERT.execute(dbUnitConnection, dataSet);
		
		resultSet = connection.createStatement().executeQuery("SELECT num, addr, date FROM location ORDER BY num");
	}

	private void assertNextRow(ResultSet rs, int expectedInt, String expectedString, Date expectedDate) throws SQLException {

		if (!rs.next())
			fail("Data set should have a row.");

		assertEquals(expectedInt, rs.getObject(1));
		assertEquals(expectedString, rs.getObject(2));
		assertEquals(DateUtils.truncate(expectedDate, Calendar.DATE), DateUtils.truncate(rs.getObject(3), Calendar.DATE));
	}
	
	private Date addDaysToToday(int numberOfDays) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, numberOfDays);

		return calendar.getTime();
	}
}
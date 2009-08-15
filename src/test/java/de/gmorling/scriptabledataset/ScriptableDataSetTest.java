package de.gmorling.scriptabledataset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.time.DateUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.gmorling.scriptabledataset.handlers.ScriptInvocationHandler;

/**
 * Test for ScriptableDataSet.
 * 
 * @author Gunnar Morling
 * 
 */
public class ScriptableDataSetTest {

	private Connection connection;

	private IDatabaseConnection dbUnitConnection;

	@Before
	public void setUp() throws Exception {

		Logger logger = Logger.getLogger("de.gmorling.scriptabledataset");
		logger.setLevel(Level.CONFIG);

		connection = DriverManager.getConnection("jdbc:derby:derbyTest;create=true");
		connection.setAutoCommit(false);
		dbUnitConnection = new DatabaseConnection(connection);

		connection.createStatement().execute("create table location(num int, addr varchar(40), date timestamp)");
	}

	@After
	public void tearDown() throws Exception {

		connection.rollback();
		connection.close();
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

		IDataSet dataSet = new ScriptableDataSet(new FlatXmlDataSet(ScriptableDataSetTest.class
				.getResourceAsStream("jruby.xml")), new ScriptableDataSetConfig("jruby", "jruby:"));

		DatabaseOperation.INSERT.execute(dbUnitConnection, dataSet);

		ResultSet rs = connection.createStatement().executeQuery("SELECT num, addr, date FROM location ORDER BY num");

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -14);

		if (!rs.next())
			fail("Data set should have a row.");

		assertEquals(6, rs.getObject(1));
		assertEquals("teertS retsbeW", rs.getObject(2));
		assertEquals(DateUtils.truncate(calendar.getTime(), Calendar.DATE), rs.getObject(3));

		rs.close();
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

		List<Class<? extends ScriptInvocationHandler>> handlers = new ArrayList<Class<? extends ScriptInvocationHandler>>();
		handlers.add(TestInvocationHandler.class);
		IDataSet dataSet = new ScriptableDataSet(new FlatXmlDataSet(ScriptableDataSetTest.class
				.getResourceAsStream("customhandler.xml")), new ScriptableDataSetConfig("jruby", "jruby:", handlers));

		DatabaseOperation.INSERT.execute(dbUnitConnection, dataSet);

		ResultSet rs = connection.createStatement().executeQuery("SELECT num, addr, date FROM location ORDER BY num");

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -14);

		if (!rs.next())
			fail("Data set should have a row.");

		// enabled by import from custom handler
		assertEquals(1, rs.getObject(1));
		assertEquals("Webster Street", rs.getObject(2));

		// enabled by import from JRuby standard handler
		assertEquals(DateUtils.truncate(calendar.getTime(), Calendar.DATE), rs.getObject(3));

		rs.close();
	}
	
	
	/**
	 * Test for using Groovy as scripting language.
	 * 
	 * @throws Exception
	 *             In case of any error.
	 */
	@Test
	public void groovyScript() throws Exception {

		IDataSet dataSet = new ScriptableDataSet(new FlatXmlDataSet(ScriptableDataSetTest.class
				.getResourceAsStream("groovy.xml")), new ScriptableDataSetConfig("groovy", "groovy:", null));

		DatabaseOperation.INSERT.execute(dbUnitConnection, dataSet);

		ResultSet rs = connection.createStatement().executeQuery("SELECT num, addr, date FROM location ORDER BY num");

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -4);

		if (!rs.next())
			fail("Data set should have a row.");

		assertEquals(6, rs.getObject(1));
		assertEquals("teertS retsbeW", rs.getObject(2));
		assertEquals(DateUtils.truncate(calendar.getTime(), Calendar.DATE), 
				DateUtils.truncate(rs.getObject(3), Calendar.DATE));

		rs.close();
	}
	

	/**
	 * Test for usage of an unknown scripting engine.
	 * 
	 * @throws Exception
	 *             In case of any error.
	 */
	@Test(expected = RuntimeException.class)
	public void unknownScriptingEngine() throws Exception {

		IDataSet dataSet = new ScriptableDataSet(new FlatXmlDataSet(ScriptableDataSetTest.class
				.getResourceAsStream("unknownscriptingengine.xml")), new ScriptableDataSetConfig("unknown", "unknown:"));

		DatabaseOperation.INSERT.execute(dbUnitConnection, dataSet);
	}
}

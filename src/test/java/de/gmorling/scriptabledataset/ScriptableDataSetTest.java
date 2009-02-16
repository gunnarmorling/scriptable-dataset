package de.gmorling.scriptabledataset;

import static org.junit.Assert.*;

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

	@Test
	public void jRubyScript() throws Exception {

		IDataSet dataSet = new ScriptableDataSet(new FlatXmlDataSet(ScriptableDataSetTest.class
				.getResourceAsStream("jruby.xml")), new ScriptableDataSetConfig("jruby", "jruby:", null));

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

		assertEquals(1, rs.getObject(1));
		assertEquals("Webster Street", rs.getObject(2));
		assertEquals(DateUtils.truncate(calendar.getTime(), Calendar.DATE), rs.getObject(3));

		rs.close();
	}

	@Test(expected = RuntimeException.class)
	public void unknownScriptingEngine() throws Exception {

		IDataSet dataSet = new ScriptableDataSet(new FlatXmlDataSet(ScriptableDataSetTest.class
				.getResourceAsStream("unknownscriptingengine.xml")), new ScriptableDataSetConfig("unknown", "unknown:",
				null));

		DatabaseOperation.INSERT.execute(dbUnitConnection, dataSet);

	}
}

package de.gmorling.scriptabledataset;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;

import org.apache.commons.lang.time.DateUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;

public class ScriptableDataSetTest {

	private static String protocol = "jdbc:derby:";

	@Test
	public void testScript() throws Exception {

		Connection conn = null;

		conn = DriverManager.getConnection(protocol + "derbyTest" + ";create=true");

		conn.setAutoCommit(false);

		Statement s = conn.createStatement();

		s.execute("create table location(num int, addr varchar(40), date timestamp)");

		IDatabaseConnection connection = new DatabaseConnection(conn);

		IDataSet dataSet = new ScriptableDataSet(new FlatXmlDataSet(ScriptableDataSetTest.class
				.getResourceAsStream("dataset.xml")), new ScriptableDataSetConfig("jruby", "jruby:", null));

		DatabaseOperation.INSERT.execute(connection, dataSet);

		ResultSet rs = s.executeQuery("SELECT num, addr, date FROM location ORDER BY num");

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -14);

		if (!rs.next())
			fail("Data set should have rows");

		assertEquals(12, rs.getObject(1));
		assertEquals("Webster Street", rs.getObject(2));
		assertEquals(DateUtils.truncate(calendar.getTime(), Calendar.DATE), rs.getObject(3));

		rs.close();
		conn.rollback();
		conn.close();
		connection.close();
	}
}

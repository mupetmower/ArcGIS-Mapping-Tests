package arcGISMapTesting4;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class PostgreSQLConnector {
	
	private String connString = "jdbc:postgresql://192.168.248.134:5432/PostGIS";
	private String user = "postgres";
	private String password = "1nt3rACT";
	
	private Connection conn;
	
	private DSLContext create;
	
	
	public PostgreSQLConnector() throws SQLException, ClassNotFoundException {
		initConnection();
		initJOOQ();
		
	}
	
	public Connection initConnection() throws SQLException, ClassNotFoundException {
		//Class.forName("org.postgresql.Driver");
		conn = DriverManager.getConnection(connString, user, password);
		return conn;
	}
	
	public DSLContext initJOOQ() {
		create = DSL.using(conn, SQLDialect.POSTGRES);
		return create;
	}
	
	public Result<Record> selectAllFrom(String table) {
		Result<Record> result = create.select().from(table).fetch();
		return result;
	}
	
}

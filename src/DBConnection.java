import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	private static String connectionurl = "jdbc:postgresql://localhost/postgres";
	private static String username = "postgres";
	private static String password = "201701439";
	private static String driverclass = "org.postgresql.Driver";

	public static Connection getConnection() {
		Connection conn = null;

		try {
			Class.forName(driverclass);// Creating a instance/loading a class
			// just loads the driver class into this class
			conn = DriverManager.getConnection(connectionurl, username, password);// connecting the database

			conn.setSchema("dbms");
			
			System.out.println("Schema : "+conn.getSchema());
			
			if (conn != null) {
				System.out.println("Connected with database!");
			} else {
				System.out.println("Cannot connect with database!");
			}
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;

	}

	public static void main(String[] args) {

		getConnection();

	}
}

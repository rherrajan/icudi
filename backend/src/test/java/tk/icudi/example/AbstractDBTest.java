//package tk.icudi.example;
//
//import javax.sql.DataSource;
//
//import org.junit.Before;
//
//public class AbstractDBTest {
//
//	private DataSource dataSource;
//
//	@Before
//	public void provideDatabase() throws Exception {
//		DBProvider dbProvider = new DBProvider();
//		dbProvider.dbUrl=System.getenv("JDBC_DATABASE_URL");
//		this.dataSource = dbProvider.dataSource();
//	}
//
//	protected DataSource getDataSource() {
//		return dataSource;
//	}
//	
//}

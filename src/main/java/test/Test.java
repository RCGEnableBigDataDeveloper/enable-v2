package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.mapr.db.Admin;
import com.mapr.db.MapRDB;

public class Test {

	public static void main(String args[]) throws Exception {

		Class.forName("com.mysql.jdbc.Driver").newInstance();
		Connection conn = DriverManager.getConnection("jdbc:mysql://54.210.131.59/mysql", "root", "");
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery("select * from " + "help_category");
		System.out.println("select * from " + "help_category");

		ResultSetMetaData rsMetaData = rs.getMetaData();

		List<String> rows = new LinkedList<>();
		while (rs.next()) {
			String row = "";
			for (int i = 1; i < rsMetaData.getColumnCount() + 1; i++) {
				row = row + rs.getString(i) + ",";
			}
			rows.add( row.substring(0, row.length() - 1));
			
		}
		System.err.println("****************** " +StringUtils.join(rows, "\n"));

		System.exit(0);
		System.err.println("STARTING ");

		final Admin admin = MapRDB.newAdmin();
		List<Path> tables = admin.listTables(new Path("/tmp"));
		System.err.println(String.format("found %s tables in path %s", tables.size(), "/tmp"));

		if (!admin.tableExists("/tmp/mytable"))
			admin.createTable("/tmp/mytable");

		for (final Path tablePath : admin.listTables(new Path("/tmp"))) {
			System.err.println(tablePath.getName());
		}

		byte buf[] = new byte[65 * 1024];
		int ac = 0;
		if (args.length != 1) {
			System.out.println("usage: MapRTest pathname");
			return;
		}

		System.setProperty("java.library.path", "/tmp/mapr/lib");

		// maprfs:/// -> uses the first entry in
		// /opt/mapr/conf/mapr-clusters.conf
		// maprfs:///mapr/my.cluster.com/
		// /mapr/my.cluster.com/

		// String uri = "maprfs:///";
		String dirname = args[ac++];

		Configuration conf = new Configuration();
		// conf.set("fs.default.name", "maprfs://54.84.48.198:7222");

		// FileSystem fs = FileSystem.get(URI.create(uri), conf); // if wanting
		// to use a different cluster
		FileSystem fs = FileSystem.get(conf);

		Path dirpath = new Path(dirname + "/dir");
		Path wfilepath = new Path(dirname + "/file.w");
		// Path rfilepath = new Path( dirname + "/file.r");

		FileStatus[] status = fs.listStatus(new Path("/tmp"));

		for (int i = 0; i < status.length; i++) {
			FileStatus fileStatus = status[i];
			String path = fileStatus.getPath().toString();
			System.out.println("XXXXXXXXXXXXXX " + path);
		}

		// Path rfilepath = wfilepath;
		//
		// // try mkdir
		// boolean res = fs.mkdirs(dirpath);
		// if (!res) {
		// System.out.println("mkdir failed, path: " + dirpath);
		// return;
		// }
		//
		// System.out.println("mkdir( " + dirpath + ") went ok, now writing
		// file");
		//
		// // create wfile
		// FSDataOutputStream ostr = fs.create(wfilepath, true, // overwrite
		// 512, // buffersize
		// (short) 1, // replication
		// (long) (64 * 1024 * 1024) // chunksize
		// );
		// ostr.write(buf);
		// ostr.close();
		//
		// System.out.println("write( " + wfilepath + ") went ok");
		//
		// // read rfile
		// System.out.println("reading file: " + rfilepath);
		// FSDataInputStream istr = fs.open(rfilepath);
		// int bb = istr.readInt();
		// istr.close();
		// System.out.println("Read ok");
	}
}

package test;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Slf4jLog;

public class DataServiceServer {
	public static void main(String[] args) throws Exception {

		Log.setLog(new Slf4jLog());
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");

		Server jettyServer = new Server(9987);
		jettyServer.setHandler(context);

		List<String> controllers = new ArrayList<String>();
		controllers.add(DataServiceResource.class.getCanonicalName());
		ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
		jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", StringUtils.join(controllers, ","));

		try {
			jettyServer.start();
			jettyServer.join();
		} finally {
			jettyServer.destroy();
		}
	}
}

/*
 * package test;
 * 
 * import java.util.ArrayList; import java.util.List;
 * 
 * import org.apache.commons.lang.StringUtils; import
 * org.eclipse.jetty.servlet.ServletContextHandler; import
 * org.eclipse.jetty.servlet.ServletHolder; import
 * org.eclipse.jetty.util.log.Log; import org.eclipse.jetty.util.log.Slf4jLog;
 * import org.mortbay.jetty.Connector; import org.mortbay.jetty.Server; import
 * org.mortbay.jetty.nio.SelectChannelConnector;
 * 
 * 
 * public class DataServiceServer { public static void main(String[] args)
 * throws Exception {
 * 
 * Log.setLog(new Slf4jLog()); ServletContextHandler context = new
 * ServletContextHandler(ServletContextHandler.SESSIONS);
 * context.setContextPath("/");
 * 
 * Server server = new Server(); Connector connector = new
 * SelectChannelConnector(); connector.setHost("10.0.2.15");
 * connector.setPort(8280); server.addConnector(connector);
 * 
 * List<String> controllers = new ArrayList<String>();
 * controllers.add(DataServiceResource.class.getCanonicalName()); ServletHolder
 * jerseyServlet =
 * context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class,
 * "/*");
 * jerseyServlet.setInitParameter("jersey.config.server.provider.classnames",
 * StringUtils.join(controllers, ","));
 * 
 * try { server.start(); server.join(); } finally { server.destroy(); } } }
 */
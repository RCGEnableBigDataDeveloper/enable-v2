package com.rcggs.datalake.connect.mapr;
import java.util.HashSet;
import java.util.Set;

import org.opcfoundation.ua.application.Client;
import org.opcfoundation.ua.application.Server;
import org.opcfoundation.ua.core.EndpointDescription;
import org.opcfoundation.ua.transport.Endpoint;
import org.opcfoundation.ua.transport.SecureChannel;
import org.opcfoundation.ua.transport.security.Cert;
import org.opcfoundation.ua.transport.security.KeyPair;
import org.opcfoundation.ua.transport.security.PrivKey;
import org.opcfoundation.ua.utils.EndpointUtil;


/**
 * Creates a test bench with an endpoint binded to each network interface. Idea is that same endpoint name is bound to different ip addresses. 
 * Also creates a client.
 * 
 * @see TestGetEndpoints for the actual test case
 */
public abstract class EndpointsTest  {

	static String uri;
	
	static Server server;
	static Client client;
	static Endpoint endpoint;
	static SecureChannel secureChannel;
	static SecureChannel unsecureChannel;
	
	public static void main(String[] args) {
		try {
			getEndpoints(args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static Set<String> getEndpoints(String[] args) throws Exception {
		
		
		Cert myClientCertificate = Cert.load( EndpointsTest.class.getResource( "/ClientCert.der" ) );
		PrivKey myClientPrivateKey = PrivKey.loadFromKeyStore( EndpointsTest.class.getResource( "/ClientCert.pfx"), "Opc.Sample.Ua.Client");
		KeyPair clientApplicationInstanceCertificate = new KeyPair(myClientCertificate, myClientPrivateKey);
		
		client = Client.createClientApplication( clientApplicationInstanceCertificate );
		
			KeyPair myClientKeys = UnitTestKeys.getKeyPair("client", 1024);
		Client client = Client.createClientApplication( myClientKeys );
		
		KeyPair myClientHttpsKeys = UnitTestKeys.getKeyPair("https_client", 1024);
		client.getApplicationHttpsSettings().setKeyPair( myClientHttpsKeys );
		//client.getApplication().getHttpsSettings().setHttpsSecurityPolicies(new HttpsSecurityPolicy[] { SecurityMode.NONE });
		client.getApplication().getHttpsSettings().setHttpsSecurityPolicies(null);
		
		EndpointDescription[] endpoints = client.discoverEndpoints("opc.tcp://eperler_lt:53530/OPCUA/SimulationServer");

		//Check that there's expected number of results

		//Check results in more detail
		Set<String> s = new HashSet<String>();
		for(int i = 0; i < endpoints.length; i++) {
			s.add(endpoints[i].getEndpointUrl());
			System.err.println(endpoints[i].getEndpointUrl());
		}
		
		return s;
	}
	
	protected void tearDown() throws Exception {
//		server.getApplication().close();
//		server = null;
	}
	
	/**	http://catalog.data.gov/dataset/federal-outer-continental-shelf-oil-and-gas-production-statistics-gulf-of-mexico
		http://catalog.data.gov/dataset/noaas-nowcoast-web-mapping-portal-to-near-real-time-coastal-information
		http://catalog.data.gov/dataset/federal-outer-continental-shelf-oil-and-gas-production-statistics-pacific
		http://catalog.data.gov/dataset/streamflow-projections-for-the-western-united-states
		http://catalog.data.gov/dataset/noaas-inundation-analysis-tool
		http://catalog.data.gov/dataset/usgs-oil-and-gas-assessment-database
		http://catalog.data.gov/dataset/oil-and-gas-annual-production-beginning-2001

		Some weather data from http://w1.weather.gov/xml/current_obs/seek.php?state=tx&Find=Find
		and maybe more detailed weather offshore like http://marine.weather.gov/MapClick.php?x=261&y=249&site=hgx&zmx=&zmy=&map_x=261&map_y=249#.WES9dZJif0Q 
		**/

	
		
}
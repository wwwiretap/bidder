package test.java;

import static org.junit.Assert.*;



import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jacamars.dsp.rtb.bidder.Controller;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.common.Node;
import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.tools.DbTools;

/**
 * Test Geo fencing
 * @author Ben M. Faul
 *
 */

public class TestRanges {
	/**
	 * Setup the RTB server for the test
	 */
	@BeforeClass
	public static void setup() {
		try {
			Config.setup();
			Controller.getInstance().deleteCampaign("ben","ben:extended-device");
			System.out.println("******************  TestRanges");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void stop() {
		Config.teardown();
	}

	/**
	 * Shut the RTB server down.
	 */
	@AfterClass
	public static void testCleanup() {
		Config.teardown();
	}

	/**
	 * Test distance calculations
	 */
	@Test
	public void testLosAngelesToSF() {
		Number laLat = 34.05;
		Number laLon =  -118.25;

		Number sfLat = 37.62;
		Number sfLon = -122.38;
		double dist = Node.getRange(laLat, laLon, sfLat, sfLon);
		assertTrue(dist==544720.8629416309);
	}

	/**
	 * Test a single geo fence region in an isolated node.
	 * @throws Exception on I/O errors.
	 */
	@Test
	public void testGeoInBidRequest() throws Exception {
		InputStream is = Configuration.getInputStream("SampleBids/smaato.json");
		BidRequest br = new BidRequest(is);
		assertEquals(br.getId(),"K6t8sXXYdM");

		String ref = "LATLON,  34.05, -118.25, 600000.0";
		/*Map m = new HashMap();
		m.put("lat", 34.05);
		m.put("lon",-118.25);
		m.put("range",600000.0);
		List list = new ArrayList();
		list.add(m); */

		Node node = new Node("LATLON","device.geo", Node.INRANGE, ref);

     	boolean b = node.test(br,null);
		assertTrue(b);

	}

	@Test
	public void testzipRangeInBidRequest() throws Exception {
		InputStream is = Configuration.getInputStream("SampleBids/smaato.json");
		BidRequest br = new BidRequest(is);
		assertEquals(br.getId(),"K6t8sXXYdM");

		Node node = new Node("LATLON","device.geo", Node.INRANGE, "ZIPCODES, 90505,90506,90507,600000");

//		System.out.println(DbTools.mapper.writeValueAsString(node));
//		System.out.println("XXXXXXX");

     /*	boolean b = node.test(br,null);
		ObjectNode map = (ObjectNode)node.getBRvalue();
		assertTrue((Double)map.get("lat").doubleValue()==37.62);
		assertTrue((Double)map.get("lon").doubleValue()==-122.38);
		assertTrue((Double)map.get("type").doubleValue()==3);

		assertTrue(b); */

	}


	/**
	 * When you interrogate "domain", app.domain and site.domain will work for either.
	 * @throws Exception on I/O errors.
	 */
	@Test
	public void testDomainMoniker() throws Exception {
		InputStream is = Configuration.getInputStream("SampleBids/nexage.txt");
		BidRequest br = new BidRequest(is);
		String domain = (String)br.interrogate("domain");
		assertNotNull(domain);
		assertTrue(domain.equals("junk1.com"));

		is = Configuration.getInputStream("SampleBids/nexageWithApp.txt");
		br = new BidRequest(is);
		domain = (String)br.interrogate("domain");
		assertNotNull(domain);
		assertTrue(domain.equals("junk1.com"));
	}

}

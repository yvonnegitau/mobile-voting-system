/*
  Copyright 2011 Radovan Murin

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package cvut.fel.mobilevoting.murinrad.communications;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import cvut.fel.mobilevoting.murinrad.datacontainers.ServerData;

import junit.framework.TestCase;
/**
 * Tester Class
 * @author Murko
 *
 */
public class XMLParserTest extends TestCase {

	public void testParseServerXML() {
		
		 
	}

	public void testParseQuestionXML() {
		fail("Not yet implemented");
	}

	public void testParseBeacon() {
		String sampleBeacon = "<serverinfo id='1'><friendlyname>TestName</friendlyname><port>1555</port></serverinfo>";
		ServerData sd = null;
		 try {
			sd = XMLParser.XMLParser.parseBeacon(sampleBeacon,"127.0.0.1");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			assertTrue(false);
			
			e.printStackTrace();
		} catch (IOException e) {
			assertTrue(false);
			e.printStackTrace();
		}
		assertNotNull(sd);
		assertEquals(sd.getAddress(), "127.0.0.1");
		assertEquals(sd.getFriendlyName(), "TestName");
		assertEquals(sd.getPort(), 1555);
	}

}

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

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;
import org.xml.sax.SAXException;

/**
 * Implementation of a HttpResponceInterceptor
 * 
 * the class intercepts the responces, parses them and then notifies the
 * appropriate components
 * 
 * @author Radovan Murin
 * 
 */
public class Interceptor implements HttpResponseInterceptor {
	ConnectionInterface server;

	/**
	 * constructor for the class
	 * 
	 * @param ci
	 *            an implementation of a ConnectionInterface
	 */
	public Interceptor(ConnectionInterface ci) {
		super();
		server = ci;

	}

	@Override
	public void process(HttpResponse response, HttpContext context)
			throws HttpException, IOException {
		if (server.parseResponceCode(response.getStatusLine().getStatusCode()
				+ "")) {

			Header[] cl = response.getHeaders("Content-length");

			if (cl != null && !cl[0].getValue().equals("0")) {

				HttpEntity hE = response.getEntity();

				byte[] buffer = new byte[Integer.parseInt(cl[0].getValue())];

				hE.getContent().read(buffer);

				try {
					XMLParser.XMLParser.parseServerXML(new String(buffer),
							server.getParent(), (Connection) server);
				} catch (SAXException e) {

					e.printStackTrace();
				} catch (ParserConfigurationException e) {

					e.printStackTrace();
				}

			}

		}
	}

}

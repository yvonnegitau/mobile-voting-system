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

import android.util.Log;

public class MyInterceptor implements HttpResponseInterceptor {
	ConnectionInterface server;
public MyInterceptor(ConnectionInterface ci) {
	super();
	server = ci;
	
}
	@Override
	public void process(HttpResponse response, HttpContext context)
			throws HttpException, IOException {
		server.parseResponceCode(response.getStatusLine().getStatusCode() + "");
		Log.d("Android mobiel voting", "IM INT THE INTERCEPTOR");
		Header[] cl = response.getHeaders("Content-length");
		Log.d("Android mobiel voting", "content length");
		if (cl != null && !cl[0].getValue().equals("0")) {
			Log.d("Android mobile voting", "Goodies!");
			HttpEntity hE = response.getEntity();
			Log.d("Android mobile voting", "Whats innit?");
			byte[] buffer = new byte[Integer.parseInt(cl[0].getValue())];
			Log.d("Android mobile voting", "Delicious cake?");
			hE.getContent().read(buffer);
			Log.d("Android mobile voting", "You shouldnt have + " + new String(buffer) );
			
			try {
				XMLParser.XMLParser.parseServerXML(new String(buffer)	,server.getParent(),(ConnectionHTTP) server);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// connection.receiveResponseEntity(new Basi)

		}

	}

}

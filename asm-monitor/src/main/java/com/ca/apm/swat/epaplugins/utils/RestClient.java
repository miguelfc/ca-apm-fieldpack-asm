package com.ca.apm.swat.epaplugins.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.wily.introscope.epagent.EpaUtils;


public class RestClient {
    
    public static final String HTTP_PROXY_HOST = "http.proxyHost";
    public static final String HTTPS_PROXY_HOST = "https.proxyHost";
    public static final String HTTP_PROXY_PORT = "http.proxyPort";
    public static final String HTTPS_PROXY_PORT = "https.proxyPort";

    private static final int BUFFER_LENGTH = 8192;
    
    /**
     * Create a new REST client.
     * @param proxyHost proxy host
     * @param proxyPort proxy port
     * @param proxyUser proxy user
     * @param proxyPassword proxy password
     */
    public RestClient(String proxyHost, String proxyPort, String proxyUser, String proxyPassword) {
        if ((null != proxyHost) && (0 != proxyHost.length())) {
            System.setProperty(HTTP_PROXY_HOST, proxyHost);
            System.setProperty(HTTPS_PROXY_HOST, proxyHost);
        }
        if ((null != proxyPort) && (0 != proxyPort.length())) {
            System.setProperty(HTTP_PROXY_PORT, proxyPort);
            System.setProperty(HTTPS_PROXY_PORT, proxyPort);
        }
        if ((null != proxyUser) && (0 != proxyUser.length())
                && (null != proxyPassword) && (0 != proxyPassword.length())) {
            Authenticator.setDefault(new ProxyAuthenticator(proxyUser, proxyPassword));
        }
    }

    /**
     * Send http request.
     * @param method method to call, e.g. GET, POST
     * @param url URL
     * @param params parameters
     * @return the http response body
     * @throws IOException if an I/O error happened
     */
    public String request(String method, URL url, String params) throws IOException {
        if (EpaUtils.getFeedback().isVerboseEnabled()) {
            EpaUtils.getFeedback().verbose(
                AsmMessages.getMessage(AsmMessages.HTTP_REQUEST, method, url));
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);

        byte[] bytes = params.getBytes();
        InputStream body = new ByteArrayInputStream(bytes);

        byte[] buffer = new byte[BUFFER_LENGTH];
        int read = 0;
        if (body != null) {
            connection.setDoOutput(true);

            OutputStream output = connection.getOutputStream();
            while ((read = body.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }

        }

        final long startTime = System.currentTimeMillis();
        connection.connect();

        InputStream responseBodyStream = connection.getInputStream();
        StringBuffer responseBody = new StringBuffer();



        while ((read = responseBodyStream.read(buffer)) != -1) {
            responseBody.append(new String(buffer, 0, read));
        }
        connection.disconnect();
        final long time = System.currentTimeMillis() - startTime;
        String response = responseBody.toString();
        
        if (EpaUtils.getFeedback().isVerboseEnabled()) {
            EpaUtils.getFeedback().verbose(AsmMessages.getMessage(AsmMessages.HTTP_RESPONSE,
                responseBody.length(), time));
        }

        if (EpaUtils.getFeedback().isDebugEnabled()) {
            String header = null;
            String headerValue = null;
            int index = 0;
            while ((headerValue = connection.getHeaderField(index)) != null) {
                header = connection.getHeaderFieldKey(index);

                if (null == header) {
                    EpaUtils.getFeedback().debug(headerValue);
                } else {
                    EpaUtils.getFeedback().debug(header + ": " + headerValue);
                }
                index++;
            }
            EpaUtils.getFeedback().debug(response);
            EpaUtils.getFeedback().debug(AsmProperties.EMPTY_STRING);
        }

        return response;
    }


    /**
     * Finds the first occurrence of the pattern in the text. Implements the
     * Knuth-Morris-Pratt Algorithm for Pattern Matching
     * @param data data to search
     * @param pattern pattern to search for
     * @param startIndex start index
     * @return start index of pattern or -1 if not found
     */
    public int indexOf(byte[] data, byte[] pattern, int startIndex) {
        int[] failure = computeFailure(pattern);

        int patternIndex = 0;
        if (data.length == 0) {
            return -1;
        }

        for (int i = startIndex; i < data.length; i++) {
            while (patternIndex > 0 && pattern[patternIndex] != data[i]) {
                patternIndex = failure[patternIndex - 1];
            }
            if (pattern[patternIndex] == data[i]) {
                patternIndex++;
            }
            if (patternIndex == pattern.length) {
                return i - pattern.length + 1;
            }
        }
        return -1;
    }

    /**
     * Computes the failure function using a boot-strapping process, where the
     * pattern is matched against itself.
     * @param pattern pattern to match
     * @return failure function
     */
    private int[] computeFailure(byte[] pattern) {
        int[] failure = new int[pattern.length];

        int patternIndex = 0;
        for (int i = 1; i < pattern.length; i++) {
            while (patternIndex > 0 && pattern[patternIndex] != pattern[i]) {
                patternIndex = failure[patternIndex - 1];
            }
            if (pattern[patternIndex] == pattern[i]) {
                patternIndex++;
            }
            failure[i] = patternIndex;
        }

        return failure;
    }

    /**
     * Read an XML input stream and convert it to a DOM document.
     * 
     * @param is XML input stream
     * @return the DOM document
     * @throws SAXException parser error, see {@link DocumentBuilder#parse(InputStream)}
     * @throws IOException I/O error, see {@link DocumentBuilder#parse(InputStream)}
     * @throws ParserConfigurationException parser configuration error,
     *     see {@link DocumentBuilderFactory#newDocumentBuilder}
     */
    public org.w3c.dom.Document readXml(InputStream is)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setValidating(false);
        dbf.setIgnoringComments(false);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setNamespaceAware(true);
        // dbf.setCoalescing(true);
        // dbf.setExpandEntityReferences(true);

        DocumentBuilder db = null;
        db = dbf.newDocumentBuilder();
        db.setEntityResolver(new NullResolver());

        // db.setErrorHandler( new MyErrorHandler());

        return db.parse(is);
    }
    
    /**
     * Default {@link EntityResolver} that always resolves to empty string.
     * Needed by SAXParser. 
     */
    class NullResolver implements EntityResolver {
        public InputSource resolveEntity(String publicId, String systemId)
                throws SAXException, IOException {
            return new InputSource(new StringReader(AsmProperties.EMPTY_STRING));
        }
    }
}
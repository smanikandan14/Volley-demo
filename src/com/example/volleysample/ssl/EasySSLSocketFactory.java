package com.example.volleysample.ssl;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.example.volleysample.R;

import com.example.volleysample.app.VolleySampleApplication;


public class EasySSLSocketFactory implements LayeredSocketFactory {

    private SSLContext sslcontext = null;
    private boolean isConnectingYourServer = true;
    
    public EasySSLSocketFactory(boolean isConnectingToGoinoutServer) {
    	this.isConnectingYourServer = isConnectingToGoinoutServer;
    }
    
    
    private static SSLContext createEasySSLContext() throws IOException {
        try {
        	
            // Client should send the valid key to Server 

        	InputStream clientStream = VolleySampleApplication.getContext().getResources().openRawResource(R.raw.production_test_client);
        	char[] password = "kiSfsEPbNgulYIv7zU8d".toCharArray();
	        
	        KeyStore keyStore = KeyStore.getInstance("PKCS12");
	        keyStore.load(clientStream, password);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, password);
            
            
	        // CA key obtained from server.
	        KeyStore trustStore  = KeyStore.getInstance( "BKS");
	        InputStream instream = null;
	        instream = VolleySampleApplication.getContext().getResources().openRawResource(R.raw.production_test_ca);

	        try {
	            trustStore.load(instream, "edenpod".toCharArray());
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            try { instream.close(); } catch (Exception ignore) {}
	        }            

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(trustStore);
            
            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(keyManagerFactory.getKeyManagers(), tmf.getTrustManagers(), null);

            return context;
        } catch (Exception e) {
        	e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }

    private static SSLContext createIgnoreSSLContext() throws IOException {
        try {
        	SSLContext context = SSLContext.getInstance("TLS");
        	context.init(null, new TrustManager[]{new IgnoreCertTrustManager()}, null);
        	return context;
        } catch (Exception e) {
        	e.printStackTrace();
        	throw new IOException(e.getMessage());
        }
    }
    
    private SSLContext getSSLContext() throws IOException {
        if (this.sslcontext == null) {
        	if(isConnectingYourServer) {
        		this.sslcontext = createEasySSLContext();
        	} else {
        		//Ignore the Certificate Authority check if not connecting to your server.
        		this.sslcontext = createIgnoreSSLContext();
        	}
        }
        
        return this.sslcontext;
    }

    /**
     * @see org.apache.http.conn.scheme.SocketFactory#connectSocket(java.net.Socket,
     *      java.lang.String, int, java.net.InetAddress, int,
     *      org.apache.http.params.HttpParams)
     */
    public Socket connectSocket(Socket sock, String host, int port,
                                InetAddress localAddress, int localPort, HttpParams params)
            throws IOException, UnknownHostException, ConnectTimeoutException {
        int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
        int soTimeout = HttpConnectionParams.getSoTimeout(params);

        InetSocketAddress remoteAddress = new InetSocketAddress(host, port);
        SSLSocket sslsock = (SSLSocket) ((sock != null) ? sock : createSocket());

        if ((localAddress != null) || (localPort > 0)) {
            // we need to bind explicitly
            if (localPort < 0) {
                localPort = 0; // indicates "any"
            }
            InetSocketAddress isa = new InetSocketAddress(localAddress,
                    localPort);
            sslsock.bind(isa);
        }

        sslsock.connect(remoteAddress, connTimeout);
        sslsock.setSoTimeout(soTimeout);
        return sslsock;

    }

    /**
     * @see org.apache.http.conn.scheme.SocketFactory#createSocket()
     */
    public Socket createSocket() throws IOException {
        return getSSLContext().getSocketFactory().createSocket();
    }

    /**
     * @see org.apache.http.conn.scheme.SocketFactory#isSecure(java.net.Socket)
     */
    public boolean isSecure(Socket socket) throws IllegalArgumentException {
        return true;
    }

    /**
     * @see org.apache.http.conn.scheme.LayeredSocketFactory#createSocket(java.net.Socket,
     *      java.lang.String, int, boolean)
     */
    public Socket createSocket(Socket socket, String host, int port,
                               boolean autoClose) throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    // -------------------------------------------------------------------
    // javadoc in org.apache.http.conn.scheme.SocketFactory says :
    // Both Object.equals() and Object.hashCode() must be overridden
    // for the correct operation of some connection managers
    // -------------------------------------------------------------------

    public boolean equals(Object obj) {
        return ((obj != null) && obj.getClass().equals(
                EasySSLSocketFactory.class));
    }

    public int hashCode() {
        return EasySSLSocketFactory.class.hashCode();
    }


}
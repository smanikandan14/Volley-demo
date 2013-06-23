package com.example.volleysample.ssl;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created with IntelliJ IDEA.
 * User: progen
 * Date: 3/30/12
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class IgnoreCertTrustManager implements X509TrustManager {


    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}

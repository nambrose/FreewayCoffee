package com.freeewaycoffee.ordermanager;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.HttpParams;

import android.content.Context;


public class FCOrderManagerHTTPClient extends DefaultHttpClient implements org.apache.http.conn.ssl.X509HostnameVerifier
{
	final Context context;
	private SSLSocketFactory sf;
	
    public FCOrderManagerHTTPClient(Context context) 
    {
        this.context = context;
    }
    
    public FCOrderManagerHTTPClient(Context context,HttpParams httpParameters )
    {
    	super(httpParameters);
    	this.context = context;
    	
    }
    
    public boolean verify(String host, SSLSession session)
	{
		return SSLSocketFactory.STRICT_HOSTNAME_VERIFIER.verify(host,session);
	}
	
	public void 	verify(String host, X509Certificate cert) throws SSLException
	{
		SSLSocketFactory.STRICT_HOSTNAME_VERIFIER.verify(host,cert);
	}
	public void 	verify(String host, SSLSocket ssl) throws IOException
	{
		if(host.equals(FCOrderManagerApp.HOST_NAME_DEBUG) || host.equals(FCOrderManagerApp.HOST_NAME_RELEASE))
		{
			SSLSocketFactory.STRICT_HOSTNAME_VERIFIER.verify(host,ssl);
			return;
		}
		else
		{
			throw new IOException("SSL Authentication Failed");
		}
	}
	public void 	verify(String host, String[] cns, String[] subjectAlts)
	{
		
	}
    @Override
    protected ClientConnectionManager createClientConnectionManager() 
    {
        SchemeRegistry registry = new SchemeRegistry();
        //registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        // Register for port 443 our SSLSocketFactory with our keystore
        // to the ConnectionManager
        registry.register(new Scheme("https", newSslSocketFactory(), 443));
        return new SingleClientConnManager(getParams(), registry);
    }
    
    private SSLSocketFactory newSslSocketFactory() 
    {
        try 
        {
            // Get an instance of the Bouncy Castle KeyStore format
            KeyStore trusted = KeyStore.getInstance("BKS");
            // Get the raw resource, which contains the keystore with
            // your trusted certificates (root and any intermediate certs)
            /*
            InputStream in_debug = context.getResources().openRawResource(R.raw.ipower_keystore);
            try
            {
                // Initialize the keystore with the provided trusted certificates
                // Also provide the password of the keystore
                trusted.load(in_debug, "1297438128".toCharArray());
            } 
            catch (IOException	e)
            {
            	String S = e.toString();
            }
            catch (NoSuchAlgorithmException e)
            {
            	String S = e.toString();
            }
            catch (CertificateException e)
            {
            	String S = e.toString();
            }
            finally 
            {
            	in_debug.close();
            }
            */
            
            /*
            InputStream in_rel = context.getResources().openRawResource(R.raw.inmotion_keystore);
            try 
            {
                // Initialize the keystore with the provided trusted certificates
                // Also provide the password of the keystore
               trusted.load(in_rel, "7234589765109876".toCharArray());
            } 
            catch (IOException	e)
            {
            	String S = e.toString();
            }
            catch (NoSuchAlgorithmException e)
            {
            	String S = e.toString();
            }
            catch (CertificateException e)
            {
            	String S = e.toString();
            }
            finally 
            {
            	in_rel.close();
            }
            
            */
            
            // Pass the keystore to the SSLSocketFactory. The factory is responsible
            // for the verification of the server certificate.
            // sf = new SSLSocketFactory(trusted);
            sf = SSLSocketFactory.getSocketFactory();
            // Hostname verification from certificate
            // http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html#d4e506
            //sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
             sf.setHostnameVerifier(this);
            return sf;
        }
        catch (Exception e) 
        {
            throw new AssertionError(e);
        }
    }
}

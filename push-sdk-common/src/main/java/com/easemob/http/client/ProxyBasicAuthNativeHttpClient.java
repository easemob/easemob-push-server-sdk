package com.easemob.http.client;

import com.easemob.http.HttpProxy;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProxyBasicAuthNativeHttpClient extends NativeHttpClient {

    @Override
    public HttpURLConnection getUrlConnection(URL url) throws IOException {
        HttpURLConnection urlConnection = super.getUrlConnection(url);

        if (getProxy() == null) {
            return urlConnection;
        }

        if (getProxy() instanceof HttpProxy) {
            HttpProxy httpProxy = (HttpProxy) getProxy();
            urlConnection.setRequestProperty(HttpProxy.Proxy_Authorization_Header_Name,
                    httpProxy.getBasicAuthorization());
        }

        return urlConnection;
    }
}

package com.easemob.http;

import java.net.Proxy;
import java.net.SocketAddress;
import java.util.Base64;

public class HttpProxy extends Proxy {

    public final static String Proxy_Authorization_Header_Name = "Proxy-Authorization";

    private String username;
    private String password;

    /**
     * Creates an entry representing a PROXY connection.
     * Certain combinations are illegal. For instance, for types Http, and
     * Socks, a SocketAddress <b>must</b> be provided.
     * <p>
     * Use the {@code Proxy.NO_PROXY} constant
     * for representing a direct connection.
     *
     * @param type the {@code Type} of the proxy
     * @param sa   the {@code SocketAddress} for that proxy
     * @throws IllegalArgumentException when the type and the address are
     *                                  incompatible
     */
    public HttpProxy(Type type, SocketAddress sa) {
        super(type, sa);
    }

    public HttpProxy(Type type, SocketAddress sa, String username, String password) {
        super(type, sa);
        this.username = username;
        this.password = password;
    }

    /**
     * proxy auth header
     *
     * @return header value of Proxy-Authorization
     */
    public String getBasicAuthorization() {
        if (username == null && password == null) {
            return null;
        }

        String encodeKey = username + ":" + password;
        byte[] encode = Base64.getEncoder().encode(encodeKey.getBytes());
        return "Basic" + " " + new String(encode);
    }
}

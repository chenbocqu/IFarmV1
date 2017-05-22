package com.qican.ifarm.utils;

import android.content.Context;

import com.qican.ifarm.data.IFarmClient;
import com.qican.ifarm.listener.OnSocketConnectListener;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class ClientUtil {

    private static MyClient client;

    public static MyClient init(Context context, URI uri) {
        client = new MyClient(context, uri);
        return client;
    }

    public static MyClient send(String msg, OnSocketConnectListener l) {
        client.setOnSocketConnectListener(l);
        client.send(msg);
        return client;
    }


    static class MyClient extends WebSocketClient {

        OnSocketConnectListener l;

        IFarmClient client;
        Context mContext;
        URI url;

        public MyClient(Context mContext, URI uri) {
            super(uri);
            this.mContext = mContext;
            this.url = uri;

            initClient(uri);
        }

        private MyClient initClient(URI url) {
            client = new IFarmClient(url);
            try {
                client.connectBlocking();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            client.send("Hello");

            return this;
        }

        @Override
        public void onOpen(ServerHandshake serverHandshake) {
            if (l != null) l.onOpen(serverHandshake);
        }

        @Override
        public void onMessage(String s) {
            if (l != null) l.onMessage(s);
        }

        @Override
        public void onClose(int i, String s, boolean b) {
            if (l != null) l.onClose(i, s, b);
        }

        @Override
        public void onError(Exception e) {
            if (l != null) l.onError(e);
        }

        public void setOnSocketConnectListener(OnSocketConnectListener l) {
            this.l = l;
        }
    }
}

package com.qican.ifarm.listener;

import org.java_websocket.handshake.ServerHandshake;

public interface OnSocketConnectListener {
    public void onOpen(ServerHandshake serverHandshake);

    public void onMessage(String s);

    public void onClose(int i, String s, boolean b);

    public void onError(Exception e);
}

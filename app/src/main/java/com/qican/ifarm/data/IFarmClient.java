package com.qican.ifarm.data;

import android.content.Context;

import com.qican.ifarm.utils.CommonTools;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class IFarmClient extends WebSocketClient {
    private Context mContext;
    private CommonTools myTool;

    public IFarmClient(URI serverURI) {
        super(serverURI);
    }

    public IFarmClient(Context mContext, URI serverURI) {
        super(serverURI);

        this.mContext = mContext;
        myTool = new CommonTools(mContext);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {

    }

    @Override
    public void onMessage(String s) {

    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {

    }
}

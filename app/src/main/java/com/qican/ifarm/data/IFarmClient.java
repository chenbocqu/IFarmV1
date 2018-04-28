package com.qican.ifarm.data;

import android.content.Context;
import android.util.Log;

import com.qican.ifarm.listener.OnInfoListener;
import com.qican.ifarm.utils.CommonTools;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class IFarmClient extends WebSocketClient {
    private Context mContext;
    String TAG = "IFarmClient";

    OnInfoListener<String> taskMsgListener;
    OnClientListener onClientListener;

    public interface OnClientListener {
        void onClose(int i, String s, boolean b);

        void onMessage(String s);
    }

    public IFarmClient(URI serverURI) {
        super(serverURI);
    }

    public IFarmClient(Context mContext, URI serverURI) {
        super(serverURI);

        this.mContext = mContext;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        Log.i(TAG, "IFarmClient onOpen：" + serverHandshake.getHttpStatusMessage());
    }

    @Override
    public void onMessage(String s) {
        Log.i(TAG, "IFarmClient onMessage 服务器消息：" + s);

        if (taskMsgListener != null)
            taskMsgListener.onInfoChanged(s);

        if (onClientListener != null) onClientListener.onMessage(s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        Log.i(TAG, "IFarmClient onClose：" + s);
        if (onClientListener != null) onClientListener.onClose(i, s, b);
    }

    @Override
    public void onError(Exception e) {
        Log.i(TAG, "IFarmClient onError：" + e.getMessage());
    }

    public void setOnTaskMsgListener(OnInfoListener<String> taskMsgListener) {
        this.taskMsgListener = taskMsgListener;
    }

    public void setOnClientListener(OnClientListener onClientListener) {
        this.onClientListener = onClientListener;
    }
}

package com.qican.ifarm.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.ControlSystem;
import com.qican.ifarm.bean.Task;
import com.qican.ifarm.data.IFarmClient;
import com.qican.ifarm.listener.OnInfoListener;
import com.qican.ifarm.ui.task.TaskInfoActivity;
import com.qican.ifarm.ui_v2.task.TaskInfoV2Activity;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.TimeUtils;

import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.List;

public class TaskService extends Service implements IFarmClient.OnClientListener {

    private TaskBinder mBinder = new TaskBinder();
    private CommonTools myTool;
    private int cnt = 0;
    IFarmClient client;

    @Override
    public void onCreate() {
        super.onCreate();
        myTool = new CommonTools(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startWebSocketConnect();

        return super.onStartCommand(intent, flags, startId);
    }

    private void startWebSocketConnect() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                connectWithService();
            }
        }.start();
    }

    private void connectWithService() {
        try {
            myTool.log("+ Websocket 正在建立连接 ：+\n[ userId, " + myTool.getUserId() + " ] \n[ signature, " + myTool.getToken() + " ]");
            client = new IFarmClient(new URI(myTool.getSocketAdd() + "controlServer?" +
                    "userId=" + myTool.getUserId() + "&signature=" + myTool.getToken()));
            client.connectBlocking();
            myTool.log("+       Websocket 连接成功...       +");

            client.setOnClientListener(this);
        } catch (Exception e) {
            myTool.log(e.getMessage());
            // TODO: 重新连接
//            tryConnectAgain();
        }
    }

    // 尝试重新连接WebSocket
    private void tryConnectAgain() {
        myTool.log("+ 与服务器的Websocket连接失败，正在准备重新连接 +");
        client = null;

        int cnt = 10; // 10秒重连
        while (cnt > 0) {
            try {
                Thread.sleep(1000);
//                myTool.log("+       Websocket将在[ " + cnt + " s ]之后重新连接...       +");
                cnt--;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        myTool.log("+       Websocket 重新连接中...       +");
        connectWithService();
    }

    @Override
    public void onDestroy() {

        if (client != null)
            client.close();

        myTool.log("+     onDestroy    +");

        try {
            Thread.sleep(2000); // 延时2s退出
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    public TaskService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // 监听Websocket,关闭关闭重连
    @Override
    public void onClose(int i, String s, boolean b) {

        // Token存在则重新连接
        if (!myTool.isErrorToken()) {
            myTool.log("Token正确，正在重连···");
            tryConnectAgain();
        } else {
            myTool.log("Token错误，没有回复···");
        }
    }

    @Override
    public void onMessage(String s) {
        if (s == null || s.length() == 0) return;

        // 判断Token是否过期
        if (isTokenError(s)) {
            myTool.log("TOKEN 错误，Service 已经关闭，等到登录时重新连接···");
            myTool.setErrorToken(true); // 设置token失效
            stopSelf();
            return;
        }

        showNotification(s);
    }

    private void showNotification(String s) {

        if (!isJSONObj(s)) return;

        myTool.log("call isJSONObj(s) !");

        JSONObject obj = null;
        try {
            obj = new JSONObject(s);
            if (!obj.has("farmId")) return;
            if (!obj.has("response")) return;

            myTool.log("服务器正在推送的任务信息 [farmId:" + obj.getString("farmId") + "]...");

            // 装载数据
            Task task = new Task();
            task.setFarmId(obj.getString("farmId"));

            if (obj.has("controllerLogId"))
                task.setControllerLogId(obj.getString("controllerLogId"));
            if (obj.has("farmName")) task.setFarmName(obj.getString("farmName"));
            if (obj.has("systemDistrict"))
                task.setSystemDistrict(obj.getString("systemDistrict"));
            if (obj.has("systemNo")) task.setSystemNo(obj.getString("systemNo"));
            if (obj.has("controlDeviceId"))
                task.setControlDeviceId(obj.getString("controlDeviceId"));
            if (obj.has("systemId")) task.setSystemId(obj.getString("systemId"));
            if (obj.has("controlOperation"))
                task.setControlOperation(obj.getString("controlOperation"));
            if (obj.has("level")) task.setLevel(obj.getString("level"));
            if (obj.has("executionTime")) task.setExecutionTime(obj.getString("executionTime"));
            if (obj.has("stopTime")) task.setStopTime(obj.getString("stopTime"));
            if (obj.has("startExecutionTime"))
                task.setStartExecutionTime(obj.getString("startExecutionTime"));
            if (obj.has("addResultTime")) ;
            task.setAddResultTime(obj.getString("addResultTime"));
            if (obj.has("functionName")) ;
            task.setFunctionName(obj.getString("functionName"));
            if (obj.has("systemType")) task.setSystemType(obj.getString("systemType"));


            if (obj.has("collectorId")) task.setCollectorId(obj.getString("collectorId"));
            if (obj.has("taskTime")) task.setTaskTime(obj.getString("taskTime"));
            if (obj.has("controlArea")) task.setControlArea(obj.getString("controlArea"));
            if (obj.has("controlType")) task.setControlType(obj.getString("controlType"));
            if (obj.has("fertilizationCan"))
                task.setFertilizationCan(obj.getString("fertilizationCan"));

            String response = obj.getString("response");
            task.setResponse(response);

            if (obj.has("type")) {
                task.setType(obj.getString("type"));
                switch (obj.getString("type")) {
                    //添加任务的推送，几种不同的回复
                    case "add":
                        showAddInfoByResponse(response, task);// 根据回复信息显示添加信息
                        break;
                    case "stop":
                        showStopInfoByResponse(response, task);// 根据回复信息显示停止信息
                        break;
                }
            } else {
                // task V2
                String title = null;
                switch (response) {
                    // 成功执行，设备运行
                    case "successExcution":
                        title = "任务成功执行";
                        break;
                    case "failExecution":
                        title = "任务执行失败";
                        break;
                    case "stopSucess":
                        title = "停止命令下发成功";
                        break;
                    case "failStop":
                        title = "停止命令下发失败";
                        break;
                    case "stopTimeout":
                        title = "停止命令超时";
                        break;
                    case "executionTimeout":
                        title = "执行命令超时";
                        break;
                    case "execuitonComplete":
                        title = "任务执行完成";
                        break;
                }
                if (title != null)
                    showNotificationV2(title, task.getSystemType() + "[操作：" + task.getFunctionName() + "]", task, 2);
            }

        } catch (JSONException e) {
            myTool.log(e.getMessage());
        }


    }

    private void showStopInfoByResponse(String response, Task task) {
        if (response == null) {
            myTool.log("服务推送的停止任务消息遇到response信息为null");
            return;
        }

        task.setStatus(Task.TaskStatus.Completed);
        myTool.log(task.toString());

        switch (response) {
            case "replyStop":
                showNotification("任务已停止", "控制器已停止" + getTaskInfo(task), task, 2);
                break;
            case "noResultTimeOutStop":
                showNotification("网络异常", getTaskInfo(task) + "失败了", task, 2);
                break;
            case "settingTimeOutStop":
                showNotification("任务已完成", getTaskInfo(task) + "已完成", task, 2);
                break;
            case "failStop":
                showNotification("任务添加失败", getTaskInfo(task) + "失败了", task, 2);
                break;
        }
    }

    // 任务描述
    private String getTaskInfo(Task task) {
        String time = TimeUtils.formatDateTime(task.getTaskTime());
        String typeDesc = "";
        switch (task.getControlType()) {
            case "irrigate":
                typeDesc = "灌溉";
                break;
            case "fertilizer":
                typeDesc = "施肥[罐" + task.getFertilizationCan() + "]";
                break;
        }
        return time + "的" + typeDesc + "任务（时长" + task.getExecutionTime() + "s）";
    }

    private void showAddInfoByResponse(String response, Task task) {
        if (response == null) {
            myTool.log("服务推送的添加任务消息遇到response信息为null");
            return;
        }
        task.setStatus(Task.TaskStatus.Waiting);
        switch (response) {
            case "success":
                showNotification("任务添加成功", getTaskInfo(task) + "已成功下发至控制器！", task, 1);
                break;
            case "error":
                showNotification("任务添加异常", getTaskInfo(task) + "遇到网络异常！", task, 1);
                break;
            case "fail":
                showNotification("任务添加失败", getTaskInfo(task) + "添加失败，请重新添加！", task, 1);
                break;
        }
    }

    // 任务推送
    private void showNotificationV2(String title, String info, Task task, int notificationId) {

        Bundle bundle = new Bundle();
        bundle.putSerializable(Task.class.getName(), task);

        Intent intent = new Intent(this, TaskInfoV2Activity.class);
        intent.putExtras(bundle);

        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(info)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ifarm_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ifarm_logo))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        manager.notify(notificationId, notification);

        myTool.log("There is a Task Push Notification !");
    }

    private void showNotification(String title, String info, Task task, int notificationId) {

        Bundle bundle = new Bundle();
        bundle.putSerializable(Task.class.getName(), task);

        Intent intent = new Intent(this, TaskInfoActivity.class);
        intent.putExtras(bundle);

        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(info)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ifarm_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ifarm_logo))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        manager.notify(notificationId, notification);

        myTool.log("There is a Task Push Notification !");
    }

    /**
     * 判断字符是否为JSONObj
     */
    private boolean isJSONObj(String response) {

        myTool.log("response=" + response);

        if (response == null || response.length() == 0) return false;

        if ('{' == response.charAt(0))
            return true;

        return false;
    }

    private boolean isTokenError(String s) {

        if (!"{".equals(s.charAt(0)))
            return false;


        // 进行JSON转换
        JSONObject obj = null;
        try {
            obj = new JSONObject(s);
            String response = "";

            if (obj.has("response"))
                response = obj.getString("response");

            // TODO: 确认 ERROR TOKEN 的返回值
            if ("error token".equals(response))
                return true;

        } catch (JSONException e) {
            myTool.log("JSON Error!");
        }
        return false;
    }


    public class TaskBinder extends Binder {
        public Task getCurrTask() {
            return null;
        }

        /**
         * 5、添加作业的数据格式(所有字段不可或缺)：
         * 输入：{
         * "farmId": 10000001,
         * "collectorId": 17130000,//设备编号
         * "level": 2,
         * "commandCategory": "add",
         * "command": "execute",
         * "controlArea": "A,B",//控制区域
         * "controlType": "fertilizer",
         * "fertilizationCan": "2",  //施肥罐，用‘，’分开
         * "waitTime": 0,
         * "executionTime": 120  //执行时间
         * "startExecutionTime": "2017-05-15 10:45:04"  //开始执行时间
         * }
         * 回复：{"response":"running"}   //已经添加到任务
         * {"response":"exist task"}  //已经添加到任务，但是要注意在这任务之前还有其他任务未执行
         * {"response", "error")}  //网络异常
         */
        public void addTask(Task newTask) {
            if (client == null) {
                myTool.log("Task Service ERROR : WebSocket initial fialed !");
                return;
            }
            JSONObject obj = new JSONObject();
            try {
                obj.put("farmId", newTask.getFarmId());
                obj.put("collectorId", newTask.getCollectorId());

                obj.put("level", newTask.getLevel());

                obj.put("commandCategory", newTask.getCommandCategory());
                obj.put("command", newTask.getCommand());

                obj.put("controlArea", newTask.getControlArea());
                obj.put("controlType", newTask.getControlType());

                obj.put("fertilizationCan", newTask.getFertilizationCan());

                obj.put("waitTime", newTask.getWaitTime());
                obj.put("executionTime", newTask.getExecutionTime());
                obj.put("startExecutionTime", newTask.getStartExecutionTime());

                client.send(obj.toString());

            } catch (JSONException e) {
                myTool.log("JSON Error for addTask()!");
            } catch (WebsocketNotConnectedException e) {
                myTool.log("socket 连接失败 ！[ " + e.getMessage() + " ]");
            }
        }

        /**
         * V2版本的添加任务，适用于通用任务添加
         *
         * @param task
         */
        public void addTaskV2(Task task) {

            myTool.log("addTaskV2 ing ...");
            if (client == null) {
                myTool.log("Task Service ERROR : WebSocket initial fialed !");
                return;
            }

            JSONObject obj = new JSONObject();
            try {
                obj.put("functionName", task.getName());
                obj.put("farmId", task.getFarmId());
                obj.put("userId", myTool.getUserId());
                obj.put("farmName", task.getFarmName());

                obj.put("systemDistrict", task.getSystemDistrict());
                obj.put("systemNo", task.getSystemNo());
                obj.put("systemId", task.getSystemId());

                obj.put("controlType", task.getControlType());
                obj.put("controlOperation", task.getControlOperation());
                obj.put("commandCategory", task.getCommandCategory());

                obj.put("command", task.getCommand());
                obj.put("executionTime", task.getExecutionTime());

                if (task.getCanNo() != null) obj.put("canNo", task.getCanNo());
                if (task.getControlArea() != null) obj.put("controlArea", task.getControlArea());

                if (task.getStartExecutionTime() != null)
                    obj.put("startExecutionTime", task.getStartExecutionTime());

                client.send(obj.toString());

            } catch (JSONException e) {
                myTool.log("JSON Error for addTaskV2()!");
            } catch (WebsocketNotConnectedException e) {
                myTool.log("socket 连接失败 ！[ " + e.getMessage() + " ]");
            }
        }

        /**
         * 输入：{
         * "farmId": 10000001,
         * "collectorId": 17130000,
         * "level": 3,
         * "commandCategory": "stop",
         * "command": "execute"
         * }
         * 回复：{"response":"running"}   //下发指令成功
         * {"response":"no task"}  //当前无任务
         * {"response", "error")}  //网络异常
         */
        public void stopTask(Task task) {

            myTool.log("stopTask -> farmId:" + task.getFarmId() + ", collectorId:" + task.getCollectorId());
            if (client == null) {
                myTool.log("Task Service ERROR : WebSocket initial fialed !");
                return;
            }

            JSONObject obj = new JSONObject();

            try {
                obj.put("farmId", task.getFarmId());
                obj.put("collectorId", task.getCollectorId());

                obj.put("level", "3");

                obj.put("commandCategory", "stop");
                obj.put("command", "execute");

                client.send(obj.toString());

            } catch (JSONException e) {
                myTool.log("JSON Error for stopTask()!");
            } catch (WebsocketNotConnectedException e) {
                myTool.log("socket 连接失败 ！[ " + e.getMessage() + " ]");
            }
        }

        /**
         * 10、撤销某项未执行的作业：
         * 输入：{
         * "controllerLogId":15
         * "farmId": 10000001,
         * "collectorId": 17130000,
         * "level": 1,
         * "commandCategory": "query",
         * "command": "delete"
         * }
         * 回复：{"response":"success"}  //成功
         * {"response":"error"}  //失败
         */
        public void deleteTask(Task task) {

            myTool.log("deleteTask -> farmId:" + task.getFarmId() + ", collectorId:" + task.getCollectorId()
                    + ", controllerLogId:" + task.getControllerLogId());
            if (client == null) {
                myTool.log("Task Service ERROR : WebSocket initial fialed !");
                return;
            }

            JSONObject obj = new JSONObject();

            try {
                obj.put("controllerLogId", Integer.parseInt(task.getControllerLogId()));
                obj.put("farmId", task.getFarmId());
                obj.put("collectorId", task.getCollectorId());

                obj.put("level", "1");

                obj.put("commandCategory", "query");
                obj.put("command", "delete");

                client.send(obj.toString());

            } catch (JSONException e) {
                myTool.log("JSON Error for deleteTask()!");
            } catch (WebsocketNotConnectedException e) {
                myTool.log("socket 连接失败 ！[ " + e.getMessage() + " ]");
            }
        }

        /**
         * 输入：{
         * "farmId": 10000001,
         * "collectorId": 17130000,
         * "level": 1,
         * "commandCategory": "query",
         * "command": "queryTasks"
         * }
         */
        public void queryTasks(ControlSystem system) {

            myTool.log("Start query tasks where [ farmId: " + system.getFarmId() + "; collectorId:" + system.getCollectorId() + " ]");

            if (client == null) {
                myTool.log("Task Service ERROR : WebSocket initial fialed !");
                return;
            }

            JSONObject obj = new JSONObject();

            try {
                obj.put("farmId", system.getFarmId());
                obj.put("collectorId", system.getCollectorId());

                obj.put("level", "1");

                obj.put("commandCategory", "query");
                obj.put("command", "queryTasks");

                client.send(obj.toString());

            } catch (JSONException e) {
                myTool.log("JSON Error for queryTasks()!");
            } catch (WebsocketNotConnectedException e) {
                myTool.log("socket 连接失败 ！[ " + e.getMessage() + " ]");
            }
        }

        public void queryRemainTime(ControlSystem system) {
            myTool.log("\n **** 查询剩余时间 **** where [ farmId: " + system.getFarmId() + "; collectorId:" + system.getCollectorId() + " ]");
            sendCommand(system, "1", "query", "execute");
        }

        private void sendCommand(ControlSystem system, String level, String commandCategory, String command) {

            if (client == null) {
                myTool.log("Task Service ERROR : WebSocket initial fialed !");
                return;
            }

            JSONObject obj = new JSONObject();

            try {
                obj.put("farmId", system.getFarmId());
                obj.put("collectorId", system.getCollectorId());

                obj.put("level", level);

                obj.put("commandCategory", commandCategory);
                obj.put("command", command);

                client.send(obj.toString());

            } catch (JSONException e) {
                myTool.log("JSON Error for sendCommand()!");
            } catch (WebsocketNotConnectedException e) {
                myTool.log("socket 连接失败 ！[ " + e.getMessage() + " ]");
            }
        }

        public void setOnTaskMsgListener(OnInfoListener<String> l) {
            if (client != null)
                client.setOnTaskMsgListener(l);
        }

        public void addIntegrateTask(List<Task> taskList) {

            myTool.log("addIntegrateTask ing ...");
            if (client == null) {
                myTool.log("Task Service ERROR : WebSocket initial fialed !");
                return;
            }

            JSONArray array = new JSONArray();

            for (int i = 0; i < taskList.size(); i++) {

                JSONObject obj = new JSONObject();
                Task task = taskList.get(i);

                try {
                    obj.put("functionName", task.getName());
                    obj.put("farmId", task.getFarmId());
                    obj.put("userId", myTool.getUserId());
                    obj.put("farmName", task.getFarmName());

                    obj.put("systemDistrict", task.getSystemDistrict());
                    obj.put("systemNo", task.getSystemNo());
                    obj.put("systemId", task.getSystemId());

                    obj.put("controlType", task.getControlType());
                    obj.put("controlOperation", task.getControlOperation());
                    obj.put("commandCategory", task.getCommandCategory());

                    obj.put("command", task.getCommand());
                    obj.put("executionTime", task.getExecutionTime());

                    if (task.getCanNo() != null) obj.put("canNo", task.getCanNo());
                    if (task.getControlArea() != null)
                        obj.put("controlArea", task.getControlArea());

                    if (task.getStartExecutionTime() != null)
                        obj.put("startExecutionTime", task.getStartExecutionTime());

                    array.put(obj);
                } catch (JSONException e) {
                    myTool.log("JSON Error for addIntegrateTask()!");
                }
            }

            // 发送出去
            try {
                client.send(array.toString());
            } catch (WebsocketNotConnectedException e) {
                myTool.log("socket 连接失败 ！[ " + e.getMessage() + " ]");
            }
        }

    }
}

/**
 * 智能农场模拟数据
 */
package com.qican.ifarm.utils;

import com.qican.ifarm.bean.ComUser;
import com.qican.ifarm.bean.ControlFunction;
import com.qican.ifarm.bean.ControlSystem;
import com.qican.ifarm.bean.Device;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.bean.IFarmCamera;
import com.qican.ifarm.bean.Label;
import com.qican.ifarm.bean.MonitorNode;
import com.qican.ifarm.bean.OperationArea;
import com.qican.ifarm.bean.Task;

import java.util.ArrayList;
import java.util.List;

public class IFarmFakeData {

    public static List<MonitorNode> getMontiorList() {
        List<MonitorNode> datas = new ArrayList<>();
        datas.add(
                new MonitorNode.Builder()
                        .setName("响堂生态农场")
                        .setStatus("在线")
                        .setHumidity("56.7")
                        .setTemperature1("34.8")
                        .setTemperature2("32.2")
                        .setWaterContent("46")
                        .setImgUrl("http://desk.fd.zol-img.com.cn/t_s960x600c5/g5/M00/02/03/ChMkJlbKxmyIJBaWAAeJtiCgphUAALHlAEaoHgAB4nO760.jpg")
                        .setUpdateTime("2017/2/23 下午3:32:19")
                        .build());
        datas.add(
                new MonitorNode.Builder()
                        .setName("美兰苏民村")
                        .setStatus("在线")
                        .setHumidity("64.7")
                        .setTemperature1("34.8")
                        .setTemperature2("32.2")
                        .setWaterContent("87.6")
                        .setImgUrl("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1880243990,2577219667&fm=23&gp=0.jpg")
                        .setUpdateTime("2011/4/25 下午3:32:19")
                        .build());
        datas.add(
                new MonitorNode.Builder()
                        .setName("秀英统历村")
                        .setStatus("在线")
                        .setHumidity("32.7")
                        .setTemperature1("35.8")
                        .setTemperature2("46.2")
                        .setWaterContent("73")
                        .setImgUrl("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=646265142,702968958&fm=23&gp=0.jpg")
                        .setUpdateTime("2016/2/23 下午3:32:19")
                        .build());
        datas.add(
                new MonitorNode.Builder()
                        .setName("琼山光明村")
                        .setStatus("在线")
                        .setHumidity("56.7")
                        .setTemperature1("34.8")
                        .setTemperature2("32.2")
                        .setWaterContent("32")
                        .setImgUrl("http://desk.fd.zol-img.com.cn/t_s960x600c5/g4/M00/0D/01/Cg-4y1ULoXCII6fEAAeQFx3fsKgAAXCmAPjugYAB5Av166.jpg")
                        .setUpdateTime("2015/2/23 下午3:32:19")
                        .build());
        return datas;
    }

    public static List<ComUser> getUserList() {
        List<ComUser> datas = new ArrayList<>();
        datas.add(new ComUser.Builder()
                .setSex("女")
                .setLabels("蘑菇,小白菜")
                .setNickName("赢了爱情")
                .setHeadImgUrl("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3972100125,3086204015&fm=116&gp=0.jpg")
                .setSignature("专注种植水稻小米20年，谁用谁知道！")
                .setBgImgUrl("http://desk.fd.zol-img.com.cn/t_s960x600c5/g5/M00/02/03/ChMkJlbKxo6ILbI9ACAmreNfn-QAALHnADNeAkAICbF593.jpg")
                .setLastLoginTime("2016-12-27")
                .build());
        datas.add(new ComUser.Builder()
                .setSex("男")
                .setLabels("养鱼,萝卜")
                .setNickName("痛定思痛")
                .setHeadImgUrl("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3444221582,1065647845&fm=116&gp=0.jpg")
                .setSignature("我把自己搞成了一个笑话。")
                .setLastLoginTime("2016-12-24")
                .setBgImgUrl("http://desk.fd.zol-img.com.cn/t_s960x600c5/g3/M03/0D/03/Cg-4V1S_EOWIMyUCAAhG5zFfIHUAATsVQNFKM0ACEb_770.jpg")
                .build());
        datas.add(new ComUser.Builder()
                .setSex("女")
                .setLabels("黄瓜,大豆")
                .setNickName("月光倾城")
                .setHeadImgUrl("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=104972859,1625707354&fm=111&gp=0.jpg")
                .setSignature("我其实是个天使，之所以留在人间，是因为体重的关系。")
                .setBgImgUrl("http://desk.fd.zol-img.com.cn/t_s960x600c5/g5/M00/02/03/ChMkJ1bKxo2IUKlMABBGn3PconAAALHnABsvKEAEEa3344.jpg")
                .setLastLoginTime("2016-12-23")
                .setBgImgUrl("http://desk.fd.zol-img.com.cn/t_s960x600c5/g5/M00/02/03/ChMkJlbKxtqIF93BABJ066MJkLcAALHrQL_qNkAEnUD253.jpg")
                .build());
        datas.add(new ComUser.Builder()
                .setSex("男")
                .setLabels("豆子,水果,蔬菜")
                .setNickName("流绪微梦")
                .setHeadImgUrl("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=432370639,967036417&fm=116&gp=0.jpg")
                .setSignature("那个暖我心房之人盗我之心而后弃我离去！")
                .setBgImgUrl("http://desk.fd.zol-img.com.cn/t_s960x600c5/g5/M00/02/03/ChMkJ1bKxo2IIlvnACAz_oeuEpkAALHmwOXvhAAIDQW467.jpg")
                .setLastLoginTime("2016-12-17")
                .build());
        datas.add(new ComUser.Builder()
                .setSex("女")
                .setLabels("蘑菇,小白菜")
                .setNickName("夏花依旧")
                .setHeadImgUrl("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=599351666,3454232541&fm=116&gp=0.jpg")
                .setSignature("做最好的自己，才能遇见最对的人！")
                .setBgImgUrl("http://desk.fd.zol-img.com.cn/t_s960x600c5/g5/M00/02/04/ChMkJ1bKx4qIGTZLAAmjEID8lj0AALH1ADgWvsACaMo983.jpg")
                .setLastLoginTime("2016-10-14")
                .build());
        return datas;
    }

    public static List<Farm> getFarmList() {
        List<Farm> datas = new ArrayList<>();
        datas.add(new Farm.Builder()
                .setId("10000001")
                .setName("花溪农场")
                .setImgUrl("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=787324823,4149955059&fm=23&gp=0.jpg")
                .setDesc("花溪农场位于风景秀丽的都江堰市蒲阳镇花溪村，占地面积约1000余亩。")
                .setTime("2016-10-17")
                .build());
        datas.add(new Farm.Builder()
                .setId("10000001")
                .setName("清境农场")
                .setImgUrl("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2006772353,1636214348&fm=23&gp=0.jpg")
                .setDesc("清境位于南投县仁爱乡，全部面积广达800多公顷，而其中的清境农场面积约有760公顷，坐拥在群山之间，视野广阔。")
                .setTime("2016-10-12")
                .build());
        datas.add(new Farm.Builder()
                .setId("10000001")
                .setName("周末农场")
                .setImgUrl("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2271934545,2694534780&fm=23&gp=0.jpg")
                .setDesc("周末农场位于国家生态文明县密云县城南，距京承高速密云县城出口仅4公里，交通方便。")
                .setTime("2016-10-2")
                .build());
        datas.add(new Farm.Builder()
                .setId("10000001")
                .setName("慢哈屯农场")
                .setImgUrl("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1801242187,3590977731&fm=23&gp=0.jpg")
                .setDesc("慢哈屯家庭农场成立于2011年，于2013年正式注册，是江西省第一家家家庭农场，农场种养面积近9000亩。")
                .setTime("2016-10-14")
                .build());
        return datas;
    }

    public static List<ControlFunction> getFunList() {
        List<ControlFunction> datas = new ArrayList<>();
        datas.add(new ControlFunction("灌溉"));
        datas.add(new ControlFunction("施肥"));
        datas.add(new ControlFunction("施药"));
        datas.add(new ControlFunction("通风"));
        datas.add(new ControlFunction("光照"));
        datas.add(new ControlFunction("温度"));
        datas.add(new ControlFunction("湿度"));
//        datas.add(new ControlFunction("CO2"));
//        datas.add(new ControlFunction("O2"));
        datas.add(new ControlFunction("遮阳"));
        return datas;
    }

    public static List<Device> getDeviceList() {
        List<Device> datas = new ArrayList<>();
        datas.add(new Device("设备1", "开"));
        datas.add(new Device("设备2", "关"));
        datas.add(new Device("设备3", "关"));
        datas.add(new Device("设备4", "开"));
        datas.add(new Device("设备5", "开"));
        datas.add(new Device("设备6", "关"));
        datas.add(new Device("设备7", "开"));
        datas.add(new Device("设备8", "开"));
        return datas;
    }

    public static Farm getFarm() {
        return new Farm.Builder()
                .setId("10000001")
                .setName("响堂生态农场")
                .setDesc("本农场主要主产空心菜、大白菜，占地约400平米，年均收入12.5万元！")
                .setTime("2017-01-06 15:01:44.0")
                .setImgUrl("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2269463426,4180877422&fm=23&gp=0.jpg")
                .setLabel(new Label("空心菜,大白菜"))
                .build();
    }

    public static List<ControlSystem> getSystemList() {
        List<ControlSystem> datas = new ArrayList<>();
        datas.add(new ControlSystem("响堂生态农场独立系统", "响堂生态农场位于风景秀丽的重庆市长寿湖景区，占地面积约1000余亩", "4-23", "http://img5.imgtn.bdimg.com/it/u=1786812688,1537849891&fm=23&gp=0.jpg"));
        datas.add(new ControlSystem("花溪农场1号系统", "花溪农场1号系统主要是对大棚幼苗进行的灌溉控制系统", "5-21", "http://img2.imgtn.bdimg.com/it/u=1744560215,364222809&fm=23&gp=0.jpg"));
        datas.add(new ControlSystem("花溪农场2号系统", "花溪农场2号系统主要是针对大棚时令蔬菜的灌溉控制系统", "4-23", "http://img2.imgtn.bdimg.com/it/u=3731387221,1353789239&fm=23&gp=0.jpg"));
        datas.add(new ControlSystem("周末农场独立系统", "周末农场独立系统针对阳光小幼苗的灌溉，年收益达到250W元", "5-21", "http://img3.imgtn.bdimg.com/it/u=2027538747,3977863732&fm=23&gp=0.jpg"));
        return datas;
    }

    public static List<IFarmCamera> getCameraList() {
        List<IFarmCamera> datas = new ArrayList<>();
        datas.add(new IFarmCamera.Builder()
                .setName("一号相机")
                .setPreImgUrl("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1610543840,3224296086&fm=23&gp=0.jpg")
                .setLoaction("重庆市长寿区大同村")
                .setShedNo(1)
                .setDeviceSerial("626439264")
                .setChannelNo(1)
                .setVerifyCode("SEGHDP")
                .build());
        datas.add(new IFarmCamera.Builder()
                .setName("二号相机")
                .setPreImgUrl("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3655280356,2560859842&fm=23&gp=0.jpg")
                .setLoaction("重庆市长寿区云集镇")
                .setShedNo(2)
                .setDeviceSerial("626439264")
                .setChannelNo(1)
                .setVerifyCode("SEGHDP")
                .build());
        datas.add(new IFarmCamera.Builder()
                .setName("三号相机")
                .setPreImgUrl("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1881844521,3810691313&fm=23&gp=0.jpg")
                .setLoaction("重庆市长寿区黄葛村")
                .setShedNo(3)
                .setDeviceSerial("626439264")
                .setChannelNo(1)
                .setVerifyCode("SEGHDP")
                .build());
        datas.add(new IFarmCamera.Builder()
                .setName("四号相机")
                .setPreImgUrl("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3078144622,342983672&fm=23&gp=0.jpg")
                .setLoaction("重庆市长寿区秀山村")
                .setShedNo(4)
                .setDeviceSerial("626439264")
                .setChannelNo(1)
                .setVerifyCode("SEGHDP")
                .build());
        datas.add(new IFarmCamera.Builder()
                .setName("五号相机")
                .setPreImgUrl("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=85177375,4035412627&fm=23&gp=0.jpg")
                .setLoaction("重庆市长寿区长寿湖村")
                .setShedNo(5)
                .setDeviceSerial("626439264")
                .setChannelNo(1)
                .setVerifyCode("SEGHDP")
                .build());
        return datas;
    }

    public static List<Task> getTaskList() {
        List<Task> datas = new ArrayList<>();
        datas.add(new Task.Builder()
                .setId(1)
                .setArea("A")
                .setName("灌溉")
                .setStartTime("2017-5-4 5:30:20")
                .setDuration("25 min")
                .setStatusByNum(123)
                .setStatus(Task.TaskStatus.Completed)
                .build());
        datas.add(new Task.Builder()
                .setId(2)
                .setArea("B")
                .setName("施肥")
                .setStatusByNum(213)
                .setStartTime("2017-5-5 15:35:00")
                .setDuration("35 min")
                .setStatus(Task.TaskStatus.Running)
                .build());
        datas.add(new Task.Builder()
                .setId(3)
                .setArea("C")
                .setName("施药")
                .setStatusByNum(56)
                .setStartTime("2017-5-6 12:25:20")
                .setDuration("32 min")
                .setStatus(Task.TaskStatus.Waiting)
                .build());
        datas.add(new Task.Builder()
                .setId(4)
                .setArea("D")
                .setName("灌溉")
                .setStatusByNum(177)
                .setStartTime("2017-5-7 23:55:20")
                .setDuration("10 min")
                .setStatus(Task.TaskStatus.Waiting)
                .build());
        datas.add(new Task.Builder()
                .setId(5)
                .setArea("E")
                .setName("通风")
                .setStatusByNum(89)
                .setStartTime("2017-5-8 14:24:20")
                .setDuration("55 min")
                .setStatus(Task.TaskStatus.Waiting)
                .build());
        return datas;
    }

    public static List<OperationArea> getOperationAreas() {
        List<OperationArea> datas = new ArrayList<>();

        datas.add(new OperationArea("01", "A"));
        datas.add(new OperationArea("02", "B"));
        datas.add(new OperationArea("03", "C"));
        datas.add(new OperationArea("04", "D"));
        datas.add(new OperationArea("05", "E"));

        return datas;
    }
}

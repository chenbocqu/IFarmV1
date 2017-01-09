/**
 * 智能农场模拟数据
 */
package com.qican.ifarm.utils;

import com.qican.ifarm.bean.ComUser;
import com.qican.ifarm.bean.ControlFunction;
import com.qican.ifarm.bean.Device;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.bean.MonitorNode;

import java.util.ArrayList;
import java.util.List;

public class IFarmFakeData {

    public static List<MonitorNode> getMontiorList() {
        List<MonitorNode> datas = new ArrayList<>();
        datas.add(
                new MonitorNode.Builder()
                        .setName("琼山光明村")
                        .setStatus("在线")
                        .setHumidity("56.7")
                        .setTemperature1("34.8")
                        .setTemperature2("32.2")
                        .setWaterContent("46")
                        .setImgUrl("http://desk.fd.zol-img.com.cn/t_s960x600c5/g5/M00/02/03/ChMkJlbKxmyIJBaWAAeJtiCgphUAALHlAEaoHgAB4nO760.jpg")
                        .setUpdateTime("2015/2/23 下午3:32:19")
                        .build());
        datas.add(
                new MonitorNode.Builder()
                        .setName("美兰苏民村")
                        .setStatus("在线")
                        .setHumidity("64.7")
                        .setTemperature1("34.8")
                        .setTemperature2("32.2")
                        .setWaterContent("87.6")
                        .setImgUrl("http://image101.360doc.com/DownloadImg/2016/10/2813/83246025_16.jpg")
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
                        .setImgUrl("http://www.3lian.com/d/file/201612/19/8be9240271a5830200cd8482c86f743f.jpg")
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
                .setName("秀英统历村")
                .setImgUrl("http://www.3lian.com/d/file/201612/19/8be9240271a5830200cd8482c86f743f.jpg")
                .setDesc("如果你在济南师范，认识一名学生，他的名字叫崔萌萌。请告诉他，他欠一个男生一辈子，永远偿还不了。他下辈子做猫做狗，也不会再做痴情种。嗯，就这样")
                .setTime("2016-10-17")
                .build());
        datas.add(new Farm.Builder()
                .setName("美兰苏民村")
                .setImgUrl("http://desk.fd.zol-img.com.cn/t_s960x600c5/g5/M00/02/03/ChMkJlbKxmyIJBaWAAeJtiCgphUAALHlAEaoHgAB4nO760.jpg")
                .setDesc("要相信那个 和你聊天聊着聊着 就一不小心睡了的人 那一定是最在乎你的人.")
                .setTime("2016-10-12")
                .build());
        datas.add(new Farm.Builder()
                .setName("秀英统历村")
                .setImgUrl("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=599351666,3454232541&fm=116&gp=0.jpg")
                .setDesc("世界上哪有那么多的将心比心 你一味的付出不过是惯出来得寸进尺的人 太过考虑别人的感受 就注定自己不好受 所以啊 余生没那么长 请你忠于自己 活得还像自己")
                .setTime("2016-10-2")
                .build());
        datas.add(new Farm.Builder()
                .setName("琼山光明村")
                .setImgUrl("http://image101.360doc.com/DownloadImg/2016/10/2813/83246025_16.jpg")
                .setDesc("我闺蜜没跟我报一个学校，只因为有个男生说，你报我学校吧，这样就能一个学校了，然后她就报了，那个男生是我对象，呵呵")
                .setTime("2016-10-14")
                .build());
        return datas;
    }

    public static List<ControlFunction> getFunList() {
        List<ControlFunction> datas = new ArrayList<>();
        datas.add(new ControlFunction("灌溉"));
        datas.add(new ControlFunction("通风"));
        datas.add(new ControlFunction("光照"));
        datas.add(new ControlFunction("温度"));
        datas.add(new ControlFunction("湿度"));
        datas.add(new ControlFunction("CO2"));
        datas.add(new ControlFunction("O2"));
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
}

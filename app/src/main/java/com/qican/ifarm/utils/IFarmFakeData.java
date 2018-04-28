/**
 * 智能农场模拟数据
 */
package com.qican.ifarm.utils;

import com.qican.ifarm.bean.ComUser;
import com.qican.ifarm.bean.ControlFunction;
import com.qican.ifarm.bean.ControlSystem;
import com.qican.ifarm.bean.ControlTask;
import com.qican.ifarm.bean.Device;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.bean.IFarmCamera;
import com.qican.ifarm.bean.Label;
import com.qican.ifarm.bean.MonitorNode;
import com.qican.ifarm.bean.News;
import com.qican.ifarm.bean.OperationArea;
import com.qican.ifarm.bean.Operations;
import com.qican.ifarm.bean.Question;
import com.qican.ifarm.bean.Subarea;
import com.qican.ifarm.bean.Task;

import java.util.ArrayList;
import java.util.List;

public class IFarmFakeData {

    public static String NONE_OPERATION = "none_of_operation";

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
                .setTime("2016-10-17 12:12:10")
                .build());
        datas.add(new Farm.Builder()
                .setId("10000001")
                .setName("清境农场")
                .setImgUrl("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2006772353,1636214348&fm=23&gp=0.jpg")
                .setDesc("清境位于南投县仁爱乡，全部面积广达800多公顷，而其中的清境农场面积约有760公顷，坐拥在群山之间，视野广阔。")
                .setTime("2016-5-12 17:25:10")
                .build());
        datas.add(new Farm.Builder()
                .setId("10000001")
                .setName("周末农场")
                .setImgUrl("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2271934545,2694534780&fm=23&gp=0.jpg")
                .setDesc("周末农场位于国家生态文明县密云县城南，距京承高速密云县城出口仅4公里，交通方便。")
                .setTime("2016-12-2 22:12:10")
                .build());
        datas.add(new Farm.Builder()
                .setId("10000001")
                .setName("慢哈屯农场")
                .setImgUrl("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1801242187,3590977731&fm=23&gp=0.jpg")
                .setDesc("慢哈屯家庭农场成立于2011年，于2013年正式注册，是江西省第一家家家庭农场，农场种养面积近9000亩。")
                .setTime("2016-1-14 15:10:10")
                .build());
        return datas;
    }

    public static List<ControlFunction> getFunList() {
        List<ControlFunction> datas = new ArrayList<>();
        datas.add(new ControlFunction("灌溉"));
        datas.add(new ControlFunction("施肥"));
        datas.add(new ControlFunction("施药"));
        datas.add(new ControlFunction("通风"));
        datas.add(new ControlFunction("温度"));
        datas.add(new ControlFunction("湿度"));
        datas.add(new ControlFunction("光照"));
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
                .setDeviceSerial("761008117")
                .setChannelNo(1)
                .setVerifyCode("PGGSWF")
                .build());
        datas.add(new IFarmCamera.Builder()
                .setName("二号相机")
                .setPreImgUrl("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3655280356,2560859842&fm=23&gp=0.jpg")
                .setLoaction("重庆市长寿区云集镇")
                .setShedNo(2)
                .setDeviceSerial("761008157")
                .setChannelNo(1)
                .setVerifyCode("TIIDUE")
                .build());
        datas.add(new IFarmCamera.Builder()
                .setName("三号相机")
                .setPreImgUrl("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1881844521,3810691313&fm=23&gp=0.jpg")
                .setLoaction("重庆市长寿区黄葛村")
                .setShedNo(3)
                .setDeviceSerial("761008117")
                .setChannelNo(1)
                .setVerifyCode("PGGSWF")
                .build());
        datas.add(new IFarmCamera.Builder()
                .setName("四号相机")
                .setPreImgUrl("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3078144622,342983672&fm=23&gp=0.jpg")
                .setLoaction("重庆市长寿区秀山村")
                .setShedNo(4)
                .setDeviceSerial("761008157")
                .setChannelNo(1)
                .setVerifyCode("TIIDUE")
                .build());
        datas.add(new IFarmCamera.Builder()
                .setName("五号相机")
                .setPreImgUrl("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=85177375,4035412627&fm=23&gp=0.jpg")
                .setLoaction("重庆市长寿区长寿湖村")
                .setShedNo(5)
                .setDeviceSerial("761008117")
                .setChannelNo(1)
                .setVerifyCode("PGGSWF")
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
                .setStopTime("2017-5-5 23:13:20")
                .setDuration("25 min")
                .setStatusByNum(123)
                .setStatus(Task.TaskStatus.Completed)
                .setFertilizationCan("3")
                .setControlArea("A,D")
                .setExecutionTime("20")
                .setStartExecutionTime("6:45")
                .setTaskTime("6:00 am")
                .setControlType("fertilizer")
                .build());
        datas.add(new Task.Builder()
                .setId(2)
                .setArea("B")
                .setName("施肥")
                .setStatusByNum(213)
                .setStartTime("2017-5-5 15:35:00")
                .setStopTime("2017-5-5 23:13:20")
                .setDuration("35 min")
                .setStatus(Task.TaskStatus.Running)
                .setFertilizationCan("1,3")
                .setControlArea("B,C,E")
                .setExecutionTime("63")
                .setStartExecutionTime("9:30")
                .setTaskTime("9:25 am")
                .setControlType("fertilizer")
                .build());
        datas.add(new Task.Builder()
                .setId(3)
                .setArea("C")
                .setName("施药")
                .setStatusByNum(56)
                .setStartTime("2017-5-6 12:25:20")
                .setStopTime("2017-5-8 23:13:20")
                .setDuration("32 min")
                .setStatus(Task.TaskStatus.Waiting)
                .setFertilizationCan("1,2")
                .setControlArea("B,D")
                .setExecutionTime("123")
                .setStartExecutionTime("10:30")
                .setTaskTime("9:45 am")
                .setControlType("fertilizer")
                .build());
        datas.add(new Task.Builder()
                .setId(4)
                .setArea("D")
                .setName("灌溉")
                .setStatusByNum(177)
                .setStartTime("2017-5-7 23:55:20")
                .setStopTime("2017-5-8 23:13:20")
                .setDuration("10 min")
                .setStatus(Task.TaskStatus.Waiting)
                .setFertilizationCan("3")
                .setControlArea("A,D")
                .setExecutionTime("20")
                .setStartExecutionTime("6:45")
                .setTaskTime("6:00 am")
                .setControlType("fertilizer")
                .build());
        datas.add(new Task.Builder()
                .setId(5)
                .setArea("E")
                .setName("通风")
                .setStatusByNum(89)
                .setStartTime("2017-5-8 14:24:20")
                .setStopTime("2017-5-10 23:13:20")
                .setDuration("55 min")
                .setStatus(Task.TaskStatus.Waiting)
                .setFertilizationCan("2,3")
                .setControlArea("B,A,E")
                .setExecutionTime("84")
                .setStartExecutionTime("11:25")
                .setTaskTime("10:25 am")
                .setControlType("fertilizer")
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

    public static List<News> getNews() {

        List<News> datas = new ArrayList<>();

        datas.add(new News("二师兄又跌了", "养猪人我来告诉你 猪价一直下跌的真正原因？", "2017-6-2 12:00", "http://media.nxin.com/nongmult/Uploads/2016-09-14/57d90658f1c85.jpg"));
        datas.add(new News("农业基地", "佑慧客农业登陆新四板,打造休闲观光型定制农业基地", "2017-6-3 12:25", "http://cms-bucket.nosdn.127.net/catchpic/1/18/182162c54a7fd5c8701576db74d63437.jpg?imageView&thumbnail=550x0"));
        datas.add(new News("农业新品种", "南充6个新品种被推介为省农业主导品种", "2017-3-2 11:00", "https://files.nxin.com/public/yuantu/2017/4/1/39/435eda83-0a68-4823-8110-c2def86dafda.png"));
        datas.add(new News("专家建议", "农业专家提醒加强春季麦田管理", "2017-6-1 12:01", "https://ss0.baidu.com/7Po3dSag_xI4khGko9WTAnF6hhy/image/h%3D200/sign=400b89ebd333c895b97e9f7be1137397/0b7b02087bf40ad12df6399f5d2c11dfa9ecceb8.jpg"));
        datas.add(new News("再现黑科技", "土豆枝上结番茄 农业新技术显神奇", "2017-5-30 12:05", "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=445667820,4136615665&fm=23&gp=0.jpg"));

        return datas;
    }

    public static List<Question> getQuestions() {
        List<Question> datas = new ArrayList<>();
        ArrayList<String> imgUrls = new ArrayList<>();
        imgUrls.add("https://gss0.baidu.com/-fo3dSag_xI4khGko9WTAnF6hhy/zhidao/wh%3D600%2C800/sign=b6ae349cb63eb1354492bfbd962e84eb/902397dda144ad346f269ff3daa20cf431ad852c.jpg");
        imgUrls.add("https://gss0.baidu.com/-fo3dSag_xI4khGko9WTAnF6hhy/zhidao/wh%3D600%2C800/sign=eff4c35964061d957d133f3e4bc426e9/5366d0160924ab18d1de5b563ffae6cd7a890b82.jpg");
        imgUrls.add("https://gss0.baidu.com/9fo3dSag_xI4khGko9WTAnF6hhy/zhidao/wh%3D600%2C800/sign=da7c8952b0a1cd1105e37a268922e4c4/37d12f2eb9389b506d628d308f35e5dde7116e36.jpg");
        datas.add(new Question("加勒比海盗5", "加勒比海盗5：死无对证被遭黑客攻击并盗取成片，你怎么看？", imgUrls, "2017-5-24 10:32:20"));

        ArrayList<String> imgUrls1 = new ArrayList<>();
        imgUrls1.add("https://gss0.baidu.com/7Po3dSag_xI4khGko9WTAnF6hhy/zhidao/wh%3D600%2C800/sign=70073596771ed21b799c26e39d5ef1fc/fcfaaf51f3deb48f7bc705bafa1f3a292df5782c.jpg");
        imgUrls1.add("https://gss0.baidu.com/9vo3dSag_xI4khGko9WTAnF6hhy/zhidao/wh%3D600%2C800/sign=14c6c71a9858d109c4b6a1b4e168e087/11385343fbf2b2115a81212ac08065380cd78e23.jpg");
        imgUrls1.add("https://gss0.baidu.com/-4o3dSag_xI4khGko9WTAnF6hhy/zhidao/wh%3D600%2C800/sign=9a4b1ce5c11349547e4be062667ebe60/3bf33a87e950352a02c8816c5943fbf2b2118b2d.jpg");
        datas.add(new Question("儿童节要不要给家长放假？", "根据报道，每年的儿童节，孩子们都会期盼如如果爸妈也能放假该多好啊！而根据国务院相关规定，6月1日不满14周岁的少年儿童放假1天。", imgUrls1, "2017-6-5 12:33:20"));

        ArrayList<String> imgUrls2 = new ArrayList<>();
        imgUrls2.add("https://gss0.baidu.com/-vo3dSag_xI4khGko9WTAnF6hhy/zhidao/wh%3D600%2C800/sign=40708c192aa446237e9fad64a8125e36/f636afc379310a559e4b2897bd4543a982261061.jpg");
        imgUrls2.add("https://gss0.baidu.com/-Po3dSag_xI4khGko9WTAnF6hhy/zhidao/wh%3D600%2C800/sign=b0384175c5fc1e17fdea84377aa0da3b/6609c93d70cf3bc76cac3b8fdb00baa1cd112a21.jpg");
        imgUrls2.add("https://gss0.baidu.com/7Po3dSag_xI4khGko9WTAnF6hhy/zhidao/wh%3D600%2C800/sign=ba83114804f41bd5da06e0f261eaadf3/f2deb48f8c5494ee3782f97a27f5e0fe99257e35.jpg");
        datas.add(new Question("为什么范冰冰计划40岁退休？", "现年35岁的范冰冰日前为《第70届戛纳电影节》担任评审，她于电影节圆满结束后接受内地传媒访问。当中她更自曝打算40岁就不当演员，去环游世界。"
                , imgUrls2, "2017-6-3 23:13:20"));
        datas.add(new Question("加勒比海盗5", "加勒比海盗5：死无对证被遭黑客攻击并盗取成片，你怎么看？", imgUrls, "2017-5-24 10:32:20"));
        datas.add(new Question("儿童节要不要给家长放假？", "根据报道，每年的儿童节，孩子们都会期盼如如果爸妈也能放假该多好啊！而根据国务院相关规定，6月1日不满14周岁的少年儿童放假1天。", imgUrls1, "2017-6-5 12:33:20"));
        datas.add(new Question("为什么范冰冰计划40岁退休？", "现年35岁的范冰冰日前为《第70届戛纳电影节》担任评审，她于电影节圆满结束后接受内地传媒访问。当中她更自曝打算40岁就不当演员，去环游世界。"
                , imgUrls2, "2017-6-3 23:13:20"));

        return datas;
    }

    public static List<Subarea> getSuberas() {
        List<Subarea> datas = new ArrayList<>();

        Subarea subarea = new Subarea();
        subarea.setName("分区A");
        subarea.setImgUrl("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=776200233,317664553&fm=200&gp=0.jpg");
        datas.add(subarea);

        Subarea subarea1 = new Subarea();
        subarea1.setName("分区B");
        subarea1.setImgUrl("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2754269779,2694862479&fm=200&gp=0.jpg");
        datas.add(subarea1);

        Subarea subarea2 = new Subarea();
        subarea2.setName("分区C");
        subarea2.setImgUrl("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=90237287,3012894917&fm=27&gp=0.jpg");
        datas.add(subarea2);

        return datas;
    }

    public static List<ControlTask> getControlTasks() {
        List<ControlTask> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ControlTask task = new ControlTask();
            task.setOperation("升温");
            task.setStartTime("2017-6-3 23:13:20");
            task.setStatus("运行中");

            tasks.add(task);

            ControlTask task1 = new ControlTask();
            task1.setOperation("降温");
            task1.setStartTime("2017-6-3 23:13:20");
            task1.setStatus("运行中");

            tasks.add(task1);
        }
        return tasks;
    }

    public static List<Operations> getOperationsByCode1(String code) {
        List<Operations> operationses = new ArrayList<>();

        switch (code) {
            case "wendu"://温度
                operationses.add(new Operations("升温", "rise"));
                operationses.add(new Operations("降温", "desc"));
                break;

            case "shidu": // 湿度
                operationses.add(new Operations("增湿", "rise"));
                operationses.add(new Operations("除湿", "desc"));
                break;

            case "通风": // 通风
                operationses.add(new Operations("进风", "rise"));
                operationses.add(new Operations("出风", "desc"));
                operationses.add(new Operations("轴流", "desc"));
                break;

            case "补光": // 补光，七组颜色补光
                operationses.add(new Operations("赤色", "rise"));
                operationses.add(new Operations("橙色", "desc"));
                operationses.add(new Operations("黄色", "desc"));
                operationses.add(new Operations("绿色", "rise"));
                operationses.add(new Operations("青色", "desc"));
                operationses.add(new Operations("蓝色", "desc"));
                operationses.add(new Operations("紫色", "desc"));
                break;

            case "外遮阳": // 外遮阳
                operationses.add(new Operations("打开", "open"));
                operationses.add(new Operations("遮蔽", "close"));
                break;

            case "内遮阳": // 内遮阳
                operationses.add(new Operations("打开", "rise"));
                operationses.add(new Operations("遮蔽", "desc"));
                break;

            case "topRollerShuttersControl": // 顶卷帘
                operationses.add(new Operations("升", "rise"));
                operationses.add(new Operations("降", "desc"));
                break;

            case "sideRollerShuttersControl": // 侧卷帘
                operationses.add(new Operations("升", "rise"));
                operationses.add(new Operations("降", "desc"));
                break;

            case "二氧化碳": // 二氧化碳
                operationses.add(new Operations("增加CO2浓度", "rise"));
                operationses.add(new Operations("降低CO2浓度", "desc"));
                break;

            case "氧气": // 氧气
                operationses.add(new Operations("增加O2浓度", "rise"));
                operationses.add(new Operations("降低O2浓度", "desc"));
                break;

            default:
                operationses.add(new Operations("暂无相关操作", NONE_OPERATION));
        }
        return operationses;
    }

    public static List<Operations> getOperationsByCode(String code) {
        List<Operations> operationses = new ArrayList<>();
        operationses.add(new Operations("暂无相关操作", NONE_OPERATION));
        return operationses;
    }
}

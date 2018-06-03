# IFarm

    XX应用

## 1. 介绍

    用于XX系统的移动端

首页
<img src="https://github.com/chenbocqu/IFarmV1/blob/master/doc/UI/home_page.jpg" width="70%"/>

首页
<img src="https://github.com/chenbocqu/IFarmV1/blob/master/doc/UI/control_funs.jpg" width="70%"/>

# 功能

## 1、数据监测

    http://localhost:8080/IFarm/device/category

返回

    array：[{
        "deviceCategory": "collectorDevice",
        "deviceCategoryName": "采集设备",
        "deviceType": [{
            "deviceType": "collectorType5",
            "deviceTypeName": "五合一采集设备"
        }]
    }, {
        "deviceCategory": "controlDevice",
        "deviceCategoryName": "控制设备",
        "deviceType": [{
            "deviceType": "type1",
            "deviceTypeName": "一路输出设备"
        }, {
            "deviceType": "type2",
            "deviceTypeName": "两路输出设备"
        }, {
            "deviceType": "type3",
            "deviceTypeName": "四路输出设备"
        }]
    }, {
        "deviceCategory": "concentrator",
        "deviceCategoryName": "集中器",
        "deviceType": [{
            "deviceType": "concentrator",
            "deviceTypeName": "集中器"
        }]
    }]

## 2、控制

    http://localhost:8080/IFarm/device/production?batch=1&deviceCategory=controlDevice&deviceType=type1

返回

    {
        "response": "success", //"error"即为失败，不会有devices信息
        "devices": [{
            "deviceId": 115174041,  //设备编号
            "deviceVerification": "ydsopF7NVf",  //验证码
            "createTime": "2018-01-31 13:47:04.0"  //创建时间
        }]
    }

当batch=3时，批量生产

    {
        "response": "success",
        "devices": [{
            "deviceId": 822724144,
            "deviceVerification": "FlYz9qrnTb",
            "createTime": "2018-01-31 13:48:49.0"
        }, {
            "deviceId": 909811140,
            "deviceVerification": "YtMhcfZgDx",
            "createTime": "2018-01-31 13:48:49.0"
        }, {
            "deviceId": 151831267,
            "deviceVerification": "AKR3C7mU32",
            "createTime": "2018-01-31 13:48:49.0"
        }]
    }



## 3、视频监控

|code|角色|备注
|---|---|---|
|farmer|农场主|无|
|vipFarmer|vip农场主|可以开通三个以后的农场管理人员|
|onlySee|只能查看农场数据|无|
|doControl|可以操作控制|无|

user目前权限分为上述四级，后续还会开通visitor模式，如果是onlySee的用户在系统控制界面，系统会返回

    "no_auth"

目前，系统的控制也需要权限认证，否则不能通过

    http://localhost:8080/IFarm/user/addSubUser?userId=00000000000&farmId=10000001&authority=onlySee

返回

    {"response":"success"}  success:成功，error：失败，full_subUser:超过3个人 no_auth:没有权限

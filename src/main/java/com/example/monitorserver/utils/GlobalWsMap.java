package com.example.monitorserver.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.monitorserver.constant.Constants;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.Statistics;
import com.example.monitorserver.po.WsVo;
import com.example.monitorserver.service.SendEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yeauty.pojo.Session;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @program: MonitorServer
 * @description:
 * @author: stop.yc
 * @create: 2022-09-09 22:33
 **/
@Component
@Slf4j
public class GlobalWsMap {

    @Autowired
    private SendEmailService sendEmailService;

    public static ConcurrentHashMap<String, List<Session>> WS_BY_USER_ID_AND_PRO_ID_MAP = new ConcurrentHashMap<>();


//    public final ConcurrentHashMap<String, List<Statistics>> PROJECT_ERROR_STATISTICS = new ConcurrentHashMap<>();


    public static void sendMessage(String projectName, Result result,Integer type) {
        WsVo wsVo = new WsVo(result.getData(),type);

        //首先先把result解析为json对象
        String jsonString = JSON.toJSONString(wsVo);

        //对所属的监控项目进行发送数据
        for (String s : WS_BY_USER_ID_AND_PRO_ID_MAP.keySet()) {
            if (s.equalsIgnoreCase(projectName)) {
                for (Session session : WS_BY_USER_ID_AND_PRO_ID_MAP.get(s)) {
                    session.sendText(jsonString);
                }
            }
        }
    }

    public static void onLine(String projectName,Session session) {


        if (CollUtil.isEmpty(WS_BY_USER_ID_AND_PRO_ID_MAP.get(projectName))) {
            ArrayList<Session> sessions = new ArrayList<>();
            sessions.add(session);

            WS_BY_USER_ID_AND_PRO_ID_MAP.put(projectName,sessions);
        }else {
            WS_BY_USER_ID_AND_PRO_ID_MAP.get(projectName).add(session);
        }
    }

    public static void leave(Session session) {
        Collection<List<Session>> values = WS_BY_USER_ID_AND_PRO_ID_MAP.values();
        for (List<Session> value : values) {
            value.remove(session);
        }
    }
//
//    public void addOneData(String projectName,Integer type) {
//
//        System.out.println("PROJECT_ERROR_STATISTICS = " + PROJECT_ERROR_STATISTICS);
//
//
//        //1.先拿出项目里面的数据集合
//        List<Statistics> statistics = PROJECT_ERROR_STATISTICS.get(projectName);
//
//        //2.如果为null,表示没有这个项目的数据
//        if (null == statistics) {
//            //新建一个数据集合,用于存放数据
//            statistics = new ArrayList<>();
//            //新建一个数据对象
//            Statistics data = new Statistics();
//            //统计数量记为1,设置时间为当前时间,并加入type
//            data.setCount(1).setLastTime(LocalDateTime.now()).setType(type);
//            //添加
//            statistics.add(data);
//            //放置
//            PROJECT_ERROR_STATISTICS.put(projectName,statistics);
//            return;
//        }
//
//        //3.表示集合不为空,表示里面有项目这个key,所以存在着集合,但是不一定有数据
//
//        //4.首先先查找对应type的数据对象
//        List<Statistics> collect = statistics
//                    .stream()
//                    .filter(s -> s.getType().equals(type))
//                    .limit(1)
//                    .collect(Collectors.toList());
//
//        //5.如果为空,表示表示没有这个类型的统计数据
//        if (CollUtil.isEmpty(collect)) {
//            //新建一个统计数据
//            Statistics data = new Statistics();
//            //
//            data.setCount(1).setLastTime(LocalDateTime.now()).setType(type);
//            //添加
//            statistics.add(data);
//
//            return;
//        }
//
//        //6.如果有数据,那么就需要取第一个元素出来,进行统计
//        Statistics needCalData = collect.get(0);
//
//        Integer count = needCalData.getCount() + 1;
//
//        //7.更新统计数据,更新最后一次错误时间
//        needCalData.setCount(count);
//
//        //8.判断是否超出阈值,(时间范围内,次数达到最大数量)
////        System.out.println("Constants.MAX_COUNT_SECONDS = " + Constants.MAX_COUNT_SECONDS);
////        System.out.println("Constants.MAX_SECONDS = " + Constants.MAX_SECONDS);
////
//        System.out.println("Duration.between(needCalData.getLastTime(),LocalDateTime.now()).toMillis() / 1000 = " + Duration.between(needCalData.getLastTime(), LocalDateTime.now()).toMillis() / 1000L);
//        if (Duration.between(needCalData.getLastTime(),LocalDateTime.now()).toMillis() / 1000L < Constants.MAX_SECONDS
//                && count >= Constants.MAX_COUNT_SECONDS) {
//
//            System.out.println("进来了啊");
//
//            System.out.println();
//            System.out.println(needCalData.getLastMailTime() == null);
//            System.out.println();
//            if (needCalData.getLastMailTime() == null) {
//                needCalData.setLastMailTime(LocalDateTime.now().plusDays(-1));
//            }
//
//            System.out.println("getLastMailTime = " + needCalData.getLastMailTime());
//            needCalData.setLastTime(LocalDateTime.now());
//            System.out.println("Duration.between(needCalData.getLastMailTime(),LocalDateTime.now()).toMillis() / 1000 = " + Duration.between(needCalData.getLastMailTime(), LocalDateTime.now()).toMillis() / 1000L);
//            if (needCalData.getLastMailTime() == null || Duration.between(needCalData.getLastMailTime(),LocalDateTime.now()).toMillis() / 1000L >= Constants.MAX_MAIL_DURATION_SECOND) {
//
//                //达到阈值,需要发送邮件进行提醒;
//
//                System.out.println("发送邮件!!!!11");
//                ChooseTypeSendEmail(projectName,needCalData);
//
//                //更新最后一次发邮件的时间
//                needCalData.setLastMailTime(LocalDateTime.now());
//            }
//            //发送邮件的间隔时间过短,则不发送
//
//
//        }else if (count >= Constants.MAX_COUNT_SECONDS){
//            //如果间隔时间过长,则重新统计
//            needCalData.setCount(0);
//
//        }
//        needCalData.setLastTime(LocalDateTime.now());
//
//    }
//
//    private void ChooseTypeSendEmail(String projectName,Statistics data) {
//
//        String subject = "【监控信息预警】";
//        String typeOfErr = "";
//        String text = "";
//        switch (data.getType()) {
//            case 1:
//                typeOfErr = "Js错误";
//                break;
//            case 2:
//                typeOfErr = "白屏错误";
//                break;
//            case 3:
//                typeOfErr = "资源错误";
//            default:break;
//        }
//
//        text = "您所监控的项目:"+ projectName + "的" + typeOfErr + "已经达到每" + Constants.MAX_SECONDS + "秒" + data.getCount() + "次,请及时上线排查错误";
//
//        sendEmailService.sendSimpleEmail(subject,text,projectName);
//    }
}

package com.example.monitorserver.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.monitorserver.po.Result;
import com.example.monitorserver.po.WsVo;
import org.yeauty.pojo.Session;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: MonitorServer
 * @description:
 * @author: stop.yc
 * @create: 2022-09-09 22:33
 **/
public class GlobalWsMap {
    public static ConcurrentHashMap<String, List<Session>> WS_BY_USER_ID_AND_PRO_ID_MAP = null;



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

        if (WS_BY_USER_ID_AND_PRO_ID_MAP == null) {
            WS_BY_USER_ID_AND_PRO_ID_MAP = new ConcurrentHashMap<>();
        }

        if (WS_BY_USER_ID_AND_PRO_ID_MAP.get(projectName) == null || WS_BY_USER_ID_AND_PRO_ID_MAP.get(projectName).isEmpty() ) {
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
            if (value.contains(session)) {
                    value.remove(session);
            }
        }
    }
}

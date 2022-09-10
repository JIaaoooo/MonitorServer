package com.example.monitorserver.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @program: MonitorServer
 * @description:
 * @author: stop.yc
 * @create: 2022-09-10 22:50
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WsVo {
    private Object data;

    private int type;
}

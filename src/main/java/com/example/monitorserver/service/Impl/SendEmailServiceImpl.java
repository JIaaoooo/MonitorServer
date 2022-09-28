package com.example.monitorserver.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.monitorserver.constant.RedisEnum;
import com.example.monitorserver.mapper.ProjectMapper;
import com.example.monitorserver.mapper.UserMapper;
import com.example.monitorserver.mapper.UserProjectMapper;
import com.example.monitorserver.po.Project;
import com.example.monitorserver.po.Statistics;
import com.example.monitorserver.po.User;
import com.example.monitorserver.po.UserProject;
import com.example.monitorserver.service.SendEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.example.monitorserver.constant.Constants.*;

/**
 * @program: MonitorServer
 * @description:
 * @author: stop.yc
 * @create: 2022-09-11 16:01
 **/
@Service
@Slf4j
public class SendEmailServiceImpl implements SendEmailService {
    @Resource
    private JavaMailSenderImpl mailSender;

    @Resource
    private UserProjectMapper userProjectMapper;

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;


//    public final ConcurrentHashMap<String, List<Statistics>> PROJECT_ERROR_STATISTICS = new ConcurrentHashMap<>();


    @Resource
    private UserMapper userMapper;

    //注入发件人地址【这里获取的是全局配置里的username】

    @Value("${spring.mail.username}")
    private String from;


    @Override
    public Boolean sendSimpleEmail(String subject, String text, String projectName) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);

        //通过项目名获取项目id
        String projectId = getProjectIdByProjectName(projectName);

        //用户id集合
        List<String> ids = getUserIdsByProjectId(projectId);

        //拿到用户id下的所有的邮箱地址
        List<String> emails = getEmailsByUserIds(ids);

        for (String email : emails) {
            //发送的对象
            message.setTo(email);

            //标题
            message.setSubject(subject);

            //内容
            message.setText(text);

            //发送
            sendEmail(message);
        }

        return true;
    }

    @Override
    public void addOneData(String projectName, Integer type) {

//        log.debug("目前各个项目的统计数据有: {}",PROJECT_ERROR_STATISTICS);
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
//                .stream()
//                .filter(s -> s.getType().equals(type))
//                .limit(1)
//                .collect(Collectors.toList());
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
//        if (Duration.between(needCalData.getLastTime(),LocalDateTime.now()).toMillis() / 1000L < MAX_SECONDS
//                && count >= Constants.MAX_COUNT_SECONDS) {
//
//            log.debug("错误频率已经到达{}秒{}次", MAX_SECONDS,Constants.MAX_COUNT_SECONDS);
//
//            if (needCalData.getLastMailTime() == null) {
//                needCalData.setLastMailTime(LocalDateTime.now().plusDays(-1));
//            }
//
//            needCalData.setLastTime(LocalDateTime.now());
//            if (needCalData.getLastMailTime() == null || Duration.between(needCalData.getLastMailTime(),LocalDateTime.now()).toMillis() / 1000L >= Constants.MAX_MAIL_DURATION_SECOND) {
//
//                //达到阈值,需要发送邮件进行提醒;
//                log.debug("距离上一次邮箱预警已经超过了{}秒,准备继续邮箱预警",Constants.MAX_MAIL_DURATION_SECOND);
//
//                //如果成功发送预警
//                if (ChooseTypeSendEmail(projectName,needCalData)) {
//                    //则更新最后一次发邮件的时间
//                    needCalData.setLastMailTime(LocalDateTime.now());
//                }
//            }
//            //发送邮件的间隔时间过短,则不发送
//            else {
//                log.debug("邮件发送过于频繁,暂停发送邮件预警");
//            }
//        }else if (count >= Constants.MAX_COUNT_SECONDS){
//            //如果间隔时间过长,则重新统计
//
//            needCalData.setCount(0);
//            statistics = statistics.stream().filter(s-> !s.getType().equals(type)).collect(Collectors.toList());
//        }
//        needCalData.setLastTime(LocalDateTime.now());

    }


    /**
     * redis缓存记录错误统计数据,并进行实时预警与邮箱发送.
     * @param projectName: 项目名
     * @param type:错误类型.
     */
    @Override
    public void newAddOneData(String projectName, Integer type){
        //先建好key
        String key = RedisEnum.MONITOR_MAIL.getMsg() + projectName + ":" + type;

        //1.获得了项目名和type(某种类型的报错),需要先查询redis缓存中有无该键
        //如果没有,则需要保留一个对象,count为1,邮箱日期为过去一天,新建保留日期.

        if (!redisTemplate.hasKey(key)) {
            //新建一个key,用来存储统计数据与邮箱发送情况
            addANewDataOfError(projectName,type);
            return ;
        }

        //2.如果存在该键,首先获取其计数情况
        int count = (int)redisTemplate.opsForHash().get(key,"count");

        //3.对其进行+1,并更新redis统计数据
        redisTemplate.opsForHash().put(key,"count",++count);
        log.debug("count 的次数已经达到了 {} 次",count);

        //4.判断该数是否已经到达了阈值
        if (count >= MAX_COUNT_SECONDS) {
            //5.达到了阈值,需要判断是否超过发送邮件的频率
            //获取上一次发送邮件的时间,
            String lastMailTime = (String) redisTemplate.opsForHash().get(key, "lastMailTime");
            LocalDateTime parse = LocalDateTimeUtil.parse(lastMailTime);

            //6.判断发送邮件的间隔时间会不会太短
            log.debug("邮件发送相距时间为:{}秒",Duration.between(parse,LocalDateTime.now()).getSeconds());
            if (Math.abs(Duration.between(parse,LocalDateTime.now()).getSeconds()) > MAX_MAIL_DURATION_SECOND) {
                //不会太短,则进行发送邮件
                chooseTypeSendEmail(projectName,new Statistics().setType(type).setCount(count));
                //并对统计数据进行清空
                redisTemplate.opsForHash().put(key,"count",0);
                redisTemplate.opsForHash().put(key,"lastMailTime",LocalDateTime.now().toString());
            }else {
                //频率过高,则不进行发送邮件
                log.debug("发送邮件过于频繁");
            }
            redisTemplate.expire(RedisEnum.MONITOR_MAIL.getMsg() + projectName +":"+ type,Duration.ofSeconds(MAX_SECONDS));
        }
    }

    private void addANewDataOfError(String projectName,Integer type) {
        Statistics statistics = new Statistics().setCount(1).setLastMailTime(LocalDateTime.now().plusDays(-1));
        Map<String, Object> map = BeanUtil.beanToMap(statistics);
        map.put("lastMailTime",map.get("lastMailTime").toString());

        redisTemplate.opsForHash().putAll(RedisEnum.MONITOR_MAIL.getMsg() + projectName +":"+ type,map);
        redisTemplate.expire(RedisEnum.MONITOR_MAIL.getMsg() + projectName +":"+ type,Duration.ofSeconds(MAX_SECONDS));
    }

    private void chooseTypeSendEmail(String projectName, Statistics data) {

        String subject = "【监控信息预警】";
        String typeOfErr = "";
        String text;
        switch (data.getType()) {
            case 1:
                typeOfErr = "Js错误";
                break;
            case 2:
                typeOfErr = "白屏错误";
                break;
            case 3:
                typeOfErr = "资源错误";
            default:break;
        }

        text = "您所监控的项目:"+ projectName + "的" + typeOfErr + "已经达到每" + MAX_SECONDS + "秒" + data.getCount() + "次,请及时上线排查错误";

        sendSimpleEmail(subject, text, projectName);
    }


    private String getProjectIdByProjectName(String projectName) {
        //首先查询监控的项目的项目id
        QueryWrapper<Project> projectQueryWrapper = new QueryWrapper<>();

        projectQueryWrapper.select("project_id").lambda().eq(Project::getProjectName, projectName);

        Project project = projectMapper.selectOne(projectQueryWrapper);

        return project.getProjectId();
    }

    private List<String> getUserIdsByProjectId(String projectId) {
        //查询监控的项目的用户id集合
        QueryWrapper<UserProject> userProjectQueryWrapper = new QueryWrapper<>();

        userProjectQueryWrapper.select("user_id").lambda().eq(UserProject::getProjectId, projectId);
        List<UserProject> userProjects = userProjectMapper.selectList(userProjectQueryWrapper);
        List<String> ids = new LinkedList<>();
        for (UserProject userProject : userProjects) {
            ids.add(userProject.getUserId());
        }

        return ids;
    }

    private List<String> getEmailsByUserIds(List<String> userIds) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        List<String> emails = new LinkedList<>();
        for (String userId : userIds) {
            userQueryWrapper.clear();

            userQueryWrapper.select("phone").lambda().eq(User::getUserId, userId);

            User user = userMapper.selectOne(userQueryWrapper);

            emails.add(user.getPhone());
        }
        return emails;
    }

    private void sendEmail(SimpleMailMessage message) {
        log.debug("发送的短信为:{}",message);
        try {
            mailSender.send(message);
            log.debug("邮件发送成功");
        } catch (MailException e) {
            log.debug("邮件发送失败");
            e.printStackTrace();
        }
    }
}

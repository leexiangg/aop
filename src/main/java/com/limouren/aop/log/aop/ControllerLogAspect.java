package com.limouren.aop.log.aop;

import com.alibaba.fastjson.JSON;
import com.limouren.aop.common.SystemEnum;
import com.limouren.aop.log.entity.MsgLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
@Aspect
public class ControllerLogAspect {
    //切点入口 Controller包下面所有类的所有方法
    private final String pointcut = "execution(* com.limouren.aop.controller..*(..))";

    private AsyncInsertMsgLog asyncInsertMsgLog;

    @Autowired
    public void setAsyncInsertMsgLog(AsyncInsertMsgLog asyncInsertMsgLog) {
        this.asyncInsertMsgLog = asyncInsertMsgLog;
    }

    private MsgLog msgLog;

    //切点
    @Pointcut(value = pointcut)
    public void log() {
    }

    @Before(value = "log()")
    public void before(JoinPoint joinPoint) throws Throwable {
        msgLog = new MsgLog();
        try {
            // 请求时间
            msgLog.setReqTime(new Date());
            // 请求数据头
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            // 请求方 前端系统
            msgLog.setReqFrom(SystemEnum.SYSTEM_FRONT.getCode());
            // 被请求方 本系统
            msgLog.setReqTo(SystemEnum.SYSTEM_SELF.getCode());
            // 请求类型
            msgLog.setReqType(request.getMethod());
            // 请求URL
            msgLog.setReqUrl(request.getRequestURI());
            // 请求模块
            String[] split = request.getRequestURI().split("/");
            if(split.length > 2) {
                // 请求模块
                msgLog.setModel(split[split.length - 2]);
                // 请求方法
                String[] methods = split[split.length - 1].split("[?]");
                msgLog.setFunction(methods[0]);
            }
            // 调用类
            msgLog.setClassName(joinPoint.getTarget().getClass().getName());
            // 调用方法
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            msgLog.setClassFunction(methodSignature.getMethod().getName());
            // 请求数据头信息
            msgLog.setReqHeader(JSON.toJSON(getHeader(request)).toString());
            // 请求IP
            msgLog.setReqIp(getIpAddress(request));
            // 请求数据
            String reqmsg = JSON.toJSON(joinPoint.getArgs()).toString();
            msgLog.setReqMsg(reqmsg);
            // 打印文件日志
            LoggerFactory.getLogger(msgLog.getClassName()).info(msgLog.getReqIp() + " " + msgLog.getReqType() + "请求 " + msgLog.getReqUrl() + "，请求信息：" + reqmsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterReturning(returning = "result", pointcut = "log()")
    public Object afterReturn(Object result) {
        try {
            String rspmsg = null;
            if(msgLog != null) {
                // 响应时间
                msgLog.setRspTime(new Date());
                if(result != null) {
                    Map<String, Object> r = JSON.parseObject(JSON.toJSON(result).toString());
                    if(r.get("result") != null) {
                        // 响应状态 成功/失败
                        if((boolean) r.get("result")) {
                            msgLog.setRspStatus("成功");
                        } else {
                            msgLog.setRspStatus("失败");
                            // 错误响应码、错误响应信息
                            if(r.get("body") != null) {
                                Map<String, String> b = (Map<String, String>) r.get("body");
                                if(b.containsKey("code"))
                                    msgLog.setErrCode(b.get("code"));
                                if(b.containsKey("content"))
                                    msgLog.setErrMsg(b.get("content"));
                            }
                        }
                    }
                    // 响应内容
                    rspmsg = JSON.toJSON(result).toString();
                    msgLog.setRspMsg(rspmsg);
                }
                // 耗时
                if(msgLog.getReqTime() != null)
                    msgLog.setUseTime(msgLog.getRspTime().getTime() - msgLog.getReqTime().getTime());
            }
            // 保存到数据库
            if(msgLog != null) {
                try {
                    asyncInsertMsgLog.addMsgLog(msgLog);
                } catch (Exception e) {}
                // 打印文件日志
                LoggerFactory.getLogger(msgLog.getClassName()).info("交易耗时" + msgLog.getUseTime() + "毫秒，响应信息：" + rspmsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @AfterThrowing(pointcut = "log()",throwing = "exception")
    public void afterThrowing(JoinPoint joinPoint, Throwable exception){
        try {
            if(msgLog != null) {
                // 响应时间
                msgLog.setRspTime(new Date());
                msgLog.setErrMsg(exception.getMessage());
                msgLog.setRspStatus("失败");
                // 耗时
                if(msgLog.getReqTime() != null)
                    msgLog.setUseTime(msgLog.getRspTime().getTime() - msgLog.getReqTime().getTime());
            }
            // 保存到数据库
            if(msgLog != null) {
                try {
                    asyncInsertMsgLog.addMsgLog(msgLog);
                } catch (Exception e) {}
                // 打印文件日志
                LoggerFactory.getLogger(msgLog.getClassName()).info("处理异常，交易耗时" + msgLog.getUseTime() + "毫秒，异常信息：" + exception.getMessage());
            }
        } catch (Exception e) {}
    }

    /**
     * 从request中获取请求方IP
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 从request中获取请求数据头
     * @param request
     * @return
     */
    public static Map<String, String> getHeader(HttpServletRequest request) {
        Map<String, String> header = new HashMap<>();
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String key = names.nextElement();
            header.put(key, request.getHeader(key));
        }
        return header;
    }
}

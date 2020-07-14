package com.limouren.aop.log.aop;

import com.alibaba.fastjson.JSON;
import com.limouren.aop.common.SystemEnum;
import com.limouren.aop.log.entity.MsgLog;
import feign.Request;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 1、在调用LoadBalancerFeignClient中的execute方法前，获取请求信息
 * 2、在LoadBalancerFeignClient中的execute方法中，如果报错，则记录调用失败
 * 3、在FeignClient接口响应完成后，获取响应内容
 */
@Component
@Aspect
public class InterfaceLogAspect {
    // 切点一：Feign中的ribbon.LoadBalancerFeignClient调用入口，为了获取请求Request
    private final String pointcutRibbon = "execution(* org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient.execute(..))";
    // 切点二：FeignClient接口，为了获取响应字符串
    private final String pointcutFeign = "execution(* com.limouren.aop.feign..*(..))";

    private MsgLog msgLog;

    private AsyncInsertMsgLog asyncInsertMsgLog;

    @Autowired
    public void setAsyncInsertMsgLog(AsyncInsertMsgLog asyncInsertMsgLog) {
        this.asyncInsertMsgLog = asyncInsertMsgLog;
    }

    //Feign中的Ribbon切点
    @Pointcut(value = pointcutRibbon)
    public void logRibbon() {
    }

    //Feign接口切点
    @Pointcut(value = pointcutFeign)
    public void logFeign() {
    }

    /**
     * 在调用FeignClient接口前，获取类和方法信息
     * @param joinPoint
     * @throws Throwable
     */
    @Before(value = "logFeign()")
    public void beforeFeign(JoinPoint joinPoint) {
        try {
            msgLog = new MsgLog();
            // 请求时间
            msgLog.setReqTime(new Date());
            // 请求方 本系统
            msgLog.setReqFrom(SystemEnum.SYSTEM_SELF.getCode());
            // 被请求方 外系统
            msgLog.setReqTo(SystemEnum.SYSTEM_OTHER.getCode());
            // 调用类
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            msgLog.setClassName(methodSignature.getDeclaringTypeName());
            // 调用方法
            msgLog.setClassFunction(methodSignature.getMethod().getName());
            // 请求数据
            String reqmsg = JSON.toJSONString(joinPoint.getArgs());
            msgLog.setReqMsg(reqmsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 在调用LoadBalancerFeignClient中的execute方法前，获取请求信息
     * @param joinPoint
     * @throws Throwable
     */
    @Before(value = "logRibbon()")
    public void beforeRibbon(JoinPoint joinPoint) {
        try {
            // 请求数据头
            Request request = (Request) joinPoint.getArgs()[0];
            // 请求类型
            msgLog.setReqType(request.method());
            // 请求URL
            msgLog.setReqUrl(request.url());
            // 请求模块
            String[] split = request.url().split("/");
            if(split.length > 2) {
                // 请求模块
                msgLog.setModel(split[split.length - 2]);
                // 请求方法
                String[] methods = split[split.length - 1].split("[?]");
                msgLog.setFunction(methods[0]);
            }
            // 请求数据头信息
            msgLog.setReqHeader(JSON.toJSONString(request.headers()));
            // 请求IP
            msgLog.setReqIp("0.0.0.0");
            // 打印文件日志
            LoggerFactory.getLogger(msgLog.getClassName()).info("调用接口平台" + msgLog.getReqType() + "请求 " + msgLog.getReqUrl() + "，请求信息：" + msgLog.getReqMsg());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 在FeignClient接口响应完成后，获取响应内容
     * @param result
     * @return
     */
    @AfterReturning(returning = "result", pointcut = "logFeign()")
    public Object afterReturn(Object result) {
        try {
            String rspmsg = null;
            if(msgLog != null) {
                // 响应时间
                msgLog.setRspTime(new Date());
                // 响应内容
                if(result != null) {
                    if(result instanceof String)
                        rspmsg = result.toString();
                    else
                        rspmsg = JSON.toJSONString(result);
                    msgLog.setRspMsg(rspmsg);
                }
                // 只要调用成功，就算成功，不管业务逻辑
                msgLog.setRspStatus("成功");
                // 耗时
                if(msgLog.getReqTime() != null)
                    msgLog.setUseTime(msgLog.getRspTime().getTime() - msgLog.getReqTime().getTime());
                // 保存到数据库
                asyncInsertMsgLog.addMsgLog(msgLog);
                // 打印文件日志
                LoggerFactory.getLogger(msgLog.getClass()).info("交易耗时" + msgLog.getUseTime() + "毫秒，响应信息：" + rspmsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 在LoadBalancerFeignClient中的execute方法中，如果报错，则记录调用失败
     * @param joinPoint
     * @param exception
     */
    @AfterThrowing(pointcut = "logRibbon()", throwing = "exception")
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
                // 记录到数据库
                try {
                    asyncInsertMsgLog.addMsgLog(msgLog);
                } catch (Exception e) {}
                // 打印文件日志
                LoggerFactory.getLogger(msgLog.getClassName()).info("处理异常，交易耗时" + msgLog.getUseTime() + "毫秒，异常信息：" + exception.getMessage());
            }
        } catch (Exception e) {}
    }
}

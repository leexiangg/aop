package com.limouren.aop.log.aop;

import com.alibaba.fastjson.JSON;
import com.limouren.aop.log.entity.MsgLog;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 异步写接口调用日志
 * 写入失败会重写一次，
 */
@Component
@EnableAsync
public class AsyncInsertMsgLog {

    private static boolean isRunnung = false;

    private static Queue<MsgLogTimes> msgLogQueue = new ConcurrentLinkedQueue<>();

    public void addMsgLog(MsgLog msgLog) {
        MsgLogTimes msgLogTimes = new MsgLogTimes(msgLog, 0);
        msgLogQueue.add(msgLogTimes);
        if(!AsyncInsertMsgLog.isRunnung)
            run();
    }

    @Async
    public void run() {
        AsyncInsertMsgLog.isRunnung = true;
        while(msgLogQueue.size() > 0) {
            MsgLogTimes msgLogTimes = msgLogQueue.poll();
            try {
                // 写数据库操作，省略
                LoggerFactory.getLogger(msgLogTimes.getMsgLog().getClassName()).info(JSON.toJSON(msgLogTimes.getMsgLog()).toString());
            } catch (Exception e) {
                msgLogTimes.plusTimes();
                if(msgLogTimes.getTimes() == 0) {
                    msgLogQueue.add(msgLogTimes);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException interruptedException) {
                    }
                }
            }
        }
        AsyncInsertMsgLog.isRunnung = false;
    }

    class MsgLogTimes {
        private MsgLog msgLog;
        private int times;

        public MsgLogTimes(MsgLog msgLog, int times) {
            this.msgLog = msgLog;
            this.times = times;
        }

        public MsgLog getMsgLog() {
            return msgLog;
        }

        public void plusTimes() {
            times++;
        }

        public int getTimes() {
            return times;
        }
    }

}

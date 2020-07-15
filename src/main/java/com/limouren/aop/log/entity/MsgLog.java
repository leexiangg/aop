package com.limouren.aop.log.entity;

import java.util.Date;

/**
 * 系统请求响应表
 *
 * @author lixiang 2020-04-25 17:52:14
 */
public class MsgLog {

    /** 主键 */
    private Long id;
    /** 请求方 1本系统/2前台系统/3接口系统 */
    private String reqFrom;
    /** 被请求方 1本系统/2前台系统/3接口系统 */
    private String reqTo;
    /** 请求类型 get post delete等 */
    private String reqType;
    /** 请求地址 */
    private String reqUrl;
    /** 模块 */
    private String model;
    /** 方法 */
    private String function;
    /** 调用类 */
    private String className;
    /** 调用方法 */
    private String classFunction;
    /** 请求数据头信息 */
    private String reqHeader;
    /** 请求IP */
    private String reqIp;
    /** 请求地区 */
    private String reqLocation;
    /** 请求时间 */
    private Date reqTime;
    /** 请求数据 */
    private String reqMsg;
    /** 响应状态 成功/失败 */
    private String rspStatus;
    /** 错误响应码 */
    private Integer errCode;
    /** 错误响应信息 */
    private String errMsg;
    /** 响应时间 */
    private Date rspTime;
    /** 响应数据 */
    private String rspMsg;
    /** 耗时 毫秒 */
    private Long useTime;


    /**
     * @return 主键
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    /**
     * @return 请求方 1本系统/2前台系统/3接口系统
     */
    public String getReqFrom() {
        return reqFrom;
    }

    public void setReqFrom(String reqFrom) {
        this.reqFrom = reqFrom;
    }
    /**
     * @return 被请求方 1本系统/2前台系统/3接口系统
     */
    public String getReqTo() {
        return reqTo;
    }

    public void setReqTo(String reqTo) {
        this.reqTo = reqTo;
    }
    /**
     * @return 请求类型 get post delete等
     */
    public String getReqType() {
        return reqType;
    }

    public void setReqType(String reqType) {
        this.reqType = reqType;
    }
    /**
     * @return 请求地址
     */
    public String getReqUrl() {
        return reqUrl;
    }

    public void setReqUrl(String reqUrl) {
        this.reqUrl = reqUrl;
    }
    /**
     * @return 模块
     */
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
    /**
     * @return 方法
     */
    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
    /**
     * @return 调用类
     */
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
    /**
     * @return 调用方法
     */
    public String getClassFunction() {
        return classFunction;
    }

    public void setClassFunction(String classFunction) {
        this.classFunction = classFunction;
    }
    /**
     * @return 请求数据头信息
     */
    public String getReqHeader() {
        return reqHeader;
    }

    public void setReqHeader(String reqHeader) {
        this.reqHeader = reqHeader;
    }
    /**
     * @return 请求IP
     */
    public String getReqIp() {
        return reqIp;
    }

    public void setReqIp(String reqIp) {
        this.reqIp = reqIp;
    }
    /**
     * @return 请求时间
     */
    public Date getReqTime() {
        return reqTime;
    }

    public void setReqTime(Date reqTime) {
        this.reqTime = reqTime;
    }
    /**
     * @return 请求数据
     */
    public String getReqMsg() {
        return reqMsg;
    }

    public void setReqMsg(String reqMsg) {
        this.reqMsg = reqMsg;
    }
    /**
     * @return 响应状态 成功/失败
     */
    public String getRspStatus() {
        return rspStatus;
    }

    public void setRspStatus(String rspStatus) {
        this.rspStatus = rspStatus;
    }
    /**
     * @return 错误响应码
     */
    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }
    /**
     * @return 错误响应信息
     */
    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
    /**
     * @return 响应时间
     */
    public Date getRspTime() {
        return rspTime;
    }

    public void setRspTime(Date rspTime) {
        this.rspTime = rspTime;
    }
    /**
     * @return 响应数据
     */
    public String getRspMsg() {
        return rspMsg;
    }

    public void setRspMsg(String rspMsg) {
        this.rspMsg = rspMsg;
    }
    /**
     * @return 耗时 毫秒
     */
    public Long getUseTime() {
        return useTime;
    }

    public void setUseTime(Long useTime) {
        this.useTime = useTime;
    }
}

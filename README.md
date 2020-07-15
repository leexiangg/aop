博文链接：[https://blog.csdn.net/leexiangg/article/details/106842906](https://blog.csdn.net/leexiangg/article/details/106842906)

# 双切面实现集中打印Feign日志
- 1、背景
- 2、单切：记录Controller日志
  - 2.1、切入点配置
  - 2.2、方法执行前
  - 2.3、方法执行后
  - 2.4、方法执行异常时
- 3、双切：记录Feign日志
  - 3.1、切入点配置
  - 3.2、在调用FeignClient接口前
  - 3.3、在调用LoadBalancerFeignClient中的execute方法前
  - 3.4、在FeignClient接口响应完成后
  - 3.5、在LoadBalancerFeignClient中的execute方法抛出异常
- 4、异步记录数据库
- 5、业务失败不回滚
- 附录一：切入点配置
- 附录二：切入时间配置
# ThreadPoolDemo
Java ThreadPool 的使用以及构建基础的工具类，以供项目使用
自定义Java线程池的各项参数，添加钩子函数，监控信息，打印日志等，
暴露给外部接口为JobExecutor.execute(TimeOutJob)等.
JobResult 用来封装任务执行结果，同时保存异常等消息。
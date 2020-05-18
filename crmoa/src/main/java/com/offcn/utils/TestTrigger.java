package com.offcn.utils;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class TestTrigger {

    public static void main(String[] args) throws Exception {
        //1.job任务 jobdetail
        JobDetail job = JobBuilder.newJob(MsgJob.class).withIdentity("job1","group1").build();

        //2.时间规则（10s  10次）
        SimpleScheduleBuilder builer = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(10).withRepeatCount(10);

        //3.时间触发器
        SimpleTrigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1","group1").withSchedule(builer).startNow().build();

        //4.调度器(调配 哪个触发器触发哪个任务)
        Scheduler sc = new StdSchedulerFactory().getScheduler();
        sc.scheduleJob(job, trigger);
        sc.start();

        Thread.sleep(120*1000);

        //停止任务调度
        sc.shutdown();

    }
}

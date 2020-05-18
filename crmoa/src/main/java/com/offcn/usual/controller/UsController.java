package com.offcn.usual.controller;

import com.offcn.pro.bean.Employee;
import com.offcn.pro.bean.Function;
import com.offcn.pro.bean.Module;
import com.offcn.pro.service.EmpService;
import com.offcn.pro.service.FunService;
import com.offcn.pro.service.ModService;
import com.offcn.usual.bean.*;
import com.offcn.usual.dao.MsgMapper;
import com.offcn.usual.service.ArcService;
import com.offcn.usual.service.BxService;
import com.offcn.usual.service.NoticeService;
import com.offcn.usual.service.TaskService;
import com.offcn.utils.MDoc;
import com.offcn.utils.MsgJob;
import com.offcn.utils.QueryObj;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("us")
public class UsController {

    @Autowired
    TaskService ts;
    @Autowired
    FunService fs;
    @Autowired
    ModService ms;
    @Autowired
    NoticeService ns;
    @Autowired
    ArcService as;

    @Autowired
    MsgMapper mm;
    @Autowired
    EmpService es;

    @Autowired
    BxService bs;

    @RequestMapping("addTask")
    @ResponseBody
    public int addTask(Task task, HttpSession session){
        Employee emp= (Employee) session.getAttribute("emp");
        task.setEmpFk(emp.getEid());
        task.setStatus(0);
        int i = ts.saveTask(task);
        return i;
    }

    /*
      获取当前登录用户下发过的所有任务
     */
    @RequestMapping("getTlist")
    public String getTlist(Model m, QueryObj qo, @RequestParam(defaultValue = "-1") int st, HttpSession session){
        Employee emp= (Employee) session.getAttribute("emp");
        Map map=new HashMap<String,Object>();
        map.put("eid",emp.getEid());
        map.put("qo",qo);
        map.put("st",st);
        List<TaskView> tlist = ts.getTlist(map);
        m.addAttribute("tlist",tlist);
        return "usual/task";
    }

    /*
       编辑回显任务
     */
    @RequestMapping("getTaskById")
    public String getTaskById(Model m,int tid){
        Task t = ts.getByTid(tid);
        m.addAttribute("t",t);
        //获得功能的对象
        Function fun = fs.getFunById(t.getFunFk());
        //m.addAttribute("f",fun);
        //获得模块
        Module mod = ms.getModByMid(fun.getModeleFk());
        m.addAttribute("m",mod);
        return "usual/task-edit";
    }

    /*
       获取当前登录用户需要执行的任务
     */
    @RequestMapping("getMyTlist")
    public String getMyTlist(@RequestParam(defaultValue = "6") int st,Model m,QueryObj qo,HttpSession session){
        //st:首页点击未完成任务数时传st为-1,查询未完成任务；否则st为默认值6，不做关于状态的查询
        Employee emp= (Employee) session.getAttribute("emp");
        Map map=new HashMap<String,Object>();
        map.put("eid",emp.getEid());
        map.put("qo",qo);
        map.put("st",st);
        List<TaskView> tlist = ts.getMyTlist(map);
        m.addAttribute("tlist",tlist);
        return "usual/task-my";
    }

    /*
      更新任务状态
     */
    @RequestMapping("updStatus")
    public String updStatus(int tid,int st){
        int i = ts.updateStatus(tid, st);
        return "redirect:/us/getMyTlist";
    }

    /*
       查询当前登录用户未完成的任务
     */
    @RequestMapping("getUnTaskCount")
    @ResponseBody
    public int getUnTaskCount(HttpSession session){
        Employee emp= (Employee) session.getAttribute("emp");
        int count = ts.getUnTaskCount(emp.getEid());
        return count;
    }


    ///////////////////////////////////公告/////////////////////////////////////
    /*
      获取最新几条公告
     */
    @RequestMapping("getLastNotice")
     @ResponseBody
     public List<Notice> getLastNotice(){
         return ns.getLast();
     }

     /*
       根据公告的主键获取公告详情
      */
     @RequestMapping("getNoticeByNid")
     @ResponseBody
     public Notice getNoticeByNid(int nid){
         Notice notice = ns.getNoticeByNid(nid);
         return notice;
     }


     /////////////////////////////////档案//////////////////////////////////
    /*
      我的档案
     */
    @RequestMapping("getMyArc")
     public String getMyArc(Model m,HttpSession session,@RequestParam(defaultValue = "0")  int eid){
         Employee emp= (Employee) session.getAttribute("emp");
         if(eid==0){
           //我的档案
             eid=emp.getEid();
         }
         Employee arcemp = as.getEmpInfo(eid);
         m.addAttribute("em",arcemp);
         return "usual/myarchives";
     }

     /*
       保存档案
      */
     @RequestMapping("saveArc")
     public String saveArc(Employee emp){
         int i = as.saveOrUpdate(emp);
         if(i>0){
             return "redirect:/us/getMyArc";
         }else{
             return "main/error";
         }

     }

     /*
       下载档案
      */
     @RequestMapping("downf")
     public ResponseEntity<byte[]> downf(HttpSession session) throws Exception {
         Employee emp= (Employee) session.getAttribute("emp");
         Employee emparc = as.getEmpInfo(emp.getEid());//
         Map map=new HashMap();
         map.put("ename",emparc.getEname());
         map.put("esex",emparc.getEsex());
         map.put("bir",new SimpleDateFormat("yyyy-MM-dd").format(emparc.getArc().getBirdate()));
         map.put("mz",emparc.getArc().getMinzu());

         MDoc m=new MDoc();
         File f=new File("E:\\4\\project\\files\\arcs","arc.doc");
         m.createDoc(map,f);


         HttpHeaders headers=new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
         headers.setContentDispositionFormData("attachment","arc.doc");
         return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(f),headers, HttpStatus.OK);
     }

     /*
       导入档案
      */
     @RequestMapping("addArchives")
     public String addArchives(MultipartFile files) throws IOException {
         //解析excel----->档案的集合------>批量插入
         InputStream is = files.getInputStream();
         //excel对象读取excel文件流
         HSSFWorkbook book=new HSSFWorkbook(is);
         List<Archives> alist=new ArrayList<>();
         //遍历工作薄
         for (int i = 0; i <book.getNumberOfSheets() ; i++) {
             HSSFSheet sheet = book.getSheetAt(i);
             if(sheet==null){
                 continue;
             }
             //遍历行
             for (int j = 0; j <sheet.getLastRowNum() ; j++) {
                 HSSFRow row = sheet.getRow(j+1);
                 if(row!=null){
                     Archives arc=new Archives();
                     arc.setDnum(row.getCell(0).getStringCellValue());
                     arc.setLandline(row.getCell(1).getStringCellValue());
                     arc.setSchool(row.getCell(2).getStringCellValue());
                     arc.setZhuanye(row.getCell(3).getStringCellValue());
                     arc.setSosperson(row.getCell(4).getStringCellValue());
                     arc.setBiyedate(row.getCell(5).getDateCellValue());
                     arc.setZzmm(row.getCell(6).getStringCellValue());
                     arc.setMinzu(row.getCell(7).getStringCellValue());
                     arc.setXueli(row.getCell(8).getStringCellValue());
                     arc.setEmail(row.getCell(9).getStringCellValue());
                     arc.setEmpFk((int)row.getCell(10).getNumericCellValue());
                     arc.setRemark(row.getCell(11).getStringCellValue());
                     arc.setBirdate(row.getCell(12).getDateCellValue());
                     alist.add(arc);
                 }
             }
         }
         //批量插入
         int i=as.saveMulti(alist);
         return "usual/archives";
     }

     ////////////////////////////////////消息/////////////////////////////////////
    /*
      定时发送消息
     */
    @RequestMapping("saveMsg")
    public String saveMsg(Msg msg,HttpSession session) throws SchedulerException {
        Employee emp= (Employee) session.getAttribute("emp");
        msg.setSendp(emp.getEid());
        msg.setMark(0);

        //Jobdetail
        JobDetail detail = JobBuilder.newJob(MsgJob.class).withIdentity("job1", "g1").build();
        JobDataMap map = detail.getJobDataMap();//JobDataMap:将数据传递到任务对象里（MsgJob）
        map.put("msg",msg);
        map.put("mm",mm);

        //简单触发器
        SimpleTrigger trigger = TriggerBuilder.newTrigger().withIdentity("tri1", "g1").
                withSchedule(SimpleScheduleBuilder.simpleSchedule()).startAt(msg.getMsgtime())
                .build();

        //调度器
        SchedulerFactory sf=new StdSchedulerFactory();//调度器工厂
        Scheduler scheduler = sf.getScheduler();//通过调度器工厂获得调度器

        scheduler.scheduleJob(detail,trigger);
        scheduler.start();


        return "usual/message-give";
    }

    /*
       获取非当前登录的其他用户
     */

    @RequestMapping("getUnselfEmps")
    @ResponseBody
    public List<Employee> getUnselfEmps(HttpSession session){
        Employee emp= (Employee) session.getAttribute("emp");
        return es.getUnselfEmps(emp.getEid());
    }

    //////////////////////////////报销///////////////////////////////////
    @RequestMapping("getbxtasks")
    public String getbxtasks(Model m,@RequestParam(defaultValue = "0") int flag){
        //flag=0:待审批的列表，=1：审批过的列表
        List<Baoxiao> blist = bs.getBxTasks(flag);
        m.addAttribute("blist",blist);
        m.addAttribute("flag",flag);
        return "usual/baoxiao-task";
    }

    @RequestMapping("getBxById")
    public String getBxById(Model m,String bxid,@RequestParam(defaultValue = "0") int tzfg){
        m.addAttribute("bx",bs.getBxById(bxid));
        //tzfg=0:跳转到财务审批页面baoxiao-task-edit，=1：申请人的修改页面
        if(tzfg==0){
            return "usual/baoxiao-task-edit";
        }else{
            return "usual/mybaoxiao-edit";
        }

    }

    /*
      更新报销状态
     */
    @RequestMapping("upBxStatus")
    public String upBxStatus(Baoxiao bx){
        bs.updateSts(bx);
        return "redirect:/us/getbxtasks?flag=1";
    }

    /*
      获取某个登录员工的报销单
     */
    @RequestMapping("getMyBxs")
    public String getMyBxs(Model m,HttpSession session){
        Employee emp= (Employee) session.getAttribute("emp");
        m.addAttribute("blist",bs.getMyBxs(emp.getEid()));
        return "usual/mybaoxiao-base";
    }

    /*
      申请人完成报销单更新
     */
    @RequestMapping("upMyBx")
    public String upMyBx(Baoxiao bx){
        bs.updateSts(bx);
        return "redirect:/us/getMyBxs";
    }



}

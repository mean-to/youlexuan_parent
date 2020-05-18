package com.offcn.pro.controller;

import com.github.pagehelper.PageInfo;
import com.offcn.cus.bean.Customer;
import com.offcn.cus.service.CusService;
import com.offcn.pro.bean.*;
import com.offcn.pro.service.*;
import com.offcn.utils.QueryObj;
import org.apache.commons.io.FileUtils;
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

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("pro")
public class ProController {

    @Autowired
    ProService ps;
    @Autowired
    CusService cs;
    @Autowired
    EmpService es;
    @Autowired
    ModService ms;
    @Autowired
    FunService fs;
    @Autowired
    AttService as;

    @RequestMapping
    public String getclist(Model m,@RequestParam(defaultValue = "1") int pageNO, QueryObj qo){
        PageInfo<Project> info = ps.getBypage(pageNO, qo);
        m.addAttribute("info",info);
        m.addAttribute("qo",qo);
        return "pro/project-base";
    }


    @RequestMapping("toAdd")
    public String toAdd(Model m){
        //客户公司列表
        List<Customer> clist = cs.getclist();
        m.addAttribute("clist",clist);
        //项目经理列表
        m.addAttribute("elist",es.getEmpsByPos(4));
        return "pro/project-base-add";
    }

    @RequestMapping("saveInfo")
    public String saveInfo(Project pro,String newcomname){
        //设置项目负责人的id
        pro.setComname(Integer.parseInt(newcomname.split("_")[0]));
        int i = ps.savePro(pro);
        if(i>0){
            return "redirect:/pro";
        }else{
            return "main/error";
        }
    }

    /*
      根据主键查询项目信息，返回到编辑页面
     */
    @RequestMapping("getProByPid")
    public String getProByPid(Model m,int pid){
        //返回要编辑的项目对象
        m.addAttribute("pro",ps.getProById(pid));
        //客户公司列表
        List<Customer> clist = cs.getclist();
        m.addAttribute("clist",clist);
        //项目经理列表
        m.addAttribute("elist",es.getEmpsByPos(4));
        return "pro/project-base-edit";
    }

    /*
    编辑保存
     */
    @RequestMapping("updatePro")
    public String updatePro(Project pro,String newcomname){
        //设置项目负责人的id
        pro.setComname(Integer.parseInt(newcomname.split("_")[0]));
        int i = ps.updatePro(pro);
        if(i>0){
            return "redirect:/pro";
        }else{
            return "main/error";
        }
    }


    ////////////////////////////////////需求//////////////////////////////////
    /*
      异步获取没有需求的项目
     */
    @RequestMapping("getProsNoHasNeed")
    @ResponseBody
    public List<Project> getProsNoHasNeed(){
        List<Project> plist = ps.getProNoHasNeed();
        return plist;
    }

    /*
       保存需求
     */
     @RequestMapping("saveAna")
     public String saveAna(Analysis ana){
         ana.setAddtime(new Date());
         ana.setUpdatetime(new Date());
         Project pro = ps.getProById(ana.getId());
         ana.setProname(pro.getPname());
         int i = ps.saveAna(ana);
         if(i>0){
             return "redirect:../views/pro/project-need.jsp";
         }else{
             return "main/error";
         }
     }

   //////////////////////////////////模块////////////////////////////////////
    @RequestMapping("getMlist")
    public String getMlist(Model m,@RequestParam(defaultValue = "1") int pageNO,QueryObj qo){
        PageInfo<Module> info = ms.getBypage(pageNO, qo);
        m.addAttribute("info",info);
        m.addAttribute("qo",qo);
        return "pro/project-model";
    }

    /*
      异步获取有需求的项目
     */
    @RequestMapping("getProsHasNeed")
    @ResponseBody
    public List<Analysis> getProsHasNeed(){
        List<Analysis> alist = ps.getProsHasNeed();
        return alist;
    }

    /*
      保存模块
     */
    @RequestMapping("saveMod")
    public String saveMod(Module mod,String newproname){
        mod.setProname(newproname.split("_")[1]);
        int i = ms.saveMod(mod);
        if(i>0){
            return "redirect:/pro/getMlist";
        }else{
            return "main/error";
        }
    }

    /*
      根据主键查询模块
     */
    @RequestMapping("getModByMid")
    public String getModByMid(Model m,int mid){
        Module mod = ms.getModByMid(mid);
        m.addAttribute("m",mod);
        return "pro/project-model-edit";
    }

    /*
       保存更新
     */
    @RequestMapping("saveEdit")
    public String saveEdit(Module m,String newpro){
        System.out.println(m.getLevel()+"+++++++++++++++++++++++++++++++++++++");
        m.setProname(newpro.split("_")[1]);
        //更新
        int i = ms.updateMod(m);
        if(i>0){
            return "redirect:/pro/getMlist";
        }else{
            return "main/error";
        }
    }

    ////////////////////////////////功能////////////////////////////////

    /*
      根据需求获取需求下的所有模块
     */
    @RequestMapping("getModsByAid")
    @ResponseBody
    public List<Module> getModsByAid(int aid){
        List<Module> mlist = ms.getmlist(aid);
        return mlist;
    }

    /*
      保存功能
     */
    @RequestMapping("saveFun")
    public String saveFun(Function fun,String newproname){
        System.out.println("==========="+newproname);
        fun.setProname(newproname.split("_")[1]);
        int i = fs.saveFun(fun);
        if(i>0){
            return "pro/project-function";
        }else{
            return "main/error";
        }
    }

    /*
      根据功能的主键查询功能
     */
    @RequestMapping("getFunById")
    public String getFunById(Model m,int fid){
        Function fun = fs.getFunById(fid);
        m.addAttribute("f",fun);
        return "pro/project-function-edit";
    }
    /*
       编辑保存功能
     */
    @RequestMapping("editFun")
    public String editFun(Function fun,String newpro){
        fun.setProname(newpro.split("_")[1]);
        int i = fs.updateFun(fun);
        if(i>0){
            return "pro/project-function";
        }else{
            return "main/error";
        }
    }

    //////////////////////////附件//////////////////////////////
    /*
      附件列表
     */
    @RequestMapping("getAttlist")
    public String getAttlist(Model m,QueryObj qo){
        List<Attachment> list = as.getAtts(qo);
        m.addAttribute("alist",list);
        return "pro/project-file";
    }

    /*
      附件添加页面中需要获取所有项目
     */
    @RequestMapping("getAllPro")
    @ResponseBody
    public List<Project> getAllPro(){
        return ps.getAllpro();
    }

    /*
       保存附件
     */
    @RequestMapping("saveAtt")
    public String saveAtt(Attachment att, MultipartFile files){
        //上传附件到服务器,同时将上传的路径设置到path字段
        String newname= UUID.randomUUID().toString().substring(24)+"_"+files.getOriginalFilename();
        File f=new File("E:\\crmoa\\att",newname);
        try {
            files.transferTo(f);
            att.setPath(newname);
            //保存附件到数据库
            att.setAddtime(new Date());
            att.setUpdatetime(new Date());
            as.saveAtt(att);
        } catch (IOException e) {
            e.printStackTrace();
            return "main/error";
        }
        return "redirect:/pro/getAttlist";
    }

    /*
       附件下载
     */
    @RequestMapping("download")
    public ResponseEntity<byte[]> download(String path) throws IOException {
        File f=new File("E:\\crmoa\\att",path);
        HttpHeaders headers=new HttpHeaders();
        headers.setContentDispositionFormData("attachment",path.substring(13));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(f),headers,HttpStatus.OK);
    }

    /*
      根据主键查询附件
     */
    @RequestMapping("getAttById")
    public String getAttById(Model m,int aid){
        //根据主键获取附件对象
        Attachment att = as.getAttById(aid);
        m.addAttribute("att",att);
        //获取附件所属的项目
        Project pro = ps.getProById(att.getProFk());
        m.addAttribute("pro",pro);
        return "pro/project-file-edit";
    }

    /*
      编辑保存附件信息
     */
    @RequestMapping("editAtt")
    public String editAtt(Attachment att,MultipartFile myfiles) throws IOException {
        //判断是否上传附件,否则保留原附件
        long size = myfiles.getSize();
        if(size>0){
            //上传新附件：就删除原来的附件
            File oldfile=new File("E:\\crmoa\\att",att.getPath());
            if(oldfile.exists()){
                oldfile.delete();
            }
            //上传新附件
            String newname= UUID.randomUUID().toString().substring(24)+"_"+myfiles.getOriginalFilename();
            File newfile=new File("E:\\crmoa\\att",newname);
            myfiles.transferTo(newfile);
            //设置path
            att.setPath(newname);
        }
        att.setUpdatetime(new Date());
        //更新操作
        int i = as.updateAtt(att);
        if(i>0){
            return "redirect:/pro/getAttlist";
        }else{
            return "main/error";
        }
    }

    /*
       删除附件
     */
    @RequestMapping("delfile")
    public String delfile(@RequestParam("id") List<Integer> ids){
        int i = as.delmulti(ids);
        if(i>0){
            return "redirect:/pro/getAttlist";
        }else{
            return "main/error";
        }

    }


    /*
       根据模块id获取功能
     */
    @RequestMapping("getFunsByMid")
    @ResponseBody
    public List<Function> getFunsByMid(int mid){
        return fs.getFunBYMid(mid);
    }

    /*
      项目经理级别下的所有员工
     */
    @RequestMapping("getEmpByPos")
    @ResponseBody
    public List<Employee> getEmpByPos(){
        return es.getLessManger();
    }


}

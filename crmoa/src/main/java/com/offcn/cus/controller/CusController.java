package com.offcn.cus.controller;

import com.github.pagehelper.PageInfo;
import com.offcn.cus.bean.Customer;
import com.offcn.cus.service.CusService;
import com.offcn.utils.QueryObj;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("cus")
public class CusController {

    @Autowired
    CusService cs;

    @RequestMapping
    public String getclist(Model m, @RequestParam(defaultValue = "1") int pageNO, QueryObj qo){
        PageInfo<Customer> info = cs.getByPage(pageNO,qo);
        m.addAttribute("info",info);
        m.addAttribute("qo",qo);
        System.out.println("dsfsssssssssss");//sout
        return "cus/customer";
    }

    @RequestMapping("saveInfo")
    public String saveInfo(Customer cus){
        cus.setAddtime(new Date());//alt+enter
        Integer id = cus.getId();
        int i=0;
        if(id!=null&&id!=0){
            //修改
            i=cs.updateCus(cus);
        }else{
            i = cs.saveCus(cus);
        }
        if(i>0){
          return "redirect:/cus";
        }else{
          return "main/error";
        }
    }

    @RequestMapping("getCusByCid")
    public String getCusByCid(Model m,int cid,@RequestParam(defaultValue = "0") int flag){
        m.addAttribute("cus",cs.getCusById(cid));
        if(flag==0){
            return "cus/customer-edit";
        }else{
            return "cus/customer-look";
        }
    }

    @RequestMapping("delcus")
    public String delcus(@RequestParam("id") List<Integer> ids){
        int i = cs.delCus(ids);
        if(i>0){
            return "redirect:/cus";
        }else{
            return "main/error";
        }
    }

    /*
    导出客户数据
     */
    @RequestMapping("exportExl")
    public ResponseEntity<byte[]> exportExl() throws Exception {
        List<Customer> clist = cs.getclist();
        HSSFWorkbook book=new HSSFWorkbook();
        HSSFSheet sheet = book.createSheet("客户信息列表");
        sheet.setDefaultColumnWidth(15);
        //表头行创建
        HSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue("职工序号");
        header.createCell(1).setCellValue("联系人姓名");
        header.createCell(2).setCellValue("公司名称");
        header.createCell(3).setCellValue("添加时间");
        header.createCell(4).setCellValue("联系电话");

        //clist数据写入到单元格
        for (int i = 0; i < clist.size(); i++) {
            Customer cus = clist.get(i);
            HSSFRow row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(cus.getId());
            row.createCell(1).setCellValue(cus.getCompanyperson());
            row.createCell(2).setCellValue(cus.getComname());
            row.createCell(3).setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(cus.getAddtime()));
            row.createCell(4).setCellValue(cus.getComphone());
        }

        /*file file = new file("e:\\customers.xls");
        fileoutputstream fos = new fileoutputstream(file);
        book.write(fos);*/
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        book.write(bos);


        HttpHeaders headers=new HttpHeaders();
        headers.setContentDispositionFormData("attchment",new String("客户列表.xls".getBytes("GBK"),"ISO-8859-1"));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<byte[]>(bos.toByteArray(),headers, HttpStatus.OK);
    }


}

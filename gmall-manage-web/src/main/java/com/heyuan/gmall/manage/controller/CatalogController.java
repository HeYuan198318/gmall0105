package com.heyuan.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.heyuan.gmall.bean.PmsBaseCatalog1;
import com.heyuan.gmall.bean.PmsBaseCatalog2;
import com.heyuan.gmall.bean.PmsBaseCatalog3;
import com.heyuan.gmall.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
//跨域访问的注解
@CrossOrigin
public class CatalogController {

    @Reference
    CatalogService catalogService;

    @RequestMapping("getCatalog3")
    @ResponseBody
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id){

        List<PmsBaseCatalog3> catalog13s=catalogService.getCatalog3(catalog2Id);
        return catalog13s;
    }

    @RequestMapping("getCatalog2")
    @ResponseBody
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id){

        List<PmsBaseCatalog2> catalog12s=catalogService.getCatalog2(catalog1Id);
        return catalog12s;
    }

    @RequestMapping("getCatalog1")
    @ResponseBody
    public List<PmsBaseCatalog1> getCatalog1(){

        List<PmsBaseCatalog1> catalog1s=catalogService.getCatalog1();
        return catalog1s;
    }


}

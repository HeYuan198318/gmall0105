package com.heyuan.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.heyuan.gmall.annotations.LoginRequired;
import com.heyuan.gmall.bean.PmsProductSaleAttr;
import com.heyuan.gmall.bean.PmsSkuInfo;
import com.heyuan.gmall.bean.PmsSkuSaleAttrValue;
import com.heyuan.gmall.service.SkuService;
import com.heyuan.gmall.service.SpuService;
import org.hibernate.validator.constraints.EAN;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {

    @Reference
    SkuService skuService;

    @Reference
    SpuService spuService;

    @RequestMapping("{skuId}.html")
    @LoginRequired(loginSuccess = false)
    public String item(@PathVariable String skuId, ModelMap map, HttpServletRequest request){
//    PmsSkuInfo pmsSkuInfo=skuService.getSkuById(skuId);
//    //sku对象
//    map.put("skuInfo",pmsSkuInfo);


        //可以拿去ip
        String remoteAddr = request.getRemoteAddr();
        //  request.getHeader("");  nginx负载均衡

        PmsSkuInfo pmsSkuInfo = skuService.getSkuById(skuId,remoteAddr);
        //sku对象
        map.put("skuInfo", pmsSkuInfo);

        //销售属性列表
        List<PmsProductSaleAttr> productSaleAttrs = spuService.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(), pmsSkuInfo.getId());
        map.put("spuSaleAttrListCheckBySku", productSaleAttrs);

        // 查询当前sku的spu的其他sku的集合的hash表
        Map<String, String> skuSaleAttrHash = new HashMap<>();
        List<PmsSkuInfo> pmsSkuInfos = skuService.getSkuSaleAttrValueListBySpu(pmsSkuInfo.getProductId());

        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            String k = "";
            String v = skuInfo.getId();
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
                k += pmsSkuSaleAttrValue.getSaleAttrValueId() + "|";// "239|245"
            }
            skuSaleAttrHash.put(k, v);
        }

        // 将sku的销售属性hash表放到页面
        String skuSaleAttrHashJsonStr = JSON.toJSONString(skuSaleAttrHash);
        map.put("skuSaleAttrHashJsonStr", skuSaleAttrHashJsonStr);

        return "item";
    }

    @RequestMapping("index")
    public String index(ModelMap modelMap){

        List<String> list=new ArrayList<>();
        for (int i=0;i<5;i++){
            list.add("循坏数据0"+i);
        }
        modelMap.put("list",list);
        modelMap.put("hello","hello thymeleaf");

        modelMap.put("check","1");
        return "index";
    }

}

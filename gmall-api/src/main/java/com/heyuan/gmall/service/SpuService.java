package com.heyuan.gmall.service;

import com.heyuan.gmall.bean.PmsBaseSaleAttr;
import com.heyuan.gmall.bean.PmsProductInfo;

import java.util.List;

public interface SpuService {
    List<PmsProductInfo> spuList(String catalog3Id);

    void saveSpuInfo(PmsProductInfo pmsProductInfo);
}

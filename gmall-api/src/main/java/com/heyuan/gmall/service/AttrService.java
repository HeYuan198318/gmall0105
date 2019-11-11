package com.heyuan.gmall.service;

import com.heyuan.gmall.bean.PmsBaseAttrInfo;
import com.heyuan.gmall.bean.PmsBaseAttrValue;
import com.heyuan.gmall.bean.PmsBaseSaleAttr;

import java.util.List;

public interface AttrService {

    List<PmsBaseAttrInfo> attrInfoList(String catalog3Id);


    String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseAttrValue> getAttrValueList(String attrId);

    List<PmsBaseSaleAttr> baseSaleAttrList();
}

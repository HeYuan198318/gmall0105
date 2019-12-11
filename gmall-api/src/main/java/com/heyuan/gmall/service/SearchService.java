package com.heyuan.gmall.service;

import com.heyuan.gmall.bean.PmsSearchParam;
import com.heyuan.gmall.bean.PmsSearchSkuInfo;

import java.util.List;

public interface SearchService {
    List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam);
}

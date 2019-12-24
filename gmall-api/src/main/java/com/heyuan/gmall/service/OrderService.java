package com.heyuan.gmall.service;


import com.heyuan.gmall.bean.OmsOrder;
import com.heyuan.gmall.bean.OmsOrderItem;

import java.util.List;

public interface OrderService {
    String checkTradeCode(String memberId, String tradeCode);

    void saveOrder(OmsOrder omsOrder);

    String genTradeCode(String memberId);

    OmsOrder getOrderByOutTradeNo(String outTradeNo);

    void updateOrder(OmsOrder omsOrder);

    List<OmsOrderItem> selectAllOrderItemValueList(String nickname);
}

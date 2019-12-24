package com.heyuan.gmall.order.mapper;

import com.heyuan.gmall.bean.OmsOrderItem;
import com.heyuan.gmall.bean.PmsProductSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface OmsOrderItemMapper extends Mapper<OmsOrderItem> {
    List<OmsOrderItem> selectAllOrderItemValueList(String username);

}

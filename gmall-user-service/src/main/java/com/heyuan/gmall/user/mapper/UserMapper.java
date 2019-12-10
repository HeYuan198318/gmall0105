package com.heyuan.gmall.user.mapper;

import com.heyuan.gmall.bean.UmsMember;
import org.springframework.stereotype.Controller;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Controller
public interface UserMapper extends Mapper<UmsMember> {

    List<UmsMember> selectAllUser();

}

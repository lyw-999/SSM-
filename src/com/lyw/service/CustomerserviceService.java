package com.lyw.service;

import com.lyw.bean.Customerservice;
import com.lyw.bean.CustomerserviceExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomerserviceService {
   
    long countByExample(CustomerserviceExample example);

    int deleteByExample(CustomerserviceExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Customerservice record);

    int insertSelective(Customerservice record);

    List<Customerservice> selectByExample(CustomerserviceExample example);

    Customerservice selectByPrimaryKey(Integer id);
  
    int updateByExampleSelective(@Param("record") Customerservice record, @Param("example") CustomerserviceExample example);

    int updateByExample(@Param("record") Customerservice record, @Param("example") CustomerserviceExample example);

    int updateByPrimaryKeySelective(Customerservice record);

    int updateByPrimaryKey(Customerservice record);

}

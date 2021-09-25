package com.lyw.dao;

import com.lyw.bean.Customerservice;
import com.lyw.bean.CustomerserviceExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CustomerserviceDAO {
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
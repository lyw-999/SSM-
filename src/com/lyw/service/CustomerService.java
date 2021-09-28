package com.lyw.service;

import com.lyw.bean.Customer;
import com.lyw.bean.CustomerExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomerService {
   
    long countByExample(CustomerExample example);

    int deleteByExample(CustomerExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Customer record);

    int insertSelective(Customer record);

    List<Customer> selectByExample(CustomerExample example);

    Customer selectByPrimaryKey(Integer id);
  
    int updateByExampleSelective(@Param("record") Customer record, @Param("example") CustomerExample example);

    int updateByExample(@Param("record") Customer record, @Param("example") CustomerExample example);

    int updateByPrimaryKeySelective(Customer record);

    int updateByPrimaryKey(Customer record);
    //  根据经纬度 计算money
    double getMoney(double gongSiLng, double gongSiLat, double customerLng, double customerLat);


}

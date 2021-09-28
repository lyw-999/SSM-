package com.lyw.dao;

import com.lyw.bean.Master;
import com.lyw.bean.MasterExample;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface MasterDAO {
    long countByExample(MasterExample example);

    int deleteByExample(MasterExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Master record);

    int insertSelective(Master record);

    List<Master> selectByExample(MasterExample example);

    Master selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Master record, @Param("example") MasterExample example);

    int updateByExample(@Param("record") Master record, @Param("example") MasterExample example);

    int updateByPrimaryKeySelective(Master record);

    int updateByPrimaryKey(Master record);

    Map login(String account,String password);
}
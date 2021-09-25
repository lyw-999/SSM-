package com.lyw.service;

import com.lyw.bean.Customerservice;
import com.lyw.bean.CustomerserviceExample;
import com.lyw.dao.CustomerserviceDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("customerserviceService")
public class CustomerserviceServiceImpl implements com.lyw.service.CustomerserviceService {
	@Autowired(required = false)
	private CustomerserviceDAO customerserviceDAO;
	public long countByExample(CustomerserviceExample example){
    	return customerserviceDAO.countByExample(example);
    }

	public int deleteByExample(CustomerserviceExample example){
    	return customerserviceDAO.deleteByExample(example);
    }

	public int deleteByPrimaryKey(Integer id){
    	return customerserviceDAO.deleteByPrimaryKey(id);
    }

	public int insert(Customerservice record){
    	return customerserviceDAO.insert(record);
    }

	public int insertSelective(Customerservice record){
    	return customerserviceDAO.insertSelective(record);
    }

	public List<Customerservice> selectByExample(CustomerserviceExample example){
    	return customerserviceDAO.selectByExample(example);
    }

	public Customerservice selectByPrimaryKey(Integer id){
    	return customerserviceDAO.selectByPrimaryKey(id);
    }
  
	public int updateByExampleSelective(Customerservice record, CustomerserviceExample example){
    	return customerserviceDAO.updateByExampleSelective(record, example);
    }

	public int updateByExample(Customerservice record, CustomerserviceExample example){
    	return customerserviceDAO.updateByExample(record, example);
    }

	public int updateByPrimaryKeySelective(Customerservice record){
    	return customerserviceDAO.updateByPrimaryKeySelective(record);
    }

	public int updateByPrimaryKey(Customerservice record){
    	return customerserviceDAO.updateByPrimaryKey(record);
    }


}

package com.lyw.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lyw.bean.Orders;
import com.lyw.bean.OrdersExample;
import com.lyw.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/orders")
public class OrdersController{
@Autowired(required = false)
private OrdersService ordersService;

    @RequestMapping("/addOrders")
    public Map addOrders(HttpServletRequest request,Orders orders){  //   /api/orders/addOrders
        Map codeMap = new HashMap();
        System.out.println("访问成功");
        System.out.println("orders = " + orders);
        String phoneNum = request.getParameter("phoneNum");
        System.out.println("phoneNum = " + phoneNum);
        orders.setPhone(phoneNum);
        // 调用service 层 做添加
        orders.setStatus("已接单");
        orders.setCreatetime(new Date());
        int i = ordersService.insertSelective(orders);
        if (i == 1) {
            codeMap.put("code",0);
            codeMap.put("msg","你的订单提交已完成.请耐心等待,我们将电话联系您");
            return codeMap;
            // 第一次提交完毕 将提交按钮为不可点击状态  并且 提交2字变为 已提交订单 请稍等
        }else{
            codeMap.put("code",4000);
            codeMap.put("msg","由于网络故障,未能添加成功,请在重新提交一次");
            return codeMap;
        }
    }


//增
// 后端订单增加 -- 针对layui的 针对前端传 json序列化的
@RequestMapping("/insert")
public Map insert(@RequestBody Orders orders){ // orders 对象传参, 规则: 前端属性要和后台的属性一致!!!
Map map = new HashMap();
int i =  ordersService.insertSelective(orders);
if(i>0){
map.put("code",200);
map.put("msg","添加成功");
return map;
}else{
map.put("code",400);
map.put("msg","添加失败,检查网络再来一次");
return map;
}
}


// 删
// 删除订单  根据 主键 id 删除
@RequestMapping("/deleteById")
public Map deleteById(@RequestParam(value = "id") Integer id) {
Map responseMap = new HashMap();
int i = ordersService.deleteByPrimaryKey(id);
if (i > 0) {
responseMap.put("code", 200);
responseMap.put("msg", "删除成功");
return responseMap;
} else {
responseMap.put("code", 400);
responseMap.put("msg", "删除失败");
return responseMap;
}
}

// 批量删除
@RequestMapping("/deleteBatch")
public Map deleteBatch(@RequestParam(value = "idList[]") List<Integer> idList) {
    for (Integer integer : idList) {
    this.deleteById(integer);
    }
    Map responseMap = new HashMap();
    responseMap.put("code", 200);
    responseMap.put("msg", "删除成功");
    return responseMap;
    }


// 改
    // 修改订单
    @RequestMapping("/update")
    public Map update(@RequestBody Orders  orders) {
    Map map = new HashMap();
    int i = ordersService.updateByPrimaryKeySelective( orders);
    if (i > 0) {
    map.put("code", 200);
    map.put("msg", "修改成功");
    return map;
    } else {
    map.put("code", 400);
    map.put("msg", "修改失败,检查网络再来一次");
    return map;
    }
    }

// 查--未分页
    // 全查
    @RequestMapping("/selectAll")
    public Map selectAll(){
    List<Orders> orderss =  ordersService.selectByExample(null);
        Map responseMap = new HashMap();
        responseMap.put("code", 0);
        responseMap.put("msg", "查询成功");
        responseMap.put("orderss", orderss);
        return responseMap;
        }

// 查-- 查询+自身对象的查询 + 分页
// 分页查询
    @RequestMapping("/selectAllByPage")
    public Map selectAllByPage(Orders orders, @RequestParam(value = "page", defaultValue = "1", required = true) Integer page,
    @RequestParam(value = "limit", required = true) Integer pageSize) {
    // 调用service 层   , 适用于 单表!!!
    PageHelper.startPage(page, pageSize);
    OrdersExample example = new OrdersExample();
    OrdersExample.Criteria criteria = example.createCriteria();

//    if (null!=orders.getYYYYYYYY()&&!"".equals(orders.getYYYYYYY())){
//         criteria.andPhoneEqualTo(orders.getPhone());   // 1
//    }

    List<Orders> orderssList = ordersService.selectByExample(example);
        PageInfo pageInfo = new PageInfo(orderssList);
        long total = pageInfo.getTotal();
        Map responseMap = new HashMap();
        responseMap.put("code", 0);
        responseMap.put("msg", "查询成功");
        responseMap.put("pageInfo", pageInfo);
        responseMap.put("count", total);
        return responseMap;
        }




    }

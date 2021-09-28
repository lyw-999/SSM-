package com.lyw.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lyw.bean.Customer;
import com.lyw.bean.CustomerExample;
import com.lyw.bean.dto.CustomerDTO;
import com.lyw.service.CustomerService;
import com.lyw.util.AliSMSUtil;
import com.lyw.util.JwtToToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "*")  // * 代表所有的域名请求
@RestController
@RequestMapping("/api/customer")
public class CustomerController{
@Autowired(required = false)
private CustomerService customerService;
@Autowired
    private  JedisPool jedisPool;
// 发送验证码
    @RequestMapping("/sendCodeNum")
    public Map sendCodeNum(String phoneNum){
        Map codeMap = new HashMap();
        //1.在发送验证码之前  随机创建6个 随机数字的验证码
       String randomNum = String.valueOf((int)((Math.random()*9+1)*100000));
       // 1.1 在发送验证码之前 需要向redis中查询 该手机有没有验证码存在 如果存在 就直接从redis中读取 而不是从阿里云再次发送

        //查询 这个 phoneNum是否存在
        Boolean b = jedisPool.getResource().exists(phoneNum);
        if(b){
            codeMap.put("code",0);
            codeMap.put("msg","验证码已经存在，请去短信中查询");
            return codeMap;
        }else{
            //2.接受到前端传过来的 手机号:phoneNum 对其调用阿里云的发送短信的工具类去发送验证码
            AliSMSUtil.sendMag(phoneNum,randomNum);
            //3. 发送之后 将手机号当做 redis key 验证码当做 redis value 存入到redis数据库中
            String setex = jedisPool.getResource().setex(phoneNum, 120, randomNum);
            System.out.println("setex = " + setex);
            jedisPool.getResource().persist(phoneNum); // 注意这里设置成-1  在线上环境 要删除！！！
            //4. 将验证码发给前端
            if (  "OK".equals(setex)) {
                codeMap.put("code",0);
                codeMap.put("msg","发送成功");
                //   codeMap.put("data",randomNum); 注意 线上不能把验证码通过json数据发送给前端 容易被人恶意利用 验证码只能该手机号看到！
                return codeMap;
            }else {
                codeMap.put("code",500);
                codeMap.put("msg","发送失败");
                return codeMap;
            }
        }
    }

// 校验手机号和验证码
    @RequestMapping("/customerLogin")
    public Map customerLogin(String phoneNum,String codeNum){
        System.out.println("phoneNum = " + phoneNum);
            // 1.根据前端传来的手机号 和验证码 来和redis 中 的数据对比
        Map codeMap = new HashMap();
        String redisCodeNum = jedisPool.getResource().get(phoneNum);  //redis中的验证码
        if (codeNum.equals(redisCodeNum)) {
            //登录成功  需要给顾客返回一个 jwt 同时把这个jwt当做key放入到redis中
            JwtToToken jwtToToken = new JwtToToken();
            CustomerDTO jwt = jwtToToken.createJwt(phoneNum);//现在用不到session 前后端分离
            //  使用jwt 比较容易 很轻松的做出单点登录  基于 jwt+redis 的单点登录
            //  把token 存入到redis中去
            jedisPool.getResource().set(phoneNum+"token",jwt.getAccessToken());

            codeMap.put("code",0);
            codeMap.put("msg","登录成功");
            codeMap.put("data",jwt);
        }else {
            //登录失败
            codeMap.put("code",400);
            codeMap.put("msg","登录失败");
        }
        return codeMap;
    }

    @RequestMapping("/customerLoginByAxios")  // "/api/customer/customerLoginByAxios"
    public Map customerLoginByAxios(@RequestBody  Map map ){
        System.out.println("map = " + map);
        Map codeMap = new HashMap();
        // 1. 根据前端 传来的手机号 和 验证码 来和redis 中的数据做对比
        String redisCodeNum = jedisPool.getResource().get((String) map.get("phoneNum")); // redis中的 验证码
        if (map.get("codeNum").equals(redisCodeNum)){
            // 相等, 登录成功
            codeMap.put("code",0);
            codeMap.put("msg","登录成功");
            return codeMap;
        }else{
            // 不相等, 登录失败
            codeMap.put("code",400);
            codeMap.put("msg","登录失败");
            return codeMap;
        }
    }

// 计算钱数
    @RequestMapping("/getMoney")  //  "/api/customer/getMoney"
    public Map getMoney(double gongSiLng ,double gongSiLat , double customerLng  ,double customerLat){
           double money =  customerService.getMoney(gongSiLng ,gongSiLat ,customerLng , customerLat);
           DecimalFormat df = new DecimalFormat("#.##");
        String format = df.format(money);
        System.out.println(df.format(money));
            Map codeMap = new HashMap();
            codeMap.put("code",0);
            codeMap.put("msg","数据请求成功");
            codeMap.put("data",format);
           return codeMap;
    }
//  计算 最优的
    @RequestMapping("/getYouMoney")  //  "/api/customer/getYouMoney"
    public Map getYouMoney(@RequestParam(value="dizhi[]") String[] dizhi  , double customerLng  , double customerLat){
        double mny = 1999999999;
        String format = "";
        String jwd = "";
        for (String s : dizhi) {
            //  System.out.println("s = " + s);
            String  jd = s.split(",")[0];
            String  wd = s.split(",")[1];
            // System.out.println("jd = " + jd);  //经度
            // System.out.println("wd = " + wd);  //纬度
            double money =  customerService.getMoney( Double.parseDouble(jd), Double.parseDouble(wd) ,customerLng , customerLat);
            // System.out.println("money = " + money);
            if (money < mny ) {
                mny = money;
                System.out.println("mny = " + mny);
                jwd =s;
            }

        }
        DecimalFormat df = new DecimalFormat("#.##");
        format = df.format(mny);
        System.out.println(df.format(mny));

        Map codeMap = new HashMap();
        codeMap.put("code",0);
        codeMap.put("msg","数据请求成功");
        codeMap.put("data",format);
        codeMap.put("url",jwd);
        return codeMap;
}

//增
// 后端订单增加 -- 针对layui的 针对前端传 json序列化的
@RequestMapping("/insert")
public Map insert(@RequestBody Customer customer){ // orders 对象传参, 规则: 前端属性要和后台的属性一致!!!
Map map = new HashMap();
int i =  customerService.insertSelective(customer);
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
int i = customerService.deleteByPrimaryKey(id);
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
    public Map update(@RequestBody Customer  customer) {
    Map map = new HashMap();
    int i = customerService.updateByPrimaryKeySelective( customer);
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
    List<Customer> customers =  customerService.selectByExample(null);
        Map responseMap = new HashMap();
        responseMap.put("code", 0);
        responseMap.put("msg", "查询成功");
        responseMap.put("customers", customers);
        return responseMap;
        }

// 查-- 查询+自身对象的查询 + 分页
// 分页查询
    @RequestMapping("/selectAllByPage")
    public Map selectAllByPage(Customer customer, @RequestParam(value = "page", defaultValue = "1", required = true) Integer page,
    @RequestParam(value = "limit", required = true) Integer pageSize) {
    // 调用service 层   , 适用于 单表!!!
    PageHelper.startPage(page, pageSize);
    CustomerExample example = new CustomerExample();
    CustomerExample.Criteria criteria = example.createCriteria();

//    if (null!=customer.getYYYYYYYY()&&!"".equals(customer.getYYYYYYY())){
//         criteria.andPhoneEqualTo(customer.getPhone());   // 1
//    }

    List<Customer> customersList = customerService.selectByExample(example);
        PageInfo pageInfo = new PageInfo(customersList);
        long total = pageInfo.getTotal();
        Map responseMap = new HashMap();
        responseMap.put("code", 0);
        responseMap.put("msg", "查询成功");
        responseMap.put("pageInfo", pageInfo);
        responseMap.put("count", total);
        return responseMap;
        }




    }

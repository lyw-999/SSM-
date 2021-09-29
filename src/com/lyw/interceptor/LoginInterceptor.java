package com.lyw.interceptor;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录的 拦截器，  为了 拦截    3端  的 登录的接口
 * <p>
 * 1）  后台管理 -- session
 * 2）  顾客  ---  token
 * 3）  工程师 ---- 暂定！！！！
 */
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private JedisPool jedisPool;

    //  方法 调用前的 拦截
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse response, Object o) throws Exception {
        // 判断  web端 有没有 登录， 即 判断 他 是否 携带 了 token ，
        // 如果携带了 token ， 则需要 判断 token 是否是在 redis 中的。
        // 如果 不在redis 中，  则是一个  过期的或者前端伪造的 token ， 则不允许登录， 让前端重新登录
        // 如果 这个 token 是ok的， 那么 校验 他的 jwt token 是否过期（ 非必要的 ）
        // 过期了也不让登录，并删除redis中的 key。
        // 排除完毕后 就可以登录了。。。

        // 那么， 后台  httpServletRequest 这个参数可以收取 前端传来的 token，那么 现在 问题
        // 在于 前端把token 放入在哪里？？ 才可以 正常的 发送到 后台！！！。 答案： 可以放在  ajax的 请求头中。
        System.out.println("  拦截器----生效了"); //  return false 是拦截了，  return true 是 放行了
        // 后台获取 token
        String token = httpServletRequest.getHeader("token");// 获取请求头
        System.out.println("token = " + token);
        if (token != null) {
            String s = JWT.decode(token).getAudience().get(0);
            System.out.println("s = " + s.substring(0, 11));   // 获得手机号
            String phoneNum = s.substring(0, 11);
            httpServletRequest.setAttribute("phoneNum",phoneNum);//将手机号放入到 request域 中
            long tokenTime = JWT.decode(token).getExpiresAt().getTime(); // 获取 token 的 过期时间   time = 1633682085000
            System.out.println("tokenTime = " + tokenTime);

            // 做单点登录。 每次使用  新的设备（手机，浏览器，平板，等等） 登录， 都会诞生一个 token值， 这个 token值每次登录都不一样的。
            // 抓住这个特点就可以 做 单点登录！！
            // 1. 登录后的请求， 需要 查看 自己的 token 和 redis 中的 token是否 一样。 如果 一样是 同一个设备， 如果不一样， 提示
            // 您需要重新登录， 因为你的设备在 ip：xxxx 登录了。
            String redisKey = phoneNum + "token";
            String customerRedisToken = jedisPool.getResource().get(redisKey);
            if (!customerRedisToken.equals(token)) {
                System.out.println(" token不相等 ");
                PrintWriter writer = response.getWriter();
                responseToken(response);
                Map codeMap = new HashMap<>();
                codeMap.put("code", 50001);
                codeMap.put("msg", "因为你的设备在 ip：xxxx 登录了"); // 作业1： 给前端提示 你的设备在哪个IP登录了。然后 保存在一个 日志数据库中。 id ， 手机号 ， ip  ， 时间
                String jsonString = JSON.toJSONString(codeMap);
                System.out.println("jsonString = " + jsonString);
                writer.print(jsonString);
                writer.flush();
                writer.close();
                return false;
            }
            /// 判断 token是否过期
            // 输出现在时间的 long 值
            long nowTime = new Date().getTime();
            long subTime=  tokenTime - nowTime;
            if ( subTime <0  ){
                // 过期的日子 减去  现在的 时间 是 负数， 证明  token 过期了
                // 比如： 面包 9-30日过期   此刻是  10月1日 那么，  9月30 - 10月1 就等于 负数
                // 给前端发 json ， 让他 重新登录 就可以啦。
                PrintWriter writer = response.getWriter();
                responseToken(response);
                Map codeMap = new HashMap<>();
                codeMap.put("code", 50001);
                codeMap.put("msg", "因为你的设备在 ip：xxxx 登录了或者你的账户名密码不对"); // 作业1： 给前端提示 你的设备在哪个IP登录了。然后 保存在一个 日志数据库中。 id ， 手机号 ， ip  ， 时间
                String jsonString = JSON.toJSONString(codeMap);
                System.out.println("jsonString = " + jsonString);
                writer.print(jsonString);
                writer.flush();
                writer.close();
                return false;

            }

    }
        // 获取了token -- 看 token 有没有 在 redis 当中，  单点登录！！！！ 基于 redis + token 的
        // 因为要用到手机号，  之前 手机号是 存入到  jwt 的 信息中的， 需要 解析出来， 生成的不同 ， 解析的方式不同
        //  DecodedJWT decode = JWT.decode(token); // 解析 jwt
        // System.out.println("decode = " + decode);
        //jedisPool.getResource().get("手机号+token")


        return true;
    }

    // 方法 执行中 的 拦截
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    // 方法 调用之后的 拦截
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse response, Object o, Exception e) throws Exception {

    }

    public static void main(String[] args) {
        // 输出现在时间的 long 值
        long time = new Date().getTime();
        System.out.println("此刻的long 值是   = " + time);
    }


    private void responseToken(HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
//                response.setHeader("Vary", "Origin");
//                    // Access-Control-Max-Age
//                    response.setHeader("Access-Control-Max-Age", "3600");
        // Access-Control-Allow-Credentials
        response.setHeader("Access-Control-Allow-Credentials", "true");
        // Access-Control-Allow-Methods
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        // Access-Control-Allow-Headers
        response.setHeader("Access-Control-Allow-Headers",
                "Origin, X-Requested-With, Content-Type, Accept, token");
    }
}


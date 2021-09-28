package com.lyw.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/*
 登录的拦截器: 为了拦截 三端 的登录的接口
 1） 后台管理--- session
 2） 顾客   ---  token
 3） 工程师 --- 暂定！！
 */
public class LoginInterceptor  implements HandlerInterceptor {
    @Autowired
    private JedisPool jedisPool;
    // 方法 调用之前 的拦截
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        // 判断对方有没有 即判断他是否携带了 token
        // 如果携带了token 则需要判断token是否在redis中的
        // 如果不在redis中 则是一个过期的或者前端伪造的token 则不允许登录 让前端重新进行登录
        // 过期了 也不让登录 并删除redis中的key  排除完毕后 就可以登录了

        // 那么后台
        System.out.println("------拦截器生效了-----  return true 是放行了 false 是 拦截了");
        //  后台获取token
        String token = httpServletRequest.getHeader("token");//获取请求头
        System.out.println("token = " + token);
//        long time = JWT.decode(token).getExpiresAt().getTime();// 获取token的过期时间
//        System.out.println("time = " + time);
//        String phone = JWT.decode(token).getAudience().get(0);
//        System.out.println("phone = " + phone);
//        System.out.println("phone 111=" + phone.substring(0,12));
        //获取了 token--- 看token有没有在redis当中  单点登录！！！基于 redis + token
        // 因为要用到手机号 手机号是存到jwt的信息中的  需要解析出来  生成的不同 解析方式也不同

       // DecodedJWT decode = JWT.decode(token);//解析jwt
       // System.out.println("decode = " + decode);
        //jedisPool.getResource().exists()
        return true;

    }
    // 方法 执行中 的拦截
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {


    }
    // 方法 调用之后 的拦截
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {


    }
}

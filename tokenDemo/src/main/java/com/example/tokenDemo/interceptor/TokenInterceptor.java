package com.example.tokenDemo.interceptor;

import com.example.tokenDemo.service.RedisService;
import com.example.tokenDemo.util.RedisTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Lenovo on 2019/7/7.
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    Logger logger= LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisService redisService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tokenName=request.getRequestURI()+request.getHeader("token_value");
        String tokenValue=request.getHeader("token_value");
        if(tokenValue!=null&& !tokenValue.equals("")){
            logger.info("tokenName:{},tokenValue:{}",tokenName,tokenValue);
            return handleToken(request,response,handler);
        }
        return false;
    }

    private boolean handleToken(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        //当大量高并发下所有带token参数的请求进来时，进行分布式锁定,允许某一台服务器的一个线程进入，锁定时间1分钟
        if (RedisTool.tryGetDistributedLock(redisService,request.getHeader("token_value"),request.getHeader("token_value"),60)) {
            if (redisService.exists(request.getRequestURI() + request.getHeader("token_value"))) {
                //当请求的url与token与redis中的存储相同时
                if (redisService.get(request.getRequestURI() + request.getHeader("token_value")).equals(request.getHeader("token_value"))) {
                    //放行的该线程删除redis中存储的token
                    redisService.del(request.getRequestURI() + request.getHeader("token_value"));
                    //放行
                    return true;
                }
            }
            //当请求的url与token与redis中的存储不相同时，解除锁定
            RedisTool.releaseDistributedLock(redisService,request.getHeader("token_value"),request.getHeader("token_value"));
            //进行拦截
            return false;
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        if (redisService.exists(request.getHeader("token_value"))) {
            RedisTool.releaseDistributedLock(redisService, request.getHeader("token_value"), request.getHeader("token_value"));
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {

    }
}

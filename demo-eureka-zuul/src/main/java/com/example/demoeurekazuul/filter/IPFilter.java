package com.example.demoeurekazuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class IPFilter extends ZuulFilter {

    private String[] whitelist;
    @Value("${yxwfilter.ip.whitelist")
    private String strIPWhitelist;

    @Value("${yxwfilter.ip.whitelistenable}")
    private String WhitelistEnabled;


    @Override
    public String filterType() {
        return null;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return false;
    }

    @Override
    public Object run() throws ZuulException {

        System.out.println(strIPWhitelist);
        whitelist = strIPWhitelist.split("\\,");

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest req = ctx.getRequest();
        String ipAddr =this.getIpAddr(req);

        System.out.println("请求IP地址为：[" + ipAddr + "]");
//        配置ip地址白名单，生产环境可以放入数据库中或者Redis
        List<String> ips = new ArrayList<>();

        for (int i = 0; i < whitelist.length; i++) {
            System.out.println(whitelist[i]);
            ips.add(whitelist[i]);

        }
        System.out.println("whitelist:" + ips.toString());
//        配置ip地址白名单，生产环境可以放入数据库中或者Redis
        if (!ips.contains(ipAddr)){
            System.out.println("未通过IP地址检验.["+ipAddr+"]");
            ctx.setResponseStatusCode(401);
            ctx.setSendZuulResponse(false);
            ctx.setResponseBody("{\"errorcode\":\"0001\",\"errmsg\":\"IpAddr is forbidden!["+ipAddr + "]\"}");
        }

        return null;
    }

    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null||ip.length()==0 ||"unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null||ip.length()==0 ||"unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null||ip.length()==0 ||"unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null||ip.length()==0 ||"unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null||ip.length()==0 ||"unknown".equalsIgnoreCase(ip)){
            ip = request.getRemoteAddr();
        }
        return ip;

    }
}

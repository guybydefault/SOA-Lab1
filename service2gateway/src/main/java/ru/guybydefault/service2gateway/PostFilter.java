package ru.guybydefault.service2gateway;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

public class PostFilter extends ZuulFilter  {

    private static Logger log = LoggerFactory.getLogger(PostFilter.class);

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 200;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestPrinter.printRequest(log);
        RequestContext.getCurrentContext().getZuulRequestHeaders().forEach((k, v) -> {log.info(k + ":" + v);});
        RequestContext.getCurrentContext().getZuulRequestHeaders().remove("Transfer-Encoding");
        return null;
    }
}

package ru.guybydefault.service2gateway;

import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class RequestPrinter {

    public static void printRequest(Logger log) {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
        log.info(String.format("Headers: %s", enumerationStream(request.getHeaderNames()).collect(Collectors.joining(", "))));
    }

    private static <T> Stream<T> enumerationStream(Enumeration<T> e) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return e.hasMoreElements();
            }

            @Override
            public T next() {
                return e.nextElement();
            }
        }, Spliterator.ORDERED), false);
    }
}

package ru.guybydefault.web;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CORSFilter implements ContainerRequestFilter, ContainerResponseFilter {

    /**
     * A preflight request is an OPTIONS request
     * with an Origin header.
     */
    private static boolean isPreflightRequest(ContainerRequestContext request) {
        return request.getHeaderString("Origin") != null
                && request.getMethod().equalsIgnoreCase("OPTIONS");
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        // if there is no Origin header, then it is not a
        // cross origin request. We don't do anything.
        if (requestContext.getHeaderString("Origin") == null) {
            return;
        }

        if (isPreflightRequest(requestContext)) {
            responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
            responseContext.getHeaders().add("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
            responseContext.getHeaders().add("Access-Control-Max-Age", "3600");
            responseContext.getHeaders().add("Access-Control-Allow-Headers", "*");
            responseContext.getHeaders().add("X-Frame-Options", "ALLOW-FROM http://localhost:4200");
        }
        // Cross origin requests can be either simple requests
        // or preflight request. We need to add this header
        // to both type of requests. Only preflight requests
        // need the previously added headers.
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
    }

    /**
     * Method for ContainerRequestFilter.
     */
    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        // If it's a preflight request, we abort the request with
        // a 200 status, and the CORS headers are added in the
        // response filter method below.
        if (isPreflightRequest(request)) {
            request.abortWith(Response.ok().build());
            return;
        }
    }
}
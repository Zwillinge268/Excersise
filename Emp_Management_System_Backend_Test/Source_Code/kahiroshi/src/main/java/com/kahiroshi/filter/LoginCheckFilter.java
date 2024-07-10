package com.kahiroshi.filter;

import com.google.gson.Gson;
import com.kahiroshi.object.Result;
import com.kahiroshi.utils.JwtUtils;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j
//Filter all resource
@WebFilter(urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //Gson already included in spring starter
    @Autowired
    private Gson gson;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        //Gson gson = new Gson();

        //Step:
        //1. user request login ? doFilter : step2
        //2. user have jwt ? step3 : return "not login"
        //3. jwt wrong || expire ? return "not login" : doFilter

        //Get url
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        String url = request.getRequestURL().toString();

        //Check request is login
        if (url.contains("login")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        //Check if JWT exist
        String jwt = request.getHeader("token");
        if (!StringUtils.hasLength(jwt)) {
            String message = gson.toJson(Result.error("Not login"));
            response.getWriter().write(message);
            return;
        }

        //Check JWT correct
        try {
            JwtUtils.parseKey(jwt);
        } catch (Exception e) {
            String message = gson.toJson(Result.error("Not login"));
            response.getWriter().write(message);
            return;
        }

        //Let it go
        filterChain.doFilter(servletRequest, servletResponse);
    }
}

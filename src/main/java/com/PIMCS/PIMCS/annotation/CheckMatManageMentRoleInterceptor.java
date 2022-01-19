package com.PIMCS.PIMCS.annotation;

import com.PIMCS.PIMCS.service.MatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CheckMatManageMentRoleInterceptor implements HandlerInterceptor {

    private final MatService matService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        CheckMatManageMentRole checkMatManageMentRole = handlerMethod.getMethodAnnotation(CheckMatManageMentRole.class);

        if(checkMatManageMentRole == null) return true;

        /*throw new UnauthenticatedUserException("로그인 후 이용 가능합니다.");*/

        String queryString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
//        System.out.println("request: " + a);

//        HashMap<String,Boolean> hashMap = matService.checkMatSerialNumberService("w1234");

        return true;
    }
}

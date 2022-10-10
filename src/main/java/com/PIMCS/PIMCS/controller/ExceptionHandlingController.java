package com.PIMCS.PIMCS.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;


@Controller
public class ExceptionHandlingController implements ErrorController {


    private final String ERROR_404_PAGE_PATH = "error/404";
    private final String ERROR_500_PAGE_PATH = "error/500";
    private final String ERROR_ETC_PAGE_PATH = "error/error";

    @RequestMapping(value = "/error")
    public String handleError(HttpServletRequest request, Model model) {

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus httpStatus = HttpStatus.valueOf(Integer.valueOf(status.toString()));

        if (status != null) {
            int statusCode = Integer.valueOf(status.toString());
            // 404 error
            if (statusCode == HttpStatus.NOT_FOUND.value()) {

                return ERROR_404_PAGE_PATH;
            }

            // 500 error
            if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return ERROR_500_PAGE_PATH;
            }
        }

        return ERROR_ETC_PAGE_PATH;
    }

}




package com.ssafy.backend.handler;

import com.ssafy.backend.model.ErrorMessage;
import com.ssafy.backend.util.NotificationManager;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestTemplate;

@ControllerAdvice
public class GlobalExceptionHandler {

  @Autowired
  private NotificationManager notificationManager;

  @ExceptionHandler(Exception.class)
  public ResponseEntity globalException(Exception e, HttpServletRequest req) {
    e.printStackTrace();
    notificationManager.sendNotification(e, req.getRequestURI(), getParams(req));

    return new ResponseEntity<>(
        new ErrorMessage("에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // 일반적으로 Param이 잘못되었을 때 발생하는 ErrorException
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity wrongParamException(Exception e, HttpServletRequest req) {
    e.printStackTrace();
    notificationManager.sendNotification(e, req.getRequestURI(), getParams(req));

    return new ResponseEntity<>(
        new ErrorMessage("입력값이 올바르지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private String getParams(HttpServletRequest req) {
    StringBuilder params = new StringBuilder();
    Enumeration<String> keys = req.getParameterNames();
    while (keys.hasMoreElements()) {
      String key = keys.nextElement();
      params.append("- ").append(key).append(" : ").append(req.getParameter(key)).append('\n');
    }

    return params.toString();
  }

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }
}
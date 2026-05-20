package com.icia.delivery.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 글로벌 예외 핸들러 클래스입니다.
 * HTML 에러 페이지를 반환하기 위해 @ControllerAdvice를 사용합니다.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 매핑되지 않은 URL 요청 시 발생하는 예외(NoHandlerFoundException) 처리 (404 에러)
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNoHandlerFoundException(NoHandlerFoundException ex) {
        logger.error("404 - 요청한 페이지를 찾을 수 없습니다: {}", ex.getRequestURL());
        ModelAndView mav = new ModelAndView("error/404"); // templates/error/404.html
        mav.addObject("errorMessage", "요청한 페이지를 찾을 수 없습니다.");
        return mav;
    }

    /**
     * StoreNotFoundException 처리 (404 에러)
     */
    @ExceptionHandler(StoreNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleStoreNotFoundException(StoreNotFoundException ex) {
        logger.error("StoreNotFoundException 발생: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error/404"); // templates/error/404.html
        mav.addObject("errorMessage", ex.getMessage());
        return mav;
    }

    /**
     * InvalidUserException 처리 (400 에러)
     */
    @ExceptionHandler(InvalidUserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleInvalidUserException(InvalidUserException ex) {
        logger.error("InvalidUserException 발생: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error/400"); // templates/error/400.html
        mav.addObject("errorMessage", ex.getMessage());
        return mav;
    }

    /**
     * 그 외 모든 예외 처리 (500 에러)
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGeneralException(Exception ex) {
        logger.error("예기치 않은 오류 발생: {}", ex.getMessage());
        ModelAndView mav = new ModelAndView("error/500"); // templates/error/500.html
        mav.addObject("errorMessage", "내부 서버 오류가 발생했습니다.");
        return mav;
    }
}

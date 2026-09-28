package com.necom.constant;

public interface AppConstants {
    String DEFAULT_PAGE_NUMBER = "1";
    String DEFAULT_PAGE_SIZE = "5";
    String DEFAULT_SORT = "id,desc";
    String FRONTEND_HOST = "*";
    String FRONTEND_URL = System.getenv("FRONTEND_URL") != null && !System.getenv("FRONTEND_URL").isEmpty() 
            ? System.getenv("FRONTEND_URL") 
            : "http://localhost";
    String BACKEND_HOST = System.getenv("BACKEND_HOST") != null && !System.getenv("BACKEND_HOST").isEmpty()
            ? System.getenv("BACKEND_HOST")
            : "http://localhost:8085";
    double DEFAULT_TAX = 0.1;

}

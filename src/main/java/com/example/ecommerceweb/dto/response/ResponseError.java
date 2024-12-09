package com.example.ecommerceweb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ResponseError {
    private String path;
    private int httpStatus;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorField;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> errorMap;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private Date timestamp;


    public ResponseError(WebRequest request, int httpStatus) {
        this.timestamp = new Date();
        this.httpStatus = httpStatus;
        this.path = request.getDescription(false).replace("uri=", "");
    }
}

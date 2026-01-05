package com.app.placify.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class ErrorResponse {
    private LocalDateTime timestamp ;
    private String message;
    private int status ;
    public ErrorResponse(LocalDateTime timestamp, String message, int status) {
        this.status = status;
        this.timestamp = timestamp ;
        this.message = message;
    }

}

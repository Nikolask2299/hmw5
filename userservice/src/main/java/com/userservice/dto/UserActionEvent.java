package com.userservice.dto;


public record UserActionEvent (
    String operation,
    String email
){}

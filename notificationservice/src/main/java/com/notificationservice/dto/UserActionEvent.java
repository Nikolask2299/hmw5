package com.notificationservice.dto;


public record UserActionEvent (
    String operation,
    String email
){}

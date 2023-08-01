package com.example.travelhana.Exception.Code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum SuccessCode {

    // 조회 성공 코드 (HTTP Response: 200 OK)
    SELECT_SUCCESS(200, "SELECT_SUCCESS", "조회 성공"),
    // 삭제 성공 코드 (HTTP Response: 200 OK)
    DELETE_SUCCESS(200, "DELETE_SUCCESS", "삭제 성공"),
    // 삽입 성공 코드 (HTTP Response: 201 Created)
    INSERT_SUCCESS(201, "INSERT_SUCCESS", "삽입 성공"),
    // 수정 성공 코드 (HTTP Response: 201 Created)
    UPDATE_SUCCESS(204, "UPDATE_SUCCESS", "수정 성공"),
    // 인증 성공 코드 (HTTP Response: 200 OK)
    AUTH_SUCCESS(200, "AUTH_SUCCESS", "인증 성공");

    private int statusCode;
    private String status;
    private String message;

}
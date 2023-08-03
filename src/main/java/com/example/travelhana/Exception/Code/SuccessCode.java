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
    AUTH_SUCCESS(200, "AUTH_SUCCESS", "인증 성공"),
    // OPEN API 호출 성공 코드 (HTTP Response: 202 OK)
    OPEN_API_SUCCESS(202,"OPEN_API_SUCCESS","호출 성공"),

    // 마커 줍기 성공 (HTTP Response: 200 OK)
    PICKUP_MARKER_SUCCESS(200, "PICKUP_MARKER_SUCCESS", "마커 줍기 성공"),
    // 마커 불러오기 성공 (HTTP Response: 200 OK)
    GET_MARKER_LIST_SUCCESS(200, "GET_MARKER_LIST_SUCCESS", "마커 불러오기 성공"),
    // 더미 마커 생성하기 성공 (HTTP Response: 201 Created)
    CREATE_DUMMY_MARKERS_SUCCESS(201, "CREATE_DUMMY_MARKERS_SUCCESS", "더미 마커 생성하기 성공"),

    // 계좌 연결하기 성공 (HTTP Response: 201 Created)
    CONNECT_ACCOUNT_SUCCESS(201, "PICKUP_MARKER_SUCCESS", "계좌 연결하기 성공"),
    // 연결된 계좌 불러오기 성공 (HTTP Response: 200 OK)
    GET_CONNECTED_ACCOUNTS_SUCCESS(200, "GET_CONNECTED_ACCOUNTS_SUCCESS", "연결된 계좌 불러오기 성공"),
    // 외부 계좌 불러오기 성공 (HTTP Response: 200 OK)
    GET_EXTERNAL_ACCOUNTS_SUCCESS(200, "GET_EXTERNAL_ACCOUNTS_SUCCESS", "외부 계좌 불러오기 성공"),
    // 더미 외부 계좌 생성하기 성공 (HTTP Response: 201 Created)
    CREATE_DUMMY_ACCOUNTS_SUCCESS(201, "CREATE_DUMMY_ACCOUNTS_SUCCESS", "더미 계좌 생성하기 성공");

    private int statusCode;
    private String status;
    private String message;

}
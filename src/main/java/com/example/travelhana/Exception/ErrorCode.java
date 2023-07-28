package com.example.travelhana.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorCode {

    INVALID_PASSWORD(400,"Internal Server Error","비밀번호는 6개의 숫자로 구성해주세요"),
    BUSINESS_EXCEPTION_ERROR(500,"TOKEN isn't userId","TOKEN isn't userId"),
    INSUFFICIENT_BALANCE(500,"Insufficient balance in this account","계좌 잔액이 부족합니다."),
    INVALID_EXCHANGEUNIT(500,"Invalid exchange unit.","유효하지 않은 화폐단위입니다."),
    NO_ACCOUNT(500,"There is no account like this id","계좌가 존재하지 않습니다."),

    NOT_ENOUGH_MARKER(400, "Not Enough Marker", "모두 주워진 마커입니다."),

    UNAUTHORIZED_PASSWORD(401, "Unauthorized Password", "비밀번호가 일치하지 않습니다."),
    UNAUTHORIZED_USER_ACCOUNT(401, "Unauthorized User to Account", "유저와 계좌 정보가 일치하지 않습니다."),

    USER_NOT_FOUND(404, "User Not Found", "해당하는 유저를 찾을 수 없습니다."),
    MARKER_NOT_FOUND(404, "Marker Not Found", "해당하는 마커를 찾을 수 없습니다."),
    ACCOUNT_NOT_FOUND(404, "Account Not Found", "해당하는 계좌를 찾을 수 없습니다."),
    EXTERNAL_ACCOUNT_NOT_FOUND(404, "External Account Not Found", "해당하는 외부 계좌를 찾을 수 없습니다."),

    LOCATION_NOT_SAME(406, "Location Not Same", "마커의 위치와 현재 위치가 다릅니다."),

    ALREADY_PICK_UPPED_MARKER(409, "Already Pick upped Marker", "이미 주운 마커입니다."),
    ALREADY_EXIST_ACCOUNT(409, "Already Connected Account", "이미 연결된 계좌입니다."),


    INTERNAL_SEVER_ERROR(500, "Internal Server Error", "서버 에러가 발생했습니다.");



    private int statusCode; //404
    private String status; //NOT_FOUNT
    private String message;

}
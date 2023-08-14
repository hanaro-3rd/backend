package com.example.travelhana.Exception.Code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorCode {

	INVALID_PASSWORD(400, "Internal Server Error", "비밀번호는 6개의 숫자로 구성해주세요"),
	USER_ALREADY_EXIST(500, "USER_ALREADY_EXIST", "이미 존재하는 유저입니다."),
	AUTH_FAILURE(500, "AUTH_FAILURE", "인증코드가 일치하지 않습니다."),
	SESSION_INVALID(500, "SESSION_INVALID", "코드가 만료되었습니다. 다시 코드를 발급받으세요."),
	BUSINESS_EXCEPTION_ERROR(500, "TOKEN isn't userId", "TOKEN isn't userId"),
	INSUFFICIENT_BALANCE(500, "Insufficient balance in this account", "계좌 잔액이 부족합니다."),
	INVALID_EXCHANGE_UNIT(500, "Invalid exchange unit.", "유효하지 않은 화폐단위입니다."),
	TOO_MUCH_PURCHASE(500, "TOO_MUCH_PURCHASE", "외화 매수는 100만원 이상 불가합니다"),
	INVALID_HISTORY_FILTER(500, "Invalid History Filter.", "유효하지 입출금내역 필터입니다."),
	INVALID_HISTORY_TYPE(500, "Invalid History Filter.", "유효하지 입출금내역 타입입니다."),
	NO_ZERO_OR_MINUS(500, "NO_ZERO_OR_MINUS", "환전금액은 양수로 입력해주세요."),
	NO_ACCOUNT(500, "There is no account like this id", "계좌가 존재하지 않습니다."),
	NO_USER(500, "NO_USER", "유저가 존재하지 않습니다."),
	NOT_ENOUGH_MARKER(400, "Not Enough Marker", "모두 주워진 마커입니다."),
	INVALID_UPDATE(500, "Invalid Update Payment", "해당 결제내역에 대한 수정 권한이 없습니다."),
	NO_KEYMONEY(500, "There is no keymoneyaccount like this id", "해당 키머니계좌가 존재하지 않습니다."),

	UNAUTHORIZED_PASSWORD(401, "Unauthorized Password", "비밀번호가 일치하지 않습니다."),
	UNAUTHORIZED_USER_ACCOUNT(401, "Unauthorized User to Account", "유저와 계좌 정보가 일치하지 않습니다."),

	USER_NOT_FOUND(404, "User Not Found", "해당하는 유저를 찾을 수 없습니다."),
	MARKER_NOT_FOUND(404, "Marker Not Found", "해당하는 마커를 찾을 수 없습니다."),
	ACCOUNT_NOT_FOUND(404, "Account Not Found", "해당하는 계좌를 찾을 수 없습니다."),
	EXTERNAL_ACCOUNT_NOT_FOUND(404, "External Account Not Found", "해당하는 외부 계좌를 찾을 수 없습니다."),
	PAYMENT_HISTORY_NOT_FOUND(404, "Payment History Not Found", "해당하는 결제 내역을 찾을 수 없습니다."),
	EXCHANGE_HISTORY_NOT_FOUND(404, "Exchange History Not Found", "해당하는 환전 내역을 찾을 수 없습니다."),

	CATEGORY_PLAN_NOT_FOUND(404, "Category Plan Not Found", "해당하는 카테고리 경비계획을 찾을 수 없습니다."),
	LOCATION_NOT_SAME(406, "Location Not Same", "마커의 위치와 현재 위치가 다릅니다."),
	PLAN_NOT_FOUND(404, "Plan Not Found", "해당하는 경비계획을 찾을 수 없습니다."),
	CATEGORY_NOT_FOUND(404, "Caregory Not Found", "해당하는 카테고리를 찾을 수 없습니다."),

	ALREADY_PICK_UPPED_MARKER(409, "Already Pick upped Marker", "이미 주운 마커입니다."),
	ALREADY_EXIST_ACCOUNT(409, "Already Connected Account", "이미 연결된 계좌입니다."),
	INTERNAL_SEVER_ERROR(500, "Internal Server Error", "서버 에러가 발생했습니다."),
	ONLY_PUCHASE_IN_HOLIDAY(500, "ONLY_PUCHASE_IN_HOLIDAY", "공휴일에는 외화 매수만 가능합니다."),
	TOO_MUCH_KEYMONEY_BALANCE(500, "TOO_MUCH_KEYMONEY_BALANCE", "키머니 잔액은 200만원을 초과할 수 없습니다"),
	MIN_CURRENCY(500, "MIN_CURRENCY", "환전 최소금액 이상으로 입력해주세요.");

	private int statusCode; //404
	private String status; //NOT_FOUNT
	private String message;

}
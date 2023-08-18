package com.example.travelhana.Service.implement;

import com.example.travelhana.Domain.*;
import com.example.travelhana.Dto.Marker.*;
import com.example.travelhana.Exception.Code.ErrorCode;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Handler.BusinessExceptionHandler;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Repository.*;
import com.example.travelhana.Service.MarkerService;
import com.example.travelhana.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MarkerServiceImpl implements MarkerService {

	private final MarkerRepository markerRepository;
	private final KeymoneyRepository keyMoneyRepository;
	private final MarkerHistoryRepository markerHistoryRepository;
	private final UserToMarkerRepository userToMarkerRepository;

	private final UserService userService;

	// 마커를 주웠는지 여부를 포함하여 Marker 엔티티를 MarkerListDto로 파싱하는 함수
	private MarkerListDto parseMarkerEntitiesToMarkerListDto(int userId, List<Marker> markers) {
		List<MarkerResultDto> returnMarkers = new ArrayList<>();
		for (Marker marker : markers) {
			MarkerResultDto returnMarker = MarkerResultDto
					.builder()
					.id(marker.getId())
					.lng(marker.getLng())
					.lat(marker.getLat())
					.place(marker.getPlace())
					.unit(marker.getUnit())
					.amount(marker.getAmount())
					.limitAmount(marker.getLimitAmount())
					.isPickUp(userToMarkerRepository.existsByUser_IdAndMarker_Id(userId,
							marker.getId()))
					.build();
			returnMarkers.add(returnMarker);
		}
		return new MarkerListDto(returnMarkers);
	}

	private Boolean inRangeOfMarker(LocationDto markerLocation, LocationDto userLocation) {
		// 마커의 위도, 경도
		double markerX = markerLocation.getLat();
		double markerY = markerLocation.getLng();
		// 유저의 위도, 경도
		double userX = userLocation.getLat();
		double userY = userLocation.getLng();

		// 지구 반지름(km)
		final double radius = 6371;
		final double toRadian = Math.PI / 180;

		double deltaLat = Math.abs(markerX - userX) * toRadian;
		double deltaLng = Math.abs(markerY - userY) * toRadian;

		double squareSinDeltaLat = Math.pow(Math.sin(deltaLat / 2), 2);
		double squareSinDeltaLng = Math.pow(Math.sin(deltaLng / 2), 2);

		double squareRoot = Math.sqrt(
				squareSinDeltaLat +
						Math.cos(markerX * toRadian) *
								Math.cos(userX * toRadian) *
								squareSinDeltaLng
		);

		double result = 2 * radius * Math.asin(squareRoot);
		double distance = (Math.round(result * 100) / 100.0);
		System.out.println("distance = " + distance);
		// 거리가 50m(0.05km)이내면 True, 아니면 False를 반환
		return distance < 0.05 ;
	}

	@Override
	public ResponseEntity<?> getMarkerList(String accessToken) {
		// access token으로 유저 가져오기
		User user = userService.getUserByAccessToken(accessToken);

		// 모든 마커를 가져옴
		List<Marker> markers = markerRepository.findAll();

		// 마커를 주웠는지 여부를 포함하여 Marker 엔티티를 MarkerListDto로 파싱 후 리턴
		MarkerListDto result = parseMarkerEntitiesToMarkerListDto(user.getId(), markers);
		ApiResponse apiResponse = ApiResponse.builder()
				.result(result)
				.resultCode(SuccessCode.SELECT_SUCCESS.getStatusCode())
				.resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
				.build();
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@Override
	@Transactional
	public ResponseEntity<?> createDummyMarker(MarkerDummyDto markerDummyDto) {
		// 입력된 마커에 대한 통화 검증
		Currency currency = Currency.getByCode(markerDummyDto.getUnit());
		if (currency == null) {
			throw new BusinessExceptionHandler(ErrorCode.INVALID_EXCHANGE_UNIT);
		}

		// 더미 마커가 없으면 더미 마커 데이터를 생성
		Boolean isExist = markerRepository.existsById(1);
		if (!isExist) {
			Marker tokyoStation = Marker
					.builder()
					.amount(100L)
					.lat(35.6812)
					.lng(139.7670)
					.limitAmount(20)
					.place("도쿄역")
					.unit("JPY")
					.build();
			markerRepository.save(tokyoStation);

			Marker tokyoTower = Marker
					.builder()
					.amount(100L)
					.lat(35.6586)
					.lng(139.7454)
					.limitAmount(20)
					.place("도쿄타워")
					.unit("JPY")
					.build();
			markerRepository.save(tokyoTower);

			Marker sensouji = Marker
					.builder()
					.amount(100L)
					.lat(35.7147)
					.lng(139.7966)
					.limitAmount(20)
					.place("센소지")
					.unit("JPY")
					.build();
			markerRepository.save(sensouji);

			Marker sibuyaSky = Marker
					.builder()
					.amount(100L)
					.lat(35.6584)
					.lng(139.7022)
					.limitAmount(20)
					.place("시부야 스카이")
					.unit("JPY")
					.build();
			markerRepository.save(sibuyaSky);
		}

		// 입력받은 마커 데이터로 마커 테이블에 레코드 생성
		Marker marker = Marker
				.builder()
				.unit(markerDummyDto.getUnit())
				.amount(markerDummyDto.getAmount())
				.limitAmount(markerDummyDto.getLimitAmount())
				.lat(markerDummyDto.getLat())
				.lng(markerDummyDto.getLng())
				.place(markerDummyDto.getPlace())
				.build();
		markerRepository.save(marker);

		// 모든 마커를 불러옴
		List<Marker> markers = markerRepository.findAll();

		// 마커를 주웠는지 여부를 포함하여 Marker 엔티티를 MarkerListDto로 파싱 후 리턴
		MarkerListDto result = parseMarkerEntitiesToMarkerListDto(0, markers);
		ApiResponse apiResponse = ApiResponse.builder()
				.result(result)
				.resultCode(SuccessCode.INSERT_SUCCESS.getStatusCode())
				.resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
				.build();
		return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
	}

	@Override
	@Transactional
	public ResponseEntity<?> pickUpMarker(
			String accessToken, int markerId, LocationDto userLocation) {
		// access token으로 유저 가져오기
		User user = userService.getUserByAccessToken(accessToken);
		int userId = user.getId();

		// markerId에 해당하는 마커가 있는지 확인
		Marker marker = markerRepository.findById(markerId)
				.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.MARKER_NOT_FOUND));

		// 현재 위치가 마커를 주울 수 있는 범위내인지 확인
		LocationDto markerLocation = new LocationDto(marker.getLat(), marker.getLng());
		if (!inRangeOfMarker(markerLocation, userLocation)) {
			throw new BusinessExceptionHandler(ErrorCode.LOCATION_NOT_SAME);
		}

		// 해당 마커의 수량이 남아있지 않은 경우
		if (marker.getLimitAmount() < 1) {
			throw new BusinessExceptionHandler(ErrorCode.LOCATION_NOT_SAME);
		}

		// userId와 markerId에 해당하는 user-marker 중간 테이블 레코드가 이미 존재하는지 확인
		Boolean isAlreadyPickUp = userToMarkerRepository.existsByUser_IdAndMarker_Id(userId,
				markerId);
		if (isAlreadyPickUp) {
			throw new BusinessExceptionHandler(ErrorCode.ALREADY_PICK_UPPED_MARKER);
		}

		// 해당 유저와 통화에 대한 외화 계좌가 있는 지 확인
		String unit = marker.getUnit();
		Long amount = marker.getAmount();
		int keymoneyId;
		Long storedKeyMoney;
		Optional<Keymoney> keymoney = keyMoneyRepository.findByUser_IdAndUnit(userId, unit);
		if (!keymoney.isPresent()) {
			// 없으면 포인트만큼 추가한 외화 계좌 생성
			storedKeyMoney = amount;
			Keymoney newKeymoney = Keymoney
					.builder()
					.user(user)
					.balance(storedKeyMoney)
					.unit(unit)
					.build();
			keymoneyId = keyMoneyRepository.save(newKeymoney).getId();
		} else {
			// 있으면 해당 외화 계좌에 포인트만큼 추가
			keymoney.get().updatePlusBalance(amount);
			storedKeyMoney = keymoney.get().getBalance();
			keymoneyId = keymoney.get().getId();
		}

		// 마커 인원수 차감
		marker.decreaseLimitAmount();

		// user-marker 중간 테이블 레코드 추가
		UserToMarker userToMarker = UserToMarker
				.builder()
				.user(user)
				.marker(marker)
				.pickDate(LocalDateTime.now())
				.build();
		userToMarkerRepository.save(userToMarker);

		// MarkerHistory 테이블 레코드 추가
		MarkerHistory markerHistory = MarkerHistory
				.builder()
				.markerId(markerId)
				.userId(userId)
				.keymoneyId(keymoneyId)
				.pickDate(LocalDateTime.now())
				.amount(amount)
				.balance(storedKeyMoney)
				.place(marker.getPlace())
				.build();
		markerHistoryRepository.save(markerHistory);

		// MarkerPickUpResultDto에 파싱 후 리턴
		MarkerPickUpResultDto result = MarkerPickUpResultDto
				.builder()
				.userId(userId)
				.place(marker.getPlace())
				.price(marker.getAmount())
				.unit(marker.getUnit())
				.build();
		ApiResponse apiResponse = ApiResponse.builder()
				.result(result)
				.resultCode(SuccessCode.INSERT_SUCCESS.getStatusCode())
				.resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
				.build();
		return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
	}

}

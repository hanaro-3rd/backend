package com.example.travelhana.Service;

import com.example.travelhana.Domain.KeyMoney;
import com.example.travelhana.Domain.Marker;
import com.example.travelhana.Domain.User;
import com.example.travelhana.Domain.UserToMarker;
import com.example.travelhana.Dto.Marker.*;
import com.example.travelhana.Exception.BusinessException;
import com.example.travelhana.Exception.ErrorCode;
import com.example.travelhana.Repository.*;
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
public class MarkerService {

	private final UserRepository userRepository;
	private final MarkerRepository markerRepository;
	private final UserToMarkerRepository userToMarkerRepository;
	private final KeyMoneyRepository keyMoneyRepository;

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
					.isPickUp(userToMarkerRepository.existsByUser_IdAndMarker_Id(userId, marker.getId()))
					.build();
			returnMarkers.add(returnMarker);
		}
		return new MarkerListDto(returnMarkers);
	}

	@Transactional
	public ResponseEntity<MarkerListDto> createDummyMarker(MarkerDummyDto markerDummyDto) {
		// 더미 마커가 없으면 더미 마커 데이터를 생성
		Boolean isExist = markerRepository.existsById(1);
		if (!isExist) {
			Marker tokyoStation = new Marker();
			tokyoStation.setAmount(100L);
			tokyoStation.setLat(35.6812);
			tokyoStation.setLng(139.7670);
			tokyoStation.setLimitAmount(20);
			tokyoStation.setPlace("도쿄역");
			tokyoStation.setUnit("JPY");
			markerRepository.save(tokyoStation);

			Marker tokyoTower = new Marker();
			tokyoTower.setAmount(100L);
			tokyoTower.setLat(35.6586);
			tokyoTower.setLng(139.7454);
			tokyoTower.setLimitAmount(20);
			tokyoTower.setPlace("도쿄타워");
			tokyoTower.setUnit("JPY");
			markerRepository.save(tokyoTower);

			Marker sensouji = new Marker();
			sensouji.setAmount(100L);
			sensouji.setLat(35.7147);
			sensouji.setLng(139.7966);
			sensouji.setLimitAmount(20);
			sensouji.setPlace("센소지");
			sensouji.setUnit("JPY");
			markerRepository.save(sensouji);

			Marker sibuyaSky = new Marker();
			sibuyaSky.setAmount(100L);
			sibuyaSky.setLat(35.6584);
			sibuyaSky.setLng(139.7022);
			sibuyaSky.setLimitAmount(20);
			sibuyaSky.setPlace("시부야 스카이");
			sibuyaSky.setUnit("JPY");
			markerRepository.save(sibuyaSky);
		}

		// 입력받은 마커 데이터로 마커 테이블에 레코드 생성
		Marker marker = new Marker();
		marker.setUnit(markerDummyDto.getUnit());
		marker.setAmount(markerDummyDto.getAmount());
		marker.setLimitAmount(markerDummyDto.getLimitAmount());
		marker.setLat(markerDummyDto.getLat());
		marker.setLng(markerDummyDto.getLng());
		marker.setPlace(markerDummyDto.getPlace());
		markerRepository.save(marker);

		// 모든 마커를 불러옴
		List<Marker> markers = markerRepository.findAll();

		// 마커를 주웠는지 여부를 포함하여 Marker 엔티티를 MarkerListDto로 파싱 후 리턴
		MarkerListDto returnMarkers = parseMarkerEntitiesToMarkerListDto(0, markers);
		return new ResponseEntity<>(returnMarkers, HttpStatus.OK);
	}

	@Transactional
	public ResponseEntity<MarkerPickUpResultDto> pickUpMarker(MarkerPickUpDto markerPickUpDto) {
		// userId에 해당하는 탈퇴하지 않은 유저가 있는지 확인
		int userId = markerPickUpDto.getUserId();
		User user = userRepository.findByIdAndIsWithdrawal(userId, false)
				.orElseThrow(() -> new BusinessException("유저를 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND));

		// markerId에 해당하는 마커가 있는지 확인
		int markerId = markerPickUpDto.getMarkerId();
		Marker marker = markerRepository.findById(markerId)
				.orElseThrow(() -> new BusinessException("마커를 찾을 수 없습니다.", ErrorCode.MARKER_NOT_FOUND));

		// 현재 위치의 위도, 경도와 마커의 위도, 경도가 같은지 확인
		Double lat = markerPickUpDto.getLat();
		Double lng = markerPickUpDto.getLng();
		if (!marker.getLat().equals(lat) || !marker.getLng().equals(lng)) {
			throw new BusinessException("현재 위치와 마커 위치가 다릅니다.", ErrorCode.LOCATION_NOT_SAME);
		}

		// 해당 마커의 수량이 남아있지 않은 경우
		if (marker.getLimitAmount() < 1) {
			throw new BusinessException("모두 주워진 마커입니다.", ErrorCode.LOCATION_NOT_SAME);
		}

		// userId와 markerId에 해당하는 user-marker 중간 테이블 레코드가 이미 존재하는지 확인
		Boolean isAlreadyPickUp = userToMarkerRepository.existsByUser_IdAndMarker_Id(userId, markerId);
		if (isAlreadyPickUp) {
			throw new BusinessException("이미 주운 마커입니다.", ErrorCode.ALREADY_PICK_UPPED_MARKER);
		}

		// 해당 유저와 통화에 대한 외화 계좌가 있는 지 확인
		String unit = marker.getUnit();
		Long amount = marker.getAmount();
		Long storedKeyMoney;
		Optional<KeyMoney> keyMoney = keyMoneyRepository.findByUser_IdAndUnit(userId, unit);
		if (!keyMoney.isPresent()) {
			// 없으면 포인트만큼 추가한 외화 계좌 생성
			storedKeyMoney = amount;
			KeyMoney newKeyMoney = KeyMoney.builder().user(user).balance(storedKeyMoney).unit(unit).build();
			keyMoneyRepository.save(newKeyMoney);
		} else {
			// 있으면 해당 외화 계좌에 포인트만큼 추가
			keyMoney.get().addBalance(amount);
			storedKeyMoney = keyMoney.get().getBalance();
		}

		// 마커 인원수 차감
		marker.decreaseLimitAmount();

		// user-marker 중간 테이블 레코드 추가
		UserToMarker userToMarker = UserToMarker.builder().user(user).marker(marker).pickDate(LocalDateTime.now()).build();
		userToMarkerRepository.save(userToMarker);

		// MarkerPickUpResultDto에 파싱 후 리턴
		MarkerPickUpResultDto result = new MarkerPickUpResultDto(userId, marker.getPlace(), storedKeyMoney, marker.getUnit());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	public ResponseEntity<MarkerListDto> getMarkerList(int userId) {
		// userId에 해당하는 탈퇴하지 않은 유저가 있는지 확인
		User user = userRepository.findByIdAndIsWithdrawal(userId, false)
				.orElseThrow(() -> new BusinessException("유저를 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND));

		// 모든 마커를 가져옴
		List<Marker> markers = markerRepository.findAll();

		// 마커를 주웠는지 여부를 포함하여 Marker 엔티티를 MarkerListDto로 파싱 후 리턴
		MarkerListDto returnMarkers = parseMarkerEntitiesToMarkerListDto(user.getId(), markers);
		return new ResponseEntity<>(returnMarkers, HttpStatus.OK);
	}

}

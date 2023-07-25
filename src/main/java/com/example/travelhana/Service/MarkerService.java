package com.example.travelhana.Service;

import com.example.travelhana.Domain.KeyMoney;
import com.example.travelhana.Domain.Marker;
import com.example.travelhana.Domain.User;
import com.example.travelhana.Domain.UserToMarker;
import com.example.travelhana.Dto.MarkerDummyDto;
import com.example.travelhana.Dto.MarkerPickUpDto;
import com.example.travelhana.Dto.MarkerPickUpResultDto;
import com.example.travelhana.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarkerService {

	private final UserRepository userRepository;
	private final MarkerRepository markerRepository;
	private final UserToMarkerRepository userToMarkerRepository;
	private final KeyMoneyRepository keyMoneyRepository;

	public ResponseEntity<List<Marker>> createDummyMarker(MarkerDummyDto markerDummyDto) {

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

		Marker marker = new Marker();
		marker.setUnit(markerDummyDto.getUnit());
		marker.setAmount(markerDummyDto.getAmount());
		marker.setLimitAmount(markerDummyDto.getLimitAmount());
		marker.setLat(markerDummyDto.getLat());
		marker.setLng(markerDummyDto.getLng());
		marker.setPlace(markerDummyDto.getPlace());
		markerRepository.save(marker);

		List<Marker> markers = markerRepository.findAll();

		return new ResponseEntity<>(markers, HttpStatus.OK);
	}

	@Transactional
	public ResponseEntity<?> pickUpMarker(MarkerPickUpDto markerPickUpDto) {

		int userId = markerPickUpDto.getUserId();
		int markerId = markerPickUpDto.getMarkerId();

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));
		Marker marker = markerRepository.findById(markerId)
				.orElseThrow(() -> new RuntimeException("마커가 존재하지 않습니다."));

		// userId와 markerId에 해당하는 user-marker 중간 테이블 레코드가 존재할 시 오류 RESPONSE
		Boolean isAlreadyPickUp = userToMarkerRepository.existsByUser_IdAndMarker_Id(userId, markerId);
		if (isAlreadyPickUp) {
			return new ResponseEntity<>("이미 주운 마커입니다.", HttpStatus.CONFLICT);
		}

		String unit = marker.getUnit();
		Long amount = marker.getAmount();

		Optional<KeyMoney> keyMoney = keyMoneyRepository.findByUser_IdAndUnit(userId, unit);

		// 해당 유저와 통화에 대한 외화 계좌가 있는 지 확인
		Long storedKeyMoney;
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

		MarkerPickUpResultDto result = new MarkerPickUpResultDto(userId, marker.getPlace(), storedKeyMoney, marker.getUnit());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

}

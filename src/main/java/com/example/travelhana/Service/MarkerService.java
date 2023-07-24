package com.example.travelhana.Service;

import com.example.travelhana.Domain.Marker;
import com.example.travelhana.Dto.MarkerDummyDto;
import com.example.travelhana.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarkerService {

	private final MarkerRepository markerRepository;

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


}

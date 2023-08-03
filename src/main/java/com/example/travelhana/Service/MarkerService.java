package com.example.travelhana.Service;

import com.example.travelhana.Dto.Marker.MarkerDummyDto;
import com.example.travelhana.Dto.Marker.MarkerListDto;
import com.example.travelhana.Dto.Marker.MarkerLocationDto;
import com.example.travelhana.Dto.Marker.MarkerPickUpResultDto;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

public interface MarkerService {

	ResponseEntity<MarkerListDto> getMarkerList(String accessToken);

	ResponseEntity<MarkerListDto> createDummyMarker(MarkerDummyDto markerDummyDto);

	ResponseEntity<MarkerPickUpResultDto> pickUpMarker(String accessToken, int markerId, MarkerLocationDto markerLocationDto);

}

package com.example.travelhana.Service;


import com.example.travelhana.Dto.Marker.MarkerDummyDto;
import com.example.travelhana.Dto.Marker.MarkerLocationDto;
import org.springframework.http.ResponseEntity;

public interface MarkerService {

	ResponseEntity<?> getMarkerList(String accessToken);

	ResponseEntity<?> createDummyMarker(MarkerDummyDto markerDummyDto);

	ResponseEntity<?> pickUpMarker(String accessToken, int markerId, MarkerLocationDto markerLocationDto);

}

package com.example.travelhana.Service;


import com.example.travelhana.Dto.Marker.MarkerDummyDto;
import com.example.travelhana.Dto.Marker.LocationDto;
import org.springframework.http.ResponseEntity;

public interface MarkerService {

	ResponseEntity<?> getMarkerList(String accessToken, LocationDto locationDto, String unit, String isPickup, String sort);

	ResponseEntity<?> createDummyMarker(MarkerDummyDto markerDummyDto);

	ResponseEntity<?> pickUpMarker(String accessToken, int markerId, LocationDto locationDto);

}

package com.example.travelhana.Service;


import com.example.travelhana.Domain.Users;
import com.example.travelhana.Dto.Marker.MarkerDummyDto;
import com.example.travelhana.Dto.Marker.LocationDto;
import org.springframework.http.ResponseEntity;

public interface MarkerService {

	ResponseEntity<?> getMarkerList(Users users, LocationDto locationDto, String unit, String isPickup, String sort);

	ResponseEntity<?> pickUpMarker(Users users, int markerId, LocationDto locationDto);

	ResponseEntity<?> createDummyMarker(MarkerDummyDto markerDummyDto);

}

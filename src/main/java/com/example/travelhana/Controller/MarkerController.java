package com.example.travelhana.Controller;

import com.example.travelhana.Domain.Users;
import com.example.travelhana.Dto.Marker.LocationDto;
import com.example.travelhana.Dto.Marker.MarkerDummyDto;
import com.example.travelhana.Service.MarkerService;
import com.example.travelhana.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/marker")
public class MarkerController {

	private final UserService userService;
	private final MarkerService markerService;

	@PostMapping("")
	public ResponseEntity<?> getMarkerList(
			@RequestHeader(value = "Authorization") String accessToken,
			@RequestBody LocationDto locationDto,
			@RequestParam(defaultValue = "all", required = false) String unit,
			@RequestParam(defaultValue = "all", required = false) String isPickup,
			@RequestParam(defaultValue = "distance", required = false) String sort) {
		Users users = userService.getUserByAccessToken(accessToken);
		return markerService.getMarkerList(users, locationDto, unit, isPickup, sort);
	}

	@PostMapping("/{markerId}")
	public ResponseEntity<?> pickUpMarker(
			@RequestHeader(value = "Authorization") String accessToken,
			@PathVariable int markerId,
			@RequestBody LocationDto locationDto) {
		Users users = userService.getUserByAccessToken(accessToken);
		return markerService.pickUpMarker(users, markerId, locationDto);
	}

	@PostMapping("/dummy")
	public ResponseEntity<?> createDummyMarkers(
			@RequestBody MarkerDummyDto markerDummyDto) {
		return markerService.createDummyMarker(markerDummyDto);
	}

}

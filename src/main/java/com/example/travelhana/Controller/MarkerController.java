package com.example.travelhana.Controller;

import com.example.travelhana.Dto.*;
import com.example.travelhana.Dto.Marker.MarkerDummyDto;
import com.example.travelhana.Dto.Marker.MarkerListDto;
import com.example.travelhana.Dto.Marker.LocationDto;
import com.example.travelhana.Dto.Marker.MarkerPickUpResultDto;
import com.example.travelhana.Service.MarkerService;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/marker")
public class MarkerController {

	private final MarkerService markerService;

	@GetMapping("")
	public ResponseEntity<?> getMarkerList(
			@RequestHeader(value = "Authorization") String accessToken) {
		return markerService.getMarkerList(accessToken);
	}

	@PostMapping("/{markerId}")
	public ResponseEntity<?> pickUpMarker(
			@RequestHeader(value = "Authorization") String accessToken, @PathVariable int markerId,
			@RequestBody LocationDto locationDto) {
		return markerService.pickUpMarker(accessToken, markerId, locationDto);
	}

	@PostMapping("/dummy")
	public ResponseEntity<?> createDummyExternalAccounts(
			@RequestBody MarkerDummyDto markerDummyDto) {
		return markerService.createDummyMarker(markerDummyDto);
	}

}

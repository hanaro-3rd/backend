package com.example.travelhana.Controller;

import com.example.travelhana.Dto.*;
import com.example.travelhana.Dto.Marker.MarkerDummyDto;
import com.example.travelhana.Dto.Marker.MarkerListDto;
import com.example.travelhana.Dto.Marker.MarkerLocationDto;
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
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = MarkerListDto.class),
			@ApiResponse(code = 404, message = "NOT_FOUND", response = ResponseDto.class)
	})
	public ResponseEntity<?> getMarkerList(
			@RequestHeader(value = "Authorization") String accessToken) {
		return markerService.getMarkerList(accessToken);
	}

	@PostMapping("/{markerId}")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = MarkerPickUpResultDto.class),
			@ApiResponse(code = 404, message = "NOT_FOUND", response = ResponseDto.class),
			@ApiResponse(code = 406, message = "NOT_ACCEPTABLE", response = ResponseDto.class),
			@ApiResponse(code = 409, message = "CONFLICT", response = ResponseDto.class),
			@ApiResponse(code = 500, message = "INTERNAL_SERVER_ERROR", response = ResponseDto.class)
	})
	public ResponseEntity<?> pickUpMarker(
			@RequestHeader(value = "Authorization") String accessToken, @PathVariable int markerId, @RequestBody MarkerLocationDto markerLocationDto) {
		return markerService.pickUpMarker(accessToken, markerId, markerLocationDto);
	}

	@PostMapping("/dummy")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = MarkerListDto.class),
			@ApiResponse(code = 404, message = "NOT_FOUND", response = ResponseDto.class)
	})
	public ResponseEntity<?> createDummyExternalAccounts(
			@RequestHeader(value = "Authorization") String ignoredAccessToken, @RequestBody MarkerDummyDto markerDummyDto) {
		return markerService.createDummyMarker(markerDummyDto);
	}

}

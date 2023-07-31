package com.example.travelhana.Controller;

import com.example.travelhana.Dto.*;
import com.example.travelhana.Dto.Marker.MarkerDummyDto;
import com.example.travelhana.Dto.Marker.MarkerListDto;
import com.example.travelhana.Dto.Marker.MarkerPickUpDto;
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

	@PostMapping("/dummy")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = MarkerListDto.class),
			@ApiResponse(code = 404, message = "NOT_FOUND", response = ResponseDto.class)
	})
	public ResponseEntity<MarkerListDto> createDummyExternalAccounts(@RequestBody MarkerDummyDto markerDummyDto) {
		return markerService.createDummyMarker(markerDummyDto);
	}

	@PostMapping("/pick-up")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = MarkerPickUpResultDto.class),
			@ApiResponse(code = 404, message = "NOT_FOUND", response = ResponseDto.class),
			@ApiResponse(code = 406, message = "NOT_ACCEPTABLE", response = ResponseDto.class),
			@ApiResponse(code = 409, message = "CONFLICT", response = ResponseDto.class),
			@ApiResponse(code = 500, message = "INTERNAL_SERVER_ERROR", response = ResponseDto.class)
	})
	public ResponseEntity<MarkerPickUpResultDto> pickUpMarker(@RequestBody MarkerPickUpDto markerPickUpDto) {
		return markerService.pickUpMarker(markerPickUpDto);
	}

	@GetMapping("/{userId}")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = MarkerListDto.class),
			@ApiResponse(code = 404, message = "NOT_FOUND", response = ResponseDto.class)
	})
	public ResponseEntity<MarkerListDto> getMarkerList(@PathVariable int userId) {
		return markerService.getMarkerList(userId);
	}
}
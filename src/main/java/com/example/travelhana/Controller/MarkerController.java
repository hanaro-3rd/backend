package com.example.travelhana.Controller;

import com.example.travelhana.Domain.Marker;
import com.example.travelhana.Dto.MarkerDummyDto;
import com.example.travelhana.Dto.MarkerPickUpDto;
import com.example.travelhana.Dto.MarkerPickUpResultDto;
import com.example.travelhana.Service.MarkerService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/marker")
public class MarkerController {

	private final MarkerService markerService;

	@PostMapping("/dummy")
	public ResponseEntity<List<Marker>> createDummyExternalAccounts(@RequestBody MarkerDummyDto markerDummyDto) {
		return markerService.createDummyMarker(markerDummyDto);
	}


	@PostMapping("/pick-up")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = MarkerPickUpResultDto.class)
	})
	public ResponseEntity<?> pickUpMarker(@RequestBody MarkerPickUpDto markerPickUpDto) {
		return markerService.pickUpMarker(markerPickUpDto);
	}

}

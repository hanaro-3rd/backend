package com.example.travelhana.service;

import com.example.travelhana.Domain.*;
import com.example.travelhana.Dto.Marker.*;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Repository.*;
import com.example.travelhana.Service.implement.MarkerServiceImpl;
import com.example.travelhana.TravelhanaApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

@Transactional
@DisplayName("마커 단위 테스트")
@SpringBootTest(classes = TravelhanaApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class MarkerServiceTest {

	@InjectMocks
	private MarkerServiceImpl markerService;

	@Mock
	private MarkerRepository markerRepository;

	@Mock
	private KeymoneyRepository keymoneyRepository;

	@Mock
	private UserToMarkerRepository userToMarkerRepository;

	@Mock
	private MarkerHistoryRepository markerHistoryRepository;

	@Test
	@DisplayName("마커 목록 불러오기 테스트 (sort=distance)")
	public void getMarkerListDistanceSortTest() throws Exception {
		// given
		Users users = Users
				.builder()
				.id(1)
				.build();

		LocationDto locationDto = new LocationDto(0D, 0D);
		String unit = "all";
		String isPickup = "all";
		String sort = "distance";

		List<Marker> markers = new ArrayList<>();
		Marker marker1 = Marker
				.builder().id(1)
				.lat((double) 1).lng((double) 1)
				.amount((long) 2).limitAmount(3)
				.build();
		Marker marker2 = Marker
				.builder().id(2)
				.lat((double) 2).lng((double) 2)
				.amount((long) 1).limitAmount(2)
				.build();
		Marker marker3 = Marker
				.builder().id(3)
				.lat((double) 3).lng((double) 3)
				.amount((long) 3).limitAmount(1)
				.build();
		markers.add(marker1);
		markers.add(marker2);
		markers.add(marker3);

		// stub
		given(markerRepository.findAll()).willReturn(markers);
		given(userToMarkerRepository.existsByUsers_IdAndMarker_Id(users.getId(), 1)).willReturn(true);
		given(userToMarkerRepository.existsByUsers_IdAndMarker_Id(users.getId(), 2)).willReturn(false);
		given(userToMarkerRepository.existsByUsers_IdAndMarker_Id(users.getId(), 3)).willReturn(true);
		// when
		ResponseEntity<?> responseEntity = markerService.getMarkerList(users, locationDto, unit, isPickup, sort);

		// then
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		MarkerListDto markerListDto = (MarkerListDto) apiResponse.getResult();
		List<MarkerResultDto> markerResultDtos = markerListDto.getMarkers();

		assertEquals(markerResultDtos.get(0).getId(), 1);
		assertEquals(markerResultDtos.get(1).getId(), 2);
		assertEquals(markerResultDtos.get(2).getId(), 3);

		assertEquals(SuccessCode.SELECT_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.SELECT_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

	@Test
	@DisplayName("마커 목록 불러오기 테스트 (sort=amount)")
	public void getMarkerListAmountSortTest() throws Exception {
		// given
		Users users = Users
				.builder()
				.id(1)
				.build();

		LocationDto locationDto = new LocationDto(0D, 0D);
		String unit = "all";
		String isPickup = "all";
		String sort = "amount";

		List<Marker> markers = new ArrayList<>();
		Marker marker1 = Marker
				.builder().id(1)
				.lat((double) 1).lng((double) 1)
				.amount((long) 2).limitAmount(3)
				.build();
		Marker marker2 = Marker
				.builder().id(2)
				.lat((double) 2).lng((double) 2)
				.amount((long) 1).limitAmount(2)
				.build();
		Marker marker3 = Marker
				.builder().id(3)
				.lat((double) 3).lng((double) 3)
				.amount((long) 3).limitAmount(1)
				.build();
		markers.add(marker1);
		markers.add(marker2);
		markers.add(marker3);

		// stub
		given(markerRepository.findAll()).willReturn(markers);
		given(userToMarkerRepository.existsByUsers_IdAndMarker_Id(users.getId(), 1)).willReturn(true);
		given(userToMarkerRepository.existsByUsers_IdAndMarker_Id(users.getId(), 2)).willReturn(false);
		given(userToMarkerRepository.existsByUsers_IdAndMarker_Id(users.getId(), 3)).willReturn(true);
		// when
		ResponseEntity<?> responseEntity = markerService.getMarkerList(users, locationDto, unit, isPickup, sort);

		// then
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		MarkerListDto markerListDto = (MarkerListDto) apiResponse.getResult();
		List<MarkerResultDto> markerResultDtos = markerListDto.getMarkers();

		assertEquals(markerResultDtos.get(0).getId(), 2);
		assertEquals(markerResultDtos.get(1).getId(), 1);
		assertEquals(markerResultDtos.get(2).getId(), 3);

		assertEquals(SuccessCode.SELECT_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.SELECT_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

	@Test
	@DisplayName("마커 목록 불러오기 테스트 (sort=limitAmount)")
	public void getMarkerListLimitAmountSortTest() throws Exception {
		// given
		Users users = Users
				.builder()
				.id(1)
				.build();

		LocationDto locationDto = new LocationDto(0D, 0D);
		String unit = "all";
		String isPickup = "all";
		String sort = "limitAmount";

		List<Marker> markers = new ArrayList<>();
		Marker marker1 = Marker
				.builder().id(1)
				.lat((double) 1).lng((double) 1)
				.amount((long) 2).limitAmount(3)
				.build();
		Marker marker2 = Marker
				.builder().id(2)
				.lat((double) 2).lng((double) 2)
				.amount((long) 1).limitAmount(2)
				.build();
		Marker marker3 = Marker
				.builder().id(3)
				.lat((double) 3).lng((double) 3)
				.amount((long) 3).limitAmount(1)
				.build();
		markers.add(marker1);
		markers.add(marker2);
		markers.add(marker3);

		// stub
		given(markerRepository.findAll()).willReturn(markers);
		given(userToMarkerRepository.existsByUsers_IdAndMarker_Id(users.getId(), 1)).willReturn(true);
		given(userToMarkerRepository.existsByUsers_IdAndMarker_Id(users.getId(), 2)).willReturn(false);
		given(userToMarkerRepository.existsByUsers_IdAndMarker_Id(users.getId(), 3)).willReturn(true);

		// when
		ResponseEntity<?> responseEntity = markerService.getMarkerList(users, locationDto, unit, isPickup, sort);

		// then
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		MarkerListDto markerListDto = (MarkerListDto) apiResponse.getResult();
		List<MarkerResultDto> markerResultDtos = markerListDto.getMarkers();

		assertEquals(markerResultDtos.get(0).getId(), 3);
		assertEquals(markerResultDtos.get(1).getId(), 2);
		assertEquals(markerResultDtos.get(2).getId(), 1);

		assertEquals(SuccessCode.SELECT_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.SELECT_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

	@Test
	@DisplayName("마커 목록 불러오기 테스트 (isPickup=true)")
	public void getMarkerListIsPickupTrueTest() throws Exception {
		// given
		Users users = Users
				.builder()
				.id(1)
				.build();

		LocationDto locationDto = new LocationDto(0D, 0D);
		String unit = "all";
		String isPickup = "true";
		String sort = "distance";

		List<Marker> markers = new ArrayList<>();
		Marker marker1 = Marker
				.builder().id(1)
				.lat((double) 1).lng((double) 1)
				.amount((long) 2).limitAmount(3)
				.build();
		Marker marker2 = Marker
				.builder().id(2)
				.lat((double) 2).lng((double) 2)
				.amount((long) 1).limitAmount(2)
				.build();
		Marker marker3 = Marker
				.builder().id(3)
				.lat((double) 3).lng((double) 3)
				.amount((long) 3).limitAmount(1)
				.build();
		markers.add(marker1);
		markers.add(marker2);
		markers.add(marker3);

		// stub
		given(markerRepository.findAll()).willReturn(markers);
		given(userToMarkerRepository.existsByUsers_IdAndMarker_Id(users.getId(), 1)).willReturn(true);
		given(userToMarkerRepository.existsByUsers_IdAndMarker_Id(users.getId(), 2)).willReturn(false);
		given(userToMarkerRepository.existsByUsers_IdAndMarker_Id(users.getId(), 3)).willReturn(true);
		// when
		ResponseEntity<?> responseEntity = markerService.getMarkerList(users, locationDto, unit, isPickup, sort);

		// then
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		MarkerListDto markerListDto = (MarkerListDto) apiResponse.getResult();
		List<MarkerResultDto> markerResultDtos = markerListDto.getMarkers();

		assertEquals(markerResultDtos.get(0).getIsPickUp(), true);
		assertEquals(markerResultDtos.get(1).getIsPickUp(), true);

		assertEquals(SuccessCode.SELECT_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.SELECT_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

	@Test
	@DisplayName("마커 목록 불러오기 테스트 (isPickup=false)")
	public void getMarkerListIsPickupFalseTest() throws Exception {
		// given
		Users users = Users
				.builder()
				.id(1)
				.build();

		LocationDto locationDto = new LocationDto(0D, 0D);
		String unit = "all";
		String isPickup = "false";
		String sort = "distance";

		List<Marker> markers = new ArrayList<>();
		Marker marker1 = Marker
				.builder().id(1)
				.lat((double) 1).lng((double) 1)
				.amount((long) 2).limitAmount(3)
				.build();
		Marker marker2 = Marker
				.builder().id(2)
				.lat((double) 2).lng((double) 2)
				.amount((long) 1).limitAmount(2)
				.build();
		Marker marker3 = Marker
				.builder().id(3)
				.lat((double) 3).lng((double) 3)
				.amount((long) 3).limitAmount(1)
				.build();
		markers.add(marker1);
		markers.add(marker2);
		markers.add(marker3);

		// stub
		given(markerRepository.findAll()).willReturn(markers);
		given(userToMarkerRepository.existsByUsers_IdAndMarker_Id(users.getId(), 1)).willReturn(true);
		given(userToMarkerRepository.existsByUsers_IdAndMarker_Id(users.getId(), 2)).willReturn(false);
		given(userToMarkerRepository.existsByUsers_IdAndMarker_Id(users.getId(), 3)).willReturn(true);

		// when
		ResponseEntity<?> responseEntity = markerService.getMarkerList(users, locationDto, unit, isPickup, sort);

		// then
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		MarkerListDto markerListDto = (MarkerListDto) apiResponse.getResult();
		List<MarkerResultDto> markerResultDtos = markerListDto.getMarkers();

		assertEquals(markerResultDtos.get(0).getIsPickUp(), false);

		assertEquals(SuccessCode.SELECT_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.SELECT_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

	@Test
	@DisplayName("마커 줍기 테스트")
	public void pickUpMarkerTest() throws Exception {
		// given
		Users users = Users
				.builder()
				.id(1)
				.build();

		int markerId = 1;
		LocationDto locationDto = new LocationDto(0D, 0D);

		Marker marker = Marker
				.builder().id(1)
				.lat(0D).lng(0D)
				.amount(2L).limitAmount(3)
				.unit("JPY")
				.build();

		Keymoney keymoney = Keymoney
				.builder()
				.id(1)
				.users(users)
				.unit("JPY")
				.balance(1000L)
				.build();

		UserToMarker userToMarker = UserToMarker
				.builder()
				.users(users)
				.marker(marker)
				.pickDate(LocalDateTime.now())
				.build();

		MarkerHistory markerHistory = MarkerHistory
				.builder()
				.markerId(markerId)
				.userId(users.getId())
				.keymoneyId(keymoney.getId())
				.pickDate(LocalDateTime.now())
				.amount(marker.getAmount())
				.balance(keymoney.getBalance()+marker.getAmount())
				.place(marker.getPlace())
				.build();

		// stub
		given(markerRepository.findById(markerId)).willReturn(Optional.ofNullable(marker));
		given(userToMarkerRepository.existsByUsers_IdAndMarker_Id(users.getId(), marker.getId())).willReturn(false);
		given(keymoneyRepository.findByUsers_IdAndUnit(users.getId(), marker.getUnit())).willReturn(Optional.ofNullable(keymoney));
		given(userToMarkerRepository.save(userToMarker)).willReturn(userToMarker);
		given(markerHistoryRepository.save(markerHistory)).willReturn(markerHistory);

		// when
		ResponseEntity<?> responseEntity = markerService.pickUpMarker(users, markerId, locationDto);

		// then
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		MarkerPickUpResultDto markerListDto = (MarkerPickUpResultDto) apiResponse.getResult();

		assertEquals(markerListDto.getUserId(), 1);
		assertEquals(markerListDto.getPrice(), 2L);
		assertEquals(markerListDto.getUnit(), "JPY");

		assertEquals(SuccessCode.INSERT_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.INSERT_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

	@Test
	@DisplayName("더미 마커 생성 테스트")
	public void createDummyMarker() throws Exception {
		// given
		Users users = Users
				.builder()
				.id(1)
				.build();

		int markerId = 1;
		LocationDto locationDto = new LocationDto(0D, 0D);

		List<Marker> markers = new ArrayList<>();

		MarkerDummyDto markerDummyDto = MarkerDummyDto
				.builder()
				.lat(0D).lng(0D)
				.amount(2L).limitAmount(3)
				.address("test address")
				.place("test place")
				.unit("JPY")
				.build();

		Marker marker = Marker
				.builder()
				.unit(markerDummyDto.getUnit())
				.amount(markerDummyDto.getAmount())
				.limitAmount(markerDummyDto.getLimitAmount())
				.lat(markerDummyDto.getLat())
				.lng(markerDummyDto.getLng())
				.place(markerDummyDto.getPlace())
				.address(markerDummyDto.getAddress())
				.build();
		markers.add(marker);

		Marker tokyoStation = Marker
				.builder()
				.amount(100L)
				.lat(35.6812)
				.lng(139.7670)
				.limitAmount(20)
				.place("도쿄역")
				.unit("JPY")
				.address("일본 도쿄도 지요다구 마루노우치 1 조메-9")
				.build();
		markers.add(tokyoStation);

		Marker tokyoTower = Marker
				.builder()
				.amount(100L)
				.lat(35.6586)
				.lng(139.7454)
				.limitAmount(20)
				.place("도쿄 타워")
				.unit("JPY")
				.address("일본 도쿄도 미나토구 시바코엔 4 조메-2-8")
				.build();
		markers.add(tokyoTower);

		Marker sensouji = Marker
				.builder()
				.amount(100L)
				.lat(35.7147)
				.lng(139.7966)
				.limitAmount(20)
				.place("센소지")
				.unit("JPY")
				.address("일본 도쿄도 다이토구 아사쿠사 2 조메-3-1")
				.build();
		markers.add(sensouji);

		Marker sibuyaSky = Marker
				.builder()
				.amount(100L)
				.lat(35.6584)
				.lng(139.7022)
				.limitAmount(20)
				.place("시부야 스카이")
				.unit("JPY")
				.address("일본 도쿄도 시부야구 시부야 2 조메-24-12")
				.build();
		markers.add(sibuyaSky);

		// stub
		given(markerRepository.existsById(1)).willReturn(false);
		given(markerRepository.save(tokyoStation)).willReturn(tokyoStation);
		given(markerRepository.save(tokyoTower)).willReturn(tokyoTower);
		given(markerRepository.save(sensouji)).willReturn(sensouji);
		given(markerRepository.save(sibuyaSky)).willReturn(sibuyaSky);
		given(markerRepository.save(marker)).willReturn(marker);
		given(markerRepository.findAll()).willReturn(markers);

		// when
		ResponseEntity<?> responseEntity = markerService.createDummyMarker(markerDummyDto);

		// then
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

		ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();
		assertNotNull(apiResponse);

		MarkerListDto markerListDto = (MarkerListDto) apiResponse.getResult();
		List<MarkerResultDto> markerResultDtos = markerListDto.getMarkers();

		assertEquals(markerResultDtos.size(), 5);

		assertEquals(SuccessCode.INSERT_SUCCESS.getStatusCode(), apiResponse.getResultCode());
		assertEquals(SuccessCode.INSERT_SUCCESS.getMessage(), apiResponse.getResultMsg());
	}

}

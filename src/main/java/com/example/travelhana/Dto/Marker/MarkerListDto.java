package com.example.travelhana.Dto.Marker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class MarkerListDto {

	List<MarkerResultDto> markers;

}

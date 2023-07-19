package com.example.travelhana.Service;

import com.example.travelhana.Util.HolidayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;

@Service
@RequiredArgsConstructor
public class AccountService {
	private final HolidayUtil holidayUtil;

	public Boolean isBusinessDay() throws URISyntaxException, IOException {
		return holidayUtil.isBusinessDay();
	}
}

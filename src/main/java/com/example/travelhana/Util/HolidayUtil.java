package com.example.travelhana.Util;

import com.example.travelhana.Data.Holidays;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class HolidayUtil {

	@Value("${HOLIDAY_ENCODED_KEY}")
	private String apikey;

	private JSONArray getHolidaysByAPI() throws URISyntaxException {
		String year = String.valueOf(LocalDate.now().getYear());

		StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getHoliDeInfo"); /*URL*/
		urlBuilder.append("?" + URLEncoder.encode("serviceKey",StandardCharsets.UTF_8) + "=" + apikey); /*Service Key*/
		urlBuilder.append("&" + URLEncoder.encode("_type",StandardCharsets.UTF_8) + "=" + URLEncoder.encode("json", StandardCharsets.UTF_8)); /*페이지번호*/
		urlBuilder.append("&" + URLEncoder.encode("pageNo",StandardCharsets.UTF_8) + "=" + URLEncoder.encode("1", StandardCharsets.UTF_8)); /*페이지번호*/
		urlBuilder.append("&" + URLEncoder.encode("numOfRows",StandardCharsets.UTF_8) + "=" + URLEncoder.encode("30", StandardCharsets.UTF_8)); /*한 페이지 결과 수*/
		urlBuilder.append("&" + URLEncoder.encode("solYear",StandardCharsets.UTF_8) + "=" + URLEncoder.encode(year, StandardCharsets.UTF_8)); /*연*/

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

		RestTemplate restTemplate = new RestTemplate();

		URI uri = new URI(urlBuilder.toString());

		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

		JSONObject parser = new JSONObject(response.getBody());

		JSONArray resp = parser.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");

		return resp;
	}

	private void makeHolidaysFile() throws URISyntaxException {

		JSONArray resp = getHolidaysByAPI();

		List<String> locdates = new ArrayList<>();

		for(Object o : resp) {
			JSONObject holiday = (JSONObject) o;
			String isHoliday = (String)holiday.get("isHoliday");
			if(isHoliday.equals("Y")) {
				locdates.add(String.valueOf(holiday.getInt("locdate")));
			}
		}

		String projectDir = System.getProperty("user.dir");
		String filePath = projectDir + "/src/main/java/com/example/travelhana/Data/Holidays.java";

		try (FileWriter file = new FileWriter(filePath)) {
			String newLine = System.lineSeparator();
			file.write("package com.example.travelhana.Data;" + newLine + newLine);
			file.write("import java.util.Arrays;" + newLine);
			file.write("import java.util.List;" + newLine + newLine);
			file.write("public class Holidays {" + newLine);
			file.write("\tpublic static final List<String> HOLIDAYS = Arrays.asList(" + newLine);
			file.write("\t\t");
			for(int i = 0; i < locdates.size(); i++) {
				file.write("\"" + locdates.get(i) + "\"");
				if(i != locdates.size() - 1) {
					file.write(", ");
				}
			}
			file.write("\n\t);\n}");

			System.out.println("공휴일 파일이 성공적으로 작성되었습니다.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Boolean isWeekend(LocalDate today) {

		DayOfWeek dayOfWeek = today.getDayOfWeek();

		if (dayOfWeek.getValue() >= 6 ) {
			System.out.println("주말입니다.");
			return true;
		} else {
			System.out.println("주말이 아닙니다.");
			return false;
		}
	}

	private Boolean isHoliday(LocalDate today) {

		List<String> holidays = Holidays.HOLIDAYS;

		String strToday = today.toString().replace("-", "");

		if (holidays.contains(strToday)) {
			System.out.println("공휴일입니다.");
			return true;
		} else {
			System.out.println("공휴일이 아닙니다.");
			return false;
		}
	}

	public Boolean isBusinessDay() throws URISyntaxException {

		LocalDate today = LocalDate.now();

		if (today.toString().endsWith("-01-01")) {
			System.out.println("1월 1일입니다. 공휴일 목록을 갱신합니다.");
			makeHolidaysFile();
			return false;
		}

		if (isWeekend(today) || isHoliday(today)) {
			return false;
		}

		return true;
	}

}

package com.example.travelhana.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtConstants {

	// Expiration Time
	public static final long MINUTE = 1000 * 60;
	public static final long HOUR = 60 * MINUTE;
	public static final long DAY = 24 * HOUR;
	public static final long MONTH = 30 * DAY;

<<<<<<< HEAD
	public static final long AT_EXP_TIME = DAY;
	public static final long RT_EXP_TIME = 10 * DAY;

	// Secret
	@Value("${jwt.secret.key}")
	public String JWT_SECRET;
=======
    public static final long AT_EXP_TIME =  DAY;
    public static final long RT_EXP_TIME =  10 * DAY;
    // Secret
    @Value("${jwt.secret.key}")
    public String JWT_SECRET;

>>>>>>> 2c922a40dc60536113a5a6cdd329816dc15e42c3

	// Header
	public static final String AT_HEADER = "access_token";
	public static final String RT_HEADER = "refresh_token";
	public static final String TOKEN_HEADER_PREFIX = "Bearer ";
	public static final String LOGIN_OR_REFRESH = "noAuthrization ";
<<<<<<< HEAD

=======
>>>>>>> 2c922a40dc60536113a5a6cdd329816dc15e42c3
}

package com.example.travelhana.Service;

import com.example.travelhana.Dto.*;
import com.example.travelhana.Dto.Account.*;
import com.example.travelhana.Exception.BusinessException;
import com.example.travelhana.Exception.ErrorCode;
import com.example.travelhana.Util.ExchangeRateUtil;
import com.example.travelhana.Util.HolidayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.travelhana.Domain.Account;
import com.example.travelhana.Domain.ExternalAccount;
import com.example.travelhana.Domain.User;
import com.example.travelhana.Repository.AccountRepository;
import com.example.travelhana.Repository.ExternalAccountRepository;
import com.example.travelhana.Repository.UserRepository;
import com.example.travelhana.Util.CryptoUtil;
import com.example.travelhana.Util.SaltUtil;
import com.example.travelhana.Projection.AccountInfoProjection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final SaltUtil saltUtil;
    private final CryptoUtil cryptoUtil;
    private final HolidayUtil holidayUtil;
    private final ExchangeRateUtil exchangeRateUtil;

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final ExternalAccountRepository externalAccountRepository;

    private List<AccountConnectResultDto> decryptAccountNum(int userId, List<AccountInfoProjection> projections) throws Exception {
        List<AccountConnectResultDto> result = new ArrayList<>();
        for (AccountInfoProjection projection : projections) {
            result.add(new AccountConnectResultDto(userId, projection.getId(), cryptoUtil.decrypt(projection.getAccountNum()), projection.getBank(), projection.getBalance()));
        }
        return result;
    }

    public ResponseEntity<ConnectedAccountListDto> getConnectedAccountList(int userId) throws Exception {
        // userId에 대한 유저의 연결된 계좌 목록 가져오기
        List<AccountInfoProjection> connectedAccounts = accountRepository.findAllByUser_Id(userId);

        // 휴일 여부 가져오기
        Boolean isBusinessDay = holidayUtil.isBusinessDay();

        // OpenAPI로 각 환율 정보 가져오기
        ExchangeRateDto usdExchangeRateDto = exchangeRateUtil.getExchangeRateByAPI("USD");
        ExchangeRateDto jpyExchangeRateDto = exchangeRateUtil.getExchangeRateByAPI("JPY");
        ExchangeRateDto eurExchangeRateDto = exchangeRateUtil.getExchangeRateByAPI("EUR");

        // 연결된 계좌, 휴일 여부, 각 환율 정보 DTO에 파싱 후 리턴
        ConnectedAccountListDto result = new ConnectedAccountListDto(decryptAccountNum(userId, connectedAccounts), isBusinessDay, usdExchangeRateDto, jpyExchangeRateDto, eurExchangeRateDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<AccountListDto> createDummyExternalAccounts(AccountDummyDto accountDummyDto) throws Exception {
        Random random = new Random();

        // 입력한 정보와 랜덤값으로 유저 정보 생성
        String userName = accountDummyDto.getUserName();
        String registrationNum = accountDummyDto.getRegistrationNum();
        String accountPassword = accountDummyDto.getAccountPassword();

        String userSalt = saltUtil.generateSalt();

        User user = User
                .builder()
                .deviceId("1234")
                .isWithdrawal(false)
                .name(userName)
                .password(saltUtil.encodePassword(userSalt, "1234"))
                .pattern(saltUtil.encodePassword(userSalt, "1234"))
                .registrationNum(registrationNum)
                .phoneNum("010-1234-1234")
                .salt(userSalt)
                .build();
        userRepository.save(user);

        // 입력한 정보와 랜덤값으로 더미 외부 계좌 정보 생성
        List<String> banks = Arrays.asList("신한", "국민", "하나", "우리", "토스", "카카오");

        for (int i = 0; i < 10; i++) {
            String accountSalt = saltUtil.generateSalt();

            String group1 = String.format("%03d", random.nextInt(1000));
            String group2 = String.format("%04d", random.nextInt(10000));
            String group3 = String.format("%04d", random.nextInt(10000));

            String accountNum = group1 + "-" + group2 + "-" + group3;

            ExternalAccount externalAccount = ExternalAccount
                    .builder()
                    .accountNum(cryptoUtil.encrypt(accountNum))
                    .bank(banks.get(random.nextInt(banks.size())))
                    .openDate(java.sql.Date.valueOf(LocalDate.now().minusDays(random.nextInt(365))))
                    .salt(accountSalt)
                    .password(saltUtil.encodePassword(accountSalt, accountPassword))
                    .registrationNum(registrationNum)
                    .balance(0L)
                    .build();
            externalAccountRepository.save(externalAccount);
        }

        // 유저의 주민번호에 해당하는 외부 계좌 목록을 불러옴
        List<AccountInfoProjection> projections = externalAccountRepository.findAllByRegistrationNum(registrationNum);

        // 계좌번호를 복호화하여 AccountListDto에 파싱 후 리턴
        AccountListDto result = new AccountListDto(decryptAccountNum(0, projections));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    public ResponseEntity<AccountListDto> findExternalAccountList(int userId) throws Exception {
        // userId에 해당하는 탈퇴하지 않은 유저가 있는지 확인
        User user = userRepository.findByIdAndIsWithdrawal(userId, false)
                .orElseThrow(() -> new BusinessException("유저를 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND));

        // 유저의 주민번호에 해당하는 외부 계좌 목록을 불러옴
        List<AccountInfoProjection> projections = externalAccountRepository.findAllByRegistrationNum(user.getRegistrationNum());

        // 계좌번호를 복호화하여 AccountListDto에 파싱 후 리턴
        AccountListDto result = new AccountListDto(decryptAccountNum(0, projections));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<AccountConnectResultDto> connectExternalAccount(AccountConnectDto connectAccountDto) throws Exception {
        // userId에 해당하는 탈퇴하지 않은 유저가 있는지 확인
        int userId = connectAccountDto.getUserId();
        User user = userRepository.findByIdAndIsWithdrawal(userId, false)
                .orElseThrow(() -> new BusinessException("유저를 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND));

        // externalAccountId에 대한 외부 계좌 존재 여부 확인
        int externalAccountId = connectAccountDto.getExternalAccountId();
        ExternalAccount externalAccount = externalAccountRepository.findById(externalAccountId)
                .orElseThrow(() -> new BusinessException("외부 계좌를 찾을 수 없습니다.", ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND));

        // 해당 유저의 계좌 소유 여부 확인
        if (!user.getRegistrationNum().equals(externalAccount.getRegistrationNum())) {
            throw new BusinessException("유저와 계좌 정보가 일치하지 않습니다.", ErrorCode.UNAUTHORIZED_USER_ACCOUNT);
        }

        // 비밀번호 확인
        String storedSalt = externalAccount.getSalt();
        String storedPassword = externalAccount.getPassword();
        String encodedPassword = saltUtil.encodePassword(storedSalt, connectAccountDto.getAccountPassword());
        if (!storedPassword.equals(encodedPassword)) {
            throw new BusinessException("비밀번호가 일치하지 않습니다.", ErrorCode.UNAUTHORIZED_PASSWORD);
        }

        // 이미 연결된 계좌 여부 확인
        String accountNum = externalAccount.getAccountNum();
        Boolean existAccount = accountRepository.existsAccountByAccountNum(accountNum);
        if (existAccount) {
            throw new BusinessException("이미 연결된 계좌입니다.", ErrorCode.ALREADY_EXIST_ACCOUNT);
        }

        // 연결된 계좌 레코드 생성
        Account account = Account
                .builder()
                .user(user)
                .accountNum(accountNum)
                .bank(externalAccount.getBank())
                .openDate(externalAccount.getOpenDate())
                .password(externalAccount.getPassword())
                .salt(externalAccount.getSalt())
                .balance(externalAccount.getBalance())
                .build();
        accountRepository.save(account);

        // 계좌번호를 복호화하여 AccountConnectResultDto 파싱 후 리턴
        AccountConnectResultDto result = new AccountConnectResultDto(userId, account.getId(), cryptoUtil.decrypt(accountNum), externalAccount.getBank(), externalAccount.getBalance());
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

}

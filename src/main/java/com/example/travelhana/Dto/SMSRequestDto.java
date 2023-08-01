package com.example.travelhana.Dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class SMSRequestDto {

    private String type;
    private String from;
    private String subject;
    private String content;
    private List<MessageDto> messages;

}

package com.authservice.models;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    private String displayName;
    private String profilePictureUrl;
    private Date birthday;
    private String address;
}
package com.isep.appli.models;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ModifyUserInfoForm {
    @Nullable
    private String email;
    @Nullable
    private String password;
    @Nullable
    private String username;
}

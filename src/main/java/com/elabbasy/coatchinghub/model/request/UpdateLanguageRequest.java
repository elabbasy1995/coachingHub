package com.elabbasy.coatchinghub.model.request;

import com.elabbasy.coatchinghub.model.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLanguageRequest {

    private Language language;
}
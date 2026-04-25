package com.elabbasy.coatchinghub.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@MappedSuperclass
public class BaseDto implements Serializable {

    private Long id;
    @JsonIgnore
    private boolean deleted;

}

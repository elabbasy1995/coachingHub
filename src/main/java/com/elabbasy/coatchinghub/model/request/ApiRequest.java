package com.elabbasy.coatchinghub.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ApiRequest<T> {
    private T filter;
    private int pageIndex;
    private int pageSize;
    private String sortBy;
    private String sortDir;

    public PageRequest buildPagination() {
        if (this.getPageSize() == 0) {
            return PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.DESC, "createdDate"));
        }
        if (Objects.nonNull(this.getSortBy())) {
            if (Objects.nonNull(this.getSortDir()) && "desc".equalsIgnoreCase(this.getSortDir())) {
                return PageRequest.of(this.getPageIndex(), this.getPageSize(), Sort.by(Sort.Direction.DESC, this.getSortBy()));
            } else {
                return PageRequest.of(this.getPageIndex(), this.getPageSize(), Sort.by(Sort.Direction.ASC, this.getSortBy()));
            }
        }
        return PageRequest.of(this.getPageIndex(), this.getPageSize(), Sort.by(Sort.Direction.DESC, "createdDate"));
    }
}

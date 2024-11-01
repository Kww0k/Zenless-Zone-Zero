package com.backend.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListVO<T> {

    private Long total;

    private List<T> list;
}

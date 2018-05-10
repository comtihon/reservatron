package com.tabler.reservatron.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO<T> {
    private boolean result;
    private T response;

    public static ResponseDTO<String> noSuchTable() {
        return new ResponseDTO<>(false, "NO_SUCH_TABLE");
    }

    public static ResponseDTO<String> reservationConflict() {
        return new ResponseDTO<>(false, "CONFLICT");
    }}

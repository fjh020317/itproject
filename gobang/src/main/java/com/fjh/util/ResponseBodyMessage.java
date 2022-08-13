package com.fjh.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseBodyMessage<V> {
    private int status;
    private String message;
    private V data;
}

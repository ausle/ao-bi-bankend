package com.ao.bi.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;

public enum StatusEnum {

    SUCCESS("成功", "success"),
    WAIT("等待", "wait"),
    RUNNING("生成中", "running"),
    FAIL("失败", "fail");

    private final String text;

    private final String value;

    StatusEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }
}
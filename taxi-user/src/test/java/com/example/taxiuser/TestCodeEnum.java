package com.example.taxiuser;

/**
 * <p>Project: taxi-test
 * <p>Description:
 * <p>
 *
 * @author lizhao 2022/10/28
 */
public enum TestCodeEnum {

  CODE_1(1, "1"),
  CODE_2(2, "2"),
  CODE_3(3, "3"),
  CODE_4(4, "4"),
  CODE_5(5, "5"),
  CODE_6(6, "6"),
  CODE_7(7, "7"),
  CODE_8(8, "8"),
  CODE_9(9, "9");

  Integer code;

  String desc;

  TestCodeEnum() {
  }

  TestCodeEnum(int code, String desc) {
  }

  int getCode() {
    return code;
  }

  String getDesc(int code) {
    for (TestCodeEnum codeEnum : values()) {
      if (codeEnum.code == code)
        return desc;
    }
    return "";
  }
}

package com.example.springbootbackendedusys.common;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class R {
  private Boolean success;
  private String message;
  private Object data;

  private R() {}

  public static R ok() {
    R r = new R();
    r.setSuccess(true);
    return r;
  }

  public static R error() {
    R r = new R();
    r.setSuccess(false);
    return r;
  }

  public R message(String message) {
    this.setMessage(message);
    return this;
  }

  public R data(Object value) {
    this.setData(value);
    return this;
  }
}

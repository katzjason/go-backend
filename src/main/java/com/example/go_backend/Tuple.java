package com.example.go_backend;

public class Tuple {
  public final int first;
  public final int second;

  public Tuple(int first, int second) {
    this.first = first;
    this.second = second;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (obj.getClass() != this.getClass()) {
      return false;
    }
    Tuple tuple = (Tuple) obj;
    if (tuple.first != this.first || tuple.second != this.second) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "(" + first + ", " + second + ")";
  }

  @Override
  public int hashCode() {
    return (51 * first) + (3 * second);
  }
}

package name.falgout.jeffrey.fx.reduce;

class IntegerAverage extends NumberAverage<Integer, Double> {
  public IntegerAverage() {}

  @Override
  protected Integer zero() {
    return 0;
  }

  @Override
  protected Integer negate(Integer number) {
    return -number;
  }

  @Override
  protected Integer add(Integer op1, Integer op2) {
    return op1 + op2;
  }

  @Override
  protected Double divide(Integer numerator, long denominator) {
    return numerator / (double) denominator;
  }
}

package name.falgout.jeffrey.fx.reduce;

class LongAverage extends NumberAverage<Long, Double> {
  LongAverage() {}

  @Override
  protected Long zero() {
    return 0L;
  }

  @Override
  protected Long negate(Long number) {
    return -number;
  }

  @Override
  protected Long add(Long op1, Long op2) {
    return op1 + op2;
  }

  @Override
  protected Double divide(Long numerator, long denominator) {
    return numerator / (double) denominator;
  }
}

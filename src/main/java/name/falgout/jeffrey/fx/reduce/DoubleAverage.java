package name.falgout.jeffrey.fx.reduce;

class DoubleAverage extends NumberAverage<Double, Double> {
  DoubleAverage() {}

  @Override
  protected Double zero() {
    return 0.0;
  }

  @Override
  protected Double negate(Double number) {
    return -number;
  }

  @Override
  protected Double add(Double op1, Double op2) {
    return op1 + op2;
  }

  @Override
  protected Double divide(Double numerator, long denominator) {
    return numerator / denominator;
  }
}

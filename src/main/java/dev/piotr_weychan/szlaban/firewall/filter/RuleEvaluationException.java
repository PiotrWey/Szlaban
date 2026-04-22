package dev.piotr_weychan.szlaban.firewall.filter;

public class RuleEvaluationException extends RuntimeException {
  public RuleEvaluationException(String message) {
    super(message);
  }
  public RuleEvaluationException(String message, Throwable cause) {
    super(message, cause);
  }
  public RuleEvaluationException(Throwable cause) {
    super(cause);
  }
}

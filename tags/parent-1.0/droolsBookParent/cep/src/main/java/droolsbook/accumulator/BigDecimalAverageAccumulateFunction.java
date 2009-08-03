package droolsbook.accumulator;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.drools.runtime.rule.AccumulateFunction;

// @extract-start 06 25
public class BigDecimalAverageAccumulateFunction implements
    AccumulateFunction {

  /**
   * creates and returns a context object
   */
  public Serializable createContext() {
    return new AverageData();
  }

  /**
   * initializes this accumulator
   */
  public void init(Serializable context) throws Exception {
    AverageData data = (AverageData) context;
    data.count = 0;
    data.total = BigDecimal.ZERO;
  }

  /**
   * @return true if this accumulator supports reverse
   */
  public boolean supportsReverse() {
    return true;
  }

  /**
   * accumulate the given value, increases count
   */
  public void accumulate(Serializable context, Object value) {
    AverageData data = (AverageData) context;
    data.count++;
    data.total = data.total.add((BigDecimal) value);
  }

  /**
   * retracts accumulated amount, decreases count 
   */
  public void reverse(Serializable context, Object value)
      throws Exception {
    AverageData data = (AverageData) context;
    data.count++;
    data.total = data.total.subtract((BigDecimal) value);
  }

  /**
   * @return currently calculated value
   */
  public Object getResult(Serializable context)
      throws Exception {
    AverageData data = (AverageData) context;
    return data.count == 0 ? BigDecimal.ZERO : data.total
        .divide(BigDecimal.valueOf(data.count),
            RoundingMode.HALF_UP);
  }
  // @extract-end

  public void readExternal(ObjectInput in) throws IOException,
      ClassNotFoundException {
  }

  public void writeExternal(ObjectOutput out)
      throws IOException {
  }

  // @extract-start 06 26
  /**
   * value holder that stores the total amount and how many 
   * numbers were aggregated
   */
  public static class AverageData implements Externalizable {
    public int count = 0;
    public BigDecimal total = BigDecimal.ZERO;

    public void readExternal(ObjectInput in)
        throws IOException, ClassNotFoundException {
      count = in.readInt();
      total = (BigDecimal) in.readObject();
    }

    public void writeExternal(ObjectOutput out)
        throws IOException {
      out.writeInt(count);
      out.writeObject(total);
    }

  }
  // @extract-end

}

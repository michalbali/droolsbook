package droolsbook.evaluator;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.BaseEvaluator;
import org.drools.base.ValueType;
import org.drools.base.evaluators.EvaluatorDefinition;
import org.drools.base.evaluators.Operator;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.VariableRestriction.ObjectVariableContextEntry;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;

//note that this cannot be converted to new api because evaluetor definition is a marker interface and it doesn;t have any methods

// @extract-start 06 20
public class InstanceEqualsEvaluatorDefinition implements
    EvaluatorDefinition {
  public static final Operator INSTANCE_EQUALS = Operator
      .addOperatorToRegistry("instanceEquals", false);
  public static final Operator NOT_INSTANCE_EQUALS = Operator
      .addOperatorToRegistry("instanceEquals", true);

  private static final String[] SUPPORTED_IDS = { 
      INSTANCE_EQUALS.getOperatorString() };

  private Evaluator[] evaluator;

  @Override
  public Evaluator getEvaluator(ValueType type,
      Operator operator) {
    return this.getEvaluator(type, operator
        .getOperatorString(), operator.isNegated(), null);
  }

  @Override
  public Evaluator getEvaluator(ValueType type,
      Operator operator, String parameterText) {
    return this.getEvaluator(type, operator
        .getOperatorString(), operator.isNegated(),
        parameterText);
  }

  @Override
  public Evaluator getEvaluator(ValueType type,
      String operatorId, boolean isNegated,
      String parameterText) {
    return getEvaluator(type, operatorId, isNegated,
        parameterText, Target.FACT, Target.FACT);
  }

  @Override
  public Evaluator getEvaluator(ValueType type,
      String operatorId, boolean isNegated,
      String parameterText, Target leftTarget,
      Target rightTarget) {
    if (evaluator == null) {
      evaluator = new Evaluator[2];
    }
    int index = isNegated ? 0 : 1;
    if (evaluator[index] == null) {
      evaluator[index] = new InstanceEqualsEvaluator(type,
          isNegated);
    }
    return evaluator[index];
  }

  @Override
  public String[] getEvaluatorIds() {
    return SUPPORTED_IDS;
  }

  @Override
  public boolean isNegatable() {
    return true;
  }

  @Override
  public Target getTarget() {
    return Target.FACT;
  }

  @Override
  public boolean supportsType(ValueType type) {
    return true;
  }

  @Override
  public void readExternal(ObjectInput in)
      throws IOException, ClassNotFoundException {
    evaluator = (Evaluator[]) in.readObject();
  }

  @Override
  public void writeExternal(ObjectOutput out)
      throws IOException {
    out.writeObject(evaluator);
  }
  // @extract-end

  // @extract-start 06 11
  public static class InstanceEqualsEvaluator extends
      BaseEvaluator {

    public InstanceEqualsEvaluator(final ValueType type,
        final boolean isNegated) {
      super(type, isNegated ? NOT_INSTANCE_EQUALS
          : INSTANCE_EQUALS);
    }

    @Override
    public boolean evaluate(
        InternalWorkingMemory workingMemory,
        InternalReadAccessor extractor, Object object,
        FieldValue value) {
      final Object objectValue = extractor.getValue(
          workingMemory, object);
      return this.getOperator().isNegated()
          ^ (objectValue == value.getValue());
    }

    @Override
    public boolean evaluate(
        InternalWorkingMemory workingMemory,
        InternalReadAccessor leftExtractor, Object left,
        InternalReadAccessor rightExtractor, Object right) {
      final Object value1 = leftExtractor.getValue(
          workingMemory, left);
      final Object value2 = rightExtractor.getValue(
          workingMemory, right);
      return this.getOperator().isNegated()
          ^ (value1 == value2);
    }

    @Override
    public boolean evaluateCachedLeft(
        InternalWorkingMemory workingMemory,
        VariableContextEntry context, Object right) {
      return this.getOperator().isNegated()
          ^ (right == ((ObjectVariableContextEntry) 
          context).left);
    }

    @Override
    public boolean evaluateCachedRight(
        InternalWorkingMemory workingMemory,
        VariableContextEntry context, Object left) {
      return this.getOperator().isNegated()
          ^ (left == ((ObjectVariableContextEntry) 
          context).right);
    }

    @Override
    public String toString() {
      return "InstanceEquals instanceEquals";
    }
  }
  // @extract-end

}

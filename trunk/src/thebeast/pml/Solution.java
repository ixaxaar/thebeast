package thebeast.pml;

import thebeast.nod.expression.RelationExpression;
import thebeast.nod.expression.Summarize;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.type.Attribute;
import thebeast.pml.formula.FactorFormula;
import thebeast.pml.formula.QueryGenerator;
import thebeast.util.Profiler;
import thebeast.util.NullProfiler;

import java.util.HashMap;

/**
 * A solution combines a ground atom set (reflecting the hidden ground atoms) and
 * a ground formula set (containing all violated/true/both ground formulas within the
 * hidden ground atoms).
 *
 * @author Sebastian Riedel
 */
public class Solution {

  private GroundFormulas groundFormulas;
  private GroundAtoms groundAtoms;
  private Model model;
  private Weights weights;
  private HashMap<FactorFormula, RelationExpression>
          localExtractors = new HashMap<FactorFormula, RelationExpression>();
  private HashMap<FactorFormula, RelationExpression>
          localSummarizer = new HashMap<FactorFormula, RelationExpression>();
  private HashMap<UserPredicate, RelationExpression>
          localCollectors = new HashMap<UserPredicate, RelationExpression>();
  private HashMap<UserPredicate, RelationExpression>
          localSummarizerForFeatures = new HashMap<UserPredicate, RelationExpression>();
  private HashMap<UserPredicate, RelationExpression>
          localJoin = new HashMap<UserPredicate, RelationExpression>();
  private HashMap<FactorFormula, RelationExpression>
          globalFalseSummarizer = new HashMap<FactorFormula, RelationExpression>(),
          globalTrueSummarizer = new HashMap<FactorFormula, RelationExpression>();
  private HashMap<FactorFormula, RelationVariable>
          tmpFeatures = new HashMap<FactorFormula, RelationVariable>();
  private HashMap<UserPredicate, RelationVariable>
          tmpFeaturesPerPred = new HashMap<UserPredicate, RelationVariable>();
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();

  private ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());

  private LocalFeatures localFeatures;

  private boolean groundFormulasNeedUpdate;

  private Profiler profiler = new NullProfiler();

  public Solution(Model model, Weights weights) {
    groundAtoms = model.getSignature().createGroundAtoms();
    groundFormulas = new GroundFormulas(model, weights);
    localFeatures = new LocalFeatures(model, weights);
    this.model = model;
    this.weights = weights;
    QueryGenerator queryGenerator = new QueryGenerator(this.weights, groundAtoms);
    for (FactorFormula factorFormula : model.getLocalFactorFormulas()) {
      localExtractors.put(factorFormula,
              queryGenerator.generateLocalFeatureExtractor(factorFormula, groundAtoms, weights));
      RelationVariable var = interpreter.createRelationVariable(factorFormula.getHeadingIndex());
      tmpFeatures.put(factorFormula, var);
      builder.expr(var).by("index").num(1.0).summarizeAs("value", Summarize.Spec.DOUBLE_SUM).summarize();
      localSummarizer.put(factorFormula, builder.getRelation());
    }
    for (FactorFormula factorFormula : model.getGlobalFactorFormulas()) {
      if (factorFormula.isParametrized()) {
        if (!factorFormula.getWeight().isNonPositive()) {
          builder.expr(groundFormulas.getFalseGroundFormulas(factorFormula));
          builder.by("index").num(-1.0).summarizeAs("value", Summarize.Spec.DOUBLE_SUM).summarize();
          globalFalseSummarizer.put(factorFormula, builder.getRelation());
        }
        if (!factorFormula.getWeight().isNonNegative()) {
          builder.expr(groundFormulas.getTrueGroundFormulas(factorFormula));
          builder.by("index").num(1.0).summarizeAs("value", Summarize.Spec.DOUBLE_SUM).summarize();
          globalTrueSummarizer.put(factorFormula, builder.getRelation());
        }
      }
    }
    for (UserPredicate predicate : model.getHiddenPredicates()) {
      RelationVariable var = interpreter.createRelationVariable(predicate.getHeadingGroupedFeatures());
      tmpFeaturesPerPred.put(predicate, var);
      builder.expr(groundAtoms.getGroundAtomsOf(predicate).getRelationVariable()).from("atoms");
      builder.expr(localFeatures.getGroupedRelation(predicate)).from("features");
      for (Attribute att : predicate.getHeading().attributes()) {
        builder.attribute("atoms", att).attribute("features", att).equality();
      }
      builder.and(predicate.getArity()).where();
      for (Attribute att : predicate.getHeading().attributes()) {
        builder.id(att.name()).attribute("atoms", att);
      }
      builder.id("features").attribute("features", UserPredicate.getFeatureIndicesAttribute());
      builder.tuple(predicate.getArity() + 1).select().query();
      localJoin.put(predicate, builder.getRelation());

//      builder.expr(var).by("index").num(1.0).summarizeAs("value", Summarize.Spec.DOUBLE_SUM).summarize();
//      localSummarizerForFeatures.put(predicate, builder.getRelation());
//      builder.expr(var).by("index");
//      builder.num(1.0).summarizeAs("value", Summarize.Spec.DOUBLE_SUM).summarize();
//      localSummarizerForFeatures.put(predicate, builder.getRelation());

      builder.expr(var).collect("features","index","value");
      localCollectors.put(predicate, builder.getRelation());
    }

  }

  public GroundFormulas getGroundFormulas() {
    if (groundFormulasNeedUpdate) updateGroundFormulas();
    return groundFormulas;
  }

  public GroundAtoms getGroundAtoms() {
    return groundAtoms;
  }

  public void updateGroundFormulas() {
    groundFormulas.extract(groundAtoms);
    groundFormulasNeedUpdate = false;
  }


  public Profiler getProfiler() {
    return profiler;
  }

  public void setProfiler(Profiler profiler) {
    this.profiler = profiler;
  }

  public FeatureVector extract() {
    //extract args + index into tmp vars
    FeatureVector vector = new FeatureVector();
    //SparseVector result = new SparseVector();
    profiler.start("local");
    for (FactorFormula formula : model.getLocalFactorFormulas()) {
      profiler.start("formula");

      profiler.start("extract");
      interpreter.assign(tmpFeatures.get(formula), localExtractors.get(formula));
      profiler.end();

      SparseVector tmp = new SparseVector();

      profiler.start("summarize");
      interpreter.assign(tmp.getValuesRelation(), localSummarizer.get(formula));
      profiler.end();

      profiler.start("add");
      vector.getFree().addInPlace(1.0,tmp);
      profiler.end();

      profiler.end();
    }
    profiler.end();
    profiler.start("global");
    for (FactorFormula formula : model.getGlobalFactorFormulas()) {
      SparseVector tmp = new SparseVector();
      if (formula.isParametrized()) {
        if (!formula.getWeight().isNonNegative())
          interpreter.insert(tmp.getValuesRelation(), globalTrueSummarizer.get(formula));
        if (!formula.getWeight().isNonPositive())
          interpreter.insert(tmp.getValuesRelation(), globalFalseSummarizer.get(formula));
        if (formula.getWeight().isNonNegative())
          vector.getNonnegative().addInPlace(1.0,tmp);
        else if (formula.getWeight().isNonPositive())
          vector.getNonpositive().addInPlace(1.0,tmp);
        else
          vector.getFree().addInPlace(1.0,tmp);
      }
    }
    profiler.end();
    return vector;
  }

  public FeatureVector extract(LocalFeatures features) {
    //SparseVector result = new SparseVector();
    FeatureVector vector = new FeatureVector();
    localFeatures.load(features);
    profiler.start("local");
    for (UserPredicate pred : model.getHiddenPredicates()) {
      profiler.start("predicate");

      //System.out.println(localFeatures.getGroupedRelation(pred).value());
      profiler.start("extract");
      RelationVariable var = tmpFeaturesPerPred.get(pred);
      interpreter.assign(var, localJoin.get(pred));
      profiler.end();

      //System.out.println(tmpFeaturesPerPred.get(pred).value());

//
//      SparseVector tmp = new SparseVector();
//      profiler.start("summarize");
//      interpreter.assign(tmp.getValuesRelation(), localSummarizerForFeatures.get(pred));
//      profiler.end();

      SparseVector tmp = new SparseVector();
      profiler.start("collect");
      interpreter.assign(tmp.getValuesRelation(), localCollectors.get(pred));
      profiler.end();

      profiler.start("add");
      vector.getFree().addInPlace(1.0, tmp);
      profiler.end();

      profiler.end();
    }
    profiler.end();
    profiler.start("global");
    for (FactorFormula formula : model.getGlobalFactorFormulas()) {
      SparseVector tmp = new SparseVector();
      if (formula.isParametrized()) {
        if (!formula.getWeight().isNonNegative())
          interpreter.insert(tmp.getValuesRelation(), globalTrueSummarizer.get(formula));
        if (!formula.getWeight().isNonPositive())
          interpreter.insert(tmp.getValuesRelation(), globalFalseSummarizer.get(formula));
        if (formula.getWeight().isNonNegative())
          vector.getNonnegative().addInPlace(1.0,tmp);
        else if (formula.getWeight().isNonPositive())
          vector.getNonpositive().addInPlace(1.0,tmp);
        else
          vector.getFree().addInPlace(1.0,tmp);
        //result.addInPlace(1.0, tmp);
      }
    }
    profiler.end();
    return vector;
  }

  public void load(GroundAtoms groundAtoms, GroundFormulas groundFormulas) {
    this.groundAtoms.load(groundAtoms);
    this.groundFormulas.load(groundFormulas);

  }

  public void load(GroundAtoms groundAtoms) {
    this.groundAtoms.load(groundAtoms);
    updateGroundFormulas();
    //this.groundFormulas.load(groundFormulas);
  }


  public void load(Solution solution) {
    this.groundAtoms.load(solution.groundAtoms);
    groundFormulasNeedUpdate = true;
  }

  public Solution copy() {
    Solution result = new Solution(model, weights);
    result.load(this);
    return result;
  }

}
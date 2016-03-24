
/* First created by JCasGen Thu Mar 24 15:20:37 CET 2016 */
package de.tudarmstadt.ukp.dkpro.core.api.semantics.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Thu Mar 24 15:20:37 CET 2016
 * @generated */
public class SemanticPredicate_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (SemanticPredicate_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = SemanticPredicate_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new SemanticPredicate(addr, SemanticPredicate_Type.this);
  			   SemanticPredicate_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new SemanticPredicate(addr, SemanticPredicate_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = SemanticPredicate.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.tudarmstadt.ukp.dkpro.core.api.semantics.type.SemanticPredicate");
 
  /** @generated */
  final Feature casFeat_category;
  /** @generated */
  final int     casFeatCode_category;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getCategory(int addr) {
        if (featOkTst && casFeat_category == null)
      jcas.throwFeatMissing("category", "de.tudarmstadt.ukp.dkpro.core.api.semantics.type.SemanticPredicate");
    return ll_cas.ll_getStringValue(addr, casFeatCode_category);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCategory(int addr, String v) {
        if (featOkTst && casFeat_category == null)
      jcas.throwFeatMissing("category", "de.tudarmstadt.ukp.dkpro.core.api.semantics.type.SemanticPredicate");
    ll_cas.ll_setStringValue(addr, casFeatCode_category, v);}
    
  
 
  /** @generated */
  final Feature casFeat_arguments;
  /** @generated */
  final int     casFeatCode_arguments;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getArguments(int addr) {
        if (featOkTst && casFeat_arguments == null)
      jcas.throwFeatMissing("arguments", "de.tudarmstadt.ukp.dkpro.core.api.semantics.type.SemanticPredicate");
    return ll_cas.ll_getRefValue(addr, casFeatCode_arguments);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setArguments(int addr, int v) {
        if (featOkTst && casFeat_arguments == null)
      jcas.throwFeatMissing("arguments", "de.tudarmstadt.ukp.dkpro.core.api.semantics.type.SemanticPredicate");
    ll_cas.ll_setRefValue(addr, casFeatCode_arguments, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public int getArguments(int addr, int i) {
        if (featOkTst && casFeat_arguments == null)
      jcas.throwFeatMissing("arguments", "de.tudarmstadt.ukp.dkpro.core.api.semantics.type.SemanticPredicate");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_arguments), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_arguments), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_arguments), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setArguments(int addr, int i, int v) {
        if (featOkTst && casFeat_arguments == null)
      jcas.throwFeatMissing("arguments", "de.tudarmstadt.ukp.dkpro.core.api.semantics.type.SemanticPredicate");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_arguments), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_arguments), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_arguments), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public SemanticPredicate_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_category = jcas.getRequiredFeatureDE(casType, "category", "uima.cas.String", featOkTst);
    casFeatCode_category  = (null == casFeat_category) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_category).getCode();

 
    casFeat_arguments = jcas.getRequiredFeatureDE(casType, "arguments", "uima.cas.FSArray", featOkTst);
    casFeatCode_arguments  = (null == casFeat_arguments) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_arguments).getCode();

  }
}



    
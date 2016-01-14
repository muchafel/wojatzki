
/* First created by JCasGen Thu Jan 07 14:15:53 CET 2016 */
package types;

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
 * Updated by JCasGen Thu Jan 14 11:10:51 CET 2016
 * @generated */
public class ClassifiedConceptOutcome_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (ClassifiedConceptOutcome_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ClassifiedConceptOutcome_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ClassifiedConceptOutcome(addr, ClassifiedConceptOutcome_Type.this);
  			   ClassifiedConceptOutcome_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ClassifiedConceptOutcome(addr, ClassifiedConceptOutcome_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = ClassifiedConceptOutcome.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("types.ClassifiedConceptOutcome");
 
  /** @generated */
  final Feature casFeat_conceptName;
  /** @generated */
  final int     casFeatCode_conceptName;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getConceptName(int addr) {
        if (featOkTst && casFeat_conceptName == null)
      jcas.throwFeatMissing("conceptName", "types.ClassifiedConceptOutcome");
    return ll_cas.ll_getStringValue(addr, casFeatCode_conceptName);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setConceptName(int addr, String v) {
        if (featOkTst && casFeat_conceptName == null)
      jcas.throwFeatMissing("conceptName", "types.ClassifiedConceptOutcome");
    ll_cas.ll_setStringValue(addr, casFeatCode_conceptName, v);}
    
  
 
  /** @generated */
  final Feature casFeat_classificationOutcome;
  /** @generated */
  final int     casFeatCode_classificationOutcome;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getClassificationOutcome(int addr) {
        if (featOkTst && casFeat_classificationOutcome == null)
      jcas.throwFeatMissing("classificationOutcome", "types.ClassifiedConceptOutcome");
    return ll_cas.ll_getStringValue(addr, casFeatCode_classificationOutcome);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setClassificationOutcome(int addr, String v) {
        if (featOkTst && casFeat_classificationOutcome == null)
      jcas.throwFeatMissing("classificationOutcome", "types.ClassifiedConceptOutcome");
    ll_cas.ll_setStringValue(addr, casFeatCode_classificationOutcome, v);}
    
  
 
  /** @generated */
  final Feature casFeat_biPolar;
  /** @generated */
  final int     casFeatCode_biPolar;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getBiPolar(int addr) {
        if (featOkTst && casFeat_biPolar == null)
      jcas.throwFeatMissing("biPolar", "types.ClassifiedConceptOutcome");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_biPolar);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setBiPolar(int addr, boolean v) {
        if (featOkTst && casFeat_biPolar == null)
      jcas.throwFeatMissing("biPolar", "types.ClassifiedConceptOutcome");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_biPolar, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public ClassifiedConceptOutcome_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_conceptName = jcas.getRequiredFeatureDE(casType, "conceptName", "uima.cas.String", featOkTst);
    casFeatCode_conceptName  = (null == casFeat_conceptName) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_conceptName).getCode();

 
    casFeat_classificationOutcome = jcas.getRequiredFeatureDE(casType, "classificationOutcome", "uima.cas.String", featOkTst);
    casFeatCode_classificationOutcome  = (null == casFeat_classificationOutcome) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_classificationOutcome).getCode();

 
    casFeat_biPolar = jcas.getRequiredFeatureDE(casType, "biPolar", "uima.cas.Boolean", featOkTst);
    casFeatCode_biPolar  = (null == casFeat_biPolar) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_biPolar).getCode();

  }
}



    
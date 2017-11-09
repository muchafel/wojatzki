
/* First created by JCasGen Thu Nov 09 15:06:29 CET 2017 */
package assertionRegression.annotationTypes;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Thu Nov 09 15:06:29 CET 2017
 * @generated */
public class Issue_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Issue.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("assertionRegression.annotationTypes.Issue");
 
  /** @generated */
  final Feature casFeat_Issue;
  /** @generated */
  final int     casFeatCode_Issue;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getIssue(int addr) {
        if (featOkTst && casFeat_Issue == null)
      jcas.throwFeatMissing("Issue", "assertionRegression.annotationTypes.Issue");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Issue);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIssue(int addr, String v) {
        if (featOkTst && casFeat_Issue == null)
      jcas.throwFeatMissing("Issue", "assertionRegression.annotationTypes.Issue");
    ll_cas.ll_setStringValue(addr, casFeatCode_Issue, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Issue_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Issue = jcas.getRequiredFeatureDE(casType, "Issue", "uima.cas.String", featOkTst);
    casFeatCode_Issue  = (null == casFeat_Issue) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Issue).getCode();

  }
}



    
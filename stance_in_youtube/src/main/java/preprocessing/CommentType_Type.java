
/* First created by JCasGen Thu Nov 10 11:34:17 CET 2016 */
package preprocessing;

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
 * Updated by JCasGen Thu Nov 10 11:34:17 CET 2016
 * @generated */
public class CommentType_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (CommentType_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = CommentType_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new CommentType(addr, CommentType_Type.this);
  			   CommentType_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new CommentType(addr, CommentType_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = CommentType.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("preprocessing.CommentType");
 
  /** @generated */
  final Feature casFeat_commentNotReply;
  /** @generated */
  final int     casFeatCode_commentNotReply;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getCommentNotReply(int addr) {
        if (featOkTst && casFeat_commentNotReply == null)
      jcas.throwFeatMissing("commentNotReply", "preprocessing.CommentType");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_commentNotReply);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCommentNotReply(int addr, boolean v) {
        if (featOkTst && casFeat_commentNotReply == null)
      jcas.throwFeatMissing("commentNotReply", "preprocessing.CommentType");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_commentNotReply, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public CommentType_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_commentNotReply = jcas.getRequiredFeatureDE(casType, "commentNotReply", "uima.cas.Boolean", featOkTst);
    casFeatCode_commentNotReply  = (null == casFeat_commentNotReply) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_commentNotReply).getCode();

  }
}



    
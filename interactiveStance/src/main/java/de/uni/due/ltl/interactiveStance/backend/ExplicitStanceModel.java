package de.uni.due.ltl.interactiveStance.backend;

import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.util.Date;


public class ExplicitStanceModel implements Serializable, Cloneable {

    private Long id;

    private String targetName;
    private int instancesInFavor;
	private int instancesAgainst;
    private ILearntModel model;

    /**
     * default constructor
     */
    public ExplicitStanceModel() {
	}
    
	public ExplicitStanceModel(Long id, String targetName, int instancesInFavor, int instancesAgainst,
			ILearntModel model) {
		this.id = id;
		this.targetName = targetName;
		this.instancesInFavor = instancesInFavor;
		this.instancesAgainst = instancesAgainst;
		this.model = model;
	}
    
    
    public Long getId() {
        return id;
    }

    public String getTargetName() {
        return targetName;
    }

    public int getInstancesInFavor() {
		return instancesInFavor;
	}

	public void setInstancesInFavor(int instancesInFavor) {
		this.instancesInFavor = instancesInFavor;
	}

	public int getInstancesAgainst() {
		return instancesAgainst;
	}

	public ILearntModel getModel() {
		return model;
	}

    

    @Override
    public ExplicitStanceModel clone() throws CloneNotSupportedException {
        try {
            return (ExplicitStanceModel) BeanUtils.cloneBean(this);
        } catch (Exception ex) {
            throw new CloneNotSupportedException();
        }
    }
}

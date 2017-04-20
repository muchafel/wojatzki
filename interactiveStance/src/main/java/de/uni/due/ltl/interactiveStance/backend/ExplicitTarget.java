package de.uni.due.ltl.interactiveStance.backend;

import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.util.Date;


public class ExplicitTarget implements Serializable, Cloneable {

    private String id;

    private String targetName;
    private int instancesInFavor;
	private int instancesAgainst;

    /**
     * default constructor
     */
    public ExplicitTarget() {
	}
    
	public ExplicitTarget(String id, String targetName, int instancesInFavor, int instancesAgainst) {
		this.id = id;
		this.targetName = targetName;
		this.instancesInFavor = instancesInFavor;
		this.instancesAgainst = instancesAgainst;
	}
    
    
    public String getId() {
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

    

    @Override
    public ExplicitTarget clone() throws CloneNotSupportedException {
        try {
            return (ExplicitTarget) BeanUtils.cloneBean(this);
        } catch (Exception ex) {
            throw new CloneNotSupportedException();
        }
    }
}

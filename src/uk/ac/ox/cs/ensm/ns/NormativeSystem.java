package uk.ac.ox.cs.ensm.ns;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.ac.ox.cs.ensm.network.NGNNode;
import uk.ac.ox.cs.ensm.network.NetworkNode;
import uk.ac.ox.cs.ensm.norm.Norm;

/**
 * A normative system is a set of norms, implemented by means
 * of an {@code ArrayList} of norms
 * 
 * @author Javier Morales (jmorales@iiia.csic.es)
 */
public class NormativeSystem extends ArrayList<Norm>
implements NetworkNode, NGNNode, Comparable<NormativeSystem> {

	//---------------------------------------------------------------------------
  // Static attributes 
  //---------------------------------------------------------------------------
	
	private static final long serialVersionUID = 8245680654641852211L; // Id
  
	//---------------------------------------------------------------------------
  // Attributes 
  //---------------------------------------------------------------------------
	
	private long id;
	private long numFollowers;
	private BigDecimal frequency;
	
  //---------------------------------------------------------------------------
  // Methods 
  //---------------------------------------------------------------------------
	
	/**
	 * 
	 */
	public NormativeSystem() {}
	
	
	public NormativeSystem(NormativeSystem ns) {
		for(Norm norm : ns) {
			this.add(norm);
		}
	}
	
	/**
	 * 
	 * @param id
	 */
	public NormativeSystem(int id) {
		this.id = id;
		this.setNumFollowers(0);
		this.frequency = BigDecimal.ZERO;
	}
	
	/**
	 * Adds a norm to the set
	 * 
	 * @param norm the norm to add
	 */
	@Override
	public boolean add(Norm norm) {
		if(!this.contains(norm)) {
			return super.add(norm);
		}
		return false;
	}
	
	/**
	 * Adds a {@code List} of norms to the set
	 * 
	 * @param norms the list of norms
	 */
	public void addAll(List<Norm> norms) {
		for(Norm norm : norms) {
			if(norm == null) {
				continue;
			}
			if(!this.contains(norm)) {
				this.add(norm);
			}
		}
	}

	/**
	 * Returns <tt>true</tt> if the norm set contains the given norm.
	 * It performs the search by comparing norm id's.
	 * 
	 * @param norm the norm to search
	 * @return <tt>true</tt> if the norm set contains the given norm
	 */
	public boolean contains(Norm norm) {
		for(Norm n : this) {
			if(n.equals(norm))
				return true;
		}
		return false;
	}

	/**
	 * Returns the norm with the given {@code id}, if the normative system
	 * contains the norm. In case the normative system does not contain the 
	 * norm, it returns {@code null} 
	 * 
	 * @param id the id of the norm
	 * @return the norm with the given {@code id}, if the normative system
	 * 					contains the norm. In case the normative system does not
	 * 					contain the norm, it returns {@code null} 
	 */
	public Norm getNormWithId(long id) {
		Norm norm = null;
		for(Norm n : this) {
			if(n.getId() == id) {
				norm = n;
				break;
			}
		}
		return norm;
	}
	
	/**
	 * 
	 * @param otherNS
	 * @return
	 */
	public boolean isSubsetOf(NormativeSystem otherNS) {
		
		/* Do all the norms in this NS exist in the other NS? */
		for(Norm norm : this) {
			if(!otherNS.contains(norm)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns the id of the normative system
	 * 
	 * @return the id of the normative system
	 * @return
	 */
	public long getId() {
		return this.id;
	}
	
	/**
	 * 
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ox.cs.ensm.ns.network.NetworkNode#getDescription()
	 */
	@Override
	public String getDescription() {
		String s = "";
		Collections.sort(this);
		
		for(Norm norm : this) {
			s += norm.getDescription() + "\n";
		}
		return s;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ox.cs.ensm.ns.network.NetworkNode#getName()
	 */
	@Override
	public String getName() {
		return "NS" + this.id;
	}

	/**
	 * Compares the id of this norm to that of {@code otherNorm}
	 * Used to sort norms by id
	 * 
	 * @param otherNorm the other norm to compare this with
	 */
	@Override
	public int compareTo(NormativeSystem otherNS)	{
		if(otherNS.getId() < this.id) {
			return 1;
		}
		else if(otherNS.getId() > this.id) {
			return -1;
		}
		return 0;
	}
	
	/**
	 * @return the numFollowers
	 */
	public long getNumFollowers() {
		return numFollowers;
	}

	/**
	 * @param numFollowers the numFollowers to set
	 */
	public void setNumFollowers(long numFollowers) {
		this.numFollowers = numFollowers;
	}
	
	/**
	 * 
	 */
	public void incNumFollowers() {
		this.numFollowers++;
	}
	
	/**
	 * 
	 */
	public void resetNumFollowers() {
		this.setNumFollowers(0);
	}
	
	/**
	 * @return the frequency
	 */
	public BigDecimal getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(BigDecimal frequency) {
		this.frequency = frequency;
	}
	
	/**
	 * 
	 */
	public String toString() {
		return this.getName() + ": " + super.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getContentDescription() {
		return super.toString();
	}
	
	/**
	 * 
	 */
	@Override
	public boolean equals(NGNNode oNode) {
		if(!(oNode instanceof NormativeSystem)){
			return false;
		}
		
		NormativeSystem oNS = (NormativeSystem) oNode;
		
		for(Norm norm : this) {
			if(!oNS.contains(norm)) {
				return false;
			}
		}
		
		for(Norm norm : oNS) {
			if(!this.contains(norm)) {
				return false;
			}
		}
		return true;
	}
}

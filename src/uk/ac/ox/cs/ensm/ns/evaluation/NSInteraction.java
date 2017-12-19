/**
 * 
 * @author Javier Morales (javier.morales@cs.ox.ac.uk)
 */
package uk.ac.ox.cs.ensm.ns.evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.ac.ox.cs.ensm.ns.NormativeSystem;

/**
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 */
public class NSInteraction {

	private NormativeSystem ns1, ns2;
	private String name;
	
	private long currentNumInteractions;
	private long currentNumConflicts;
	private long totalNumInteractions;
	private long totalNumConflicts;
	
	/**
	 * 
	 */
	public NSInteraction(NormativeSystem ns1, NormativeSystem ns2) {
		this.ns1 = ns1;
		this.ns2 = ns2;
		
		this.currentNumInteractions = 0;
		this.currentNumConflicts = 0;
		this.totalNumInteractions = 0;
		this.totalNumConflicts = 0;
		
		this.name = generateName(ns1, ns2);
	}

	/**
	 * @return the nsa
	 */
	public NormativeSystem getNS1() {
		return ns1;
	}

	/**
	 * @return the nsb
	 */
	public NormativeSystem getNS2() {
		return ns2;
	}

	/**
	 * @return the currentNumInteractions
	 */
	public long getCurrentNumInteractions() {
		return currentNumInteractions;
	}

	/**
	 * @param currentNumInteractions the currentNumInteractions to set
	 */
	public void setCurrentNumInteractions(long currentNumInteractions) {
		this.currentNumInteractions = currentNumInteractions;
	}

	/**
	 * @return the currentNumConflicts
	 */
	public long getCurrentNumConflicts() {
		return currentNumConflicts;
	}

	/**
	 * @param currentNumConflicts the currentNumConflicts to set
	 */
	public void setCurrentNumConflicts(long currentNumConflicts) {
		this.currentNumConflicts = currentNumConflicts;
	}

	/**
	 * @return the totalNumInteractions
	 */
	public long getTotalNumInteractions() {
		return totalNumInteractions;
	}

	/**
	 * @param totalNumInteractions the totalNumInteractions to set
	 */
	public void setTotalNumInteractions(long totalNumInteractions) {
		this.totalNumInteractions = totalNumInteractions;
	}

	/**
	 * @return the totalNumConflicts
	 */
	public long getTotalNumConflicts() {
		return totalNumConflicts;
	}

	/**
	 * @param totalNumConflicts the totalNumConflicts to set
	 */
	public void setTotalNumConflicts(long totalNumConflicts) {
		this.totalNumConflicts = totalNumConflicts;
	}

	/**
	 * 
	 */
	public void resetCurrentInteractions() {
		this.setCurrentNumInteractions(0);
		this.setCurrentNumConflicts(0);
	}

	/**
	 * 
	 */
	public void addInteraction() {
		this.currentNumInteractions++;
		this.totalNumInteractions++;
	}
	
	/**
	 * 
	 */
	public void addConflicts(long numConflicts) {
		this.currentNumConflicts += numConflicts;
		this.totalNumConflicts += numConflicts;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 */
	public static String generateName(NormativeSystem ns1, NormativeSystem ns2) {
		List<NormativeSystem> nss = new ArrayList<NormativeSystem>();
		nss.add(ns1);
		nss.add(ns2);
		Collections.sort(nss);
		
		String name = nss.get(0).getName() + "-" + nss.get(1).getName();
		return name;
	}

}

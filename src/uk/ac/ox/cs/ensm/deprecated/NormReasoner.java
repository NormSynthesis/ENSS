package uk.ac.ox.cs.ensm.deprecated;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.ox.cs.ensm.agent.AgentAction;
import uk.ac.ox.cs.ensm.agent.language.PredicatesDomains;
import uk.ac.ox.cs.ensm.config.DomainFunctions;
import uk.ac.ox.cs.ensm.config.Goal;
import uk.ac.ox.cs.ensm.metrics.EvolutionaryNSMetrics;
import uk.ac.ox.cs.ensm.norm.applicability.NormComplianceOutcomes;
import uk.ac.ox.cs.ensm.norm.applicability.NormsApplicableInView;
import uk.ac.ox.cs.ensm.norm.reasoning.Constraint;
import uk.ac.ox.cs.ensm.norm.reasoning.NormEngine;
import uk.ac.ox.cs.ensm.perception.View;
import uk.ac.ox.cs.ensm.perception.ViewTransition;


/**
 * The norm reasoner employs the {@code NormEngine} (by extending it) to
 * reason about norms applicability and compliance. It includes methods to
 * compute which norms apply to the agents in a {@code View}, and to
 * assess if the agents have complied or infringed norms in a transition of 
 * views (a {@code ViewTransition})
 * 
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 * @see NormEngine
 * @see View
 * @see ViewTransition
 */
public class NormReasoner extends NormEngine {

	//---------------------------------------------------------------------------
	// Attributes
	//---------------------------------------------------------------------------

//	private PredicatesDomains predDomains;	// Predicates and their domains
	private DomainFunctions dmFunctions;		// Domain functions 
	
	//---------------------------------------------------------------------------
	// Methods
	//---------------------------------------------------------------------------

	/**
	 * Constructor
	 * 
	 * @param dmFunctions the domain functions
	 */
	public NormReasoner(List<Goal> goals, PredicatesDomains predDomains,
			DomainFunctions dmFunctions, EvolutionaryNSMetrics nsMetrics) {
		
		super(predDomains);
		
		this.predDomains = predDomains;
		this.dmFunctions = dmFunctions;
	}

//	/**
//	 * Returns a {@code List} of the norms that apply to the
//	 * agents in a given {@code viewTransition}
//	 * 
//	 *  @param viewTransition the transition of views
//	 *  @return 	a {@code List} of the norms that apply to the
//	 * 						agents in a given {@code viewTransition}
//	 */
//	public NormsApplicableInView getNormsApplicable(
//			ViewTransition viewTransition) {
//
//		List<Long> agentIds = new ArrayList<Long>();
//		View pView = viewTransition.getView(-1);
//		View view = viewTransition.getView(0);
//
//		NormsApplicableInView nAppl = new NormsApplicableInView();
//		nAppl.setViewTransition(viewTransition);
//
//		/* Just check norm applicability for those agents that
//		 * exist in all views of the stream */
//		List<Long> pViewAgentIds = pView.getAgentIds();
//		List<Long> viewAgentIds = view.getAgentIds();
//		for(Long agentId : pViewAgentIds)	{
//			if(viewAgentIds.contains(agentId)) {
//				agentIds.add(agentId);
//			}
//		}
//
//		/* Add norm applicability for each agent 
//		 * (View Agent Applicable norms) */
//		for(Long agentId : agentIds) {
//			
//			AgentContext aContext = this.dmFunctions.
//					agentContext(agentId, pView);
//			
//			if(aContext == null) {
//				continue;
//			}
//			NormsApplicableToAgentContext nAppToPred =
//					this.getNormsApplicable(
//							aContext.getDescription());
//
//			if(nAppToPred.getApplicableNorms().size() > 0) {
//				nAppl.add(agentId, nAppToPred);
//			}
//		}
//		return nAppl;
//	}

//	/**
//	 * Returns a list of norms that are applicable to a given agent context
//	 * 
//	 * @param agContext an agent context 
//	 * @return a list of norms that are applicable to a given agent context
//	 */
//	public NormsApplicableToAgentContext getNormsApplicable(
//			SetOfPredicatesWithTerms context) {
//
//		/* Add facts to the rule engine and reason about norms */
//		this.reset();
//		this.addFacts(context);
//		this.reason();
//		
//		/* Generate object to return */
//		NormsApplicableToAgentContext nAppToPred =
//				new NormsApplicableToAgentContext(context, this.applicableConstraints);
//		
//		return nAppToPred;
//	}
	
	/**
	 * Employs {@code normApplicability} to compute and return 
	 * an object {@code NormComplianceOutcomes} that contains information
	 * about the norms that agents complied with or infringed in a given
	 * {@code ViewTransition} which is contained in the {@code normApplicability}  
	 * 
	 * @param 	normApplicability an object containing a {@code ViewTransition}
	 * 					a list of agents to which some norms apply in the given
	 * 					transition of views
	 * @param goal the goal 
	 * @return an object {@code NormComplianceOutcomes} that contains information
	 * 					about the norms that agents complied with or infringed in a given
	 * 					{@code ViewTransition} which is contained in the 
	 * 					{@code normApplicability}  
	 * @see NormsApplicableInView
	 * @see NormComplianceOutcomes
	 */
	public NormComplianceOutcomes checkNormComplianceAndOutcomes(
			NormsApplicableInView normApplicability, Goal goal) {

		NormComplianceOutcomes gNormCompliance = new NormComplianceOutcomes();
//		ViewTransition trans = normApplicability.getViewTransition();
//		List<Long> agentIds = normApplicability.getAgentIds();
//
//		/* Check norm compliance and conflict of each agent in the view */
//		for(Long agentId : agentIds) {
//			List<Conflict> conflicts = this.dmFunctions.getConflicts(goal, trans, agentId);
//			NormsApplicableToAgentContext agentApplNorms = normApplicability.get(agentId);			 
//			
//			for(Norm norm : agentApplNorms.getApplicableNorms()) {
//				boolean isFulfilment = this.hasFulfilledNorm(agentId, norm, trans);
//				int numConflicts = conflicts.size();
//				
//				SetOfPredicatesWithTerms agContext = agentApplNorms.getAgentContext();
//			
//				/* Divide applicable norms between complied/infringed norms
//				 * with/without conflict*/		
//				if(isFulfilment) {
//					int numFulfilments = this.getNumFulfilments(agentId, norm, trans);
//					int numFulfilmentsWithConflict = numConflicts;
//					int numFulfilmentsWithNoConflict = numFulfilments - numConflicts;
//  				
//					gNormCompliance. addFulfilmentsWithConflict(agContext, norm, 
//  						numFulfilmentsWithConflict);
//  				gNormCompliance.addFulfilmentsWithNoConflict(agContext, norm,
//  						numFulfilmentsWithNoConflict);
//				}
//				else {
//					int numInfringements = this.getNumInfringements(agentId, norm, trans);
//					int numInfringementsWithConflict = numConflicts;
//					int numInfringementsWithNoConflict = numInfringements - numConflicts;
//					
//  				gNormCompliance.addInfringementsWithConflict(agContext, norm,
//  						numInfringementsWithConflict);
//  				gNormCompliance.addInfringementsWithNoConflict(agContext, norm,
//  						numInfringementsWithNoConflict);
//				}
//			}
//		}
		return gNormCompliance;
	}

	/**
	 * Returns the number of fulfilments of a norm that an agent
	 * performed in a view transition 
	 * 
	 * @param agId an agent id
	 * @param norm a norm
	 * @param vTrans a transition between two views
	 * @return the number of fulfilments of a norm that an agent
	 * 				 performed in a view transition
	 */
	public int getNumFulfilments(long agId, Constraint norm, ViewTransition vTrans) {
		int numFulfilments = 0;
		
		List<AgentAction> actions = 
				this.dmFunctions.getAction(agId, vTrans);
		
		for(AgentAction action : actions) {
			if(this.hasFulfilledNorm(agId, norm, action, vTrans)) {
				numFulfilments++;
			}
		}
		return numFulfilments;
	}
	
	/**
	 * Returns the number of infringements of a norm that an agent
	 * performed in a view transition 
	 * 
	 * @param agId an agent id
	 * @param norm a norm
	 * @param vTrans a transition between two views
	 * @return the number of infringements of a norm that an agent
	 * 				 performed in a view transition
	 */
	public int getNumInfringements(long agId, Constraint norm, ViewTransition vTrans) {
		int numInfringements = 0;
		
		List<AgentAction> actions = 
				this.dmFunctions.getAction(agId, vTrans);
		
		for(AgentAction action : actions) {
			if(!this.hasFulfilledNorm(agId, norm, action, vTrans)) {
				numInfringements++;
			}
		}
		return numInfringements;
	}
	
	/**
	 * Returns <tt>true<tt> if the agent with id {@code agId} has fulfilled
	 * the given {@code norm} in the transition of views {@code vTrans}.
	 * Otherwise, it returns <tt>false<tt>
	 * @param agId the id of the agent
	 * @param norm the norm 
	 * @param vTrans the transition of views
	 * 
	 * @return <tt>true<tt> if the agent with id {@code agId} has fulfilled
	 * 					the given {@code norm} in the transition of views
	 * 					{@code vTrans}
	 */
	public boolean hasFulfilledNorm(long agId, Constraint norm, ViewTransition vTrans) {

		/* Retrieve the actions that the agent performed 
		 * during the transition of states */
		List<AgentAction> agActions = 
				this.dmFunctions.getAction(agId, vTrans);

		for(AgentAction agAction : agActions) {

			/* If the norm prohibits to perform the action, and the agent did not
			 * performed the given action, then it fulfilled the norm */
			if(this.hasFulfilledNorm(agId, norm, agAction, vTrans)) {
				return true;
			}

			/* If the norm obligates to perform the action, and the agent 
			 * performed the given action, then it infringed the norm */
			else return false;
		}
		return false;
	}

	/**
	 * Returns <tt>true<tt> if the agent with id {@code agId} has fulfilled
	 * the given {@code norm} in the transition of views {@code vTrans}.
	 * Otherwise, it returns <tt>false<tt>
	 * @param agId the id of the agent
	 * @param norm the norm 
	 * @param vTrans the transition of views
	 * 
	 * @return <tt>true<tt> if the agent with id {@code agId} has fulfilled
	 * 					the given {@code norm} in the transition of views
	 * 					{@code vTrans}
	 */
	public boolean hasFulfilledNorm(long agId, Constraint norm,
			AgentAction agAction, ViewTransition vTrans) 
	{
		AgentAction normAction = norm.getAction();
	
//		/* If the norm prohibits to perform the action, and the agent did not
//		 * performed the given action, then it fulfilled the norm */
//		if(modality == NormModality.Prohibition &&
//				agAction != normAction) {
//			return true;
//		}

		/* If the norm obligates to perform the action, and the agent 
		 * performed the given action, then it infringed the norm */
//		else if(modality == NormModality.Obligation && 
//				agAction == normAction) {
		if(agAction == normAction) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns <tt>true</tt> if norm {@code nA} satisfies norm {@code nB},
	 * namely their postconditions are equal, and the precondition of 
	 * {@code nA} satisfies the precondition of {@code nB}
	 * 
	 * @param nA the norm to satisfy nB
	 * @param nB the norm to be satisfied by nA
	 * @return <tt>true</tt> if norm {@code nA} satisfies norm {@code nB},
	 * 					namely their postconditions are equal, and the precondition of 
	 * 					{@code nA} satisfies the precondition of {@code nB}
	 */
	public boolean satisfies(Constraint nA, Constraint nB) {
//		SetOfPredicatesWithTerms nAPrecond = nA.getPrecondition();
//		SetOfPredicatesWithTerms nBPrecond = nB.getPrecondition();

//		NormModality nAModality = nA.getModality();
//		NormModality nBModality = nB.getModality();
//		AgentAction nAAction = nA.getAction();
//		AgentAction nBAction = nB.getAction();
//
//		/* Check that post-conditions are the same*/
//		if(nAModality != nBModality || nAAction != nBAction) {
//			return false;
//		}
//		for(String predicate : nAPrecond.getPredicates()) {
//			String termA = nAPrecond.getTerms(predicate).get(0);
//			String termB = nBPrecond.getTerms(predicate).get(0);
//
//			if(termA.equals(termB)) {
//				continue;
//			}
//			SetOfStrings nAParentTerms = this.predDomains.
//					getParentTerms(predicate, termA);
//
//			if(!nAParentTerms.contains(termB)) {
				return false;
//			}
//		}
//		return true;
	}
	
	/**
	 * 
	 * @param vTrans
	 * @return
	 */
	public Set<Long> getAgentsInView(ViewTransition vTrans) {
		Set<Long> agentIds = new HashSet<Long>();
		
		View pView = vTrans.getView(-1);
		View view = vTrans.getView(0);

		/* Just check norm applicability for those agents that
		 * exist in all views of the stream */
		List<Long> pViewAgentIds = pView.getAgentIds();
		List<Long> viewAgentIds = view.getAgentIds();
		for(Long agentId : pViewAgentIds)	{
			if(viewAgentIds.contains(agentId)) {
				agentIds.add(agentId);	
			}
		}
		return agentIds;
	}
	
	/**
	 * 
	 * @param norms
	 * @return
	 */
	public List<Constraint> getSatisfiedNorms(Constraint norm, List<Constraint> norms) {
		return this.getNormsSatisfied(norm, norms, true);
	}
	
	/**
	 * 
	 * @param norms
	 * @return
	 */
	public List<Constraint> getNotSatisfiedNorms(Constraint norm, List<Constraint> norms) {
		return this.getNormsSatisfied(norm, norms, false);
	}
	
	/**
	 * 
	 * @param norms
	 * @return
	 */
	public List<Constraint> getNormsSatisfying(List<Constraint> norms, Constraint norm) {
		return this.getNormsThatSatisfy(norms, norm, true);
	}
	
	/**
	 * 
	 * @param norms
	 * @return
	 */
	public List<Constraint> getNormsNotSatisfying(List<Constraint> norms, Constraint norm) {
		return this.getNormsThatSatisfy(norms, norm, false);
	}
	
	/**
	 * 
	 * @param norms
	 * @param satisfaction
	 * @return
	 */
	private List<Constraint> getNormsSatisfied(Constraint nA, List<Constraint> norms, boolean satisfaction) {
		List<Constraint> ret = new ArrayList<Constraint>();
		for(Constraint nB : norms) {
			if(!nA.equals(nB) && this.satisfies(nA, nB) == satisfaction) {
				ret.add(nB);
			}
		}
		return ret;
	}
	
	/**
	 * 
	 * @param norms
	 * @param satisfaction
	 * @return
	 */
	private List<Constraint> getNormsThatSatisfy(List<Constraint> norms, Constraint nB, boolean satisfaction) {
		List<Constraint> ret = new ArrayList<Constraint>();
		for(Constraint nA : norms) {
			if(!nA.equals(nB) && this.satisfies(nA, nB) == satisfaction) {
				ret.add(nA);
			}
		}
		return ret;
	}
}

package uk.ac.ox.cs.ensm.norm.reasoning;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.ox.cs.ensm.agent.language.PredicatesDomains;
import uk.ac.ox.cs.ensm.config.DomainFunctions;
import uk.ac.ox.cs.ensm.norm.Norm;
import uk.ac.ox.cs.ensm.ns.NormativeSystem;
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
public class NSReasoner extends NormEngine {

	//---------------------------------------------------------------------------
	// Methods
	//---------------------------------------------------------------------------

	/**
	 * Constructor
	 * 
	 * @param dmFunctions the domain functions
	 */
	public NSReasoner(NormativeSystem ns, PredicatesDomains predDomains, 
			DomainFunctions dmFunctions) {
		
		super(predDomains);
		
		/* Add the norms of the normative system to the norms engine */
		this.setNormativeSystem(ns);
	}

	/**
	 * Constructor
	 * 
	 * @param dmFunctions the domain functions
	 */
	public NSReasoner(List<Norm> norms, PredicatesDomains predDomains, 
			DomainFunctions dmFunctions) {
		
		super(predDomains);
		
		/* Add the norms of the normative system to the norms engine */
		for(Norm norm : norms) {
			this.addNorm(norm, 1);
		}
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

//	/**
//	 * Returns <tt>true<tt> if the agent with id {@code agId} has complied with
//	 * the given {@code NormativeSystem} in the view transition {@code vTrans} 
//	 * (i.e., the agent has not infringed any of its norms). Otherwise, it 
//	 * returns <tt>false<tt>
//	 * 
//	 * @param agId the id of the agent
//	 * @param norm the norm 
//	 * @param vTrans the transition of views
//	 * 
//	 * @return 	<tt>true<tt> if the agent with id {@code agId} has
//	 * 					complied with the given {@code NormativeSystem} in the 
//	 * 					view transition {@code vTrans}
//	 */
//	public boolean hasCompliedWithNS(long agId, AgentContext agCtxt, 
//			NormativeSystem ns, ViewTransition vTrans) {
//
//		/* Retrieve the actions that the agent performed 
//		 * during the transition of states */
//		List<AgentAction> agActions = 
//				this.dmFunctions.agentAction(agId, vTrans);
//
//		for(AgentAction agAction : agActions) {
//			List<Norm> applNorms = this.getNormsApplicable(
//					agCtxt.getDescription()).getApplicableNorms();
//			
//			for(Norm norm : ns) {
//				
//				/* If a norm prohibits to perform an action, and the agent 
//				 * performed the given action, then it did not comply with 
//				 * the applicable norm */
//				if(applNorms.contains(norm)) {
//					if(!this.hasFulfilledNorm(agId, norm, agAction, vTrans)) {
//						return false;
//					}	
//				}
//			}
//		}
//		
//		/* If either no norms applied to the agent, or the agent fulfilled 
//		 * its applicable norms, then the agent has complied with
//		 * its normative system. Then, return true */
//		return true;
//	}
//
//	/**
//	 * Returns <tt>true<tt> if the agent with id {@code agId} has fulfilled
//	 * the given {@code norm} in the transition of views {@code vTrans}.
//	 * Otherwise, it returns <tt>false<tt>
//	 * @param agId the id of the agent
//	 * @param norm the norm 
//	 * @param vTrans the transition of views
//	 * 
//	 * @return <tt>true<tt> if the agent with id {@code agId} has fulfilled
//	 * 					the given {@code norm} in the transition of views
//	 * 					{@code vTrans}
//	 */
//	public boolean hasFulfilledNorm(long agId, Norm norm,
//			AgentAction agAction, ViewTransition vTrans) 
//	{
////		NormModality modality = norm.getModality();
//		Combination<AgentAction> ac = norm.getActionCombination();
//		
//		// retrieve the role of the agent in the game
//	
////		/* If the norm prohibits to perform the action, and the agent did not
////		 * performed the given action, then it fulfilled the norm */
////		if(modality == NormModality.Prohibition &&
////				agAction != normAction) {
////			return true;
////		}
////
////		/* If the norm obligates to perform the action, and the agent 
////		 * performed the given action, then it infringed the norm */
////		else if(modality == NormModality.Obligation && 
////				agAction == normAction) {
//			if(agAction == normAction) {
//			return true;
//		}
//		return false;
//	}
	
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
}

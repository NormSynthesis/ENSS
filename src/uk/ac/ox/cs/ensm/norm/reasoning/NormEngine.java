package uk.ac.ox.cs.ensm.norm.reasoning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jess.JessEvent;
import jess.JessException;
import jess.JessListener;
import jess.Rete;
import uk.ac.ox.cs.ensm.agent.AgentAction;
import uk.ac.ox.cs.ensm.agent.AgentContext;
import uk.ac.ox.cs.ensm.agent.language.PredicatesDomains;
import uk.ac.ox.cs.ensm.agent.language.SetOfPredicatesWithTerms;
import uk.ac.ox.cs.ensm.norm.Norm;
import uk.ac.ox.cs.ensm.ns.NormativeSystem;

/**
 * Computes norms' applicability for the agent contexts, namely the
 * situations that the agents perceive from their local point of view.
 * To reason about norm applicability, the norm engine employs Jess
 * ({@link http://herzberg.ca.sandia.gov/}), an engine to reason about rules.
 * Jess works as follows: 
 * <ol>
 * <li> the user adds some rules of the form "IF .. THEN..." to the Jess rule
 * 			database. Each norm consists of a precondition (IF), namely a string
 * 			of world facts that describes a state of the world, and a
 * 			postcondition (THEN), namely the actions that must be performed
 * 			whenever the situation described in the postcondition of the rule
 * 			is satisfied;
 * <li> the user adds some world facts to the facts database in the form of 
 * 			strings that describe the current state of the world; and
 * <li>	the Jess rule engine executes its algorithms to assess which norms
 * 			apply to the facts that describe the current state of the world
 * </ol>
 * As an example, consider we add to the Jess rules database a rule like
 * IF "It is 8 in the morning" THEN "I must go to the gym".
 * Consider now that it is 7 in the morning, and we add to Jess the fact 
 * "It is 7 in the morning". Then, Jess will not find any rule that applies
 * to that fact. However, if we add the fact "It is 8 in the morning", Jess
 * will fire the previous rule and "I must go to the gym" will hold.
 * <p>
 * In Jess, the facts in rules' preconditions and world facts have a
 * specific format, which differs from the format of the norms' preconditions
 * and agent context used by the Norm Synthesis Machine. For this reason,
 * the norm engine employs a {@code JessFactsGenerator} to translate the facts
 * in the NSM to facts that the Jess rule engine can understand.
 * 
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 * @see JessFactsGenerator
 */
public class NormEngine implements JessListener {

	//---------------------------------------------------------------------------
	// Attributes																															
	//---------------------------------------------------------------------------

	protected PredicatesDomains predDomains	;	// predicates and their domains
	protected Map<Norm,List<Constraint>> normsConstraints;					
	protected List<Constraint> constraints;		
	protected List<Constraint> applicableConstraints;			// norms applicable to the facts
	protected JessFactsGenerator factFactory; // to create facts for Jess
	protected Rete ruleEngine;								// the Jess rule engine

	//---------------------------------------------------------------------------
	// Methods
	//---------------------------------------------------------------------------

	/**
	 * Constructor
	 * 
	 * @param predDomains predicates and their domains
	 */
	public NormEngine(PredicatesDomains predDomains) 	{
		this.predDomains = predDomains;
		this.factFactory = new JessFactsGenerator(predDomains);

		this.ruleEngine = new Rete();
		
		this.constraints = new ArrayList<Constraint>();
		this.normsConstraints = new HashMap<Norm,List<Constraint>>();
		this.applicableConstraints = new ArrayList<Constraint>();

		/* Add this reasoner ass a listener of the rule engine */
		ruleEngine.addJessListener(this);
		ruleEngine.setEventMask(ruleEngine.getEventMask() 
				| JessEvent.DEFRULE_FIRED);

		this.addPredicateTemplates();
	}

	/**
	 * Resets the norm engine by clearing the facts in the Jess rule engine
	 */
	public void reset() {
		try {
			ruleEngine.eval("(reset)");
			this.applicableConstraints.clear();
		}
		catch (JessException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * Adds a {@code SetOfPredicatesWithTerms} that represents the context of 
	 * an agent in the scenario, namely the world facts that the agent knows.
	 * Recall that the context of an agent is a  piece of information that
	 * the agent knows about the state of the system, described from 
	 * its local point of view
	 * 
	 * @param agContext the world fact that describes the context of an agent
	 */
	public String addFacts(SetOfPredicatesWithTerms agContext)  {
		String facts = this.factFactory.generateFacts(agContext,
				JessFactType.WorldFact);

		/* Clear previous facts and add new ones */
		try {
			ruleEngine.eval(facts);
		}
		catch (JessException e) {
			e.printStackTrace();
		}		
		return facts;
	}

	/**
	 * Executes the Jess rule reasoning algorithm and returns a {@code List}
	 * with the norms that apply to the facts that have been previously
	 * added to the Jess facts database
	 * 
	 * @return a {@code List} with the norms that apply to the facts that have
	 * 					been previously added to the Jess facts database
	 */
	public List<Constraint> reason() {
		try {
			ruleEngine.run();
		} 
		catch (JessException e) {
			e.printStackTrace();
		}
		return this.applicableConstraints;
	}

	/**
	 * Adds the norms of the normative system to the Jess rules database
	 * 
	 * @param ns the normative system
	 */
	public void setNormativeSystem(NormativeSystem ns) {
		for(Norm norm : ns) {
			this.addNorm(norm, 1);
		}
	}

	/**
	 * Adds a norm to the Jess rules database. With this aim, it must
	 * previously translate the precondition of the norm to facts in the format
	 * that Jess can understand.
	 * 
	 * @param norm the norm to add
	 */
	public void addNorm(Norm norm, int salience) {
		if(!this.contains(norm)) {
			
			int numRoles = norm.getContexts().size();
			for(int role=0; role<numRoles; role++) {
				AgentContext context = norm.getContexts().get(role);
				AgentAction action = norm.getActionCombination().get(role);
				
				/* Create constraint restricting the role's behaviour */
				Constraint constraint = new Constraint(role,context,action,norm);
				
				/* Translate the norm's precondition to the format of the 
				 * Jess rules precondition */
				String facts = this.factFactory.generateFacts(context.getDescription(), 
						JessFactType.RulePrecondition);

				/* Generate Jess rule */
				String jessRule = "(defrule " + constraint.getName() + " \"N\" "+ 
						"(declare (salience " + salience + "))" +
						facts + "=> )";

				try {
					ruleEngine.eval(jessRule);
					constraints.add(constraint);
					
					this.addConstraint(constraint, norm);
				}
				catch (JessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param constraint
	 * @param norm
	 */
	private void addConstraint(Constraint constraint, Norm norm) {
		if(!normsConstraints.containsKey(norm)) {
			normsConstraints.put(norm, new ArrayList<Constraint>());
		}
		normsConstraints.get(norm).add(constraint);
	}

	/**
	 * Removes a norm from the rule database of Jess
	 * 
	 * @param norm the norm to remove
	 */
	public void removeNorm(Norm norm) {
		try {
			for(Constraint constraint : normsConstraints.get(norm)) {
				ruleEngine.unDefrule(constraint.getName());
				constraints.remove(constraint);
			}
			normsConstraints.remove(norm);
		}
		catch (JessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return
	 */
	public List<Norm> getNorms() {
		return new ArrayList<Norm>(this.normsConstraints.keySet());
	}

	/**
	 * 
	 * @return
	 */
	public boolean contains(Norm norm) {
		return this.normsConstraints.containsKey(norm);
	}

	//--------------------------------------------------------------------------------
	// Rules
	//--------------------------------------------------------------------------------

	/**
	 * Adds to Jess a template for each possible predicate in the domain
	 */
	private void addPredicateTemplates() {
		try {
			ruleEngine.reset();

			/* Add templates to the knowledge base*/
			for(String predicate : predDomains.getPredicates()) {
				String template = "(deftemplate " + predicate + " (slot value))";
				ruleEngine.eval(template);
			}
		}
		catch (JessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fired when a rule has been activated. Updates the linked norm
	 * 
	 * @param je the Jess event that informs about the fired norm 
	 */
	@Override
	public void eventHappened(JessEvent je) throws JessException {
		int type = je.getType();
		Constraint constraint = null;
		
		switch (type) {
		case JessEvent.DEFRULE_FIRED:
			constraint = retrieveFiredConstraint(je.getObject());
			break;
		}

		/* Activate the norm associated to this rule */
		this.applicableConstraints.add(constraint);
	}

	/**
	 * Returns the name of the norm that Jess has fired
	 * 
	 * @param o the fired norm
	 * @return the id of the fired norm
	 */
	private Constraint retrieveFiredConstraint(Object o) {
		String s = o.toString();
		int ind = s.indexOf("MAIN::");
		int i = ind + 6;
		int j = i;

		/* Retrieve norm id */
		while(!s.substring(j, j+1).equals("-")) {
			j++;
		}
		int normId = Integer.valueOf(s.substring(i+1, j));
		
		/* Retrieve constraint id */
		i=j;
		while(!s.substring(j, j+1).equals(" ")) {
			j++;
		}
		int constraintId = Integer.valueOf(s.substring(i+1, j));
		
		return this.getConstraint(normId, constraintId);
	}
	
	/**
	 * 
	 * @param normId
	 * @param constraintId
	 * @return
	 */
	private Constraint getConstraint(int normId, int constraintId) {
		for(Norm norm : this.normsConstraints.keySet()) {
			if(norm.getId() == normId) {
				return normsConstraints.get(norm).get(constraintId);
			}
		}
		return null; // should never arrive here
	}
}

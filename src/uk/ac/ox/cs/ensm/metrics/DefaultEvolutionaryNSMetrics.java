package uk.ac.ox.cs.ensm.metrics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ox.cs.ensm.EvolutionaryNSM;
import uk.ac.ox.cs.ensm.config.EvolutionaryNSMSettings;
import uk.ac.ox.cs.ensm.game.Game;
import uk.ac.ox.cs.ensm.game.NormativeGame;
import uk.ac.ox.cs.ensm.network.NormativeGamesNetwork;
import uk.ac.ox.cs.ensm.norm.Norm;
import uk.ac.ox.cs.ensm.ns.NormativeSystem;

/**
 * Class containing information about several norm synthesis metrics
 * 
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 */
public class DefaultEvolutionaryNSMetrics implements EvolutionaryNSMetrics {

	//---------------------------------------------------------------------------
	// Attributes
	//---------------------------------------------------------------------------

	private static final int K = 20;
	protected boolean monomorphicConvergence;		// has the norm synthesis process converged?
	private boolean polymorphicConvergence;	// has it converged to a polymorphic population?
	private boolean normsRemainedStable;

	private int numGenerations;

	private List<NormativeSystem> convergedNSs;

	protected EvolutionaryNSMSettings ensmSettings;			// norm synthesis settings
	protected NormativeGamesNetwork ngNetwork;	// the normative network

	private BufferedWriter metricsOutput;
	private double timeStep;

	private Map<NormativeGame,Map<Norm,Double>> lastNormFreqs;
	private int numStableNormGenerations;
	
	//---------------------------------------------------------------------------
	// Methods
	//---------------------------------------------------------------------------

	/**
	 * Constructor 
	 */
	public DefaultEvolutionaryNSMetrics(EvolutionaryNSM nsm) {
		this.ensmSettings = nsm.getSettings();
		this.ngNetwork = nsm.getNormativeGamesNetwork();

		this.lastNormFreqs = new HashMap<NormativeGame,
				Map<Norm,Double>>();

		this.convergedNSs = new ArrayList<NormativeSystem>();
		this.monomorphicConvergence = false;
		this.normsRemainedStable = false;
		this.numGenerations = 0;
	}

	/**
	 * Updates the norm synthesis metrics
	 */
	public void update(double timeStep) {
		this.timeStep = timeStep;
		this.numGenerations++;

		if(!monomorphicConvergence) {

			/* Check if the NS population has remained
			 *  stable for the last K ticks */
			this.checkConvergence();
		}
		else {

			File outputFile = new File("output/traffic/Stability.dat");
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile,true));
				String s = "";

				/* Get the id of the most frequency normative system */
				long nsid = 0;
				BigDecimal freq = BigDecimal.ZERO;
				List<NormativeSystem> nss = this.ngNetwork.getActiveNormativeSystems();

				for(NormativeSystem ns : nss) {
					if(ns.getFrequency().compareTo(freq)>0) {
						nsid = ns.getId();
						freq = ns.getFrequency();
					}
				}

				s += nsid + ";";

				for(NormativeSystem ns : this.ngNetwork.getActiveNormativeSystems()) {
					if(ns.getId()!=nsid) {
						s+=ns.getId() + ";";
					}
				}
				s+= "\n";

				bw.write(s);
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	private void checkConvergence() {

		/* Leave the simulation at least 5,000 ticks to find games */
		if(this.timeStep < 6000) {
			return;
		}
		
		/* Retrieve all games that have enough information to replicate its norms */
		List<NormativeGame> validGames = this.ngNetwork.getValidNormativeGames();

		/* Check valid games to assess whether the system has converged monomorphically.
		 * First, let's assume it has converged (i.e., converged = true) */

		if(!validGames.isEmpty()) {
			this.monomorphicConvergence = true;
			this.polymorphicConvergence = false;
			this.normsRemainedStable = true;
		}

		for(NormativeGame game : validGames) {

			/* If any of the norms of the game is less than 85% frequent, 
			 * then we can say that the game has not converged yet */
			boolean noMajoritaryNorm = true;
			for(Norm norm : game.getNormSpace()) {
				if(game.getFrequency(norm) > 0.9) {
					noMajoritaryNorm = false;
					break;
				}
			}
			if(noMajoritaryNorm) {
				this.monomorphicConvergence = false;
				break;	
			}
		}

		/* If there is monomorphic convergence, there is nothing else to do */
		if(this.monomorphicConvergence) {
			return;
		}

		/* If not, check whether there is polymorphic convergence. 
		 * First, we check whether the frequencies of the norms of each game
		 * have remained stable during the last round. First, let us assume 
		 * they have (i.e., normsRemainedStable = true) */
		
		/* Check each game to assess whether there its norms have remained stable */
		for(NormativeGame nGame : validGames) {
			for(Norm norm : nGame.getNormSpace()) {

				if(!lastNormFreqs.containsKey(nGame)) {
					this.normsRemainedStable = false;
					break;
				}
				if(!lastNormFreqs.get(nGame).containsKey(norm)) {
					this.normsRemainedStable = false;
					break;
				}
				Map<Norm,Double> nfreq = lastNormFreqs.get(nGame);

				int lastFreq = (int)(nfreq.get(norm) * 100);
				int currFreq = (int)(nGame.getFrequency() * 100);

				/* If the frequency of a norm has changed during the 
				 * current round, there is no polymorphic convergence yet*/
				if(Math.abs(lastFreq - currFreq) > 0) {
					this.normsRemainedStable = false;
					break;
				}
			}
		}

		/* Finally, if all norms remained stable, increase the number of rounds
		 * of norm population stability. If the norms have remained stable for 
		 * a given number of rounds > K, then we can say that the system
		 * has converged to a polymorphic population */		
		if(normsRemainedStable) {
			this.numStableNormGenerations++;

			if(this.numStableNormGenerations >= K) {
				NormativeSystem nsWithGreaterFreq = this.ngNetwork.
						getActiveNormativeSystems().get(0);

				for(NormativeSystem ns : this.ngNetwork.
						getActiveNormativeSystems()) {

					if(ns.getFrequency().compareTo(nsWithGreaterFreq.
							getFrequency()) > 0 ) {

						nsWithGreaterFreq = ns;
					}
				}
				this.convergedNSs.add(nsWithGreaterFreq);
				this.polymorphicConvergence = true;
			}
		}
		else {
			this.numStableNormGenerations=0;
		}

		lastNormFreqs.clear();

		/* Set */
		for(NormativeGame nGame : this.ngNetwork.getNormativeGames()) {
			lastNormFreqs.put(nGame, new HashMap<Norm,Double>());

			for(Norm norm : nGame.getNormSpace()) {
				double currFreq = nGame.getFrequency(norm);
				lastNormFreqs.get(nGame).put(norm,currFreq);
			}			
		}
	}


	/**
	 * 
	 */
	public void save() {
		int converged = this.hasConverged()? 1 : 0;
		converged = this.isPolymorfic()? 2 : converged;

		int numGames = this.ngNetwork.getGames().size();
		int numNss = this.ngNetwork.getNormativeSystems().size();
		int numGenNorms = this.ngNetwork.getNorms().size();
		int numNormsConverged = -1;
		
//		BigDecimal avgFitness = BigDecimal.ZERO;

		/* Monomorphic convergence */
		if(this.monomorphicConvergence) {
			numNormsConverged = numGames;
//			avgFitness = this.ngNetwork.getAvgFitness(convergedNSs.get(0));
		}
		
		/* Polymorphic convergence */
		else if(this.polymorphicConvergence) {
			numGenerations -= K;

//			double avgNssFitness = 0.0;
//			for(NormativeSystem ns : convergedNSs) {
//				numNormsConverged = convergedNSs.size();
//				avgNssFitness += this.ngNetwork.getFitness(ns);	
//			}
//			avgNssFitness /= convergedNSs.size();
		}


		/* Generate new metrics line */
		String line = 
				String.valueOf(converged) + ";" +
						String.valueOf(timeStep) + ";" + 
						String.valueOf(numGenerations) + ";" + 	
						String.valueOf(numGames) + ";" +
						String.valueOf(numNss) + ";" +
						String.valueOf(numGenNorms) + ";" +

						/* Convergence metrics */
						String.valueOf(numNormsConverged) + ";\n";

		/* Write metrics to file in mutual exclusion (with a lock) */
		this.writeToMetricsFile(line);
	}


	/**
	 * @param line
	 */
	private void writeToMetricsFile(String line) {
		//		FileOutputStream out;

		try {

			/* Create file writer */
			FileOutputStream out = new FileOutputStream(
					"output/traffic/EvNSMetrics.csv", true);

			FileLock lock = out.getChannel().lock();

			metricsOutput = new BufferedWriter(
					new OutputStreamWriter(out, "utf-8"));

			metricsOutput.write(line);

			lock.release();
			metricsOutput.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return
	 */
	public List<NormativeSystem> getConvergedNSs() {
		return this.convergedNSs;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ox.cs.ensm.metrics.NormSynthesisMetrics#hasConverged()
	 */
	@Override
	public boolean hasConverged() {
		return this.monomorphicConvergence || this.polymorphicConvergence;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isPolymorfic() {
		return this.polymorphicConvergence;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ox.cs.ensm.metrics.NormSynthesisMetrics#getNormSynthesisSettings()
	 */
	@Override
	public EvolutionaryNSMSettings getNormSynthesisSettings() {
		return ensmSettings;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ox.cs.ensm.metrics.NormSynthesisMetrics#addNewComputationTime(long)
	 */
	@Override
	public void addNewComputationTime(double compTime) {}

	/**
	 * 
	 * @return
	 */
	public void addGameReward(Game game, double reward) {}
}
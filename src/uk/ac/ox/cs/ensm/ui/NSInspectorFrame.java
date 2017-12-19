/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ox.cs.ensm.ui;

import java.util.Collections;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import uk.ac.ox.cs.ensm.network.NormativeGamesNetwork;
import uk.ac.ox.cs.ensm.norm.Norm;
import uk.ac.ox.cs.ensm.ns.NormativeSystem;

/**
 *
 * @author Javi
 */
public class NSInspectorFrame extends javax.swing.JFrame {

	private static final long serialVersionUID = 3790512631004299058L;
	private static NSInspector manager;

	private NormativeGamesNetwork ngNetwork;
	private NormativeSystem selectedNS;
	private NormativeSystem selectedNSInteracted;

	/**
	 * Creates new form NSInspectorFrame
	 */
	public NSInspectorFrame(NSInspector mngr) {
		initComponents();

		manager = mngr;
		this.ngNetwork = mngr.getNormSynthesisMachine().
				getNormativeGamesNetwork();
		
		this.updateTreeNS();
//		manager.showNSFrequenciesChart();
	}

	/**
	 * 
	 * 
	 * @param cSolution
	 */
	private void updateTreeNS() {
		List<NormativeSystem> nss = this.ngNetwork.getActiveNormativeSystems();
		Collections.sort(nss);

		TreePath focus = null;

		this.treeNS.removeAll();

		/* Update tree of norms in use */
		DefaultMutableTreeNode rootNode = 
				new DefaultMutableTreeNode("Normative systems");

		TreeModel tmodel = new DefaultTreeModel(rootNode);

		// Add norms that are being evaluated
		for(NormativeSystem ns : nss) {
			this.fillTree(rootNode, ns);
		}

		this.treeNS.setModel(tmodel);
		this.treeNS.setSelectionPath(focus);
		this.treeNS.validate();
	}

	/**
	 * 
	 * @param node
	 * @param children
	 */
	private void fillTree(DefaultMutableTreeNode parentNode, NormativeSystem ns) {
		DefaultMutableTreeNode nsNode = new DefaultMutableTreeNode(ns);
		parentNode.add(nsNode);

		for(Norm norm : ns) {
			DefaultMutableTreeNode normNode = new DefaultMutableTreeNode(norm);
			nsNode.add(normNode);
		}
	}

	/**
	 * 
	 * @param evt
	 */
	private void treeNSChanged(TreeSelectionEvent evt) {
		if(treeNS.getLastSelectedPathComponent() 
				instanceof DefaultMutableTreeNode) {

			DefaultMutableTreeNode srcNode = (DefaultMutableTreeNode)treeNS.
					getLastSelectedPathComponent();
			Object src = srcNode.getUserObject();

			if(src instanceof NormativeSystem) {
				this.selectedNS = (NormativeSystem)src;
				this.showSelectedNSInfo(selectedNS);
				this.updateTreeNSInteractions(selectedNS);
			}
		}
	}

	/**
	 * 
	 * @param ns
	 */
	private void showSelectedNSInfo(NormativeSystem ns) {
		String s = "";

		double fitness = this.ngNetwork.getFitness(ns);
		int value = (int) (100 * ((fitness + 30) / 31));
		
		this.pbNSFitness.setValue(value);

		int freq = (int)(100 * Double.parseDouble(ns.getFrequency().toString()));
		this.pbNSFrequency.setValue((int)freq);

		s += "Fitness: " + fitness + "\n";
		s += "Norms: \n";

		for(Norm norm : ns) {
			s += " - " + norm.getName() + ": " + norm.getDescription() + "\n";
		}

		this.txtSelectedNSInfo.setText(s);
	}

	/**
	 * 
	 * 
	 * @param cSolution
	 */
	private void updateTreeNSInteractions(NormativeSystem ns) {
		if(ns == null) {
			return;
		}

		TreePath focus = null;
		List<NormativeSystem> cNSs = this.ngNetwork.
				getConcurrentNormativeSystems(ns);

		this.treeNSInteractions.removeAll();

		/* Update tree of norms in use */
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(
				ns.getName() + " interactions");

		TreeModel tmodel = new DefaultTreeModel(rootNode);

		// Add norms that are being evaluated
		for(NormativeSystem cns : cNSs) {
			this.fillTree(rootNode, cns);
		}

		this.treeNSInteractions.setModel(tmodel);
		this.treeNSInteractions.setSelectionPath(focus);
		this.treeNSInteractions.validate();
	}

	/**
	 * 
	 * @param evt
	 */
	private void treeNSInteractionsChanged(TreeSelectionEvent evt) {
		if(treeNSInteractions.getLastSelectedPathComponent() 
				instanceof DefaultMutableTreeNode) {

			DefaultMutableTreeNode srcNode = 
					(DefaultMutableTreeNode)treeNSInteractions.
					getLastSelectedPathComponent();

			Object src = srcNode.getUserObject();

			if(src instanceof NormativeSystem) {
				this.selectedNSInteracted = (NormativeSystem)src;
				this.showSelectedNSInteractionInfo(this.selectedNS, 
						this.selectedNSInteracted);
			}
		}
	}

	/**
	 * 
	 * @param ns
	 */
	private void showSelectedNSInteractionInfo(NormativeSystem ns1, NormativeSystem ns2) {
		String s = "";

		/* Set conflict ratio and payoff */
//		BigDecimal cRatio = this.ngNetwork.getConflictRatio(ns1, ns2);
//		this.pbConflictRatio.setValue((int)(
//				cRatio.multiply(new BigDecimal(100)).doubleValue()));

//		double payoff = this.ngNetwork.getPayoff(ns1, ns2).doubleValue();
//		int value = (int) 
//				(100 *
//						( (payoff + punishmentForConflicts) ) / 
//							(rewardForNonConflicts + punishmentForConflicts)
//						);
//		this.pbNSInteractionPayoff.setValue(value);

		s += "Normative systems: " + ns1.getName() + "-" + ns2.getName() + "\n";
//		s += "Conflict ratio: "+ cRatio + "\n";
//		s += "Payoff: " + payoff + "\n";

		this.txtSelectedNSInteractionInfo.setText(s);		
	}

	/**
	 * 
	 * @param evt
	 */
	private void btnShowNSFitnessActionPerformed(java.awt.event.ActionEvent evt) {                                                 
		if(this.selectedNS != null) {
			manager.addNSFitnessChart(this.selectedNS);
		}
	}                                                

	/**
	 * 
	 * @param evt
	 */
	private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {
		this.updateTreeNS();
		this.updateTreeNSInteractions(this.selectedNS);
	}

	/**
	 * 
	 * @param evt
	 */
	private void btnShowConflictRatioRangeActionPerformed(java.awt.event.ActionEvent evt) {                                                          
		if(this.selectedNSInteracted != null) {
			manager.addNSInteractionConflictRatioChart(this.selectedNS, this.selectedNSInteracted);
		}
	}  

	/**
	 * 
	 * @param evt
	 */
	private void btnShowNSFrequenciesActionPerformed(java.awt.event.ActionEvent evt) {                                                     
		manager.showNSFrequenciesChart();
	}  

	/**
	 * 
	 * @param evt
	 */
	private void btnShowFreqEvolutionActionPerformed(java.awt.event.ActionEvent evt) {                                                     

	}   

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		}
		catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(
					NSInspectorFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(
					NSInspectorFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(
					NSInspectorFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(
					NSInspectorFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new NSInspectorFrame(manager).setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify     
	private javax.swing.JButton btnShowConflictRatioRange;
	private javax.swing.JButton btnShowFreqEvolution;
	private javax.swing.JButton btnShowNSFitness;
	private javax.swing.JButton btnShowNSFrequencies;
	private javax.swing.JButton btnUpdate;
	private javax.swing.JLabel lblConflictRatio;
	private javax.swing.JLabel lblNSFitnessRange;
	private javax.swing.JLabel lblNSFrequency;
	private javax.swing.JLabel lblNSInteractionPayoff;
	private javax.swing.JLabel lblNSInteractions;
	private javax.swing.JLabel lblNormativeSystem;
	private javax.swing.JLabel lblTitle;
	private javax.swing.JPanel panelSelectedNS;
	private javax.swing.JPanel panelSelectedNSInteraction;
	private javax.swing.JProgressBar pbConflictRatio;
	private javax.swing.JProgressBar pbNSFitness;
	private javax.swing.JProgressBar pbNSFrequency;
	private javax.swing.JProgressBar pbNSInteractionPayoff;
	private javax.swing.JScrollPane scrPSelectedNSInfo;
	private javax.swing.JScrollPane scrPSelectedNSInteractionInfo;
	private javax.swing.JScrollPane scrPanelNS;
	private javax.swing.JScrollPane scrPanelNSInteractions;
	private javax.swing.JTree treeNS;
	private javax.swing.JTree treeNSInteractions;
	private javax.swing.JTextArea txtSelectedNSInfo;
	private javax.swing.JTextArea txtSelectedNSInteractionInfo;
	// End of variables declaration      



	//	/* Add tree listeners */
	//	treeNS.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
	//		public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
	//			treeNSChanged(evt);
	//		}
	//	});	
	//
	//	/* Add tree listeners */
	//	treeNSInteractions.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
	//		public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
	//			treeNSInteractionsChanged(evt);
	//		}
	//	});	
	//	
	//
	//	pbNSFitness.setOpaque(true);
	//	pbNSFitness.setStringPainted(true);
	//	
	//	pbNSFrequency.setOpaque(true);
	//	pbNSFrequency.setStringPainted(true);
	//
	//	pbConflictRatio.setOpaque(true);
	//	pbConflictRatio.setStringPainted(true);
	//
	//	pbNSInteractionPayoff.setOpaque(true);
	//	pbNSInteractionPayoff.setStringPainted(true);


	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated Code">                          
	private void initComponents() {

		lblTitle = new javax.swing.JLabel();
		lblNormativeSystem = new javax.swing.JLabel();
		lblNSInteractions = new javax.swing.JLabel();
		scrPanelNS = new javax.swing.JScrollPane();
		treeNS = new javax.swing.JTree();
		panelSelectedNS = new javax.swing.JPanel();
		btnShowNSFitness = new javax.swing.JButton();
		lblNSFitnessRange = new javax.swing.JLabel();
		scrPSelectedNSInfo = new javax.swing.JScrollPane();
		txtSelectedNSInfo = new javax.swing.JTextArea();
		pbNSFitness = new javax.swing.JProgressBar();
		lblNSFrequency = new javax.swing.JLabel();
		pbNSFrequency = new javax.swing.JProgressBar();
		btnShowFreqEvolution = new javax.swing.JButton();
		scrPanelNSInteractions = new javax.swing.JScrollPane();
		treeNSInteractions = new javax.swing.JTree();
		panelSelectedNSInteraction = new javax.swing.JPanel();
		scrPSelectedNSInteractionInfo = new javax.swing.JScrollPane();
		txtSelectedNSInteractionInfo = new javax.swing.JTextArea();
		lblConflictRatio = new javax.swing.JLabel();
		pbConflictRatio = new javax.swing.JProgressBar();
		lblNSInteractionPayoff = new javax.swing.JLabel();
		pbNSInteractionPayoff = new javax.swing.JProgressBar();
		btnShowConflictRatioRange = new javax.swing.JButton();
		btnUpdate = new javax.swing.JButton();
		btnShowNSFrequencies = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		lblTitle.setBackground(new java.awt.Color(255, 255, 255));
		lblTitle.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
		lblTitle.setText("Normative Systems Inspector");
		lblTitle.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		lblTitle.setOpaque(true);

		lblNormativeSystem.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		lblNormativeSystem.setText("Normative system");

		lblNSInteractions.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		lblNSInteractions.setText("Normative system interactions");

		scrPanelNS.setViewportView(treeNS);

		panelSelectedNS.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Selected normative system", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(0, 0, 204))); // NOI18N

		btnShowNSFitness.setText("Range");
		btnShowNSFitness.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnShowNSFitnessActionPerformed(evt);
			}
		});

		lblNSFitnessRange.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		lblNSFitnessRange.setText("Fitness");

		txtSelectedNSInfo.setColumns(20);
		txtSelectedNSInfo.setRows(5);
		scrPSelectedNSInfo.setViewportView(txtSelectedNSInfo);

		pbNSFitness.setOpaque(true);

		lblNSFrequency.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		lblNSFrequency.setText("Frequency");

		pbNSFrequency.setOpaque(true);

		btnShowFreqEvolution.setText("Evolution");
		btnShowFreqEvolution.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnShowFreqEvolutionActionPerformed(evt);
			}
		});

		/* Add tree listeners */
		treeNS.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
			public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
				treeNSChanged(evt);
			}
		});	

		/* Add tree listeners */
		treeNSInteractions.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
			public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
				treeNSInteractionsChanged(evt);
			}
		});	


		pbNSFitness.setOpaque(true);
		pbNSFitness.setStringPainted(true);

		pbNSFrequency.setOpaque(true);
		pbNSFrequency.setStringPainted(true);

		pbConflictRatio.setOpaque(true);
		pbConflictRatio.setStringPainted(true);

		pbNSInteractionPayoff.setOpaque(true);
		pbNSInteractionPayoff.setStringPainted(true);

		javax.swing.GroupLayout panelSelectedNSLayout = new javax.swing.GroupLayout(panelSelectedNS);
		panelSelectedNS.setLayout(panelSelectedNSLayout);
		panelSelectedNSLayout.setHorizontalGroup(
				panelSelectedNSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(panelSelectedNSLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(panelSelectedNSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(scrPSelectedNSInfo)
								.addGroup(panelSelectedNSLayout.createSequentialGroup()
										.addGroup(panelSelectedNSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(lblNSFrequency)
												.addComponent(lblNSFitnessRange))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(panelSelectedNSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(pbNSFitness, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
												.addComponent(pbNSFrequency, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(panelSelectedNSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
												.addComponent(btnShowNSFitness, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(btnShowFreqEvolution, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
						.addContainerGap())
				);
		panelSelectedNSLayout.setVerticalGroup(
				panelSelectedNSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(panelSelectedNSLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(panelSelectedNSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(panelSelectedNSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
										.addComponent(lblNSFrequency, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
										.addComponent(pbNSFrequency, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addComponent(btnShowFreqEvolution))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(panelSelectedNSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(panelSelectedNSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
										.addComponent(lblNSFitnessRange, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(pbNSFitness, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
								.addComponent(btnShowNSFitness))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(scrPSelectedNSInfo)
						.addContainerGap())
				);

		scrPanelNSInteractions.setViewportView(treeNSInteractions);

		panelSelectedNSInteraction.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Selected interaction", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(0, 0, 204))); // NOI18N

		txtSelectedNSInteractionInfo.setColumns(20);
		txtSelectedNSInteractionInfo.setRows(5);
		scrPSelectedNSInteractionInfo.setViewportView(txtSelectedNSInteractionInfo);

		lblConflictRatio.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		lblConflictRatio.setText("Conflict ratio");

		lblNSInteractionPayoff.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		lblNSInteractionPayoff.setText("Payoff");

		btnShowConflictRatioRange.setText("Range");
		btnShowConflictRatioRange.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnShowConflictRatioRangeActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout panelSelectedNSInteractionLayout = new javax.swing.GroupLayout(panelSelectedNSInteraction);
		panelSelectedNSInteraction.setLayout(panelSelectedNSInteractionLayout);
		panelSelectedNSInteractionLayout.setHorizontalGroup(
				panelSelectedNSInteractionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(panelSelectedNSInteractionLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(panelSelectedNSInteractionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(scrPSelectedNSInteractionInfo)
								.addGroup(panelSelectedNSInteractionLayout.createSequentialGroup()
										.addGroup(panelSelectedNSInteractionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
												.addComponent(lblConflictRatio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(lblNSInteractionPayoff, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(panelSelectedNSInteractionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(pbNSInteractionPayoff, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addGroup(panelSelectedNSInteractionLayout.createSequentialGroup()
														.addComponent(pbConflictRatio, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(btnShowConflictRatioRange)))))
						.addContainerGap())
				);
		panelSelectedNSInteractionLayout.setVerticalGroup(
				panelSelectedNSInteractionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(panelSelectedNSInteractionLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(panelSelectedNSInteractionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addGroup(panelSelectedNSInteractionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
										.addComponent(pbConflictRatio, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
										.addComponent(lblConflictRatio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addComponent(btnShowConflictRatioRange))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(panelSelectedNSInteractionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(pbNSInteractionPayoff, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblNSInteractionPayoff, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(scrPSelectedNSInteractionInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
						.addContainerGap())
				);

		btnUpdate.setText("Update");
		btnUpdate.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnUpdateActionPerformed(evt);
			}
		});

		btnShowNSFrequencies.setText("Show NS frequencies");
		btnShowNSFrequencies.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnShowNSFrequenciesActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(layout.createSequentialGroup()
										.addComponent(scrPanelNS, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(panelSelectedNS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGroup(layout.createSequentialGroup()
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(scrPanelNSInteractions, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(lblNSInteractions)
												.addComponent(lblNormativeSystem)
												.addComponent(btnUpdate))
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(layout.createSequentialGroup()
														.addGap(10, 10, 10)
														.addComponent(panelSelectedNSInteraction, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
												.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(btnShowNSFrequencies)))))
						.addContainerGap())
				);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(lblNormativeSystem)
								.addComponent(btnShowNSFrequencies))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(panelSelectedNS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(scrPanelNS, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(lblNSInteractions)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(panelSelectedNSInteraction, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(scrPanelNSInteractions, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(btnUpdate)
						.addContainerGap())
				);

		pack();
	}// </editor-fold>      
}

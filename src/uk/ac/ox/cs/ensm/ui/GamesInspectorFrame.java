/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ox.cs.ensm.ui;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import uk.ac.ox.cs.ensm.EvolutionaryNSM;
import uk.ac.ox.cs.ensm.agent.AgentAction;
import uk.ac.ox.cs.ensm.game.NormativeGame;
import uk.ac.ox.cs.ensm.network.NormativeGamesNetwork;

/**
 *
 * @author Javi
 */
public class GamesInspectorFrame extends javax.swing.JFrame {

	/* Serial ID */
	private static final long serialVersionUID = -6753361552111937934L;

	/* Games inspector manager */
	private static GamesInspector manager;

	/* Evolutionary NSM */
	private EvolutionaryNSM ensm;

	/* NS Network */
	private NormativeGamesNetwork ngNetwork;

	/* Actions available to the agents in all games */
	private List<AgentAction> actions;

	/* Game selected to show its information */	
	private NormativeGame selectedGame;

	/**
	 * Creates new form GamesInspector
	 */
	public GamesInspectorFrame(GamesInspector mngr) {
		manager = mngr;
		this.ensm = manager.getNormSynthesisMachine();
		this.ngNetwork = ensm.getNormativeGamesNetwork();	

		/* Retrieve the list of all actions available to the agents in the games */
		this.actions = new ArrayList<AgentAction>(ensm.getGrammar().getActions());

		this.sortActions(actions);

		/* Create models for the payoff matrix and the conflict ratio matrix */
		DefaultTableModel modelPayoff = this.createTablesModel();
		DefaultTableModel modelCR = this.createTablesModel();

		/* Initialise and update UI components */
		initComponents(modelPayoff, modelCR);
		this.updateTreeGames();

		//		manager.showGameFrequenciesChart();
	}

	/**
	 * @param actions2
	 */
	private void sortActions(List<AgentAction> actions) {
		List<String> strActions = new ArrayList<String>();
		List<AgentAction> newActions = new ArrayList<AgentAction>();

		for(AgentAction ac : actions) {
			strActions.add(ac.toString());
		}

		Collections.sort(strActions);
		for(String strAc : strActions) {
			for(AgentAction ac : actions) {
				if(ac.toString().equals(strAc)) {
					newActions.add(ac);
				}
			}
		}
		this.actions = newActions;
	}

	/**
	 * 
	 */
	private DefaultTableModel createTablesModel() {
		return new DefaultTableModel(
				new Object [][] {
					{null, null, null},
					{null, null, null}
				},
				new String [] {"", "Go", "Stop"}) {

			private static final long serialVersionUID = 3150168233274761543L;

			Class[] types = new Class [] {
					java.lang.String.class, java.lang.String.class, java.lang.String.class
			};
			boolean[] canEdit = new boolean [] {
					false, false, false
			};

			public Class getColumnClass(int columnIndex) {
				return types [columnIndex];
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit [columnIndex];
			}
		};		

	}

	/**
	 * 
	 */
	private void updateTreeGames() {
		TreePath focus = null;

		this.treeGames.removeAll();

		/* Update tree of norms in use */
		DefaultMutableTreeNode rootNode = 
				new DefaultMutableTreeNode("Games");

		TreeModel tmodel = new DefaultTreeModel(rootNode);

		// Add norms that are being evaluated
		for(NormativeGame game : ngNetwork.getNormativeGames()) {
			DefaultMutableTreeNode gameNode = new DefaultMutableTreeNode(game);
			rootNode.add(gameNode);
		}

		this.treeGames.setModel(tmodel);
		this.treeGames.setSelectionPath(focus);
		this.treeGames.validate();
	}

	/**
	 * 
	 * @param evt
	 */
	private void treeGamesChanged(TreeSelectionEvent evt) {
		if(treeGames.getLastSelectedPathComponent() 
				instanceof DefaultMutableTreeNode) {

			DefaultMutableTreeNode srcNode = (DefaultMutableTreeNode)treeGames.
					getLastSelectedPathComponent();
			Object src = srcNode.getUserObject();

			if(src instanceof NormativeGame) {
				this.selectedGame = (NormativeGame)src;
				this.showSelectedGameInfo(selectedGame);
			}
		}
	}

	/**
	 * @param game
	 */
	private void showSelectedGameInfo(NormativeGame game) {
		String info = game.toString();

		/* Update game frequency and description */
		this.pbGameFrequency.setValue((int)(game.getFrequency()*100));
		this.txtSelectedNSInfo.setText(info);

		/* Update payoff matrix and conflict ratio matrix */
		//	DecimalFormat df = new DecimalFormat("####0.00");
		//		int row = 0;
		//
		//		for(AgentAction acA : game.getActionSpace(0)) {
		//
		//			this.tablePayoff.getModel().setValueAt(acA.toString(), row, 0);
		//			this.tableCR.getModel().setValueAt(acA.toString(), row, 0);
		//
		//			int col = 1;
		//			for(AgentAction acB : game.getActionSpace(1)) {
		//
		//				/* Set conflict ratio */
		//				BigDecimal crAB, crBA;
		//				
		//				if(game.getNumRoles() == 1) {
		//					Combination<AgentAction> ap = new Combination<AgentAction>(acA);
		//					crAB = game.getPayoff(ap, 0);
		//					crBA = game.getPayoff(ap, 0);
		//				}
		//				else {
		//					Combination<AgentAction> ac = new Combination<AgentAction>(acA, acB);
		//					crAB = game.getPayoff(ac, 0);
		//					crBA = game.getPayoff(ac, 1);
		//				}
		//				
		//				String s = (Double.isNaN(crAB.doubleValue()) ? "???" : df.format(crAB)) +
		//						" , " + (Double.isNaN(crBA.doubleValue()) ? "???" : df.format(crBA));
		//				
		//				this.tableCR.getModel().setValueAt(s, row, col);
		//
		//				col++;
		//			}
		//			row++;
		//		}
	}

	//	/* Add tree listeners */
	//	treeGames.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
	//		public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
	//			treeGamesChanged(evt);
	//		}
	//	});	

	/**
	 * This method is called from within the constructor to initialise the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated Code">                          
	private void initComponents(DefaultTableModel modelPayoff,
			DefaultTableModel modelCR) {

		lblTitle = new javax.swing.JLabel();
		scrPaneGames = new javax.swing.JScrollPane();
		treeGames = new javax.swing.JTree();
		lblGames = new javax.swing.JLabel();
		btnUpdate = new javax.swing.JButton();
		panelSelectedGame = new javax.swing.JPanel();
		scrPSelectedGameInfo = new javax.swing.JScrollPane();
		txtSelectedNSInfo = new javax.swing.JTextArea();
		lblGameDesc = new javax.swing.JLabel();
		pbGameFrequency = new javax.swing.JProgressBar();
		btnShowFreqEvolution = new javax.swing.JButton();
		lblCRMatrix = new javax.swing.JLabel();
		lblPayoffMatrix = new javax.swing.JLabel();
		scrPaneCR = new javax.swing.JScrollPane();
		tableCR = new javax.swing.JTable();
		scrPanePayoff = new javax.swing.JScrollPane();
		tablePayoff = new javax.swing.JTable();
		lblGameFrequency = new javax.swing.JLabel();
		lblPayoffB = new javax.swing.JLabel();
		lblPayoffA = new javax.swing.JLabel();
		lblCRA = new javax.swing.JLabel();
		lblCRB = new javax.swing.JLabel();
		btnShowGameFrequencies = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		lblTitle.setBackground(new java.awt.Color(255, 255, 255));
		lblTitle.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
		lblTitle.setText("Games Inspector");
		lblTitle.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		lblTitle.setOpaque(true);

		scrPaneGames.setViewportView(treeGames);

		lblGames.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		lblGames.setText("Games");

		btnUpdate.setText("Update");
		btnUpdate.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnUpdateActionPerformed(evt);
			}
		});

		panelSelectedGame.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Selected game", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(0, 0, 204))); // NOI18N

		txtSelectedNSInfo.setColumns(20);
		txtSelectedNSInfo.setRows(20);

		scrPSelectedGameInfo.setViewportView(txtSelectedNSInfo);

		lblGameDesc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		lblGameDesc.setText("Game description");

		pbGameFrequency.setOpaque(true);
		pbGameFrequency.setStringPainted(true);

		btnShowFreqEvolution.setText("Evolution");
		btnShowFreqEvolution.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnShowFreqEvolutionActionPerformed(evt);
			}
		});

		lblCRMatrix.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		lblCRMatrix.setText("Conflict ratio matrix");

		lblPayoffMatrix.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		lblPayoffMatrix.setText("Payoff matrix");

		tableCR.setModel(modelCR);
		scrPaneCR.setViewportView(tableCR);

		tablePayoff.setModel(modelPayoff);
		scrPanePayoff.setViewportView(tablePayoff);

		lblGameFrequency.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		lblGameFrequency.setText("Frequency");

		lblPayoffB.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
		lblPayoffB.setForeground(new java.awt.Color(51, 51, 255));
		lblPayoffB.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		lblPayoffB.setText("B");

		lblPayoffA.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
		lblPayoffA.setForeground(new java.awt.Color(51, 51, 255));
		lblPayoffA.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		lblPayoffA.setText("A");

		lblCRA.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
		lblCRA.setForeground(new java.awt.Color(51, 51, 255));
		lblCRA.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		lblCRA.setText("A");

		lblCRB.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
		lblCRB.setForeground(new java.awt.Color(51, 51, 255));
		lblCRB.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		lblCRB.setText("B");

		javax.swing.GroupLayout panelSelectedGameLayout = new javax.swing.GroupLayout(panelSelectedGame);
		panelSelectedGame.setLayout(panelSelectedGameLayout);
		panelSelectedGameLayout.setHorizontalGroup(
				panelSelectedGameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(panelSelectedGameLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(panelSelectedGameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(scrPSelectedGameInfo)
								.addGroup(panelSelectedGameLayout.createSequentialGroup()
										.addComponent(lblGameFrequency)
										.addGap(10, 10, 10)
										.addComponent(pbGameFrequency, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(btnShowFreqEvolution))
								.addComponent(lblPayoffB, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblPayoffMatrix, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(panelSelectedGameLayout.createSequentialGroup()
										.addComponent(lblPayoffA, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(scrPanePayoff, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
								.addGroup(panelSelectedGameLayout.createSequentialGroup()
										.addComponent(lblCRA, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(scrPaneCR, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
								.addComponent(lblCRB, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(panelSelectedGameLayout.createSequentialGroup()
										.addGroup(panelSelectedGameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(lblGameDesc)
												.addComponent(lblCRMatrix))
										.addGap(0, 0, Short.MAX_VALUE)))
						.addContainerGap())
				);
		panelSelectedGameLayout.setVerticalGroup(
				panelSelectedGameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(panelSelectedGameLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(panelSelectedGameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(pbGameFrequency, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btnShowFreqEvolution, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblGameFrequency, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(lblGameDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(scrPSelectedGameInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(lblPayoffMatrix)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(lblPayoffB)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(panelSelectedGameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(lblPayoffA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(scrPanePayoff, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(lblCRMatrix)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(lblCRB)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(panelSelectedGameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(scrPaneCR, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblCRA, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap())
				);

		btnShowGameFrequencies.setText("Show game frequencies");
		btnShowGameFrequencies.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnShowGameFrequenciesActionPerformed(evt);
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
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(btnUpdate)
												.addComponent(scrPaneGames, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(lblGames, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(layout.createSequentialGroup()
														.addGap(0, 0, Short.MAX_VALUE)
														.addComponent(btnShowGameFrequencies))
												.addComponent(panelSelectedGame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
						.addContainerGap())
				);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(8, 8, 8)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(lblGames, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btnShowGameFrequencies, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
										.addComponent(scrPaneGames, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(btnUpdate))
								.addComponent(panelSelectedGame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addContainerGap())
				);

		/* Add tree listeners */
		treeGames.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
			public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
				treeGamesChanged(evt);
			}
		});	

		/* Fit dimension of the scroll panel to that of its inner table */
		Dimension d = tablePayoff.getPreferredSize();
		scrPanePayoff.setPreferredSize(new Dimension(d.width,
				tablePayoff.getRowHeight()*tablePayoff.getRowCount()+1));

		d = tableCR.getPreferredSize();
		scrPaneCR.setPreferredSize(new Dimension(d.width, 
				tableCR.getRowHeight()*tableCR.getRowCount()+1));

		pack();
	}// </editor-fold>   
	/**
	 * 
	 * @param evt
	 */
	private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {                                          
		updateTreeGames();
	}                                         

	/**
	 * 
	 * @param evt
	 */
	private void btnShowFreqEvolutionActionPerformed(java.awt.event.ActionEvent evt) {                                                     
		manager.showGameFreqEvolutionChart();
	}                                                    

	/**
	 * 
	 * @param evt
	 */
	private void btnShowGameFrequenciesActionPerformed(java.awt.event.ActionEvent evt) {                                                     
		manager.showGameFrequenciesChart();
	}                                                    

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
		/* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
		 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
		 */
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(GamesInspectorFrame.class.getName()).
			log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(GamesInspectorFrame.class.getName()).
			log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(GamesInspectorFrame.class.getName()).
			log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(GamesInspectorFrame.class.getName()).
			log(java.util.logging.Level.SEVERE, null, ex);
		}

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new GamesInspectorFrame(manager).setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify                     
	private javax.swing.JButton btnShowFreqEvolution;
	private javax.swing.JButton btnShowGameFrequencies;
	private javax.swing.JButton btnUpdate;
	private javax.swing.JLabel lblCRA;
	private javax.swing.JLabel lblCRB;
	private javax.swing.JLabel lblCRMatrix;
	private javax.swing.JLabel lblGameDesc;
	private javax.swing.JLabel lblGameFrequency;
	private javax.swing.JLabel lblGames;
	private javax.swing.JLabel lblPayoffA;
	private javax.swing.JLabel lblPayoffB;
	private javax.swing.JLabel lblPayoffMatrix;
	private javax.swing.JLabel lblTitle;
	private javax.swing.JPanel panelSelectedGame;
	private javax.swing.JProgressBar pbGameFrequency;
	private javax.swing.JScrollPane scrPSelectedGameInfo;
	private javax.swing.JScrollPane scrPaneCR;
	private javax.swing.JScrollPane scrPaneGames;
	private javax.swing.JScrollPane scrPanePayoff;
	private javax.swing.JTable tableCR;
	private javax.swing.JTable tablePayoff;
	private javax.swing.JTree treeGames;
	private javax.swing.JTextArea txtSelectedNSInfo;
	// End of variables declaration                   
}

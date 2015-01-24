package fr.univpau.m2ti.sma.fishmarket.ihm;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;

/**
 * The view for the seller agents.
 * 
 * @author Josuah Aron
 *
 */
public class SellerView extends JFrame
						implements ActionListener
{
	/** The agent for which this view is created. */
	private SellerAgent myAgent;
	
	/** Holds the spinner which defines the minimal value for the price. */
	private JSpinner minPriceSpinner;
	
	/** Holds the spinner which defines the maximal value for the price. */
	private JSpinner maxPriceSpinner;
	
	/** Holds the spinner which defines the price at the beginning of the auction. */
	private JSpinner startingPriceSpinner;
	
	/** Holds the spinner which defines the starting step by which the price can be increased or decreased. */
	private JSpinner priceStepSpinner;
	
	/** Holds the spinner which defines the minimal value for the price step. */
	private JSpinner minPriceStepSpinner;
	
	/** Holds the spinner which defines the duration of the waiting for bidders. */
	private JSpinner waitingBidDurationSpinner;
	
	/** Holds the text field which inform about the number of subscribers to the auction. */
	private JSpinner subscriberCountSpinner;
	
	/** Holds the table which inform about the last announced price of the auction and the number of bid for this announce. */
	private JTable currentAnnounceTable;
	
	/** Holds the table which inform about the past announced prices of the auction and the number of bids for these announces. */
	private JTable announceHistoryTable;
	
	private JTextField fishSupplyNameTextField;
	
	/** Holds the text field which inform about the number of bidders for the past announced prices. */
	private JButton startButton;
	
	/** The default width of the window. */
	public static final int DEFAULT_WIDTH = 600;
	
	/** The default height of the window. */
	public static final int DEFAULT_HEIGHT = 300;

	/** Action command for the start button click listener (action listener). */
	private static final String START_BUTTON_ACTION_COMMAND = "STRAT_BUTTON_ACTION_COMMAND";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5048176398667805254L;
	
	/**
	 * A view for a seller agent.
	 * 
	 * @param myAgent the agent for which the view is created.
	 */
	public SellerView(SellerAgent myAgent)
	{
		this.myAgent = myAgent;
		
		this.instantiateWidgets();
		
		this.addListeners();
		
		this.assemble();
		
		this.setTitle("Seller agent: "+this.myAgent.getAID().getLocalName());
	    this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	    this.setLocationRelativeTo(null);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		if(event.getActionCommand().equals(START_BUTTON_ACTION_COMMAND))
		{
			this.minPriceSpinner.setEnabled(false);
			this.maxPriceSpinner.setEnabled(false);
			
			this.startButton.setEnabled(false);
			
			this.myAgent.setFishSupplyName(
					this.fishSupplyNameTextField.getText());
			
			this.myAgent.notifyStartCommand();
		}
	}
	
	/**
	 * 
	 * @param minPrice the value which is to be display on the field for the minimal value of the price for the auction.
	 */
	public void setMinPrice(float minPrice)
	{
		this.minPriceSpinner.setValue(minPrice);
	}
	
	/**
	 * 
	 * @param maxPrice the value which is to be display on the field for the maximal value of the price for the auction.
	 */
	public void setMaxPrice(float maxPrice)
	{
		this.maxPriceSpinner.setValue(maxPrice);
	}
	
	/**
	 * 
	 * @param price the value which is to be display on the field for the value of the price for the auction.
	 */
	public void setStartingPrice(float price)
	{
		this.startingPriceSpinner.setValue(price);	
	}
	
	/**
	 * 
	 * @param priceStep the value which is to be display on the field for the starting value of the step by which the price can be increased or decreased.
	 */
	public void setPriceStep(float priceStep)
	{
		this.priceStepSpinner.setValue(priceStep);
	}
	
	/**
	 * 
	 * @param minPriceStep  the value which is to be display on the field for the minimal value of the price step.
	 */
	public void setMinPriceStep(float minPriceStep)
	{
		this.minPriceSpinner.setValue(minPriceStep);
	}
	
	/**
	 * 
	 * @param millis the value which is to be display on the field for the timeout of the waiting for bids.
	 */
	public void setWaitBidDuration(long millis)
	{
		this.waitingBidDurationSpinner.setValue(millis);
	}
	
	/**
	 * 
	 * @param subscriberCount the value which is to be display on the field for the number of subscriber for the auction.
	 */
	public void setSubscriberCount(int subscriberCount)
	{
		this.subscriberCountSpinner.setValue(
				subscriberCount);
	}
	
	/**
	 * Update the UI after a new bid.
	 * 
	 * @param bidCount  the value which is to be display on the field for the number of bids for the last announced price.
	 */
	public void notifyNewSubscriber()
	{
		int subscriberCount =
				(int) this.subscriberCountSpinner.getValue();
		
		if(subscriberCount == 0)
		{
			this.startButton.setEnabled(true);
		}
		
		this.subscriberCountSpinner.setValue(++subscriberCount);
	}
	
	/**
	 * Updates the UI after a new announce.
	 * 
	 * @param price  the value which is to be display on the field for the last announce price. 
	 */
	public void notifyNewAnnounce(float newPrice)
	{
		SellerTableModel historyModel =
				(SellerTableModel) this.announceHistoryTable.getModel();
		
		SellerTableModel currentAnnounceModel =
				(SellerTableModel) this.currentAnnounceTable.getModel();
		
		if(currentAnnounceModel.getRowCount() == 1)
		{
			Float currentPrice = (Float) currentAnnounceModel.getValueAt(0, 0);
			Integer currentBidCount = (Integer) currentAnnounceModel.getValueAt(0, 1);
			
			historyModel.addValue(currentPrice, currentBidCount);
			
			currentAnnounceModel.setValueAt(newPrice, 0, SellerTableModel.PRICE_COLUMN);
			currentAnnounceModel.setValueAt(0, 0, SellerTableModel.BID_COUNT_COLUMN);
			
			historyModel.fireTableDataChanged();
		}
		else
		{
			currentAnnounceModel.addValue(newPrice);
		}
		
		currentAnnounceModel.fireTableDataChanged();
	}
	
	/**
	 * Update the UI after a new bid.
	 */
	public void notifyNewBid()
	{
		SellerTableModel currentAnnounceModel =
				(SellerTableModel) this.currentAnnounceTable.getModel();
		
		Integer currentBidCount = (Integer) currentAnnounceModel.getValueAt(0, SellerTableModel.BID_COUNT_COLUMN);
		
		currentAnnounceModel.setValueAt(
				++currentBidCount, 0, SellerTableModel.BID_COUNT_COLUMN);
		
		currentAnnounceModel.fireTableDataChanged();
	}
	
	/**
	 * 
	 * @return the value which is displayed on the field for the minimal value of the price for the auction.
	 */
	public float getMinPrice()
	{
		return ((Double) this.minPriceSpinner.getValue()).floatValue();
	}
	
	/**
	 * 
	 * @return the value which is displayed on the field for the maximal value of the price for the auction.
	 */
	public float getMaxPrice()
	{
		return ((Double)this.maxPriceSpinner.getValue()).floatValue();
	}
	
	/**
	 * 
	 * @return  the value which is displayed on the field for the  value of the starting price for the auction.
	 */
	public float getStartingPrice()
	{
		return ((Double) this.startingPriceSpinner.getValue()).floatValue();
	}
	
	/**
	 * 
	 * @return  the value which is displayed on the field for the starting value of the step by which the price can be increased or decreased.
	 */
	public float getPriceStep()
	{
		return ((Double) this.priceStepSpinner.getValue()).floatValue();
	}
	
	/**
	 * 
	 * @return  the value which is displayed on the field for the minimal value of the price step.
	 */
	public float getMinPriceStep()
	{
		return ((Double) this.minPriceStepSpinner.getValue()).floatValue();
	}
	
	/**
	 * 
	 * @return the value which is displayed on the field for the timeout of the waiting for bids for the last announced price.
	 */
	public long getWaitBidDuration()
	{
		return ((Integer) this.waitingBidDurationSpinner.getValue()).intValue();
	}
	
	/**
	 * Instantiates the dynamic widgets of the view.
	 */
	private void instantiateWidgets()
	{
		this.minPriceSpinner = new JSpinner(
				new SpinnerNumberModel(
						(double)this.myAgent.getMinPrice(),
						0d,
						(double)this.myAgent.getMinPrice()*10,
						(double)this.myAgent.getMinPrice()/10
						)
				);
		
		this.maxPriceSpinner = new JSpinner(
				new SpinnerNumberModel(
						(double)this.myAgent.getMaxPrice(),
						0d,
						(double)this.myAgent.getMaxPrice()*10,
						(double)this.myAgent.getMaxPrice()/10
						)
				);
		
		this.startingPriceSpinner = new JSpinner(
				new SpinnerNumberModel(
						(double)this.myAgent.getCurrentPrice(),
						0d,
						(double)this.myAgent.getCurrentPrice()*10,
						(double)this.myAgent.getCurrentPrice()/10
						)
				);
		
		this.priceStepSpinner = new JSpinner(
				new SpinnerNumberModel(
						(double)this.myAgent.getPriceStep(),
						0d,
						(double)this.myAgent.getPriceStep()*10,
						(double)this.myAgent.getPriceStep()/10
						)
				);
		
		this.minPriceStepSpinner = new JSpinner(
				new SpinnerNumberModel(
						(double)this.myAgent.getMinPriceStep(),
						0d,
						(double)this.myAgent.getMinPriceStep()*10,
						(double)this.myAgent.getMinPriceStep()/10
						)
				);
		
		this.waitingBidDurationSpinner = new JSpinner(
				new SpinnerNumberModel(
						(int)this.myAgent.getBidWaitingDuration(),
						0,
						(int)this.myAgent.getBidWaitingDuration()*10,
						(int)this.myAgent.getBidWaitingDuration()/10
						)
				);
		
		this.subscriberCountSpinner = new JSpinner(
				new SpinnerNumberModel(0, 0, 100, 1));
		this.subscriberCountSpinner.setEnabled(false);
		
		this.fishSupplyNameTextField = new JTextField("Fish supply");
		
		this.currentAnnounceTable = new JTable(new SellerTableModel());
		
		this.announceHistoryTable = new JTable(new SellerTableModel());
		
		this.startButton = new JButton("Start");
		this.startButton.setEnabled(false);
	}
	
	/**
	 * Adds listeners to the dynamic widgets of the view.
	 */
	private void addListeners()
	{
		this.minPriceSpinner.addChangeListener(
				new ChangeListener(){
					@Override
					public void stateChanged(ChangeEvent e)
					{
						JSpinner spinner = (JSpinner) e.getSource();
						
						myAgent.setMinPrice(((Double)spinner.getValue()).floatValue());
					}
				});
		
		this.maxPriceSpinner.addChangeListener(
				new ChangeListener(){
					@Override
					public void stateChanged(ChangeEvent e)
					{
						JSpinner spinner = (JSpinner) e.getSource();
						
						myAgent.setMaxPrice(((Double)spinner.getValue()).floatValue());
					}
				});
		
		this.startingPriceSpinner.addChangeListener(
				new ChangeListener(){
					@Override
					public void stateChanged(ChangeEvent e)
					{
						JSpinner spinner = (JSpinner) e.getSource();
						
						myAgent.setCurrentPrice(((Double)spinner.getValue()).floatValue());
					}
				});
		
		this.priceStepSpinner.addChangeListener(
				new ChangeListener(){
					@Override
					public void stateChanged(ChangeEvent e)
					{
						JSpinner spinner = (JSpinner) e.getSource();
						
						myAgent.setPriceStep(((Double)spinner.getValue()).floatValue());
					}
				});
		
		this.minPriceStepSpinner.addChangeListener(
				new ChangeListener(){
					@Override
					public void stateChanged(ChangeEvent e)
					{
						JSpinner spinner = (JSpinner) e.getSource();
						
						myAgent.setMinPriceStep(((Double)spinner.getValue()).floatValue());
					}
				});
		
		this.waitingBidDurationSpinner.addChangeListener(
				new ChangeListener(){
					@Override
					public void stateChanged(ChangeEvent e)
					{
						JSpinner spinner = (JSpinner) e.getSource();
						
						myAgent.setBidWaitingDuration(((Integer)spinner.getValue()).longValue());
					}
				});
		
		this.startButton.setActionCommand(START_BUTTON_ACTION_COMMAND);
		this.startButton.addActionListener(this);
	}
	
	/**
	 * Assembles the widgets of this view and realises the layout.
	 */
	private void assemble()
	{
		// Left pane
		JPanel leftPane = this.createLeftPane();
		
		// Right Pane
		JPanel rightPane = this.createRightPane();
		
		// This frame
		Container windowPane = this.getContentPane();
		
		windowPane.setLayout(new BorderLayout(10,0));
		
		windowPane.add(leftPane, BorderLayout.WEST);
		windowPane.add(rightPane, BorderLayout.CENTER);
	}
	
	/**
	 * View assembly helper.
	 * 
	 * @return the left panel of the view.
	 */
	private JPanel createLeftPane()
	{
		JPanel leftPane = new JPanel();
		
		leftPane.setLayout(
				new BoxLayout(leftPane, BoxLayout.Y_AXIS));
		
		// Fish supply pane
		JPanel fishSupplyPanel = new JPanel();
		
		fishSupplyPanel.setBorder(
				this.createTitleBorder("Fish supply"));
		
		fishSupplyPanel.setLayout(new GridLayout(0, 2));
		
		fishSupplyPanel.add(new JLabel("Name"));
		fishSupplyPanel.add(this.fishSupplyNameTextField);
		
		// Price configuration
		JPanel priceConfigPanel = new JPanel();
		
		priceConfigPanel.setBorder(
				this.createTitleBorder("Price"));
		
		priceConfigPanel.setLayout(new GridLayout(0, 2));
		
		priceConfigPanel.add(new JLabel("Min"));
		priceConfigPanel.add(this.minPriceSpinner);
		
		priceConfigPanel.add(new JLabel("Max"));
		priceConfigPanel.add(this.maxPriceSpinner);
		
		priceConfigPanel.add(new JLabel("Step"));
		priceConfigPanel.add(this.priceStepSpinner);
		
		priceConfigPanel.add(new JLabel("Min step"));
		priceConfigPanel.add(this.minPriceStepSpinner);
		
		// Price configuration
		JPanel bidWaitingConfigPanel = new JPanel();
		
		bidWaitingConfigPanel.setBorder(
				this.createTitleBorder("Bid waiting"));
		
		bidWaitingConfigPanel.setLayout(new GridLayout(0, 2));
		
		bidWaitingConfigPanel.add(new JLabel("Timeout"));
		bidWaitingConfigPanel.add(this.waitingBidDurationSpinner);
		
		// Subscription feedback
		JPanel subscriptionsFeedbackPanel = new JPanel();
		
		subscriptionsFeedbackPanel.setBorder(
				this.createTitleBorder("Subscribers"));
		
		subscriptionsFeedbackPanel.setLayout(new GridLayout(0, 2));
		
		subscriptionsFeedbackPanel.add(new JLabel("Registered"));
		subscriptionsFeedbackPanel.add(this.subscriberCountSpinner);
		
		// Assemble
		leftPane.add(fishSupplyPanel);
		leftPane.add(priceConfigPanel);
		leftPane.add(bidWaitingConfigPanel);
		leftPane.add(subscriptionsFeedbackPanel);
		
		leftPane.add(this.startButton);
		
		return leftPane;
	}
	
	/**
	 * View assembly helper.
	 * 
	 * @return the right panel of the view.
	 */
	private JPanel createRightPane()
	{
		JPanel rightPane = new JPanel();
		
		rightPane.setLayout(
				new BoxLayout(rightPane, BoxLayout.Y_AXIS));
		
		rightPane.setBorder(
				this.createTitleBorder("Auction progress"));
		
		// Current announce price panel
		JPanel currentAnnouncePanel = new JPanel();
		
		currentAnnounceTable.setSize(currentAnnouncePanel.getWidth(), 100);
		
		currentAnnouncePanel.setBorder(
				this.createTitleBorder("Current announce"));
		
		currentAnnouncePanel.setLayout(
				new BoxLayout(currentAnnouncePanel, BoxLayout.Y_AXIS));
		
		currentAnnouncePanel.add(this.currentAnnounceTable.getTableHeader());
		currentAnnouncePanel.add(this.currentAnnounceTable);
		
		// Current announce price panel
		JPanel announceHistoryPanel = new JPanel();
		
		announceHistoryPanel.setBorder(
				this.createTitleBorder("History"));
		
		JScrollPane scrollPane = new JScrollPane(this.announceHistoryTable);
		
//		this.announceHistoryTable.setFillsViewportHeight(true);
		
		announceHistoryPanel.setLayout(
				new BoxLayout(announceHistoryPanel, BoxLayout.Y_AXIS));
		
		announceHistoryPanel.add(scrollPane);
		
		// Assemble
		rightPane.add(currentAnnouncePanel);
		rightPane.add(announceHistoryPanel);
		
		return rightPane;
	}
	
	/**
	 * Allows all panels to have the same style for titles.
	 * 
	 * @param title the title of a JPanel.
	 * 
	 * @return a title border object.
	 */
	private TitledBorder createTitleBorder(String title)
	{
		return BorderFactory.createTitledBorder(title);
	}
	
	/**
	 * Presenter for the auction progress table view (JTable).
	 * 
	 * @author Josuah Aron
	 *
	 */
	private static class SellerTableModel  extends AbstractTableModel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 8031870852112649553L;

		/** The announced prices. */
		private List<Float> prices =
				new ArrayList<Float>();
		
		/** The number of bids for the announced prices. */
		private List<Integer> bids =
				new ArrayList<Integer>();
		
		/** The names of the columns of the table. */
		private static final String [] COLUMN_NAMES= new String [] {
				"Price",
				"Bids"
		};
		
		/** The index of the column for the prices. */
		public static final int PRICE_COLUMN = 0;
		
		/** The index of the column for the number of bids. */
		public static final int BID_COUNT_COLUMN = 1;
		
		@Override
		public int getColumnCount()
		{
			return COLUMN_NAMES.length;
		}

		@Override
		public int getRowCount()
		{
			return prices.size();
		}
		
		@Override
		public String getColumnName(int column)
		{
			return SellerTableModel.COLUMN_NAMES[column];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if(columnIndex == PRICE_COLUMN)
			{
				return prices.get(rowIndex);
			}
			else
			{
				return bids.get(rowIndex);
			}
		}
		
		@Override
		public void setValueAt(Object aValue,
	              int rowIndex,
	              int columnIndex)
		{
			if(columnIndex == PRICE_COLUMN)
			{
				prices.set(rowIndex, (Float)aValue);
			}
			else
			{
				bids.set(rowIndex, (Integer)aValue);
			}
		}
		
		/**
		 * Inserts a row.
		 * 
		 * @param price the announce price.
		 * @param bidCount the initial number of bids for this announce. 
		 */
		public void addValue(float price, int bidCount)
		{
			prices.add(0,price);
			bids.add(0, bidCount);
		}
		
		/**
		 * Inserts a row.
		 * 
		 * @param price the announce price.
		 * @param bidCount the initial number of bids for this announce. 
		 */
		public void addValue(float price)
		{
			this.addValue(price, 0);
		}
	}
}

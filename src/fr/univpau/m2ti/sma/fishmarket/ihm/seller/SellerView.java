package fr.univpau.m2ti.sma.fishmarket.ihm.seller;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
	
	/** The number of bidders on the current announced price. */
	private int bidCount = 0;
	
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
	
	/** Holds the text field which inform about the current price of the auction. */
	private JLabel announcedPriceLabel;
	
	/** Holds the text field which inform about the number of bidders for the current price. */
	private JLabel bidCountLabel;
	
	/** Holds the text field which inform about the past announced prices. */
	private JLabel announcedPriceHistoryLabel;
	
	/** Holds the text field which inform about the number of bidders for the past announced prices. */
	private JLabel bidderCountHistoryLabel;
	
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
			
			this.myAgent.notifyStartAuctionCommand();
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
	public void notifyNewAnnounce(float price)
	{
		this.announcedPriceHistoryLabel.setText(
				this.announcedPriceLabel.getText() + "\n" +
						this.announcedPriceHistoryLabel.getText());
		
		this.announcedPriceLabel.setText(String.valueOf(price));
		
		this.bidderCountHistoryLabel.setText(
				this.bidCountLabel.getText() + "\n" +
						this.bidderCountHistoryLabel.getText());
		
		this.bidCount = 0;
		
		this.bidCountLabel.setText("0");
	}
	
	/**
	 * Update the UI after a new bid.
	 */
	public void notifyNewBid()
	{
		this.bidCountLabel.setText(
				String.valueOf(++this.bidCount));
	}
	
	/**
	 * 
	 * @return the value which is displayed on the field for the minimal value of the price for the auction.
	 */
	public float getMinPrice()
	{
		return (Float) this.minPriceSpinner.getValue();
	}
	
	/**
	 * 
	 * @return the value which is displayed on the field for the maximal value of the price for the auction.
	 */
	public float getMaxPrice()
	{
		return (Float) this.maxPriceSpinner.getValue();
	}
	
	/**
	 * 
	 * @return  the value which is displayed on the field for the  value of the starting price for the auction.
	 */
	public float getStartingPrice()
	{
		return (Float) this.startingPriceSpinner.getValue();
	}
	
	/**
	 * 
	 * @return  the value which is displayed on the field for the starting value of the step by which the price can be increased or decreased.
	 */
	public float getPriceStep()
	{
		return (Float) this.priceStepSpinner.getValue();
	}
	
	/**
	 * 
	 * @return  the value which is displayed on the field for the minimal value of the price step.
	 */
	public float getMinPriceStep()
	{
		return (Float) this.minPriceStepSpinner.getValue();
	}
	
	/**
	 * 
	 * @return the value which is displayed on the field for the timeout of the waiting for bids for the last announced price.
	 */
	public long getWaitBidDuration()
	{
		return (Long) this.waitingBidDurationSpinner.getValue();
	}
	
	/**
	 * Instantiates the dynamic widgets of the view.
	 */
	private void instantiateWidgets()
	{
		this.minPriceSpinner = new JSpinner(
				new SpinnerNumberModel(
						(double)this.myAgent.getMaxPrice(),
						0d,
						(double)this.myAgent.getMaxPrice()*10,
						(double)this.myAgent.getMaxPrice()/10
						)
				);
		
		this.maxPriceSpinner = new JSpinner(
				new SpinnerNumberModel(
						(double)this.myAgent.getMinPrice(),
						0d,
						(double)this.myAgent.getMinPrice()*10,
						(double)this.myAgent.getMinPrice()/10
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
		
		this.announcedPriceLabel = new JLabel();
		
		this.bidCountLabel = new JLabel();
		
		this.announcedPriceHistoryLabel = new JLabel();
		
		this.bidderCountHistoryLabel = new JLabel();
		
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
						
						myAgent.setMinPrice((float)spinner.getValue());
					}
				});
		
		this.maxPriceSpinner.addChangeListener(
				new ChangeListener(){
					@Override
					public void stateChanged(ChangeEvent e)
					{
						JSpinner spinner = (JSpinner) e.getSource();
						
						myAgent.setMaxPrice((float)spinner.getValue());
					}
				});
		
		this.startingPriceSpinner.addChangeListener(
				new ChangeListener(){
					@Override
					public void stateChanged(ChangeEvent e)
					{
						JSpinner spinner = (JSpinner) e.getSource();
						
						myAgent.setCurrentPrice((float)spinner.getValue());
					}
				});
		
		this.priceStepSpinner.addChangeListener(
				new ChangeListener(){
					@Override
					public void stateChanged(ChangeEvent e)
					{
						JSpinner spinner = (JSpinner) e.getSource();
						
						myAgent.setPriceStep((float)spinner.getValue());
					}
				});
		
		this.minPriceStepSpinner.addChangeListener(
				new ChangeListener(){
					@Override
					public void stateChanged(ChangeEvent e)
					{
						JSpinner spinner = (JSpinner) e.getSource();
						
						myAgent.setMinPriceStep((float)spinner.getValue());
					}
				});
		
		this.waitingBidDurationSpinner.addChangeListener(
				new ChangeListener(){
					@Override
					public void stateChanged(ChangeEvent e)
					{
						JSpinner spinner = (JSpinner) e.getSource();
						
						myAgent.setBidWaitingDuration((int)spinner.getValue());
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
		
		rightPane.setLayout(new GridLayout(1,2));
		
		rightPane.setBorder(
				this.createTitleBorder("Auction progress"));
		
		// Announced price panel
		JPanel announcedPricePanel = new JPanel();
		
		announcedPricePanel.setBorder(
				this.createTitleBorder("Price"));
		
		announcedPricePanel.setLayout(
				new BoxLayout(announcedPricePanel, BoxLayout.Y_AXIS));
		
		announcedPricePanel.add(this.announcedPriceLabel);
		announcedPricePanel.add(this.announcedPriceHistoryLabel);
		
		// Bid count panel
		JPanel bidCountPanel = new JPanel();
		
		bidCountPanel.setBorder(
				this.createTitleBorder("Bids"));
		
		bidCountPanel.setLayout(
				new BoxLayout(bidCountPanel, BoxLayout.Y_AXIS));
		
		bidCountPanel.add(this.bidCountLabel);
		bidCountPanel.add(this.bidderCountHistoryLabel);
		
		// Assemble
		rightPane.add(announcedPricePanel);
		rightPane.add(bidCountPanel);
		
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
}

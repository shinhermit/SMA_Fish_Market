package fr.univpau.m2ti.sma.fishmarket.ihm.seller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
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
	private int bidderCount = 0;
	
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
	private JSpinner waitBiddurationSpinner;
	
	/** Holds the text field which inform about the number of subscribers to the auction. */
	private JSpinner subscriberCountSpinner;
	
	/** Holds the text field which inform about the current price of the auction. */
	private JTextField annoucedPriceTextField;
	
	/** Holds the text field which inform about the number of bidders for the current price. */
	private JTextField bidderCountTextField;
	
	/** Holds the text field which inform about the past announced prices. */
	private JTextField announcedPriceHistoryTextField;
	
	/** Holds the text field which inform about the number of bidders for the past announced prices. */
	private JTextField bidderCountHistoryTextField;
	
	/** Holds the text field which inform about the number of bidders for the past announced prices. */
	private JButton startButton;
	
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
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		if(event.getActionCommand().equals(START_BUTTON_ACTION_COMMAND))
		{
			this.minPriceSpinner.setEnabled(false);
			this.maxPriceSpinner.setEnabled(false);
			
			this.startButton.setEnabled(false);
		}
		
		this.myAgent.notifyStartAuctionCommand();
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
		this.waitBiddurationSpinner.setValue(millis);
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
	 * @param bidderCount  the value which is to be display on the field for the number of bids for the last announced price.
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
		this.announcedPriceHistoryTextField.setText(
				this.annoucedPriceTextField.getText() + "\n" +
						this.announcedPriceHistoryTextField.getText());
		
		this.annoucedPriceTextField.setText(String.valueOf(price));
		
		this.bidderCountHistoryTextField.setText(
				this.bidderCountTextField.getText() + "\n" +
						this.bidderCountHistoryTextField.getText());
		
		this.bidderCount = 0;
		
		this.bidderCountTextField.setText("0");
	}
	
	/**
	 * Update the UI after a new bid.
	 */
	public void notifyNewBidder()
	{
		this.bidderCountTextField.setText(
				String.valueOf(++this.bidderCount));
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
		return (Long) this.waitBiddurationSpinner.getValue();
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
		
		this.waitBiddurationSpinner = new JSpinner(
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
		
		
		this.annoucedPriceTextField = new JTextField();
		
		this.bidderCountTextField = new JTextField();
		
		this.announcedPriceHistoryTextField = new JTextField();
		
		this.bidderCountHistoryTextField = new JTextField();
		
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
		
		this.waitBiddurationSpinner.addChangeListener(
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
	
	private void assemble()
	{
		
	}
}

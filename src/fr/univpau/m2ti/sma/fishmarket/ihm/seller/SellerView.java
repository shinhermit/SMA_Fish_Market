package fr.univpau.m2ti.sma.fishmarket.ihm.seller;

import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;

/**
 * The view for the seller agents.
 * 
 * @author Josuah Aron
 *
 */
public class SellerView extends JFrame
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
	private JTextField subscriberCountTextField;
	
	/** Holds the text field which inform about the current price of the auction. */
	private JTextField currentPriceTextField;
	
	/** Holds the text field which inform about the number of bidders for the current price. */
	private JTextField bidderCountTextField;
	
	/** Holds the text field which inform about the past announced prices. */
	private JTextField priceHistoryTextField;
	
	/** Holds the text field which inform about the number of bidders for the past announced prices. */
	private JTextField bidderCountHistoryTextField;
	
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
		this.subscriberCountTextField.setText(
				String.valueOf(subscriberCount));
	}
	
	/**
	 * Updates the UI after a new announce.
	 * 
	 * @param price  the value which is to be display on the field for the last announce price. 
	 */
	public void notifyNewAnnounce(float price)
	{
		this.priceHistoryTextField.setText(
				this.currentPriceTextField.getText() + "\n" +
						this.priceHistoryTextField.getText());
		
		this.currentPriceTextField.setText(String.valueOf(price));
		
		this.bidderCountHistoryTextField.setText(
				this.bidderCountTextField.getText() + "\n" +
						this.bidderCountHistoryTextField.getText());
		
		this.bidderCount = 0;
		
		this.bidderCountTextField.setText("0");
	}
	
	/**
	 * Update the UI after a new bid.
	 * 
	 * @param bidderCount  the value which is to be display on the field for the number of bids for the last announced price.
	 */
	public void notifyNewBidder(int bidderCount)
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
}

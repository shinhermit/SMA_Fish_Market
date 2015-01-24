package fr.univpau.m2ti.sma.fishmarket.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.logging.Level;
import java.util.logging.Logger;

import fr.univpau.m2ti.sma.fishmarket.behaviour.market.CreateAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.CreateAuctionSellerFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.ihm.SellerView;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;

@SuppressWarnings("serial")
/**
 * A seller agent of the fish market protocol.
 * 
 * @author Josuah Aron
 *
 */
public class SellerAgent extends Agent
{
	/** The AID of the market agent. */
	private AID marketAgentID = null;
	
	/** A user friendly name for the auction. */
	private String fishSupplyName = "";
	
	/** The minimal value for the price. */
	private float minPrice = 200f;
	
	/** The maximal value for the price. */
	private float maxPrice = 1000f;
	
	/** The current price of the fish supply. */
	private float currentPrice;
	
	/** How much the price can currently be decreased or increased. */
	private float priceStep;
	
	/** How much the price can currently be decreased or increased. */
	private float minPriceStep;
	
	/** The default value for the bid waiting duration. */
	public static final long DEFAULT_BID_WAITING_DURATION = 60*1000l; // 1 min
	
	/** The amount of time to wait for bids after an announce. */
	private long bidWaitingDuration = DEFAULT_BID_WAITING_DURATION;
	
	/** Tells whether the user decided to start the auction or not. */
	private boolean startCommandReceived = false;
	
	/** Tells whether the user decided to start the auction or not. */
	private boolean cancelCommandReceived = false;
	
	/** The view for this seller agent. */
	private SellerView myView;
	
	/** Allows logging. */
	private static final Logger LOGGER =
			Logger.getLogger(SellerAgent.class.getName());

	/**
	 * Creates a seller agent of the fish market protocol.
	 */
	public SellerAgent()
	{
		this.currentPrice = minPrice;
		this.priceStep = (maxPrice - minPrice) / 2f;
		this.minPriceStep = this.priceStep / 10f;
	}
	
	@Override
	protected void setup()
	{
		this.marketAgentID =
				this.lookupMarketAgent();
		
		this.addBehaviour(
				new CreateAuctionSellerFSMBehaviour(this));
		// RunningAuctionSellerFSMBehaviour is to be added when the creation terminates.
		
		this.myView = new SellerView(this);
		this.myView.setVisible(true);
	}
    
	/**
	 * Retrieves the AID of the market agent.
	 * @return the AID of the market agent.
	 */
    protected AID lookupMarketAgent()
    {
    	AID marketAgent = null;
    	
        DFAgentDescription marketTemplate = new DFAgentDescription();
        ServiceDescription marketSd = new ServiceDescription();
        
        // Template for searching the market agent
        marketSd.setType(MarketAgent.SERVICE_DESCIPTION);
        marketTemplate.addServices(marketSd);
        
        try
        {
            DFAgentDescription[] marketResult = DFService.search(this, marketTemplate);
            
            if(marketResult.length == 1)
            {
            	marketAgent = marketResult[0].getName();
            }
            else
            {
            	SellerAgent.LOGGER.log(
            			Level.SEVERE, null,
            			"Logic error: multiple market agents found !");
            }
        }
        catch (FIPAException fe)
        {
            SellerAgent.LOGGER.log(Level.SEVERE, null, fe);
        }
        
        return marketAgent;
    }

    /**
     * 
     * @return the AID of the market agent.
     */
	public AID getMarketAgent()
	{
		return marketAgentID;
	}

	/**
	 * 
	 * @return the minimal price for the fish supply.
	 */
	public float getMinPrice()
	{
		return minPrice;
	}

	/**
	 * 
	 * @return the maximal price for the fish supply (for termination issue).
	 */
	public float getMaxPrice()
	{
		return maxPrice;
	}

	/**
	 * 
	 * @return how much the price can currently be decreased or increased.
	 */
	public float getPriceStep()
	{
		return priceStep;
	}

	/**
	 * 
	 * @return the current price of the fish supply.
	 */
	public float getCurrentPrice()
	{
		return currentPrice;
	}

	/**
	 * 
	 * @return the minimal value under which the price step must not go.
	 */
	public float getMinPriceStep()
	{
		return minPriceStep;
	}

	/**
	 * 
	 * @param minPriceStep the minimal value under which the price step must not go.
	 */
	public void setMinPriceStep(float minPriceStep)
	{
		this.minPriceStep = Math.abs(minPriceStep);
		
		if(this.minPrice > this.priceStep)
		{
			SellerAgent.LOGGER.log(Level.WARNING,
					"Setting the min price step (" + this.minPrice +
					") to a value lower than the price step("+this.priceStep+").");
		}
	}

	/**
	 * 
	 * @param currentPrice the current price of the fish supply.
	 */
	public void setCurrentPrice(float currentPrice)
	{
		this.currentPrice =
				Math.max(this.minPrice,
						Math.min(this.maxPrice, currentPrice));
	}

	/**
	 * 
	 * @param minPrice the minimal price at which the fish supply can be sold.
	 */
	public void setMinPrice(float minPrice)
	{
		this.minPrice = Math.min(
				Math.abs(minPrice), this.maxPrice);
		
		this.setCurrentPrice(this.currentPrice);
		
		this.priceStep = (this.maxPrice - this.minPrice) / 2f;
	}

	/**
	 * 
	 * @param minPrice the maximal price at which the fish supply can be sold.
	 */
	public void setMaxPrice(float maxPrice)
	{
		this.maxPrice = Math.max(
				Math.abs(maxPrice), this.minPrice);
		
		this.setCurrentPrice(this.currentPrice);

		this.priceStep = (this.maxPrice - this.minPrice) / 2f;
	}

	/**
	 * 
	 * @param priceStep how much the price can currently be decreased or increased.
	 */
	public void setPriceStep(float priceStep)
	{
		this.priceStep = Math.abs(priceStep);
	}
	
	/**
	 * Increase the amount by which the price can currently be increased.
	 */
	public void increasePriceStep()
	{
		this.priceStep += this.priceStep / 2f;
	}

	/**
	 * Increase the amount by which the price can currently be decreased.
	 */
	public void decreasePriceStep()
	{
		this.priceStep -= this.priceStep / 2f;
	}
	
	/**
	 * Increases the price by the current price step.
	 */
	public void increasePrice()
	{
		this.currentPrice += this.priceStep;
	}
	
	/**
	 * Decreases the price by the current price step.
	 */
	public void decreasePrice()
	{
		this.currentPrice -= this.priceStep;
	}

	/**
	 * 
	 * @return the amount of time to wait for bids after an announce.
	 */
	public long getBidWaitingDuration()
	{
		return bidWaitingDuration;
	}

	/**
	 * 
	 * @param bidWaitingDuration the amount of time to wait for bids after an announce.
	 */
	public void setBidWaitingDuration(long bidWaitingDuration)
	{
		this.bidWaitingDuration = Math.abs(bidWaitingDuration);
	}
	
	/**
	 * Notifies that the user decided to start the auction.
	 */
	public void notifyStartCommand()
	{
		this.startCommandReceived = true;
		
		jade.lang.acl.ACLMessage mess = new jade.lang.acl.ACLMessage(FishMarket.Performatives.TO_SUBSCRIBE);
		
		mess.addReceiver(this.getAID());
		mess.addReceiver(CreateAuctionMarketFSMBehaviour.MESSAGE_TOPIC);
		super.send(mess);
	}
	
	/**
	 * 
	 * @return true if a start auction command has been received from the user, false otherwise.
	 */
	public boolean isStartCommandReceived()
	{
		return this.startCommandReceived;
	}
	
	/**
	 * Notifies that the user decided to cancel the auction.
	 */
	public void notifyCancelCommandReceived()
	{
		this.cancelCommandReceived = true;
	}
	
	/**
	 * 
	 * @return true if a cancel auction command has been received from the user, false otherwise.
	 */
	public boolean isCancelCommandReceived()
	{
		return this.cancelCommandReceived;
	}

	/**
	 * 
	 * @return a user friendly name for the auction.
	 */
	public String getFishSupplyName()
	{
		return fishSupplyName;
	}

	/**
	 * 
	 * @param fishSupplyName a user friendly name for the auction.
	 */
	public void setFishSupplyName(String fishSupplyName)
	{
		this.fishSupplyName = fishSupplyName;
	}
	
	/**
	 * Notifies that a new bidder agent subscribed to the auction created by this agent.
	 */
	public void notifyNewSubscriber()
	{
		this.myView.notifyNewSubscriber();
	}
}

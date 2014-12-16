package fr.univpau.m2ti.sma.fishmarket.agent;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.univpau.m2ti.sma.fishmarket.behaviour.market.AuctionManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.BidderManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.SellerManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;

@SuppressWarnings("serial")
/**
 * The Market Agent of the fish market protocol.
 * 
 * @author Josuah Aron
 *
 */
public class MarketAgent extends Agent
{
	/** Registers the last auction creation request. */
	private Auction auctionCreationRequest;
	
	/** The set of registered auction. */
	private Set<Auction> auctions;
	
	/** Tells whether this agent has ended it's task or not. */
	private boolean isDone = false;
	
	/** Allows logging. */
	private static final Logger LOGGER =
			Logger.getLogger(MarketAgent.class.getName());
	
	/** The service offered by this agent (for registration to the DRF). */
	public static final String SERVICE_DESCIPTION = "FISH_MARKET_SERVICE";
	
	/**
	 * Creates a market agent for a fish market protocol.
	 */
	public MarketAgent()
	{
		this.auctions = new HashSet<Auction>();
	}
	
	@Override
	protected void setup()
	{
		// Register service to DF
        try
        {
            _register2DF();
        }
        catch (FIPAException ex)
        {
            MarketAgent.LOGGER.log(Level.SEVERE, null, ex);
        }
        
        // Add behaviors
        this.addBehaviour(new BidderManagementBehaviour(this));
        this.addBehaviour(new SellerManagementBehaviour(this));
        this.addBehaviour(new AuctionManagementBehaviour(this));
	}
	
	@Override
	protected void takeDown()
	{
		// De-register service from DF
        try
        {
            DFService.deregister(this);
        }
        catch (FIPAException fe)
        {
            MarketAgent.LOGGER.log(Level.SEVERE, null, fe);
        }
        
        super.takeDown();
	}
	
	/**
	 * Defines whether this agent reached a end state.
	 * 
	 * @param isDone 
	 * 		true to tag this agent as a ended agent (which reached a ending state in his FSMBehaviour).
	 */
	public void setIsDone(boolean isDone)
	{
		this.isDone = isDone;
	}
	
	/**
	 * Tells whether this agent reached a end state or not.
	 * 
	 * @return true if this agent reached a end state in his FSMBehaviour, false otherwise.
	 */
	public boolean isDone()
	{
		return this.isDone;
	}
	
	/**
	 * Used in the auction creation behavior, register the request
	 * for access by the other states of the behavior.
	 * 
	 * @param auction the requested auction.
	 */
	public void registerAuctionCreationRequest(Auction auction)
	{
		this.auctionCreationRequest = auction;
	}
	
	/**
	 * Used in the auction creation behavior, provides the last request auction creation.
	 * 
	 * @return the last requested auction.
	 */
	public Auction getAuctionCreationRequest()
	{
		return this.auctionCreationRequest;
	}
	
	/**
	 * Tells whether an auction is already registered in this market agent or not.
	 * 
	 * @param auction
	 * 		the checked auction.
	 * @return true if the auction is already registered on this market agent, false otherwise.
	 */
	public boolean isRegisteredAuction(Auction auction)
	{
		if(auction == null)
		{
			return false;
		}
		
		else if(this.auctions.contains(auction))
		{
			return true;
		}
		
		else
		{
			return false;
		}
	}
	
	public void registerAuction(Auction auction)
	{
		this.auctions.add(auction);
	}
    
    /**
     * Add a description of this agent's services to the DF.
     * 
     * @throws FIPAException when an error occurs.
     */
    private void _register2DF()
    		throws FIPAException
    {
        ServiceDescription sd = new ServiceDescription();
        sd.setType(MarketAgent.SERVICE_DESCIPTION);
        sd.setName(this.getAID().getName());
        
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(this.getAID());
        
        dfd.addServices(sd);
        DFService.register(this, dfd);
    }
}

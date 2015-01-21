package fr.univpau.m2ti.sma.fishmarket.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	/** The set of registered auction. */
	private Map<Auction, Set<AID>> auctions =
			new HashMap<Auction, Set<AID>>();
	
	/** Associate an auction with it's seller agent. */
	private Map<Auction, AID> sellers =
			new HashMap<Auction, AID>();
	
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
	{}
	
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
        
        // Add behaviours
        this.addBehaviour(new BidderManagementBehaviour(this));
        this.addBehaviour(new SellerManagementBehaviour(this));
        // Auction management behaviour are add by SellerManagementBehaviour
        // sub-behaviour (confirm auction registration behaviour)
	}
	
	@Override
	protected void takeDown()
	{
		// Unregister service from DF
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
		
		else if(this.auctions.keySet().contains(auction))
		{
			return true;
		}
		
		else
		{
			return false;
		}
	}
	
	/**
	 * Registers a new auction.
	 * 
	 * @param auction the auction which is to be registered.
	 * @param seller the AID of the seller in this auction.
	 */
	public void registerAuction(Auction auction, AID seller)
	{
		this.auctions.put(auction, new HashSet<AID>());
		this.sellers.put(auction, seller);
	}
	
	/**
	 * 
	 * @return the list registered auction.
	 */
	public Set<Auction> getRegisteredAuctions()
	{
		return new HashSet<Auction>(this.auctions.keySet());
	}
	
	/**
	 * Retrieves a registered auction by the AID the the seller who created it.
	 * 
	 * @param auctionID the ID of the auction.
	 * 
	 * @return the looked-up auction if found, null otherwise.
	 */
	public Auction findAuction(String auctionID)
	{
		Auction auction = null;
		boolean found = false;
		
		Iterator<Auction> it =
				this.auctions.keySet().iterator();
		
		while(it.hasNext() && !found)
		{
			auction = it.next();
			
			found = auction.getID().equals(auctionID);
		}
		
		if(!found)
		{
			auction = null;
		}
		
		return auction;
	}
	
	/**
	 * 
	 * @param auctionID the ID of an auction.
	 * 
	 * @return the seller in this auction.
	 */
	public AID getSeller(String auctionID)
	{
		return this.sellers.get(new Auction(auctionID));
	}
	
	/**
	 * Adds an agent to the set of registered bidder for an auction.
	 * 
	 * @param auctionID the AID of the auction.
	 * @param bidderAID the AID of the bidder agent who wants to subscribe to the auction.
	 */
	public void addSuscriber(String auctionID, AID bidderAID)
	{
		Set<AID> suscribers =
				this.auctions.get(new Auction(auctionID));
		
		suscribers.add(bidderAID);
	}
	
	/**
	 * Tells whether a bidder agent is a subscriber on an auction.
	 * 
	 * @param auctionID the ID of the auction.
	 * @param bidderAID the AID of the bidder agent which is checked.
	 * 
	 * @return true if the bidder agent is found in the subscriber list of the auction represented by it's seller AID.
	 */
	public boolean isSuscriber(String auctionID, AID bidderAID)
	{
		Set<AID> suscribers =
				this.auctions.get(new Auction(auctionID));
		
		return suscribers.contains(bidderAID);
	}
	
	/**
	 * Provides the subscribers of an auction.
	 * 
	 * @param auctionID the AID of the seller who created the auction (uniquely identifies the auction).
	 * 
	 * @return the list of subscribers for the given auction.
	 */
	public Set<AID> getSubscribers(String auctionID)
	{
		return this.auctions.get(new Auction(auctionID));
	}
	
	/**
	 * Defines the status of an auction.
	 * 
	 * @param auctionID  the ID of the auction.
	 * @param status the new status of the auction (public static fields of class Auction).
	 */
	public void setAuctionStatus(String auctionID, int status)
	{
		Auction auction = this.findAuction(auctionID);
		
		if(auction != null)
		{
			auction.setStatus(status);
		}
	}
	
	/**
	 * Defines the status of an auction.
	 * 
	 * @param auctionID  the ID of the auction.
	 * @param status the new status of the auction (public static fields of class Auction).
	 */
	public void setAuctionPrice(String auctionID, float price)
	{
		Auction auction = this.findAuction(auctionID);
		
		if(auction != null)
		{
			auction.setCurrentPrice(price);
		}
	}
	
	/**
	 * 
	 * @param auctionID an auction that we want to delete.
	 */
	public void deleteAuction(String auctionID)
	{
		this.auctions.remove(new Auction(auctionID));
		this.sellers.remove(new Auction(auctionID));
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

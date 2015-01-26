package fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.market.states;

import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.SubscribeToAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;
import fr.univpau.m2ti.sma.fishmarket.protocol.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
/**
 * A behaviour which is to be associated with a state of the market agent's FSM behaviour.
 * 
 * @author Josuah Aron
 *
 */
public class ProvideAuctionListMarketBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private SubscribeToAuctionMarketFSMBehaviour myFSM;
	
	/** Allows logging. */
	private static final Logger LOGGER =
			Logger.getLogger(ProvideAuctionListMarketBehaviour.class.getName());
	
	/**
	 * Creates a behaviour which is to be associated with a MarketAgent FSMBehaviour's state.
	 * 
	 * @param myMarketAgent
	 * 			the market agent to which the composite FSM behaviour
	 * 			(which contains the state to which this behaviour is associated) is added.
	 * @param myFSM the FSM behaviour of which this behaviour represents a state.
	 */
	public ProvideAuctionListMarketBehaviour(
			MarketAgent myMarketAgent,
			SubscribeToAuctionMarketFSMBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
	}

	@Override
	public void action()
	{
		// Filter auctions
		HashSet<Auction> runninAuctions =
				new HashSet<Auction>();
		
		for(Auction auction : ((MarketAgent)myAgent).getRegisteredAuctions())
		{
			if(auction.getStatus() != Auction.STATUS_OVER
					&& auction.getStatus() != Auction.STATUS_CANCELLED)
			{
				runninAuctions.add(auction);
			}
		}
		
		// Provide auction list
		ACLMessage reply =
				this.myFSM.getRequest().createReply();
		
		reply.setPerformative(
				FishMarket.Performatives.TO_PROVIDE);
		
		// Add topic
		reply.addReceiver(SubscribeToAuctionMarketFSMBehaviour.MESSAGE_TOPIC);
		
		// Set content and send
		try
		{
			reply.setContentObject(runninAuctions);
			
			myAgent.send(reply);
		}
		catch (IOException e)
		{
			ProvideAuctionListMarketBehaviour.LOGGER.log(Level.SEVERE, null, e);
		}
	}
}

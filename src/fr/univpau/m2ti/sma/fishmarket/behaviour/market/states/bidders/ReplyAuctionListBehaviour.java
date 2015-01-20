package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.bidders;

import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.BidderManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class ReplyAuctionListBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private BidderManagementBehaviour myFSM;
	
	/** Allows logging. */
	private static final Logger LOGGER =
			Logger.getLogger(ReplyAuctionListBehaviour.class.getName());
	
	/**
	 * Creates a behaviour which is to be associated with a MarketAgent FSMBehaviour's state.
	 * 
	 * @param myMarketAgent
	 * 			the market agent to which the composite FSM behaviour
	 * 			(which contains the state to which this behaviour is associated) is added.
	 * @param myFSM the FSM behaviour of which this behaviour represents a state.
	 */
	public ReplyAuctionListBehaviour(
			MarketAgent myMarketAgent,
			BidderManagementBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
	}

	@Override
	public void action()
	{
		
		ACLMessage reply = this.myFSM.getRequest().createReply();
		
		try
		{
			reply.setContentObject(
					(HashSet<Auction>)
					((MarketAgent)myAgent).getRegisteredAuctions());
			
			myAgent.send(reply);
		}
		catch (IOException e)
		{
			ReplyAuctionListBehaviour.LOGGER.log(Level.SEVERE, null, e);
		}
	}
}

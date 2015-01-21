package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.bidders;

import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.BidderManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
/**
 * A behaviour which is to be associated with a state of the market agent's FSM behaviour.
 * 
 * @author Josuah Aron
 *
 */
public class ProvideAuctionListBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private BidderManagementBehaviour myFSM;
	
	/** Allows logging. */
	private static final Logger LOGGER =
			Logger.getLogger(ProvideAuctionListBehaviour.class.getName());
	
	/**
	 * Creates a behaviour which is to be associated with a MarketAgent FSMBehaviour's state.
	 * 
	 * @param myMarketAgent
	 * 			the market agent to which the composite FSM behaviour
	 * 			(which contains the state to which this behaviour is associated) is added.
	 * @param myFSM the FSM behaviour of which this behaviour represents a state.
	 */
	public ProvideAuctionListBehaviour(
			MarketAgent myMarketAgent,
			BidderManagementBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
	}

	@Override
	public void action()
	{
		ACLMessage reply =
				this.myFSM.getRequest().createReply();
		
		reply.setPerformative(
				FishMarket.Performatives.TO_PROVIDE);
		
		try
		{
			reply.setContentObject(
					(HashSet<Auction>)
					((MarketAgent)myAgent).getRegisteredAuctions());
			
			myAgent.send(reply);
		}
		catch (IOException e)
		{
			ProvideAuctionListBehaviour.LOGGER.log(Level.SEVERE, null, e);
		}
	}
}

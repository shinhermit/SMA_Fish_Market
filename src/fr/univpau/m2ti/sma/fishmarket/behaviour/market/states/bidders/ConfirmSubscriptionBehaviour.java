package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.bidders;

import java.util.logging.Level;
import java.util.logging.Logger;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.BidderManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
/**
 * A behaviour which is to be associated with a state of the marker agent's FSM behaviour.
 * 
 * @author Josuah Aron
 *
 */
public class ConfirmSubscriptionBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private BidderManagementBehaviour myFSM;
	
	/** Allows logging. */
	private static final Logger LOGGER =
			Logger.getLogger(ConfirmSubscriptionBehaviour.class.getName());
	
	/**
	 * Creates a behaviour which is to be associated with a MarketAgent FSMBehaviour's state.
	 * 
	 * @param myMarketAgent
	 * 			the market agent to which the composite FSM behaviour
	 * 			(which contains the state to which this behaviour is associated) is added.
	 * @param myFSM the FSM behaviour of which this behaviour represents a state.
	 */
	public ConfirmSubscriptionBehaviour(
			MarketAgent myMarketAgent,
			BidderManagementBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
	}

	@Override
	public void action()
	{
		MarketAgent myMarketAgent =
				(MarketAgent) super.myAgent;
		
		ACLMessage mess = this.myFSM.getRequest();
		ACLMessage reply = mess.createReply();
		AID sellerAID = null;
		AID bidderAID = mess.getSender();
		
		// Register subscription
		try
		{
			sellerAID = (AID) mess.getContentObject();
		}
		catch (UnreadableException e)
		{
			ConfirmSubscriptionBehaviour.LOGGER.log(Level.SEVERE, null, e);
		}
		
		myMarketAgent.addSuscriber(sellerAID, bidderAID);
		
		// Reply confirm
		reply.setPerformative(
				FishMarket.Performatives.CONFIRM_BIDDER_SUBSCRIPTION);
		
		try
		{
			reply.setContentObject((AID)mess.getContentObject());
		}
		catch (Exception e)
		{
			ConfirmSubscriptionBehaviour.LOGGER.log(Level.SEVERE, null, e);
		}
		
		super.myAgent.send(reply);
	}
}

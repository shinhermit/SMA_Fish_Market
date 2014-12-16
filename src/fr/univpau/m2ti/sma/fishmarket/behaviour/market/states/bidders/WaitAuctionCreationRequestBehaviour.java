package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.bidders;

import java.util.logging.Level;
import java.util.logging.Logger;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
public class WaitAuctionCreationRequestBehaviour extends Behaviour
{
	/** Tells whether this behavior is over or not. Over when an auction creation request has been received.*/
	private boolean isDone = false;
	
	/** Allows logging. */
	private static final Logger LOGGER =
			Logger.getLogger(MarketAgent.class.getName());
	
	/**
	 * Creates a behavior which is to be associated with a MarketAgent FSMBehaviour's state.
	 * @param myMarketAgent the market agent to which the composite FSM behavior
	 * (which contains the state to which this behavior is associated) is added.
	 */
	public WaitAuctionCreationRequestBehaviour(MarketAgent myMarketAgent)
	{
	}
	
	@Override
	public void action()
	{
		// Wait that myAgent receives message
		this.block();
		
		// Create message filter (template)
		MessageTemplate filter = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
		
		// Receive messages
		ACLMessage mess = myAgent.receive(filter);
		
		try
		{
			Object content = mess.getContentObject();
			
			if(content instanceof Auction)
			{
				((MarketAgent)myAgent).registerAuctionCreationRequest((Auction)content);
				
				this.isDone = true;
			}
		} catch (UnreadableException e)
		{
			WaitAuctionCreationRequestBehaviour.LOGGER.log(Level.WARNING, null, e);
		}
	}

	@Override
	public boolean done()
	{
		return isDone;
	}
}

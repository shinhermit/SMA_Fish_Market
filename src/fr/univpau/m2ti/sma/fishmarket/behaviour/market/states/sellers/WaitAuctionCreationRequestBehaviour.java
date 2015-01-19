package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.sellers;

import java.util.logging.Level;
import java.util.logging.Logger;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.SellerManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
/**
 * A behaviour which is to be associated with a state of the marker agent's FSM behaviour.
 * 
 * @author Josuah Aron
 *
 */
public class WaitAuctionCreationRequestBehaviour extends Behaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private SellerManagementBehaviour myFSM;
	
	/** Tells whether this behaviour is over or not. Over when an auction creation request has been received.*/
	private boolean isDone;
	
	/** Allows logging. */
	private static final Logger LOGGER =
			Logger.getLogger(MarketAgent.class.getName());
	
	/**
	 * Creates a behaviour which is to be associated with a MarketAgent FSMBehaviour's state.
	 * 
	 * @param myMarketAgent
	 * 			the market agent to which the composite FSM behaviour
	 * 			(which contains the state to which this behaviour is associated) is added.
	 */
	public WaitAuctionCreationRequestBehaviour(
			MarketAgent myMarketAgent,
			SellerManagementBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void action()
	{
		// Wait that myAgent receives message
		this.block();
		
		this.isDone = false;
		
		// Create message filter (template)
		MessageTemplate filter =
				MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
		
		// Receive messages
		ACLMessage mess = myAgent.receive(filter);
		
		try
		{
			Object content = mess.getContentObject();
			
			if(content instanceof Auction)
			{
				this.myFSM.registerAuctionCreationRequest(
						(Auction)content);
				
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
		return isDone || ((MarketAgent)myAgent).isDone();
	}
	
	@Override
	public int onEnd()
	{
		return ((MarketAgent)myAgent).isDone() ?
				SellerManagementBehaviour.TRANSITION_USER_TERMINATE :
					SellerManagementBehaviour.TRANSITION_AUCTION_REQUEST_RECEIVED;
	}
}

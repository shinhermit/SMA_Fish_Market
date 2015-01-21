package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.sellers;

import java.util.logging.Level;
import java.util.logging.Logger;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.SellerManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
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
public class WaitRegistrationRequestBehaviour extends Behaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private SellerManagementBehaviour myFSM;
	
	/** Tells whether this behaviour is over or not. Over when an auction creation request has been received.*/
	private boolean isDone;
	
	/** Allows logging. */
	private static final Logger LOGGER =
			Logger.getLogger(WaitRegistrationRequestBehaviour.class.getName());

	/** Allows filtering incoming messages. */
	private static final MessageTemplate MESSAGE_FILTER =
				MessageTemplate.and(
					MessageTemplate.MatchTopic(
							SellerManagementBehaviour.MESSAGE_TOPIC),
					MessageTemplate.or(
							MessageTemplate.MatchPerformative(
									FishMarket.Performatives.TO_PROVIDE),
							MessageTemplate.MatchPerformative(
									FishMarket.Performatives.TO_SUBSCRIBE)));
	
	/**
	 * Creates a behaviour which is to be associated with a MarketAgent FSMBehaviour's state.
	 * 
	 * @param myMarketAgent
	 * 			the market agent to which the composite FSM behaviour
	 * 			(which contains the state to which this behaviour is associated) is added.
	 * @param myFSM the FSM behaviour of which this behaviour represents a state.
	 */
	public WaitRegistrationRequestBehaviour(
			MarketAgent myMarketAgent,
			SellerManagementBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void action()
	{
		this.isDone = false;
		
		// Delete any previous request
		this.myFSM.setRequest(null);
		
		// Wait that myAgent receives message
		this.block();
		
		// Receive messages
		ACLMessage mess = myAgent.receive(
				WaitRegistrationRequestBehaviour.MESSAGE_FILTER);
		
		if(mess != null)
		{
			try
			{
				Object content = mess.getContentObject();
				
				if(content != null)
				{
					if(content instanceof Auction)
					{
						this.myFSM.setRequest(mess);
						
						this.isDone = true;
					}
					else
					{
						ACLMessage reply = mess.createReply();
						reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
						
						super.myAgent.send(reply);
					}
				}
			} catch (UnreadableException e)
			{
				WaitRegistrationRequestBehaviour.LOGGER.log(Level.WARNING, null, e);
			}
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

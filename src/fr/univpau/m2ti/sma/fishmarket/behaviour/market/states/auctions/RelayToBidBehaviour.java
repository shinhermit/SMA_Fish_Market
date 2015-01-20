package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.auctions;

import java.util.logging.Level;
import java.util.logging.Logger;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.AuctionManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
/**
 * A behaviour which is to be associated with a state of the market agent's FSM behaviour.
 * 
 * @author Josuah Aron
 *
 */
public class RelayToBidBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private AuctionManagementBehaviour myFSM;
	
	/** The transition which is to be activated next. */
	private int transition;
	
	/** Allows filtering incoming messages. */
	private final MessageTemplate messageFilter;
	
	/** Allows logging. */
	private static final Logger LOGGER =
			Logger.getLogger(RelayToBidBehaviour.class.getName());
	
	/**
	 * Creates a behaviour which is to be associated with a MarketAgent FSMBehaviour's state.
	 * 
	 * @param myMarketAgent
	 * 			the market agent to which the composite FSM behaviour
	 * 			(which contains the state to which this behaviour is associated) is added.
	 * @param myFSM the FSM behaviour of which this behaviour represents a state.
	 */
	public RelayToBidBehaviour(
			MarketAgent myMarketAgent,
			AuctionManagementBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
		
		this.messageFilter = this.createFilter();
	}

	@Override
	public void action()
	{
		// Wait messages in order to choose a transition
		boolean received = this.receive(
				AuctionManagementBehaviour.CANCELLETION_WAIT_DELAY);
		
		if(received)
		{
			this.transition =
						AuctionManagementBehaviour.TRANSITION_AUCTION_CANCELLED_RECEIVED;
		}
		
		// relay
		else
		{
			ACLMessage toRelay = this.myFSM.getRequest();
			
			toRelay.clearAllReceiver();
			
			// Put back message topic
			toRelay.addReceiver(
					AuctionManagementBehaviour.MESSAGE_TOPIC);
			
			// set seller agent as subscriber
			toRelay.addReceiver(this.myFSM.getSeller());
			
			// Send
			super.myAgent.send(toRelay);
			
			// Select transition
			this.transition =
					AuctionManagementBehaviour.TRANSITION_TO_BID_RELAYED;
			
			// Delete request
			this.myFSM.setRequest(null);
		}
	}
	
	@Override
	public int onEnd()
	{
		return this.transition;
	}
	
	/**
	 * @param millis the amount of time to wait for a message.
	 * 
	 * @return true if a message has actually been received, false otherwise.
	 */
	private boolean receive(final long millis)
	{
		boolean received = false;
		
		// Wait that myAgent receives message
		this.block(millis);
		
		// Receive messages
		ACLMessage mess = myAgent.receive(
				this.messageFilter);
		
		if(mess != null)
		{
			this.myFSM.setRequest(mess);
			
			received = true;
		}
		
		return received;
	}

	/**
	 * Creates a filter for incoming messages.
	 * 
	 * @return a message template which can be used to filter incoming messages.
	 */
	private MessageTemplate createFilter()
	{
		MessageTemplate filter = null;
		
		// Create message filter
		TopicManagementHelper topicHelper;
		try
		{
			topicHelper = (TopicManagementHelper) myAgent.getHelper(
					TopicManagementHelper.SERVICE_NAME);
			
			final AID topic =
					topicHelper.createTopic(
							FishMarket.Topics.TOPIC_AUCTION_MANAGEMENT);
			
			topicHelper.register(topic);
			
			filter = MessageTemplate.and(
					MessageTemplate.MatchTopic(topic),
					MessageTemplate.MatchPerformative(
							FishMarket.Performatives.AUCTION_CANCELLED));
		}
		catch (ServiceException e)
		{
			RelayToBidBehaviour.LOGGER.log(Level.SEVERE, null, e);
		}
		
		return filter;
	}
}

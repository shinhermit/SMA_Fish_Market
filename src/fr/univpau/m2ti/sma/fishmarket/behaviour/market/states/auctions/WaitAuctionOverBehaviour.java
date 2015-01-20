package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.auctions;

import java.util.logging.Level;
import java.util.logging.Logger;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.AuctionManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.BidderManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.SellerManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.Behaviour;
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
public class WaitAuctionOverBehaviour extends Behaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private AuctionManagementBehaviour myFSM;
	
	/** Tells whether this behaviour is over or not. Over when an auction creation request has been received.*/
	private boolean isDone;
	
	/** Will hold the selected transition among those to the next possible states. */
	private int transition;
	
	/** Allows logging. */
	private static final Logger LOGGER =
			Logger.getLogger(WaitAuctionOverBehaviour.class.getName());
	
	/** Allows filtering incoming messages. */
	private final MessageTemplate messageFilter;
	
	/**
	 * Creates a behaviour which is to be associated with a MarketAgent FSMBehaviour's state.
	 * 
	 * @param myMarketAgent
	 * 			the market agent to which the composite FSM behaviour
	 * 			(which contains the state to which this behaviour is associated) is added.
	 * @param myFSM the FSM behaviour of which this behaviour represents a state.
	 */
	public WaitAuctionOverBehaviour(
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
		this.isDone = false;
		
		// Delete any previous request
		this.myFSM.setRequest(null);
		
		// Wait that myAgent receives message
		this.block();
		
		// Receive messages
		ACLMessage mess = myAgent.receive(
				this.messageFilter);
		
		if(mess != null)
		{
			this.myFSM.setRequest(mess);
			
			this.isDone = true;
			
			if(mess.getPerformative() ==
					FishMarket.Performatives.REQUEST_AUCTION_LIST)
			{
				this.transition =
						BidderManagementBehaviour
						.TRANSITION_AUCTION_LIST_REQUEST_RECEIVED;
			}
			else
			{
				this.transition =
						BidderManagementBehaviour
						.TRANSITION_BIDDER_SUBSCRIPTION_REQUEST_RECEIVED;
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
					this.transition;
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
					MessageTemplate.or(
							MessageTemplate.MatchPerformative(
									FishMarket.Performatives.REQUEST_AUCTION_LIST),
							MessageTemplate.MatchPerformative(
									FishMarket.Performatives.REQUEST_BIDDER_SUBSCRIPTION)));
		}
		catch (ServiceException e)
		{
			WaitAuctionOverBehaviour.LOGGER.log(Level.SEVERE, null, e);
		}
		
		return filter;
	}
}

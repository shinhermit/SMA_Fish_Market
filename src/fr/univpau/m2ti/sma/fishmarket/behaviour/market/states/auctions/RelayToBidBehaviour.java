package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.auctions;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.RunningAuctionManagementFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
/**
 * A behaviour which is to be associated with a state of the market agent's FSM behaviour.
 * 
 * @author Josuah Aron
 *
 */
public class RelayToBidBehaviour extends WakerBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private RunningAuctionManagementFSMBehaviour myFSM;
	
	/** The transition which is to be activated next. */
	private int transition;
	
	/** Allows filtering incoming messages. */
	private final MessageTemplate messageFilter;
	
	/** The amount of time to wait for incoming messages before relaying a <i>to_bid</i>.*/
	public static final long WAIT_TO_CANCEL_DURATION = 500; // 0.5 sec
	
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
			RunningAuctionManagementFSMBehaviour myFSM)
	{
		super(myMarketAgent, WAIT_TO_CANCEL_DURATION);
		
		this.myFSM = myFSM;
		
		this.messageFilter = this.createFilter();
	}

	@Override
	public void onWake()
	{
		if(this.cancelReceived(
				WAIT_TO_CANCEL_DURATION))
		{
			// DEBUG
			System.out.println("Market: relaying to cancel !");
			
			this.transition =
						RunningAuctionManagementFSMBehaviour.TRANSITION_TO_CANCEL;
		}
		
		else
		{
			// DEBUG
			System.out.println("Market: relaying to bid !");
			
			ACLMessage request = this.myFSM.getRequest();
			
			this.myFSM.addBidder(request.getSender());
			
			// RELAY
			ACLMessage toRelay = new ACLMessage(
					request.getPerformative());
			
			// set seller agent as receiver
			toRelay.addReceiver(this.myFSM.getSeller());
			
			// Message topic
			toRelay.addReceiver(
					RunningAuctionManagementFSMBehaviour.MESSAGE_TOPIC);
			
			// Conversation ID
			toRelay.setConversationId(
					request.getConversationId());
			
			// Content (price)
			toRelay.setContent(request.getContent());
			
			// Send
			super.myAgent.send(toRelay);
			
			// Select transition
			this.transition =
					RunningAuctionManagementFSMBehaviour.TRANSITION_TO_WAIT_REP_BID;
			
			// DEBUG
			System.out.println("Market: setting transition to wwait rep bid !");
			
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
	private boolean cancelReceived(final long millis)
	{
		boolean received = false;
		
		// Wait that myAgent receives message
		this.block(millis);
		
		// Receive messages
		ACLMessage mess = super.myAgent.receive(
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
		return MessageTemplate.and(
				this.myFSM.getMessageFilter(),
				MessageTemplate.MatchPerformative(
						FishMarket.Performatives.TO_CANCEL));
	}
}

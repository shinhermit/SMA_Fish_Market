package fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states.auction;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.AuctionManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.FishSellerBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class WaitMoreBidBehaviour extends WakerBehaviour
{
	/** The FSM behaviour to which this behaviour is to be added. */
	private FishSellerBehaviour myFSM;
	
	/** The selected transition to the next state. */
	private int transition =
			FishSellerBehaviour.TRANSITION_TO_ANNOUNCE;
	
	/** Allows filtering incoming messages. */
	private final MessageTemplate messageFilter;
	
	/** The time to wait for more bid. */
	public static final long MORE_BID_WAIT_DURATION = 20000l; // 20 sec
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public WaitMoreBidBehaviour(
			SellerAgent mySellerAgent,
			FishSellerBehaviour myFSM)
	{
		super(mySellerAgent, MORE_BID_WAIT_DURATION);
		
		this.myFSM = myFSM;
		
		this.messageFilter =
				this.createMessageFilter();
	}
	
	@Override
	public void onWake()
	{
		ACLMessage mess = null;
		
		SellerAgent mySellerAgent =
				(SellerAgent)super.myAgent;
		
		// Receive messages
		do
		{
			mess = myAgent.receive(
					this.messageFilter);
		}
		while(mess != null);
		
		// Select transition
		float nextPrice = mySellerAgent.getCurrentPrice() + mySellerAgent.getPriceStep();
		float maxPrice = mySellerAgent.getMaxPrice();
		
		float nextPriceStep = mySellerAgent.getPriceStep() / 2f;
		float minPriceStep = mySellerAgent.getMinPriceStep();
		
		boolean repBidOk = false;
		this.transition =
				FishSellerBehaviour.TRANSITION_TO_ANNOUNCE;
		
		if(nextPrice < maxPrice)
		{
			mySellerAgent.increasePrice();
		}
		else if(nextPriceStep >= minPriceStep)
		{
			mySellerAgent.decreasePriceStep();
			mySellerAgent.decreasePrice();
		}
		else
		{
			repBidOk = true;
			
			this.transition =
					FishSellerBehaviour.TRANSITION_TO_ATTRIBUTE;
		}
		
		// send rep_bid
		ACLMessage reply = new ACLMessage(
				FishMarket.Performatives.REP_BID);
		
		reply.clearAllReceiver();
		
		// Set topic
		reply.addReceiver(
				AuctionManagementBehaviour.MESSAGE_TOPIC);
		
		// Set conversation id
		reply.setConversationId(
				this.myFSM.getConversationId());
		
		// Receiver
		reply.addReceiver(
				mySellerAgent.getMarketAgent());
		
		// Add selected bidder AID
		reply.setContent(String.valueOf(repBidOk));
		
		// Send
		mySellerAgent.send(reply);
	}

	@Override
	public int onEnd()
	{
		return this.transition;
	}
	
	/**
	 * 
	 * @return the message filter to use in this behaviour.
	 */
	private MessageTemplate createMessageFilter()
	{
		return MessageTemplate.and(
				this.myFSM.getMessageFilter(),
				MessageTemplate.MatchPerformative(
						FishMarket.Performatives.TO_BID));
	}
}

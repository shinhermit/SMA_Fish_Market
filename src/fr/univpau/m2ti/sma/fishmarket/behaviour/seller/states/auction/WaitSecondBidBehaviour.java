package fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states.auction;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.RunningAuctionManagementFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.RunningAuctionSellerFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class WaitSecondBidBehaviour extends WakerBehaviour
{
	/** The FSM behaviour to which this behaviour is to be added. */
	private RunningAuctionSellerFSMBehaviour myFSM;
	
	/** The selected transition to the next state. */
	private int transition;
	
	/** The time to wait for the first bid. */
	private static final long WAIT_SECOND_BID_CYCLE_DURATION = 1000l; // 1 sec
	
	/** The time to wait for the first bid. */
	private static final int WAIT_SECOND_MAX_CYCLE_COUNT = 10*60; // to reach 10 min
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public WaitSecondBidBehaviour(
			SellerAgent mySellerAgent,
			RunningAuctionSellerFSMBehaviour myFSM)
	{
		super(mySellerAgent, WAIT_SECOND_BID_CYCLE_DURATION);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void onWake()
	{
		this.myFSM.notifyNewWaitCycle();
		
		// DEBUG
		System.out.println("Seller: checking messages for second bid.");
		
		// Receive messages
		ACLMessage mess = myAgent.receive(
				this.getMessageFilter());
		
		if(mess != null)
		{
			this.myFSM.resetWaitCycleCount();
			
			this.transition =
					RunningAuctionSellerFSMBehaviour.TRANSITION_TO_WAIT_MORE_BID;
			
			// DEBUG
			System.out.println("Seller: setting transition to wait more bid.");
		}
		else if(this.myFSM.getWaitCycleCount() <
						WAIT_SECOND_MAX_CYCLE_COUNT)
		{
			// DEBUG
			System.out.println("Seller: setting transition to wait second bid.");
			
			// Continue to wait
			this.transition =
					RunningAuctionSellerFSMBehaviour.TRANSITION_TO_WAIT_SECOND_BID;
			
			this.reset(WAIT_SECOND_BID_CYCLE_DURATION);
		}
		else
		{
			this.myFSM.resetWaitCycleCount();
			
			// DEBUG
			System.out.println("Seller: setting transition to attribute.");
			
			this.transition =
					RunningAuctionSellerFSMBehaviour.TRANSITION_TO_ATTRIBUTE;
			
			SellerAgent mySellerAgent =
					(SellerAgent)super.myAgent;
			
			// send rep_bid(OK)
			ACLMessage reply = new ACLMessage(
					FishMarket.Performatives.REP_BID);
			
			// Receiver
			reply.addReceiver(
					mySellerAgent.getMarketAgent());
			
			// Set topic
			reply.addReceiver(
					RunningAuctionManagementFSMBehaviour.MESSAGE_TOPIC);
			
			// Set conversation id
			reply.setConversationId(
					this.myFSM.getConversationId());
			
			// Add price and send
			reply.setContent(String.valueOf(true));
			
			// Send
			mySellerAgent.send(reply);
		}
	}
	
	@Override
	public int onEnd()
	{
		return this.transition;
	}
	
	/**
	 * 
	 * 
	 * @return the message filter to use in this behaviour.
	 */
	private MessageTemplate getMessageFilter()
	{
		SellerAgent mySellerAgent =
				(SellerAgent) super.myAgent;
		
		return MessageTemplate.and(
				this.myFSM.getMessageFilter(),
				MessageTemplate.and(
						MessageTemplate.MatchContent(
								String.valueOf(mySellerAgent.getCurrentPrice())),
						MessageTemplate.MatchPerformative(
								FishMarket.Performatives.TO_BID)));
	}
}

package fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states.auction;

import java.util.logging.Level;
import java.util.logging.Logger;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.AuctionManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.FishSellerBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
public class WaitSecondBidBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this behaviour is to be added. */
	private FishSellerBehaviour myFSM;
	
	/** The selected transition to the next state. */
	private int transition =
			FishSellerBehaviour.TRANSITION_TO_ATTRIBUTE;
	
	/** Allows filtering incoming messages. */
	private final MessageTemplate messageFilter;
	
	/** The time to wait for the second bid. */
	private static final long SECOND_BID_WAIT_DURATION = 20000l; // 20 sec
	
	/** Allows logging. */
	private static final Logger LOGGER =
			Logger.getLogger(WaitSecondBidBehaviour.class.getName());
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public WaitSecondBidBehaviour(
			SellerAgent mySellerAgent,
			FishSellerBehaviour myFSM)
	{
		super(mySellerAgent);
		
		this.myFSM = myFSM;
		
		this.messageFilter =
				this.createMessageFilter();
	}
	
	@Override
	public void action()
	{
		this.block(SECOND_BID_WAIT_DURATION);
		
		// Receive messages
		ACLMessage mess = myAgent.receive(
				this.messageFilter);
		
		SellerAgent mySellerAgent =
				(SellerAgent)super.myAgent;
		
		AID bidder = null;
		
		if(mess != null)
		{
			try
			{
				bidder = (AID) mess.getContentObject();
			}
			catch (UnreadableException e)
			{
				WaitSecondBidBehaviour.LOGGER.log(Level.SEVERE, null, e);
			}
			
			if(bidder != null)
			{
				this.transition =
						FishSellerBehaviour.TRANSITION_TO_WAIT_MORE_BID;
			}
		}
		
		if(mess == null || bidder == null)
		{
			this.transition =
					FishSellerBehaviour.TRANSITION_TO_ATTRIBUTE;
			
			// send rep_bid(OK)
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

package fr.univpau.m2ti.sma.fishmarket.auction.running.fsm;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.seller.states.AnnoucePriceBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.seller.states.AttributeFishSupplyBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.seller.states.GiveFishSupplyBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.seller.states.HandleMultipleBidBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.seller.states.TerminateCancelBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.seller.states.TerminateSuccessBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.seller.states.WaitBidBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.seller.states.WaitPaymentBehaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
/**
 * The behaviour of the seller agent when attending his auction.
 * 
 * @author Josuah Aron
 *
 */
public class RunningAuctionSellerFSMBehaviour extends FSMBehaviour
{
	/** The id of the conversation of this auction. */
	private final String conversationId;
	
	/** The number of bids received for the last announced price. */
	private int bidCount = 0;
	
	/** The number of wait cycle which has been done. */
	private int waitCycleCount = 0;
	
	/** The state in which the seller makes an announcement of the current price. */
	private static final String STATE_ANNONCE_PRICE =
			"STATE_ANNONCE_PRICE";
	
	/** The state in which the seller waits for the first bid. */
	private static final String STATE_WAIT_BID =
			"STATE_WAIT_BID";
	
	/** The state in which the seller waits for a second bid. */
	private static final String STATE_HANDLE_MULTIPLE_BID =
			"STATE_HANDLE_MULTIPLE_BID";
	
	/** The state in which the seller send an attribution notification to the selected bidder. */
	private static final String STATE_SEND_TO_ATTRIBUTE =
			"STATE_SEND_TO_ATTRIBUTE";
	
	/** The state in which the seller send the fish supply to the selected bidder. */
	private static final String STATE_SEND_TO_GIVE =
			"STATE_SEND_TO_GIVE";
	
	/** The state in which the seller waits for the payment from the selected bidder. */
	private static final String STATE_WAIT_TO_PAY =
			"STATE_WAIT_TO_PAY";
	
	/** The final state when the auction succeeded. */
	private static final String STATE_TERMINATE_SUCCESS =
			"STATE_TERMINATE_SUCCESS";
	
	/** The final state when the auction was cancelled. */
	private static final String STATE_TERMINATE_CANCEL =
			"STATE_TERMINATE_CANCEL";
	
	/** The code to activate the transition which leads to wait for bid. */
	public static final int TRANSITION_TO_WAIT_TO_BID;
	
	/** The code to activate the transition which leads to the state to terminate when the auction is cancelled. */
	public static final int TRANSITION_TO_TERMINATE_CANCEL;
	
	/** The code to activate the transition which allows to continue to wait for a first incoming bid. */
	public static final int TRANSITION_TO_HANDLE_MULTIPLE_BID;
	
	/** The code to activate the transition which leads to the state which allows to attribute the fish supply. */
	public static final int TRANSITION_TO_ATTRIBUTE;
	
	/** The code to activate the transition which leads to the state which allows to attribute the fish supply. */
	public static final int TRANSITION_TO_WAIT_TO_PAY;
	
	/** The code to activate the transition which leads to the state which allows to attribute the fish supply. */
	public static final int TRANSITION_TO_TERMINATE_SUCCESS;
	
	/** The code to activate the transition which leads back to the first state. */
	public static final int TRANSITION_TO_ANNOUNCE;
	
	static
	{
		int start = -1;
		
		TRANSITION_TO_TERMINATE_CANCEL = ++start;
		TRANSITION_TO_HANDLE_MULTIPLE_BID = ++start;
		TRANSITION_TO_ATTRIBUTE = ++start;
		TRANSITION_TO_ANNOUNCE = ++start;
		TRANSITION_TO_WAIT_TO_PAY = ++start;
		TRANSITION_TO_TERMINATE_SUCCESS = ++start;
		TRANSITION_TO_WAIT_TO_BID = ++start;
	}
	
	/**
	 * Creates the behaviour of the seller agent when his auction is running.
	 * 
	 * @param mySellerAgent the seller agent to which the behaviour is to be added.
	 */
	public RunningAuctionSellerFSMBehaviour(
			SellerAgent mySellerAgent, String conversationId)
	{
		super(mySellerAgent);
		
		this.conversationId = conversationId;
		
		// Add states
		this.registerFirstState(
				new AnnoucePriceBehaviour(mySellerAgent, this),
				STATE_ANNONCE_PRICE);
		
		this.registerState(
				new WaitBidBehaviour(mySellerAgent, this),
				STATE_WAIT_BID);
		
		this.registerState(
				new HandleMultipleBidBehaviour(mySellerAgent, this),
				STATE_HANDLE_MULTIPLE_BID);
		
		this.registerState(
				new AttributeFishSupplyBehaviour(mySellerAgent, this),
				STATE_SEND_TO_ATTRIBUTE);
		
		this.registerState(
				new GiveFishSupplyBehaviour(mySellerAgent, this),
				STATE_SEND_TO_GIVE);
		
		this.registerState(
				new WaitPaymentBehaviour(mySellerAgent, this),
				STATE_WAIT_TO_PAY);
		
		this.registerState(
				new WaitPaymentBehaviour(mySellerAgent, this),
				STATE_WAIT_TO_PAY);
		
		this.registerLastState(
				new TerminateCancelBehaviour(mySellerAgent, this),
				STATE_TERMINATE_CANCEL);
		
		this.registerLastState(
				new TerminateSuccessBehaviour(mySellerAgent, this),
				STATE_TERMINATE_SUCCESS);
		
		// Add transitions
		this.registerDefaultTransition(STATE_ANNONCE_PRICE,
				STATE_WAIT_BID);
		
		this.registerTransition(STATE_WAIT_BID,
				STATE_WAIT_BID, TRANSITION_TO_WAIT_TO_BID);
		
		this.registerTransition(STATE_WAIT_BID,
				STATE_ANNONCE_PRICE, TRANSITION_TO_ANNOUNCE);
		
		this.registerTransition(STATE_WAIT_BID,
				STATE_TERMINATE_CANCEL, TRANSITION_TO_TERMINATE_CANCEL);
		
		this.registerTransition(STATE_WAIT_BID,
				STATE_SEND_TO_ATTRIBUTE, TRANSITION_TO_ATTRIBUTE);
		
		this.registerTransition(STATE_WAIT_BID,
				STATE_HANDLE_MULTIPLE_BID, TRANSITION_TO_HANDLE_MULTIPLE_BID);
		
		this.registerTransition(STATE_HANDLE_MULTIPLE_BID,
				STATE_SEND_TO_ATTRIBUTE, TRANSITION_TO_ATTRIBUTE);
		
		this.registerTransition(STATE_HANDLE_MULTIPLE_BID,
				STATE_ANNONCE_PRICE, TRANSITION_TO_ANNOUNCE);
		
		this.registerDefaultTransition(
				STATE_SEND_TO_ATTRIBUTE, STATE_SEND_TO_GIVE);
		
		this.registerDefaultTransition(
				STATE_SEND_TO_GIVE, STATE_WAIT_TO_PAY);
		
		this.registerTransition(STATE_WAIT_TO_PAY,
				STATE_WAIT_TO_PAY, TRANSITION_TO_WAIT_TO_PAY);
		
		this.registerTransition(STATE_WAIT_TO_PAY,
				STATE_TERMINATE_SUCCESS, TRANSITION_TO_TERMINATE_SUCCESS);
	}

	/**
	 * 
	 * @return the identifier of the the conversation for this auction.
	 */
	public String getConversationId()
	{
		return conversationId;
	}
	
	/**
	 * Notifies that a new bid has been received for the last announced price.
	 */
	public void notifyNewBid()
	{
		++ this.bidCount;
		
		((SellerAgent)this.myAgent).notifyNewBid();
	}
	
	/**
	 * 
	 * @return the number of bid received for the last announced price.
	 */
	public int getBidCount()
	{
		return this.bidCount;
	}
	
	/**
	 * Sets the number of bid received to 0.
	 */
	public void resetBidCount()
	{
		this.bidCount = 0;
	}
	
	/**
	 * Notifies that a new bid wait cycle is over.
	 */
	public void notifyWaitCycle()
	{
		++ this.waitCycleCount;
	}
	
	/**
	 * 
	 * @return the number of bid wait cycles which have been done.
	 */
	public int getWaitCycleCount()
	{
		return this.waitCycleCount;
	}
	
	/**
	 * Sets the number of bid wait cycle to 0.
	 */
	public void resetWaitCycleCount()
	{
		this.waitCycleCount = 0;
	}
	
	/**
	 * 
	 * @return the base message filter for conversation for this auction.
	 */
	public MessageTemplate getMessageFilter()
	{
		return MessageTemplate.and(
				MessageTemplate.MatchTopic(
						RunningAuctionMarketFSMBehaviour.MESSAGE_TOPIC),
				MessageTemplate.MatchConversationId(
						this.conversationId));
	}
}

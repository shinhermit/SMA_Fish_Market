package fr.univpau.m2ti.sma.fishmarket.behaviour.seller;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.RunningAuctionManagementFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states.auction.*;
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
	
	/** The number of block cycles h=which have been made in order to wait for incoming bids. */
	private int waitCycleCount = 0;
	
	/** The state in which the seller makes an announcement of the current price. */
	private static final String STATE_ANNONCE_PRICE =
			"STATE_ANNONCE_PRICE";
	
	/** The state in which the seller waits for the first bid. */
	private static final String STATE_WAIT_FIRST_BID =
			"STATE_WAIT_FIRST_BID";
	
	/** The state in which the seller waits for a second bid. */
	private static final String STATE_WAIT_SECOND_BID =
			"STATE_WAIT_SECOND_BID";
	
	/** The state in which the seller waits for more bids. */
	private static final String STATE_WAIT_MORE_BID =
			"STATE_WAIT_MORE_BID";
	
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
	
	/** The code to activate the transition which leads to the state to terminate when the auction is cancelled. */
	public static final int TRANSITION_TO_TERMINATE_CANCEL;
	
	/** The code to activate the transition which allows to continue to wait for a first incoming bid. */
	public static final int TRANSITION_TO_WAIT_FIRST_BID;
	
	/** The code to activate the transition which leads to the state to wait for a second bid after the first has been received. */
	public static final int TRANSITION_TO_WAIT_SECOND_BID;
	
	/** The code to activate the transition which leads to the state which allows to attribute the fish supply. */
	public static final int TRANSITION_TO_ATTRIBUTE;
	
	/** The code to activate the transition which leads to the state which allows to attribute the fish supply. */
	public static final int TRANSITION_TO_WAIT_TO_PAY;
	
	/** The code to activate the transition which leads to the state which allows to attribute the fish supply. */
	public static final int TRANSITION_TO_TERMINATE_SUCCESS;
	
	/** The code to activate the transition which leads to the state to wait for more bids. */
	public static final int TRANSITION_TO_WAIT_MORE_BID;
	
	/** The code to activate the transition which leads back to the first state. */
	public static final int TRANSITION_TO_ANNOUNCE;
	
	static
	{
		int start = -1;
		
		TRANSITION_TO_TERMINATE_CANCEL = ++start;
		TRANSITION_TO_WAIT_FIRST_BID = ++start;
		TRANSITION_TO_WAIT_SECOND_BID = ++start;
		TRANSITION_TO_WAIT_MORE_BID = ++start;
		TRANSITION_TO_ATTRIBUTE = ++start;
		TRANSITION_TO_ANNOUNCE = ++start;
		TRANSITION_TO_WAIT_TO_PAY = ++start;
		TRANSITION_TO_TERMINATE_SUCCESS = ++start;
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
				new WaitFirstBidBehaviour(mySellerAgent, this),
				STATE_WAIT_FIRST_BID);
		
		this.registerState(
				new WaitSecondBidBehaviour(mySellerAgent, this),
				STATE_WAIT_SECOND_BID);
		
		this.registerState(
				new WaitMoreBidBehaviour(mySellerAgent, this),
				STATE_WAIT_MORE_BID);
		
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
		this.registerDefaultTransition(
				STATE_ANNONCE_PRICE, STATE_WAIT_FIRST_BID);
		
		this.registerTransition(STATE_WAIT_FIRST_BID,
				STATE_WAIT_FIRST_BID, TRANSITION_TO_WAIT_FIRST_BID);
		
		this.registerTransition(STATE_WAIT_FIRST_BID,
				STATE_WAIT_SECOND_BID, TRANSITION_TO_WAIT_SECOND_BID);
		
		this.registerTransition(STATE_WAIT_FIRST_BID,
				STATE_ANNONCE_PRICE, TRANSITION_TO_ANNOUNCE);
		
		this.registerTransition(STATE_WAIT_FIRST_BID,
				STATE_TERMINATE_CANCEL, TRANSITION_TO_TERMINATE_CANCEL);
		
		this.registerTransition(STATE_WAIT_SECOND_BID,
				STATE_WAIT_SECOND_BID, TRANSITION_TO_WAIT_SECOND_BID);
		
		this.registerTransition(STATE_WAIT_SECOND_BID,
				STATE_SEND_TO_ATTRIBUTE, TRANSITION_TO_ATTRIBUTE);
		
		this.registerTransition(STATE_WAIT_SECOND_BID,
				STATE_WAIT_MORE_BID, TRANSITION_TO_WAIT_MORE_BID);
		
		this.registerTransition(STATE_WAIT_MORE_BID,
				STATE_SEND_TO_ATTRIBUTE, TRANSITION_TO_ATTRIBUTE);
		
		this.registerTransition(STATE_WAIT_MORE_BID,
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
	 * 
	 * @return the base message filter for conversation for this auction.
	 */
	public MessageTemplate getMessageFilter()
	{
		return MessageTemplate.and(
				MessageTemplate.MatchTopic(
						RunningAuctionManagementFSMBehaviour.MESSAGE_TOPIC),
				MessageTemplate.MatchConversationId(
						this.conversationId));
	}

	/**
	 * 
	 * @return the number of block cycles h=which have been made in order to wait for incoming bids.
	 */
	public int getWaitCycleCount()
	{
		return waitCycleCount;
	}

	/**
	 * 
	 * @param waitCycleCount the number of block cycles h=which have been made in order to wait for incoming bids.
	 */
	public void setWaitCycleCount(int waitCycleCount)
	{
		this.waitCycleCount = waitCycleCount;
	}
	
	/**
	 * Resets the number of block cycles h=which have been made in order to wait for incoming bids.
	 */
	public void resetWaitCycleCount()
	{
		this.waitCycleCount = 0;
	}
	
	/**
	 * Notifies that a new sleep cycle in order to wait for bidders before starting to announce.
	 * @return the updated number of wait cycles.
	 */
	public int notifyNewWaitCycle()
	{
		return ++ this.waitCycleCount;
	}
}

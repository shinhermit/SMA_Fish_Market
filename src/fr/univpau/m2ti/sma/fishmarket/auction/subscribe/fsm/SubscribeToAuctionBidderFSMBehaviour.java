package fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm;

import fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.bidder.states.*;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
public class SubscribeToAuctionBidderFSMBehaviour extends FSMBehaviour
{
	private static final String STATE_SUBSCRIPTION_PROCESS_START =
			"STATE_SUBSCRIPTION_PROCESS_START";
	private static final String STATE_WAIT_AUCTION_LIST =
			"STATE_WAIT_AUCTION_LIST";
	private static final String STATE_PICK_AUCTION =
			"STATE_PICK_AUCTION";
	private static final String STATE_WAIT_SUBSCRIPTION_REPLY =
			"STATE_WAIT_SUBSCRIPTION_REPLY";
	private static final String STATE_CREATE_BIDDER_FSM =
			"STATE_CREATE_BIDDER_FSM";
	private static final String STATE_TERMINATE_FAILURE =
			"STATE_TERMINATE_FAILURE";
private static final String STATE_TERMINATE_SUCCESS =
			"STATE_TERMINATE_SUCCESS";

	/**
	 * Return code which activates the transition to wait for an auction list.
	 */
	public static final int TRANSITION_REQUEST_AUCTION_LIST;

	/**
	 * Return code which activates the transition to the state where auctions are picked.
	 */
	public static final int TRANSITION_AUCTION_LIST_RECEIVED;

	/**
	 * Return code which activates the transition to wait for a subscription response.
	 */
	public static final int TRANSITION_REQUEST_SUBSCRIPTION;

	/**
	 * Return code which activates the transition confirming that our subscription was accepted.
	 */
	public static final int TRANSITION_SUBSCRIPTION_ACCEPTED;

	/**
	 * Return code which activates the transition confirming that our subscription was refused.
	 */
	public static final int TRANSITION_SUBSCRIPTION_REFUSED;

	/**
	 * Return code which activates the transition to the exit with failure state of this fsm.
	 */
	public static final int TRANSITION_TERMINATE_FAILURE;

	/**
	 * Return code which activates the transition to the exit with success state of this fsm.
	 */
	public static final int TRANSITION_TERMINATE_SUCCESS;

	/**
	 * Return code which activates the transition to the start state of this FSM.
	 */
	public static final int TRANSITION_RETURN_TO_SUBSCRIPTION_PROCESS_START;

	/**
	 * Stay on the wait auction list state.
	 */
	public static final int TRANSITION_WAIT_AUCTION_LIST;

	/**
	 * Stay on the wait subscription result state.
	 */
	public static final int TRANSITION_WAIT_SUBSCRIPTION_RESULT;

	/**
	 * Stay on pick auction while user chooses an auction.
	 */
	public static final int TRANSITION_WAIT_USER_CHOICE;

	/**
	 * An empty auction list has been received.
	 */
	public static final int STATUS_EMPTY_AUCTION_LIST;

	/**
	 * Used to pass the auction list to pick auctions from.
	 */
	private ACLMessage request;

	private String lastSubscribedAuction;

	private Set<String> subscribedAuctions = new HashSet<String>();

	static
	{
		int start = -1;
		TRANSITION_REQUEST_AUCTION_LIST = ++start;
		TRANSITION_AUCTION_LIST_RECEIVED = ++start;
		TRANSITION_REQUEST_SUBSCRIPTION = ++start;
		TRANSITION_SUBSCRIPTION_ACCEPTED = ++start;
		TRANSITION_SUBSCRIPTION_REFUSED = ++start;
		TRANSITION_TERMINATE_FAILURE = ++start;
		TRANSITION_TERMINATE_SUCCESS = ++start;
		TRANSITION_RETURN_TO_SUBSCRIPTION_PROCESS_START = ++start;
		TRANSITION_WAIT_AUCTION_LIST = ++start;
		TRANSITION_WAIT_SUBSCRIPTION_RESULT = ++start;
		TRANSITION_WAIT_USER_CHOICE = ++start;

		start = -1;
		STATUS_EMPTY_AUCTION_LIST = ++start;
	}

	public SubscribeToAuctionBidderFSMBehaviour(Agent a)
	{
		super(a);

		// Declare and register states

		SubscriptionProcessStartBidderBehaviour initialState =
				new SubscriptionProcessStartBidderBehaviour(a, this);

		this.registerFirstState(
				initialState,
				STATE_SUBSCRIPTION_PROCESS_START
		);

		WaitAuctionListBidderBehaviour waitAuctionListBehaviour =
				new WaitAuctionListBidderBehaviour(a, this);

		this.registerState(
				waitAuctionListBehaviour,
				STATE_WAIT_AUCTION_LIST
		);

		PickAuctionBidderBehaviour pickAuctionBehaviour =
				new PickAuctionBidderBehaviour(a, this);

		this.registerState(
				pickAuctionBehaviour,
				STATE_PICK_AUCTION
		);

		WaitSubscriptionReplyBidderBehaviour waitSubscriptionReplyBehaviour =
				new WaitSubscriptionReplyBidderBehaviour(a, this);

		this.registerState(
				waitSubscriptionReplyBehaviour,
				STATE_WAIT_SUBSCRIPTION_REPLY
		);

//		CreateBidderFSMBehaviour createBidderFSMBehaviour =
//				new CreateBidderFSMBehaviour(a, this);
//
//		this.registerState(
//				createBidderFSMBehaviour,
//				STATE_CREATE_BIDDER_FSM
//		);

		TerminateFailureBidderBehaviour terminateFailureBidderBehaviour =
				new TerminateFailureBidderBehaviour(a);

		this.registerLastState(
				terminateFailureBidderBehaviour,
				STATE_TERMINATE_FAILURE
		);

		TerminateSuccessBidderBehaviour terminateSuccessBidderBehaviour =
				new TerminateSuccessBidderBehaviour(a);

		this.registerLastState(
				terminateSuccessBidderBehaviour,
				STATE_TERMINATE_SUCCESS
		);

		// Transitions
		this.registerTransition(
				STATE_SUBSCRIPTION_PROCESS_START,
				STATE_WAIT_AUCTION_LIST,
				SubscribeToAuctionBidderFSMBehaviour.TRANSITION_REQUEST_AUCTION_LIST
		);

		this.registerTransition(
				STATE_WAIT_AUCTION_LIST,
				STATE_PICK_AUCTION,
				SubscribeToAuctionBidderFSMBehaviour.TRANSITION_AUCTION_LIST_RECEIVED
		);

		this.registerTransition(
				STATE_WAIT_AUCTION_LIST,
				STATE_WAIT_AUCTION_LIST,
				SubscribeToAuctionBidderFSMBehaviour.TRANSITION_WAIT_AUCTION_LIST
		);

		this.registerTransition(
				STATE_PICK_AUCTION,
				STATE_WAIT_SUBSCRIPTION_REPLY,
				SubscribeToAuctionBidderFSMBehaviour.TRANSITION_REQUEST_SUBSCRIPTION
		);

		this.registerTransition(
				STATE_PICK_AUCTION,
				STATE_PICK_AUCTION,
				SubscribeToAuctionBidderFSMBehaviour.TRANSITION_WAIT_USER_CHOICE
		);

		this.registerTransition(
				STATE_WAIT_SUBSCRIPTION_REPLY,
				STATE_WAIT_SUBSCRIPTION_REPLY,
				SubscribeToAuctionBidderFSMBehaviour.TRANSITION_WAIT_SUBSCRIPTION_RESULT
		);

		// Return to process start when auction list empty
		this.registerTransition(
				STATE_PICK_AUCTION,
				STATE_SUBSCRIPTION_PROCESS_START,
				SubscribeToAuctionBidderFSMBehaviour.TRANSITION_RETURN_TO_SUBSCRIPTION_PROCESS_START
		);

		this.registerTransition(
				STATE_WAIT_SUBSCRIPTION_REPLY,
				STATE_TERMINATE_SUCCESS,
				SubscribeToAuctionBidderFSMBehaviour.TRANSITION_SUBSCRIPTION_ACCEPTED
		);

		this.registerTransition(
				STATE_WAIT_SUBSCRIPTION_REPLY,
				STATE_PICK_AUCTION,
				SubscribeToAuctionBidderFSMBehaviour.TRANSITION_SUBSCRIPTION_REFUSED
		);

//		this.registerDefaultTransition(
//				STATE_CREATE_BIDDER_FSM,
//				STATE_TERMINATE_SUCCESS
//		);

		//transitions to subscription process end
		this.registerTransition(
				STATE_SUBSCRIPTION_PROCESS_START,
				STATE_TERMINATE_FAILURE,
				SubscribeToAuctionBidderFSMBehaviour.TRANSITION_TERMINATE_FAILURE
		);

		this.registerTransition(
				STATE_WAIT_AUCTION_LIST,
				STATE_TERMINATE_FAILURE,
				SubscribeToAuctionBidderFSMBehaviour.TRANSITION_TERMINATE_FAILURE
		);

		this.registerTransition(
				STATE_PICK_AUCTION,
				STATE_TERMINATE_FAILURE,
				SubscribeToAuctionBidderFSMBehaviour.TRANSITION_TERMINATE_FAILURE
		);

	}

	/**
	 * @return the last received message containing an auction list.
	 */
	public ACLMessage getRequest()
	{
		return this.request;
	}

	/**
	 * @param request A message containing an auction list to store.
	 */
	public void setRequest(ACLMessage request)
	{
		this.request = request;
	}

	/**
	 * Stores seller in list of subscribed auctions.
	 *
	 * @param seller
	 */
	public void subscribeToAuction(String seller)
	{
		this.subscribedAuctions.add(seller);
	}

	/**
	 * Returns true if auction has already been subscribed to.
	 *
	 * @param auctionId
	 * @return
	 */
	public boolean hasSubscribedToAuction(String auctionId)
	{
		return this.subscribedAuctions.contains(auctionId);
	}

	public void setLastSubscribedAuction(String auction)
	{
		this.lastSubscribedAuction = auction;
	}

	public String getLastSubscribedAuction()
	{
		return this.lastSubscribedAuction;
	}

}

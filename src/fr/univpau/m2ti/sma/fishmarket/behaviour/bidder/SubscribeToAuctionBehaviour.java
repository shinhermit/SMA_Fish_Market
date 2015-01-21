package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder;

import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.subscription.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
public class SubscribeToAuctionBehaviour extends FSMBehaviour
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
	private static final String STATE_SUBSCRIPTION_PROCESS_END =
			"STATE_SUBSCRIPTION_PROCESS_END";

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
	 * Return code which activates the transition to the exit state of this FSM.
	 */
	public static final int TRANSITION_EXIT_SUBSCRIPTION_PROCESS;

	/**
	 * Return code which activates the transition to the start state of this FSM.
	 */
	public static final int TRANSITION_RETURN_TO_SUBSCRIPTION_PROCESS_START;

	/**
	 * An empty auction list has been received.
	 */
	public static final int STATUS_EMPTY_AUCTION_LIST;

	/**
	 * Used to pass the auction list to pick auctions from.
	 */
	private ACLMessage request;

	private AID lastSubscribedAuction;

	private Set<AID> subscribedAuctions = new HashSet<AID>();

	static
	{
		int start = -1;
		TRANSITION_REQUEST_AUCTION_LIST = ++start;
		TRANSITION_AUCTION_LIST_RECEIVED = ++start;
		TRANSITION_REQUEST_SUBSCRIPTION = ++start;
		TRANSITION_SUBSCRIPTION_ACCEPTED = ++start;
		TRANSITION_SUBSCRIPTION_REFUSED = ++start;
		TRANSITION_EXIT_SUBSCRIPTION_PROCESS = ++start;
		TRANSITION_RETURN_TO_SUBSCRIPTION_PROCESS_START = ++start;

		start = -1;
		STATUS_EMPTY_AUCTION_LIST = ++start;
	}

	public SubscribeToAuctionBehaviour(Agent a)
	{
		super(a);

		// Declare and register states

		SubscriptionProcessStartBehaviour initialState =
				new SubscriptionProcessStartBehaviour(a, this);

		this.registerFirstState(
				initialState,
				STATE_SUBSCRIPTION_PROCESS_START
		);

		WaitAuctionListBehaviour waitAuctionListBehaviour =
				new WaitAuctionListBehaviour(a, this);

		this.registerState(
				waitAuctionListBehaviour,
				STATE_WAIT_AUCTION_LIST
		);

		PickAuctionBehaviour pickAuctionBehaviour =
				new PickAuctionBehaviour(a, this);

		this.registerState(
				pickAuctionBehaviour,
				STATE_PICK_AUCTION
		);

		WaitSubscriptionReplyBehaviour waitSubscriptionReplyBehaviour =
				new WaitSubscriptionReplyBehaviour(a, this);

		this.registerState(
				waitSubscriptionReplyBehaviour,
				STATE_WAIT_SUBSCRIPTION_REPLY
		);

		CreateBidderFSMBehaviour createBidderFSMBehaviour =
				new CreateBidderFSMBehaviour(a, this);

		this.registerState(
				createBidderFSMBehaviour,
				STATE_CREATE_BIDDER_FSM
		);

		SubscriptionProcessEndBehaviour subscriptionProcessEndBehaviour =
				new SubscriptionProcessEndBehaviour(a);

		this.registerState(
				subscriptionProcessEndBehaviour,
				STATE_SUBSCRIPTION_PROCESS_END
		);

		// Transitions
		this.registerTransition(
				STATE_SUBSCRIPTION_PROCESS_START,
				STATE_WAIT_AUCTION_LIST,
				SubscribeToAuctionBehaviour.TRANSITION_REQUEST_AUCTION_LIST
		);

		this.registerTransition(
				STATE_WAIT_AUCTION_LIST,
				STATE_PICK_AUCTION,
				SubscribeToAuctionBehaviour.TRANSITION_AUCTION_LIST_RECEIVED
		);

		this.registerTransition(
				STATE_PICK_AUCTION,
				STATE_WAIT_SUBSCRIPTION_REPLY,
				SubscribeToAuctionBehaviour.TRANSITION_REQUEST_SUBSCRIPTION
		);

		// Return to process start when auction list empty
		this.registerTransition(
				STATE_PICK_AUCTION,
				STATE_SUBSCRIPTION_PROCESS_START,
				SubscribeToAuctionBehaviour.TRANSITION_RETURN_TO_SUBSCRIPTION_PROCESS_START
		);

		this.registerTransition(
				STATE_WAIT_SUBSCRIPTION_REPLY,
				STATE_CREATE_BIDDER_FSM,
				SubscribeToAuctionBehaviour.TRANSITION_SUBSCRIPTION_ACCEPTED
		);

		this.registerTransition(
				STATE_WAIT_SUBSCRIPTION_REPLY,
				STATE_PICK_AUCTION,
				SubscribeToAuctionBehaviour.TRANSITION_SUBSCRIPTION_REFUSED
		);

		this.registerTransition(
				STATE_CREATE_BIDDER_FSM,
				STATE_SUBSCRIPTION_PROCESS_START,
				SubscribeToAuctionBehaviour.TRANSITION_RETURN_TO_SUBSCRIPTION_PROCESS_START
		);

		this.registerDefaultTransition(
				STATE_CREATE_BIDDER_FSM,
				STATE_PICK_AUCTION
		);

		//transitions to subscription process end
		this.registerTransition(
				STATE_SUBSCRIPTION_PROCESS_START,
				STATE_SUBSCRIPTION_PROCESS_END,
				SubscribeToAuctionBehaviour.TRANSITION_EXIT_SUBSCRIPTION_PROCESS
		);

		this.registerTransition(
				STATE_WAIT_AUCTION_LIST,
				STATE_SUBSCRIPTION_PROCESS_END,
				SubscribeToAuctionBehaviour.TRANSITION_EXIT_SUBSCRIPTION_PROCESS
		);

		this.registerTransition(
				STATE_PICK_AUCTION,
				STATE_SUBSCRIPTION_PROCESS_END,
				SubscribeToAuctionBehaviour.TRANSITION_EXIT_SUBSCRIPTION_PROCESS
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
	public void subscribeToAuction(AID seller)
	{
		this.subscribedAuctions.add(seller);
	}

	/**
	 * Returns true if auction has already been subscribed to.
	 *
	 * @param seller
	 * @return
	 */
	public boolean hasSubscribedToAuction(String auctionId)
	{
		return this.subscribedAuctions.contains(auctionId);
	}

	public void setLastSubscribedAuction(AID auction)
	{
		this.lastSubscribedAuction = auction;
	}

	public AID getLastSubscribedAuction()
	{
		return this.lastSubscribedAuction;
	}

}

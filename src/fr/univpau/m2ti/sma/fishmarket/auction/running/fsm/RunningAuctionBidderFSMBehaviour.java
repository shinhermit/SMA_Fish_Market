package fr.univpau.m2ti.sma.fishmarket.auction.running.fsm;

import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.bidder.states.*;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class RunningAuctionBidderFSMBehaviour extends FSMBehaviour
{
	private static final String STATE_INITIAL_OR_AFTER_FAILED_BID =
			"STATE_INITIAL_OR_AFTER_FAILED_BID";
	private static final String STATE_ABOUT_TO_BID =
			"STATE_ABOUT_TO_BID";
	private static final String STATE_WAIT_BID_RESULT =
			"STATE_WAIT_BID_RESULT";
	private static final String STATE_WAIT_ATTRIBUTION =
			"STATE_WAIT_ATTRIBUTION";
	private static final String STATE_WAIT_FISH =
			"STATE_WAIT_FISH";
	private static final String STATE_PAYMENT =
			"STATE_PAYMENT";
	private static final String STATE_AUCTION_OVER_SUCCESSFULLY =
			"STATE_AUCTION_OVER_SUCCESSFULLY";
	private static final String STATE_AUCTION_OVER_UNSUCESSFULLY =
			"STATE_AUCTION_OVER_UNSUCESSFULLY";
	private static final String STATE_OTHER_BIDDER_WON =
			"STATE_OTHER_BIDDER_WON";
	private static final String STATE_AUCTION_WITHDRAWAL =
				"STATE_AUCTION_WITHDRAWAL";


	/** Return code which activates the transition to wait for an auction list. */
	public static final int TRANSITION_RECEIVED_FIRST_ANNOUNCE;

	/** Return code which activates the transition to the state where auctions are picked. */
	public static final int TRANSITION_RECEIVED_SUBSEQUENT_ANNOUNCE;

	/** Return code which activates the transition to the state where auctions are picked. */
	public static final int TRANSITION_BID;

	/** Return code which activates the transition to wait for a subscription response. */
	public static final int TRANSITION_RECEIVED_REP_BID_OK;

	/** Return code which activates the transition confirming that our subscription was accepted. */
	public static final int TRANSITION_RECEIVED_REP_BID_NOK;

	public static final int TRANSITION_GO_GET_FISH;

	public static final int TRANSITION_TO_PAYMENT;

	/** Return code which activates the transition confirming that our subscription was accepted. */
	public static final int TRANSITION_RECEIVED_AUCTION_CANCELLED;

	/** Return code which activates the transition confirming that our subscription was accepted. */
	public static final int TRANSITION_RECEIVED_AUCTION_OVER;

	public static final int TRANSITION_WAIT_AUCTION_OVER;

	public static final int TRANSITION_WAIT_FIRST_ANNOUNCE;

	public static final int TRANSITION_WAIT_BID_RESULT;

	public static final int TRANSITION_WAIT_ATTRIBUTION;

	public static final int TRANSITION_WAIT_FISH;

	public static final int TRANSITION_WAIT_USER_CHOICE;

	static
	{
		int start = 1;
		TRANSITION_RECEIVED_FIRST_ANNOUNCE = ++start;
		TRANSITION_RECEIVED_SUBSEQUENT_ANNOUNCE = ++start;
		TRANSITION_BID = ++start;
		TRANSITION_RECEIVED_REP_BID_OK = ++start;
		TRANSITION_RECEIVED_REP_BID_NOK = ++start;
		TRANSITION_GO_GET_FISH = ++start;
		TRANSITION_TO_PAYMENT = ++start;
		TRANSITION_RECEIVED_AUCTION_CANCELLED = ++start;
		TRANSITION_RECEIVED_AUCTION_OVER = ++start;
		TRANSITION_WAIT_AUCTION_OVER = ++start;
		TRANSITION_WAIT_FIRST_ANNOUNCE = ++start;
		TRANSITION_WAIT_BID_RESULT = ++start;
		TRANSITION_WAIT_ATTRIBUTION = ++start;
		TRANSITION_WAIT_FISH = ++start;
		TRANSITION_WAIT_USER_CHOICE = ++start;
	}

	/** Message holder */
	private ACLMessage request;

	public RunningAuctionBidderFSMBehaviour (Agent a)
	{
		super(a);
		// Declare and register states
		this.registerFirstState(
				new InitialOrAfterFailedBidBidderBehaviour(a, this),
				STATE_INITIAL_OR_AFTER_FAILED_BID
		);

		this.registerState(
				new AboutToBidBidderBehaviour(a, this),
				STATE_ABOUT_TO_BID
		);

		this.registerState(
				new WaitBidResultBidderBehaviour(a, this),
				STATE_WAIT_BID_RESULT
		);

		this.registerState(
				new WaitAttributionBidderBehaviour(a, this),
				STATE_WAIT_ATTRIBUTION
		);

		this.registerState(
				new WaitFishBidderBehaviour(a, this),
				STATE_WAIT_FISH
		);

		this.registerState(
				new PaymentBidderBehaviour(a, this),
				STATE_PAYMENT
		);

		this.registerLastState(
				new AuctionOverSuccessfullyBidderBehaviour(a, this),
				STATE_AUCTION_OVER_SUCCESSFULLY
		);

		this.registerLastState(
				new AuctionOverUnsuccessfullyBidderBehaviour(a, this),
				STATE_AUCTION_OVER_UNSUCESSFULLY
		);

		this.registerLastState(
				new OtherBidderWonBidderBehaviour(a, this),
				STATE_OTHER_BIDDER_WON
		);

		this.registerLastState(
				new WithdrawFromAuctionBidderBehaviour(a, this),
				STATE_AUCTION_WITHDRAWAL
		);

		// Transitions
		this.registerTransition(
				STATE_INITIAL_OR_AFTER_FAILED_BID,
				STATE_ABOUT_TO_BID,
				RunningAuctionBidderFSMBehaviour.TRANSITION_RECEIVED_FIRST_ANNOUNCE
		);

		this.registerTransition(
				STATE_INITIAL_OR_AFTER_FAILED_BID,
				STATE_INITIAL_OR_AFTER_FAILED_BID,
				RunningAuctionBidderFSMBehaviour.TRANSITION_WAIT_FIRST_ANNOUNCE
		);

		this.registerTransition(
				STATE_ABOUT_TO_BID,
				STATE_WAIT_BID_RESULT,
				RunningAuctionBidderFSMBehaviour.TRANSITION_WAIT_BID_RESULT
		);

		this.registerTransition(
				STATE_ABOUT_TO_BID,
				STATE_ABOUT_TO_BID,
				RunningAuctionBidderFSMBehaviour.TRANSITION_RECEIVED_SUBSEQUENT_ANNOUNCE
		);

		this.registerTransition(
				STATE_ABOUT_TO_BID,
				STATE_ABOUT_TO_BID,
				RunningAuctionBidderFSMBehaviour.TRANSITION_WAIT_USER_CHOICE
		);

		this.registerTransition(
				STATE_WAIT_BID_RESULT,
				STATE_WAIT_ATTRIBUTION,
				RunningAuctionBidderFSMBehaviour.TRANSITION_RECEIVED_REP_BID_OK
		);

		this.registerTransition(
				STATE_WAIT_BID_RESULT,
				STATE_WAIT_BID_RESULT,
				RunningAuctionBidderFSMBehaviour.TRANSITION_WAIT_BID_RESULT
		);

		this.registerTransition(
				STATE_WAIT_BID_RESULT,
				STATE_INITIAL_OR_AFTER_FAILED_BID,
				RunningAuctionBidderFSMBehaviour.TRANSITION_RECEIVED_REP_BID_NOK
		);

		this.registerTransition(
				STATE_WAIT_ATTRIBUTION,
				STATE_WAIT_FISH,
				RunningAuctionBidderFSMBehaviour.TRANSITION_GO_GET_FISH
		);

		this.registerTransition(
				STATE_WAIT_ATTRIBUTION,
				STATE_WAIT_ATTRIBUTION,
				RunningAuctionBidderFSMBehaviour.TRANSITION_WAIT_ATTRIBUTION
		);

		this.registerTransition(
				STATE_WAIT_FISH,
				STATE_PAYMENT,
				RunningAuctionBidderFSMBehaviour.TRANSITION_TO_PAYMENT
		);

		this.registerTransition(
				STATE_WAIT_FISH,
				STATE_WAIT_FISH,
				RunningAuctionBidderFSMBehaviour.TRANSITION_WAIT_FISH
		);

		this.registerTransition(
				STATE_PAYMENT,
				STATE_PAYMENT,
				RunningAuctionBidderFSMBehaviour.TRANSITION_WAIT_AUCTION_OVER
		);

		this.registerTransition(
				STATE_PAYMENT,
				STATE_AUCTION_OVER_SUCCESSFULLY,
				RunningAuctionBidderFSMBehaviour.TRANSITION_RECEIVED_AUCTION_OVER
		);

		// Transitions to unsuccessful ends
		this.registerTransition(
				STATE_INITIAL_OR_AFTER_FAILED_BID,
				STATE_AUCTION_OVER_UNSUCESSFULLY,
				RunningAuctionBidderFSMBehaviour.TRANSITION_RECEIVED_AUCTION_CANCELLED
		);

		this.registerTransition(
				STATE_INITIAL_OR_AFTER_FAILED_BID,
				STATE_OTHER_BIDDER_WON,
				RunningAuctionBidderFSMBehaviour.TRANSITION_RECEIVED_AUCTION_OVER
		);

		this.registerTransition(
				STATE_ABOUT_TO_BID,
				STATE_AUCTION_OVER_UNSUCESSFULLY,
				RunningAuctionBidderFSMBehaviour.TRANSITION_RECEIVED_AUCTION_CANCELLED
		);

		this.registerTransition(
				STATE_ABOUT_TO_BID,
				STATE_OTHER_BIDDER_WON,
				RunningAuctionBidderFSMBehaviour.TRANSITION_RECEIVED_AUCTION_OVER
		);

		this.registerTransition(
				STATE_WAIT_BID_RESULT,
				STATE_AUCTION_OVER_UNSUCESSFULLY,
				RunningAuctionBidderFSMBehaviour.TRANSITION_RECEIVED_AUCTION_CANCELLED
		);

		this.registerTransition(
				STATE_WAIT_BID_RESULT,
				STATE_OTHER_BIDDER_WON,
				RunningAuctionBidderFSMBehaviour.TRANSITION_RECEIVED_AUCTION_OVER
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

	@Override
	public int onEnd()
	{
		return super.onEnd();
	}
}

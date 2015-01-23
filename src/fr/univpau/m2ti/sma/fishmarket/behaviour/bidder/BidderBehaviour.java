package fr.univpau.m2ti.sma.fishmarket.behaviour.bidder;

import fr.univpau.m2ti.sma.fishmarket.behaviour.bidder.states.auction.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class BidderBehaviour extends FSMBehaviour
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

	public static final int TRANSITION_WAIT_FIRST_ANNOUNCE;

	public static final int TRANSITION_WAIT_BID_RESULT;

	public static final int TRANSITION_WAIT_ATTRIBUTION;

	public static final int TRANSITION_WAIT_FISH;

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
		TRANSITION_WAIT_FIRST_ANNOUNCE = ++start;
		TRANSITION_WAIT_BID_RESULT = ++start;
		TRANSITION_WAIT_ATTRIBUTION = ++start;
		TRANSITION_WAIT_FISH = ++start;
	}

	/** Message holder */
	private ACLMessage request;

	private float maxPrice;

	/** Price at last bid */
	private float biddingPrice;

	public BidderBehaviour (Agent a, float maxPrice)
	{
		super(a);
		this.maxPrice = maxPrice;

		// Declare and register states
		this.registerFirstState(
				new InitialOrAfterFailedBidBehaviour(a, this),
				STATE_INITIAL_OR_AFTER_FAILED_BID
		);

		this.registerState(
				new AboutToBidBehaviour(a, this),
				STATE_ABOUT_TO_BID
		);

		this.registerState(
				new WaitBidResultBehaviour(a, this),
				STATE_WAIT_BID_RESULT
		);

		this.registerState(
				new WaitAttributionBehaviour(a, this),
				STATE_WAIT_ATTRIBUTION
		);

		this.registerState(
				new WaitFishBehaviour(a, this),
				STATE_WAIT_FISH
		);

		this.registerState(
				new PaymentBehaviour(a, this),
				STATE_PAYMENT
		);

		this.registerLastState(
				new AuctionOverSuccessfullyBehaviour(a, this),
				STATE_AUCTION_OVER_SUCCESSFULLY
		);

		this.registerLastState(
				new AuctionOverUnsuccessfullyBehaviour(a, this),
				STATE_AUCTION_OVER_UNSUCESSFULLY
		);

		this.registerLastState(
				new OtherBidderWonBehaviour(a, this),
				STATE_OTHER_BIDDER_WON
		);

		// Transitions
		this.registerTransition(
				STATE_INITIAL_OR_AFTER_FAILED_BID,
				STATE_ABOUT_TO_BID,
				BidderBehaviour.TRANSITION_RECEIVED_FIRST_ANNOUNCE
		);

		this.registerTransition(
				STATE_INITIAL_OR_AFTER_FAILED_BID,
				STATE_INITIAL_OR_AFTER_FAILED_BID,
				BidderBehaviour.TRANSITION_WAIT_FIRST_ANNOUNCE
		);

		this.registerTransition(
				STATE_ABOUT_TO_BID,
				STATE_WAIT_BID_RESULT,
				BidderBehaviour.TRANSITION_BID
		);

		this.registerTransition(
				STATE_ABOUT_TO_BID,
				STATE_ABOUT_TO_BID,
				BidderBehaviour.TRANSITION_RECEIVED_SUBSEQUENT_ANNOUNCE
		);

		this.registerTransition(
				STATE_WAIT_BID_RESULT,
				STATE_WAIT_ATTRIBUTION,
				BidderBehaviour.TRANSITION_RECEIVED_REP_BID_OK
		);

		this.registerTransition(
				STATE_WAIT_BID_RESULT,
				STATE_WAIT_BID_RESULT,
				BidderBehaviour.TRANSITION_WAIT_BID_RESULT
		);

		this.registerTransition(
				STATE_WAIT_BID_RESULT,
				STATE_INITIAL_OR_AFTER_FAILED_BID,
				BidderBehaviour.TRANSITION_RECEIVED_REP_BID_NOK
		);

		this.registerTransition(
				STATE_WAIT_ATTRIBUTION,
				STATE_WAIT_FISH,
				BidderBehaviour.TRANSITION_GO_GET_FISH
		);

		this.registerTransition(
				STATE_WAIT_ATTRIBUTION,
				STATE_WAIT_ATTRIBUTION,
				BidderBehaviour.TRANSITION_WAIT_ATTRIBUTION
		);

		this.registerTransition(
				STATE_WAIT_FISH,
				STATE_PAYMENT,
				BidderBehaviour.TRANSITION_TO_PAYMENT
		);

		this.registerTransition(
				STATE_WAIT_FISH,
				STATE_WAIT_FISH,
				BidderBehaviour.TRANSITION_WAIT_FISH
		);

		this.registerDefaultTransition(
				STATE_PAYMENT,
				STATE_AUCTION_OVER_SUCCESSFULLY
		);

		// Transitions to unsuccessful ends
		this.registerTransition(
				STATE_INITIAL_OR_AFTER_FAILED_BID,
				STATE_AUCTION_OVER_UNSUCESSFULLY,
				BidderBehaviour.TRANSITION_RECEIVED_AUCTION_CANCELLED
		);

		this.registerTransition(
				STATE_INITIAL_OR_AFTER_FAILED_BID,
				STATE_OTHER_BIDDER_WON,
				BidderBehaviour.TRANSITION_RECEIVED_AUCTION_OVER
		);

		this.registerTransition(
				STATE_ABOUT_TO_BID,
				STATE_AUCTION_OVER_UNSUCESSFULLY,
				BidderBehaviour.TRANSITION_RECEIVED_AUCTION_CANCELLED
		);

		this.registerTransition(
				STATE_ABOUT_TO_BID,
				STATE_OTHER_BIDDER_WON,
				BidderBehaviour.TRANSITION_RECEIVED_AUCTION_OVER
		);

		this.registerTransition(
				STATE_WAIT_BID_RESULT,
				STATE_AUCTION_OVER_UNSUCESSFULLY,
				BidderBehaviour.TRANSITION_RECEIVED_AUCTION_CANCELLED
		);

		this.registerTransition(
				STATE_WAIT_BID_RESULT,
				STATE_OTHER_BIDDER_WON,
				BidderBehaviour.TRANSITION_RECEIVED_AUCTION_OVER
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

	public float getPriceLimit()
	{
		return this.maxPrice;
	}

	public void setBiddingPrice(float biddingPrice)
	{
		this.biddingPrice = biddingPrice;
	}

	public float getBiddingPrice()
	{
		return this.biddingPrice;
	}

	@Override
	public int onEnd()
	{
		return super.onEnd();
	}
}

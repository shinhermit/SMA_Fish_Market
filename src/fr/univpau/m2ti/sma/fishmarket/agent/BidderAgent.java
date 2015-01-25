package fr.univpau.m2ti.sma.fishmarket.agent;

import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionBidderFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.SubscribeToAuctionBidderFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.SubscribeToAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;
import fr.univpau.m2ti.sma.fishmarket.gui.BidderView;
import fr.univpau.m2ti.sma.fishmarket.protocol.FishMarket;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class BidderAgent extends Agent
{
	protected Logger logger =
			Logger.getLogger(BidderAgent.class.getName());


	private boolean bidsAutomatically = false;

	private Auction selectedAuction;

	private FSMBehaviour subscribeToAuctionFSM = null;
	private FSMBehaviour runningAuctionFSM = null;

	private BidderView bidderView;

	private float maxPrice;



	private static final String NO_MARKET_AVAILABLE =
			"Market agent couldn't be located.";

	private float biddingPrice;

	private boolean withinBiddingTimeFrame = false;
	public boolean castBid = false;



	@Override
	protected void setup()
	{
		// Look up market
		super.setup();

		//find new auction
		//this.createAuctionFinderFSM();
		this.bidderView = new BidderView(this);
		this.bidderView.prepare();
		this.bidderView.display();
	}

	@Override
	protected void takeDown()
	{
		// Cancel auction
		super.takeDown();
	}

	/**
	 * Called at the end of the auction subscription process.
	 *
	 * @param maxPrice
	 */
	public void createBidderFSM(float maxPrice)
	{
		this.bidderView.initBidList(this.getSubscribedAuction());

		if (this.runningAuctionFSM != null)
		{
			this.removeBehaviour(this.runningAuctionFSM);
		}

		this.runningAuctionFSM = new RunningAuctionBidderFSMBehaviour(this);

		this.addBehaviour(this.runningAuctionFSM);
	}

	public void createAuctionFinderFSM()
	{
		if (this.subscribeToAuctionFSM != null)
		{
			this.removeBehaviour(this.subscribeToAuctionFSM);
		}


		this.subscribeToAuctionFSM = new SubscribeToAuctionBidderFSMBehaviour(this);
		// Register behaviour
		this.addBehaviour(this.subscribeToAuctionFSM);

	}

	public void restoreInitialViewState()
	{
		this.bidderView.initialState();
	}

	public void displayAlert(String message)
	{
		this.bidderView.alert(message);
	}

	public void displayAuctionInformation(String message)
	{
		this.bidderView.addBidInformation(message);
	}



	/**
	 * Called by BidderView when refresh button is used.
	 */
	public void refreshAuctionList()
	{
		this.createAuctionFinderFSM();
	}

	/**
	 * Called by PickAuctionBehaviour.
	 * @param auctionList
	 */
	public void displayAuctionList(HashSet<Auction> auctionList)
	{
		this.bidderView.displayAuctionList(auctionList);
	}

	public void setBidsAutomatically(boolean bidsAutomatically)
	{
		this.bidsAutomatically = bidsAutomatically;
	}

	public boolean bidsAutomatically()
	{
		return this.bidsAutomatically;
	}

	/**
	 * Called by BidderView when an auction is selected and the suscribe
	 * button is used.
	 *
	 * @param selectedAuction
	 */
	public void subscribeToAuction(Auction selectedAuction)
	{
		this.selectedAuction = selectedAuction;

		//notify behaviour by sending a message
		ACLMessage unblockMessage = new ACLMessage(FishMarket.Performatives.TO_ANNOUNCE);
		unblockMessage.addReceiver(SubscribeToAuctionMarketFSMBehaviour.MESSAGE_TOPIC);
		unblockMessage.addReceiver(this.getAID());
		this.send(unblockMessage);
	}

	public Auction getSubscribedAuction()
	{
		return this.selectedAuction;
	}

	/**
	 * Called by BidderView when the bid button is clicked.
	 */
	public void takeUserBidIntoAccount()
	{
//		//notify behaviour by sending a message
//		ACLMessage userBidMessage = new ACLMessage(FishMarket.Performatives.TO_BID);
//		userBidMessage.addReceiver(
//				RunningAuctionMarketFSMBehaviour.MESSAGE_TOPIC
//		);
//		userBidMessage.addReceiver(this.getAID());
//
//		this.send(userBidMessage);
		this.castBid = true;
	}

	public boolean isWithinBiddingTimeFrame()
	{
		return withinBiddingTimeFrame;
	}

	public void setWithinBiddingTimeFrame(boolean withinBiddingTimeFrame)
	{
		this.withinBiddingTimeFrame = withinBiddingTimeFrame;
	}

	public boolean castBid()
	{
		return castBid;
	}

	public void setCastBid(boolean castBid)
	{
		this.castBid = castBid;
	}

	/**
	 * Called when a new bid is received.
	 *
	 * @param information
	 */
	public void displayBidInformation(String information)
	{
		this.bidderView.addBidInformation(information);
	}

	/**
	 * Displays an alert signaling that market is not available.
	 */
	public void alertNoMarket()
	{
		this.bidderView.alert(BidderAgent.NO_MARKET_AVAILABLE);
	}

	public float getMaxPrice()
	{
		return maxPrice;
	}

	public void setMaxPrice(float maxPrice)
	{
		this.maxPrice = maxPrice;
	}

	/**
	 * Returns the price which came with the last to_announce.
	 *
	 * @return
	 */
	public float getBiddingPrice()
	{
		return this.biddingPrice;
	}

	/**
	 * Called by behaviours when a new announce is received.
	 */
	public void handleAnnounce(float price)
	{
		this.biddingPrice = price;

		this.bidderView.addBidInformation("New price announce " + String.valueOf(price));

		if (this.bidsAutomatically())
		{
			//disable bid button
			this.bidderView.disableBidButton();
		}
		else
		{
			//enable bid button
			this.bidderView.enableBidButton();
		}
	}

	public AID getMarketAgentAID()
	{
		AID marketAgent = null;

		DFAgentDescription marketTemplate = new DFAgentDescription();
		ServiceDescription marketSd = new ServiceDescription();

		// Template for searching the market agent
		marketSd.setType(MarketAgent.SERVICE_DESCIPTION);
		marketTemplate.addServices(marketSd);

		try
		{
			DFAgentDescription[] marketResult = DFService.search(this, marketTemplate);

			if(marketResult.length == 1)
			{
				marketAgent = marketResult[0].getName();
			}
			else
			{
				logger.log(Level.SEVERE, null, "Logic error: multiple market agents found !");
			}
		}
		catch (FIPAException fe)
		{
			logger.log(Level.SEVERE, null, fe);
		}

		return marketAgent;
	}
}
